package org.DatabaseSystem.querying.update;

import java.io.IOException;
import java.util.List;

public interface UpdateQuery {

     boolean validate_query(String userQuery);
     String getTableName(String userQuery);
     UserUpdateInputs getSetColumnAndValues(String userQuery);
      void updateQueryImp(String user_query, String database) throws IOException;
      void updateWhereConditionChecks(String Table_name, UserUpdateInputs WhereColumnAndValues, String database) throws IOException;
      List<List<String>> setUpdateCondition(List<List<String>> table_value_List, List<String> table_details, String column_name, String column_value, String metaTable_location ) throws IOException;
      void updateRecordsInTable(List<List<String>> FinalUpdatedValuesList, String get_Where_Column_Name, List<String> table_details, String get_Where_Column_Value, String database, String Table_name) throws IOException;

    }
