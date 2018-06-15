package io.github.leibnizhu.docbuilder;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 读取mysql数据库下表的结构信息
 */
public class Runner {
    private String database;

    public Runner(String database) {
        this.database = database;
    }

    public String buildDocument() {
        try {
            // 获取数据库下的所有表名称
            List<Table> tables = getAllTableName();
            // 获得表的建表语句
            buildTableComment(tables);
            // 获得表中所有字段信息
            buildColumns(tables);
            conn.close();
            // 写文件
            return buildString(tables);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 写文件
     */
    private String buildString(List<Table> tables) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("***-概要设计文档\n\n" +
                "| 版本号 | 作者 | 日期 | 备注 |\n" +
                "|-----|-----|---|-----|\n" +
                "| V0.1 | *** | \n\n")
                .append(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                .append(" | 初稿 |\n---\n\n")
        .append("# 1. 简介  \n\n# 2. 模块设计说明  \n\n# 3. MySQL数据库表结构  \n");
        int i = 1;
        for (Table table : tables) {
            System.out.println(table.getTableName());
            buffer.append("## 3.").append(i++).append(" ")
                    .append(table.getComment()==null || table.getComment().isEmpty()? table.getTableName():table.getComment()).append("  \n");
            buffer.append("**表名** : ").append(table.getTableName()).append("  \n");
            buffer.append("**主键** : ").append(table.getKeysStr()).append("  \n");
            buffer.append("**Java类名** : ").append(table.getObjectName()).append("  \n");
            buffer.append("**表结构** : ").append(table.getComment()).append("  \n\n");
            buffer.append("|列名|类型|必须|说明|\n");
            buffer.append("|:-------|:-------|:-------|:-------|\n");
            List<Column> columns = table.getColumns();
            for (Column column : columns) {
                String param = column.getParam();
                if ("del".equals(param) || "delDtm".equals(param)) continue;
                String type = column.getType();
                String need = column.getNeed();
                String comment = column.getComment();
                buffer.append("|").append(param).append("|").append(type).append("|").append(need).append("|").append("".equals(comment) ? "无" : comment).append("|\n");
            }
            buffer.append("\n**建表语句** :\n```sql\n").append(table.getDdl()).append("\n```\n\n");
        }
        return buffer.toString();
    }

    private Connection conn;
    /**
     * 连接数据库
     */
    //FIXME 地址\用户名\密码改到配置文件中
    //FIXME 统一数据库连接管理(和MainVerticle类统一)
    private Connection getMySQLConnection() throws ClassNotFoundException, SQLException {
        if(conn == null || conn.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + this.database+"?useUnicode=true&characterEncoding=UTF-8&useSSL=false", "root", "root");
        }
        return conn;
    }

    /**
     * 获取当前数据库下的所有表名称
     */
    private List<Table> getAllTableName() throws SQLException, ClassNotFoundException {
        List<Table> tables = new ArrayList<>();
        Connection conn = getMySQLConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SHOW TABLES");
        while (rs.next()) {
            String tableName = rs.getString(1);
            String objectName = camelCase(tableName);
            Table table = new Table(tableName, objectName);
            tables.add(table);
        }
        rs.close();
        stmt.close();
        return tables;
    }

    /**
     * 获得某表的建表语句
     */
    private void buildTableComment(List<Table> tables) throws SQLException, ClassNotFoundException {
        Connection conn = getMySQLConnection();
        Statement stmt = conn.createStatement();
        for (Table table : tables) {
            ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + table.getTableName());
            if (rs != null && rs.next()) {
                String createDDL = rs.getString(2);
                String comment = parse(createDDL);
                table.setDdl(createDDL);
                table.setComment(comment);
            }
            if (rs != null) rs.close();
        }
        stmt.close();
    }

    /**
     * 获得某表中所有字段信息
     */
    private void buildColumns(List<Table> tables) throws SQLException, ClassNotFoundException {
        Connection conn = getMySQLConnection();
        Statement stmt = conn.createStatement();
        for (Table table : tables) {
            List<Column> columns = new ArrayList<>();
            ResultSet rs = stmt.executeQuery("show full columns from " + table.getTableName());
            if (rs != null) {
                while (rs.next()) {
                    String field = rs.getString("Field");
                    String type = rs.getString("Type");
                    String comment = rs.getString("Comment");
                    boolean canBeBull = rs.getString("Null").equals("YES");
                    Column column = new Column(field, camelCase(field), type, comment, canBeBull);
                    if(rs.getString("Key").equals("PRI")) //主键判定
                        table.getKeys().add(field);
                    columns.add(column);
                }
            }
            if (rs != null) {
                rs.close();
            }
            table.setColumns(columns);
        }
        stmt.close();
    }

    /**
     * 返回注释信息
     */
    private static String parse(String all) {
        String comment;
        int index = all.indexOf("COMMENT='");
        if (index < 0) {
            return "";
        }
        comment = all.substring(index + 9);
        comment = comment.substring(0, comment.length() - 1);
        return comment;
    }

    /**
     * 例如：employ_user_id变成employUserId
     */
    private static String camelCase(String str) {
        String[] str1 = str.split("_");
        int size = str1.length;
        String str2;
        StringBuilder str4 = null;
        String str3;
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                str2 = str1[i];
                str4 = new StringBuilder(str2);
            } else {
                str3 = initCap(str1[i]);
                str4.append(str3);
            }
        }
        return str4 != null ? str4.toString() : null;
    }

    /**
     * 把输入字符串的首字母改成大写
     */
    private static String initCap(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

}
