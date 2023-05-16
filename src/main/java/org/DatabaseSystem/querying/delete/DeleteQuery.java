package org.DatabaseSystem.querying.delete;

import org.DatabaseSystem.querying.select.UserQueryInputs;

import java.io.IOException;

public interface DeleteQuery {
    public boolean validateQuery(String userQuery);
    public void extractDeleteColumns(String userQuery, String database) throws IOException;
    public void checkDataWithTables(String tableName, UserQueryInputs userQueryInputs, String database) throws IOException;


    }
