package io.github.leibnizhu.docbuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 主Verticle，工作：
 * 1. 初始化公共资源
 * 2. 部署其他DB相关的Verticle
 * 3. 配置Router并n启动Web服务器，监听
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-11 20:37.
 */
public class MainVerticle extends AbstractVerticle {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Router mainRouter;
    private HttpServer server;

    @Override
    public void start() throws Exception {
        super.start();
        initComponents();//初始化工具类/组件
        mountSubRouters();//挂载所有子路由
        startServer();//启动服务器
    }

    @Override
    public void stop() throws Exception {
        ConnectionManager.close();
        super.stop();
    }

    /**
     * 初始化工具类/组件
     */
    private void initComponents() {
        Constants.init(context);
        ConnectionManager.init();
        this.mainRouter = Router.router(vertx);
        this.server = vertx.createHttpServer();
    }

    /**
     * 挂载所有子路由
     */
    private void mountSubRouters() {
        //请求体解析
        mainRouter.route().handler(BodyHandler.create());
        //静态资源路由
        mainRouter.get("/").handler(ctx -> ctx.reroute("/static/index.html"));
        mainRouter.get("/static/*").handler(StaticHandler.create().setWebRoot("static"));
        mainRouter.get("/db").handler(this::getDatabases);
        mainRouter.get("/doc/:db").handler(this::getDocument);
    }

    private void getDocument(RoutingContext rc) {
        String db = rc.request().getParam("db");
        Runner runner = new Runner(db);
        try {
            rc.response().putHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("概要设计文档","UTF-8")+".md")
                    .end(runner.buildDocument());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getDatabases(RoutingContext rc) {
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES");
            List<String> dbs = new ArrayList<>();
            while (rs.next()) {
               dbs.add(rs.getString(1));
            }
            rs.close();
            stmt.close();
            JsonObject resp = new JsonObject().put("dbs", dbs);
            rc.response().end(resp.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动服务器
     */
    private void startServer() {
        Integer port = config().getInteger("serverPort", 8233);
        server.requestHandler(mainRouter::accept).listen(port, res -> {
            if(res.succeeded()){
                log.info("监听{}端口的HTTP服务器启动成功", port);
            } else {
                log.error("监听{}端口的HTTP服务器失败，原因：{}", port, res.cause().getLocalizedMessage());
            }
        });
    }
}
