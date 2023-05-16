package org.DatabaseSystem.querying.select;

public class UserQueryInputs {
    String Where_Column_name;
    String value;
    String operator;

    public UserQueryInputs(String Where_Column_name, String value, String operator) {
        this.Where_Column_name = Where_Column_name;
        this.value = value;
        this.operator = operator;
    }

    public String getWhere_Column_name() {
        return Where_Column_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String getOperator() {
        return operator;
    }

}
