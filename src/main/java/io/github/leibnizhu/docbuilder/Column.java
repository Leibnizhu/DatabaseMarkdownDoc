package io.github.leibnizhu.docbuilder;

public class Column {
    // 数据库字段名称
    private String field;
    // 服务端model属性名称
    private String param;
    // 数据库字段类型
    private String type;
    // 数据库字段注释
    private String comment;
    //是否必须填(非空)
    private String need;

    public String getNeed() {
        return need;
    }

    public void setNeed(String need) {
        this.need = need;
    }


    public Column(String field, String param, String type, String comment, boolean nullable) {
        this.field = field;
        this.param = param;
        this.type = type;
        this.comment = comment;
        this.need = nullable?"否":"是";
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
