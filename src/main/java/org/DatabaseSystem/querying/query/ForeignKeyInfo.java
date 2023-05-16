package org.DatabaseSystem.querying.query;

public class ForeignKeyInfo {
    String columnName;
    String foreignTable;
    String foreignTableKey;

    public ForeignKeyInfo(String columnName, String foreignTable, String foreignTableKey) {
        this.columnName = columnName;
        this.foreignTable = foreignTable;
        this.foreignTableKey = foreignTableKey;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setForeignTable(String foreignTable) {
        this.foreignTable = foreignTable;
    }

    public void setForeignTableKey(String foreignTableKey) {
        this.foreignTableKey = foreignTableKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getForeignTable() {
        return foreignTable;
    }

    public String getForeignTableKey() {
        return foreignTableKey;
    }
}
