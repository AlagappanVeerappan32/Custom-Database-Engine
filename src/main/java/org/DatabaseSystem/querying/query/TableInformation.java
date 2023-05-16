package org.DatabaseSystem.querying.query;

public class TableInformation {
    String columnName;
    String columnType;
    int columnLength;
    Boolean isPrimaryKey;
    Boolean isForeignKey;
    String foreignTable;
    String foreignTableKey;


    public TableInformation(String columnName, String columnType, int columnLength, Boolean isPrimaryKey, Boolean isForeignKey, String foreignTable, String foreignTableKey) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnLength = columnLength;
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
        this.foreignTable = foreignTable;
        this.foreignTableKey = foreignTableKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public int getColumnLength() {
        return columnLength;
    }

    public Boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public String getForeignTable() {
        return foreignTable;
    }

    public String getForeignTableKey() {
        return foreignTableKey;
    }

    public void setIsPrimaryKey(Boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public void setIsForeignKey(Boolean isForeignKey) {
        this.isForeignKey = isForeignKey;
    }

    public void setForeignTable(String foreignTable) {
        this.foreignTable = foreignTable;
    }

    public void setForeignTableKey(String foreignTableKey) {
        this.foreignTableKey = foreignTableKey;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
