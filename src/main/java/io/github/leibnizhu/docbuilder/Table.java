package io.github.leibnizhu.docbuilder;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Table {// 数据库表名
    private String tableName;
    // 服务端model名
    private String objectName;
    // 数据库表的备注
    private String comment;
    // 数据库表的建表语句
    private String ddl;
    // 表包含的字段
    private List<Column> columns;
    //主键
    private Set<String> keys = new HashSet<>();

    public Table(String tableName, String objectName) {
        this.tableName = tableName;
        this.objectName = objectName;
    }

    public Set<String> getKeys() {
        return keys;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public String getKeysStr() {
        return String.join(",", this.keys.toArray(new String[0]));
    }
}
