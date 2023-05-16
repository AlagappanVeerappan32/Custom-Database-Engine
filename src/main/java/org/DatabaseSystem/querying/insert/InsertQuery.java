package org.DatabaseSystem.querying.insert;

import java.io.IOException;

public interface InsertQuery {
    void insertTableQuery(String database, String userQuery) throws IOException;
    boolean validate_query(String userQuery);

    boolean checkInsertConditionQuery(String database, String tableName, String columns, String values) throws IOException;

}
