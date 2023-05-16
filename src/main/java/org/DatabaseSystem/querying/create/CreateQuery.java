package org.DatabaseSystem.querying.create;

import org.DatabaseSystem.querying.query.ForeignKeyInfo;
import org.DatabaseSystem.querying.query.TableInformation;

import java.io.IOException;
import java.util.List;

public interface CreateQuery {

    List<TableInformation> setPrimaryAndForeignKeys(String table_name, List<TableInformation>table_details, String[] primary_key_columns, List<ForeignKeyInfo>foreign_table_details);
    void createTableQuery(String database, String user_query) throws IOException;
    void metaData(List<TableInformation> table_details, String table_name, String database) throws IOException;
}
