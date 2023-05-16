package org.DatabaseSystem.querying.query;

import java.io.IOException;

public interface Query {

    void writingQuery(String databaseName, String UserName) throws IOException;
    void createTable(String database, String user_query)throws IOException;
    void insertIntoTable(String database, String query)throws IOException;
    void selectTable(String query, String database) throws IOException;
    void deleteTable(String query, String database) throws IOException ;
    void updateTable(String query, String database) throws IOException;





}
