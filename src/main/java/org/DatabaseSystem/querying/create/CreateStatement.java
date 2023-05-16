package org.DatabaseSystem.querying.create;
import org.DatabaseSystem.Constants;
import org.DatabaseSystem.querying.query.ForeignKeyInfo;
import org.DatabaseSystem.querying.query.TableInformation;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateStatement implements CreateQuery {

    PersistentFileStorage persistentFileStorage =new PersistentFileStorageImplementation();

    /***
     * This method represents whether a primary key or foreign key details are present if so
     * it gets the column name and sets the value to true. so the application can know the value is unique
     * finally it sets the value , so it can be used further in the code.
     *
     * @param tableName  table name that the user mentioned in teh query
     * @param tableInformationList details like column name, primary key , value of the table
     * @param primaryKeyColumns  the primary column if it is present
     * @param foreignTableDetails the foreign key and the table details of the table if it is present
     * @return it sets the primary and foreign key and returns the list of class objects.
     */
    public List<TableInformation> setPrimaryAndForeignKeys(String tableName, List<TableInformation>tableInformationList,
                                                           String[] primaryKeyColumns, List<ForeignKeyInfo>foreignTableDetails)
    {
        for(String primaryKeyValue : primaryKeyColumns)
        {
            for(TableInformation getPrimaryColumn : tableInformationList)
                if(primaryKeyValue.equalsIgnoreCase(getPrimaryColumn.getColumnName()))
                {
                    getPrimaryColumn.setIsPrimaryKey(true);
                }
        }
        for(ForeignKeyInfo foreignKeyValue : foreignTableDetails)
        {
            for(TableInformation getForeignColumn : tableInformationList)
                if(foreignKeyValue.getColumnName().equalsIgnoreCase(getForeignColumn.getColumnName()))
                {
                    getForeignColumn.setIsForeignKey(true);
                    getForeignColumn.setForeignTableKey(foreignKeyValue.getForeignTableKey());
                    getForeignColumn.setForeignTable(foreignKeyValue.getForeignTable());
                }
        }
        return tableInformationList;
    }

    /***
     * This method represents, the query is valid or not, if valid it captures the colum name and values and process it
     * also checks primary and foreign key details. if the values matches the given data types,it is stored in the table
     * The default value for certain data type lengths are given and
     * The query should match the user create table condition on data types and lengths
     * @param database current database name of the user
     * @param userQuery the user query given by the user
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    public void createTableQuery(String database, String userQuery) throws IOException {
        userQuery = userQuery.trim().toLowerCase();
        Pattern pattern = Pattern.compile("CREATE TABLE (\\w+)[\\s\\n\\r]*[(](.*)[)];$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userQuery);
        if (!matcher.find()) {
            System.out.println("Incorrect Query, the query is not valid");
            return;
        }
        String columns ="";
        String getTableTableName = matcher.group(1).trim();
        String columnValueList = matcher.group(2).trim();
        List<TableInformation> tableValueDetails = new ArrayList<>();
        boolean primaryKey = false;
        String[] primaryKeyColumns=new String[0];
        List<ForeignKeyInfo> foreignTableDetails = new ArrayList<>();
        String[] columnInfoList;
        String columnDataType = "";
        int columnLength = -99;
        String[] dataTypeAllowed = {"int", "boolean", "varchar", "float"};
        String[] columnQuerySplit = columnValueList.split(",");
        for (String valueOfColumns : columnQuerySplit) {

            // if it starts with primary key and pk value = true then
            if (valueOfColumns.trim().toUpperCase().startsWith("PRIMARY KEY") && !primaryKey) {
                Pattern primaryKeyPattern = Pattern.compile("PRIMARY KEY[\\s\\n\\r]?\\((.*)\\)$", Pattern.CASE_INSENSITIVE);
                Matcher primaryKeyMatcher = primaryKeyPattern.matcher(valueOfColumns);
                if (!primaryKeyMatcher.find()) {
                    System.out.println("Invalid syntax");
                    return;
                }
                String pkMatchStr = primaryKeyMatcher.group(1).trim();

                primaryKeyColumns = pkMatchStr.split(",");
                primaryKey = true;
            }

            // if it starts with primary key and pk value = false then
            else if (valueOfColumns.trim().toUpperCase().startsWith("PRIMARY KEY") && primaryKey) {
                System.out.println("Incorrect Query. Only one primary key representation allowed!");
                return;
            }
            else if (valueOfColumns.trim().toUpperCase().startsWith("FOREIGN KEY")) {
                Pattern fKeyPattern = Pattern.compile("FOREIGN KEY[\s\n\t]?[(](.*)[)][\s\n\t]+REFERENCES[\s\n\t]+(.*)[(](.*)[)]", Pattern.CASE_INSENSITIVE);
                Matcher fKeyMatcher = fKeyPattern.matcher(valueOfColumns);
                if (!fKeyMatcher.find()) {
                    System.out.println("Invalid syntax near foreign key");
                    return;
                } else {
                    String tableColumnName = fKeyMatcher.group(1).trim();
                    String foreignTable = fKeyMatcher.group(2).trim();
                    String foreignTableKey = fKeyMatcher.group(3).trim();

                    String ForeignKeyPath=Constants.TABLE_LOCATIONS.replace("{db}",database).replace("{table}",foreignTable);
                    if(new File(ForeignKeyPath).exists())
                    {
                        List<String> check_file= persistentFileStorage.readFile(ForeignKeyPath);
                        if(!Arrays.stream(check_file.get(0).split(Constants.DELIMITER)).toList().contains(foreignTableKey)) {
                            System.out.println("Foreign key: " + foreignTableKey + " does not exist in "
                                    + foreignTable + " table!!");
                            return;
                        }
                    }
                    else {
                        System.out.println("Table " + foreignTable + " does not exist!!");
                        return;
                    }
                    foreignTableDetails.add(new ForeignKeyInfo(tableColumnName,foreignTable,foreignTableKey));
                }

            } else {
                columnInfoList = valueOfColumns.trim().split(" ");
                if (columnInfoList.length < 2) {
                    System.out.println("invalid query");
                    return;
                }

                String columnName = columnInfoList[0];
                String columnDatatype = "";
                int column_Length = -99;
                for (String datatype : dataTypeAllowed) {
                    if (!columnInfoList[1].contains(datatype)) {
                        continue;
                    }
                    if (datatype.equals("int") || datatype.equals("float")) {
                        column_Length = -1;
                        columnDatatype = datatype;

                    } else if (datatype.equals("boolean")) {
                        column_Length = -2;
                        columnDatatype = datatype;

                    } else {
                        Matcher lengthMatcher = Pattern.compile("varchar[(](.*)[)]", Pattern.CASE_INSENSITIVE).matcher(columnInfoList[1]);
                        if (lengthMatcher.find()) {
                            column_Length = Integer.parseInt(lengthMatcher.group(1));
                            columnDatatype = datatype;
                        }
                    }
                    break;
                }
                if (columnDatatype.isEmpty()) {
                    return;
                }
                tableValueDetails.add(new TableInformation(columnName, columnDatatype, column_Length, false, false, "none", "none"));
            }
        }

        for (TableInformation table_list:tableValueDetails)
        {
            columns = columns + table_list.getColumnName()+ Constants.DELIMITER;
        }
        String tableFilePath =Constants.TABLE_LOCATIONS.replace("{db}",database).replace("{table}",getTableTableName);
        if(new File(tableFilePath).exists())
        {
            System.out.println("Table already exist");
            return;
        }else {
            persistentFileStorage.createFile(tableFilePath);
            persistentFileStorage.writeFile(columns,tableFilePath);
        }
        tableValueDetails = setPrimaryAndForeignKeys(getTableTableName,tableValueDetails,primaryKeyColumns,foreignTableDetails);
        if(tableValueDetails!=null)
        {
            metaData(tableValueDetails,getTableTableName,database);
        }
        System.out.println(getTableTableName+" successfully created");
        System.out.println("create query executed successfully");

    }


    /***
     * This method is used to create a meta file for every table that a user creates and stores meta information of the table
     * it also checks if a meta file already exist or not as well.
     * @param table_details details like column name, primary key , value of the table
     * @param table_name table name mentioned by the user in the query
     * @param database current database name of the user
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void metaData(List<TableInformation> table_details, String table_name, String database) throws IOException {
        String metaTableFilePath =Constants.META_TABLE_LOCATIONS.replace("{db}",database).replace("{table}",table_name);
        persistentFileStorage.createFile(metaTableFilePath);
        for (TableInformation column:table_details) {
            String columnValueFromMeta = column.getColumnName()+Constants.DELIMITER+column.getColumnType()+Constants.DELIMITER+
                    column.getColumnLength()+Constants.DELIMITER+column.getIsPrimaryKey()+Constants.DELIMITER+column.getForeignTable()+Constants.DELIMITER
                    +column.getForeignTableKey();
            persistentFileStorage.writeFile(columnValueFromMeta,metaTableFilePath);
        }
    }
}


