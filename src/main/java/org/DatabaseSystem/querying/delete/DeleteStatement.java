package org.DatabaseSystem.querying.delete;
import org.DatabaseSystem.Constants;
import org.DatabaseSystem.querying.SelectAndDeleteWhereImplementation;
import org.DatabaseSystem.querying.SelectAndDeleteOperations;
import org.DatabaseSystem.querying.select.UserQueryInputs;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteStatement implements DeleteQuery{
    static PersistentFileStorage persistentFileStorage =new PersistentFileStorageImplementation();

    SelectAndDeleteOperations selectAndDeleteOperations=new SelectAndDeleteWhereImplementation();

    /***
     * This method sole purpose is to analyze the query using regex operation
     * @param userQuery the user query given by the user
     * @return true or false based on analyzing query
     */
    @Override
    public boolean validateQuery(String userQuery) {
        if(userQuery.trim().toLowerCase().contains("where"))
        {
            String regex = "DELETE FROM (.*) WHERE (.*);";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userQuery);
            if (matcher.find()) {
                return true;
            } else {
                return false;
            }
        }else {
            String regex = "DELETE FROM (.*);";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userQuery);
            if (matcher.find()) {
                return true;
            } else {
                return false;
            }
        }
    }


    /***
     * This method is used to call the functions , it validates if query is valid if so, performs delete operations
     * @param userQuery the user query given by the user
     * @param database current database name that is in use
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void extractDeleteColumns(String userQuery, String database) throws IOException {

        if (!validateQuery(userQuery)) {
            System.out.println("invalid query format");
            return;
        }
        String Table_name = selectAndDeleteOperations.extractTableName(userQuery);
        UserQueryInputs WhereColumnAndValues =selectAndDeleteOperations.operatorConditions(userQuery);
        checkDataWithTables(Table_name,WhereColumnAndValues,database);

    }

    /***
     * This method is used to delete the data in the files based on where condition, if not all the content in the tables
     * It checks whether the table exist or not, if so checks is there where condition, based on that it gets the values to be
     * deleted using the selectAndDeleteOperations method and deletes the data from the table
     * @param tableName the table name
     * @param userQueryInputs the query given by the user
     * @param database current database name that is in use
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void checkDataWithTables(String tableName, UserQueryInputs userQueryInputs,String database) throws IOException {
        String tableLocation = Constants.TABLE_LOCATIONS.replace("{db}", database).replace("{table}", tableName);
        String metaTableLocation = Constants.META_TABLE_LOCATIONS.replace("{db}", database).replace("{table}", tableName);

        List<String> tableColumnValue;

        if (!new File(tableLocation).exists()) {
            System.out.println("Table doesn't exist");
            return;
        }
        List<String> tableDetails = persistentFileStorage.readFile(tableLocation);
        tableColumnValue = Arrays.stream(tableDetails.get(0).split(Constants.DELIMITER)).toList();
        List<List<String>> AddDeleteValues = new ArrayList<>();



        if (userQueryInputs != null) {
            selectAndDeleteOperations.checkDataTypes(userQueryInputs, tableColumnValue, metaTableLocation, tableLocation, AddDeleteValues);

            List<String> tableData = new ArrayList<>();
            for(List<String> addDeleteValue: AddDeleteValues) {
                tableData.add(String.join(Constants.DELIMITER, addDeleteValue));
            }

            List<String> finalStringList = new ArrayList<>();

            for(String data: tableDetails) {
                if(tableData.contains(data)) {
                    continue;
                }
                else {
                    finalStringList.add(data);
                }
            }
            persistentFileStorage.createFile(tableLocation);
            for(String data: finalStringList) {
                persistentFileStorage.writeFile(data, tableLocation);
            }
            System.out.println("The table rows has been deleted");
            System.out.println("delete query executed successfully");
        }
        else
        {
            persistentFileStorage.createFile(tableLocation);
            String column = tableDetails.get(0);
            persistentFileStorage.writeFile(column,tableLocation);
            System.out.println("The table has been deleted");
            System.out.println("delete query executed successfully");

        }
    }

}
