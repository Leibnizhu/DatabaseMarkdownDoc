package io.github.leibnizhu.docbuilder;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.github.leibnizhu.docbuilder.Constants.*;

/**
 * ConnectionPoolManager接口的HikariCPi连接池实现类
 *
 * @author Leibniz.Hu
 * Created on 2017-10-12 12:28.
 */
class ConnectionManager {
    private Logger log = LoggerFactory.getLogger(getClass());
    private volatile static ConnectionManager INSTANCE = null;
    private Connection conn;

    /**
     * 连接数据库
     */
    Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PSWD);
        }
        return conn;
    }

    /**
     * 因为是单例，这个唯一的构造器是私有的
     * 主要任务是初始化连接池
     * 从vertx配置中读取u数据库相关配置，然后创建JDBCClient，保存到私有变量
     *
     * @author Leibniz.Hu
     */
    private ConnectionManager() {
        try {
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PSWD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化的方法
     * 传入Vertx对象，用于调用私有构造器，产生单例对象
     */
    static void init() {
        if (INSTANCE != null) {
            throw new RuntimeException("ConnectionManager is already initialized, please do not call init() any more!!!");
        }
        Vertx vertx = Constants.vertx();
        if (vertx == null) {
            throw new RuntimeException("请先初始化Constants类！");
        }
        INSTANCE = new ConnectionManager(); //创建单例实例
        INSTANCE.log.info("数据库连接管理器初始化成功！");
    }

    /**
     * 获取单例对象的方法
     */
    static ConnectionManager getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("ConnectionManager is still not initialized!!!");
        }
        return INSTANCE;
    }

    static void close() {
        try {
            INSTANCE.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
