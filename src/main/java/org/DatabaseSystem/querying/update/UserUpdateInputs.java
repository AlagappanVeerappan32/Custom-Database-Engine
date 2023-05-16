package org.DatabaseSystem.querying.update;

public class UserUpdateInputs {
    String ColumnName;
    String ColumnValue;
    String WhereColumn_name;
    String operator;
    String WhereColumn_value;

    public UserUpdateInputs(String columnName, String columnValue) {
        ColumnName = columnName;
        ColumnValue = columnValue;
    }

    public UserUpdateInputs(String columnName, String columnValue, String whereColumn_name, String operator, String whereColumn_value) {
        ColumnName = columnName;
        ColumnValue = columnValue;
        WhereColumn_name = whereColumn_name;
        this.operator = operator;
        WhereColumn_value = whereColumn_value;
    }

    public String getColumnName() {
        return ColumnName;
    }

    public String getColumnValue() {
        return ColumnValue;
    }

    public String getOperator() {
        return operator;
    }

    public String getWhereColumn_name() {
        return WhereColumn_name;
    }

    public String getWhereColumn_value() {
        return WhereColumn_value;
    }
}
