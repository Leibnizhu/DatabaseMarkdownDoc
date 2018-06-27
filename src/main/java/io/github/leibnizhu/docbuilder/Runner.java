package io.github.leibnizhu.docbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 读取mysql数据库下表的结构信息
 */
class Runner {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String database;

    Runner(String database) {
        this.database = database;
    }

    String buildDocument() {
        try {
            // 获取数据库下的所有表名称
            List<Table> tables = getAllTableName();
            // 获得表的建表语句
            buildTableComment(tables);
            // 获得表中所有字段信息
            buildColumns(tables);
            // 写文件
            return buildString(tables);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 写文件
     */
    private String buildString(List<Table> tables) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("xxx-概要设计文档\n\n" +
                "| 版本号 | 作者 | 日期 | 备注 |\n" +
                "|-----|-----|---|-----|\n" +
                "| V0.1 | xxx | \n\n")
                .append(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                .append(" | 初稿 |\n---\n\n")
        .append("# 1. 简介  \n\n# 2. 模块设计说明  \n\n# 3. MySQL数据库表结构  \n");
        int i = 1;
        for (Table table : tables) {
            log.debug("正在生成{}表的文档...", table.getTableName());
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

    /**
     * 获取当前数据库下的所有表名称
     */
    private List<Table> getAllTableName() throws SQLException {
        List<Table> tables = new ArrayList<>();
        Connection conn = ConnectionManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM information_schema.TABLES WHERE table_schema = '" + this.database + "'");
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
    private void buildTableComment(List<Table> tables) throws SQLException {
        Connection conn = ConnectionManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        for (Table table : tables) {
            ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + this.database + "." + table.getTableName());
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
    private void buildColumns(List<Table> tables) throws SQLException {
        Connection conn = ConnectionManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        for (Table table : tables) {
            List<Column> columns = new ArrayList<>();
            ResultSet rs = stmt.executeQuery("show full columns from " + this.database + "." + table.getTableName());
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
