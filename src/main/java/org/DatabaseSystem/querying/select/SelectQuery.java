package org.DatabaseSystem.querying.select;

import java.io.IOException;
import java.util.List;

public interface SelectQuery {
     boolean validateQuery(String userQuery);
     List<String> checkColumns(String query);
     void extractSelectColumns(String user_query, String database) throws IOException;
     List<List<String>> checkDataWithTables(String tableName, UserQueryInputs userQueryInputs, List<String> allColumns, String database) throws IOException;


    }
