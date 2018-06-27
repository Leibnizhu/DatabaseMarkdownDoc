package io.github.leibnizhu.docbuilder;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:56.
 */
class Constants {
    static String JDBC_URL;
    static String JDBC_USER;
    static String JDBC_PSWD;
    static String JDBC_DRIVER;

    private static Vertx vertx;

    static Vertx vertx() {
        return vertx;
    }

    static void init(Context vertxContext) {
        Constants.vertx = vertxContext.owner();
        JsonObject config = vertxContext.config();
        JDBC_URL = config.getString("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
        JDBC_USER = config.getString("jdbcUser", "root");
        JDBC_PSWD = config.getString("jdbcPassword", "root");
        JDBC_DRIVER = config.getString("jdbcDriver", "com.mysql.cj.jdbc.Driver");
    }
}
