package org.DatabaseSystem.querying.select;
import org.DatabaseSystem.Constants;
import org.DatabaseSystem.querying.SelectAndDeleteWhereImplementation;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;
import org.DatabaseSystem.querying.SelectAndDeleteOperations;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectQueryImplementation implements SelectQuery {

    static PersistentFileStorage persistentFileStorage = new PersistentFileStorageImplementation();
    SelectAndDeleteOperations selectAndDeleteOperations=new SelectAndDeleteWhereImplementation();


    /***
     * This method sole purpose is to analyze the query using regex operation
     * @param userQuery the user query given by the user
     * @return true or false based on analyzing query
     */
    @Override
    public boolean validateQuery(String userQuery) {
        String regex = "select[\\s\\r\\n]+(.*)[\\s\\r\\n]+from[\\s\\r\\n]*(\\w+)"
                + "([\\s\\r\\n]+where[\\s\\r\\n]+(\\w+)[\\s\\r\\n]*(=|<=|>=|<>|<|>)[\\s\\r\\n]*"
                + "([']([\\w]+)[']|([\\w]+)))?;";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userQuery);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * This method is used to collect the number of column in the query
     * @param query the user query given by the user
     * @return column values from the user query
     */
    @Override
    public List<String> checkColumns(String query) {
        String regex = "SELECT(.*)FROM";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher_val = pattern.matcher(query);
        if (matcher_val.find()) {
            String extractedColumns = matcher_val.group(1).trim();
            List<String> columnList;
            if (Pattern.matches(".*[*].*", extractedColumns)) {
                columnList = Arrays.asList("ALL");
                return columnList;
            } else {
                List<String> rawColumns = List.of(extractedColumns.trim().split(","));
                for (String columnValue : rawColumns) {
                    columnValue.trim();
                    if (!columnValue.equals("")) {
                        columnList = Arrays.asList(columnValue);
                    }
                }
                columnList = rawColumns;
                return columnList;
            }
        }
        return null;
    }

    /***
     * This method is used to call the functions , it validates if query is valid if so, performs select operations
     * @param userQuery the user query given by the user
     * @param database the database name
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void extractSelectColumns(String userQuery, String database) throws IOException {
        if (!validateQuery(userQuery)) {
            System.out.println("invalid query format1");
            return;
        }
        String tableName = selectAndDeleteOperations.extractTableName(userQuery);

        List<String> columnValueList = checkColumns(userQuery);

        UserQueryInputs WhereColumnAndValues = selectAndDeleteOperations.operatorConditions(userQuery);

        checkDataWithTables(tableName, WhereColumnAndValues, columnValueList, database);

    }

    /***
     *
     * @param tableName table name
     * @param userQueryInputs user values from where conditions
     * @param allColumns columns that are required by the user (*) or specific columns
     * @param database current database
     * @return the value to display to the user
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public List<List<String>> checkDataWithTables(String tableName, UserQueryInputs userQueryInputs, List<String> allColumns, String database) throws IOException {
        String tableLocation = Constants.TABLE_LOCATIONS.replace("{db}", database).replace("{table}", tableName);
        String metaTable_location = Constants.META_TABLE_LOCATIONS.replace("{db}", database).replace("{table}", tableName);
//        List<String> filtered_table_details_col_check = null;
        List<String> tableColumnValue = null;
        List<List<String>> ValueToDisplay = new ArrayList<>();

        if (!new File(tableLocation).exists()) {
            System.out.println("Table doesn't exist");
            return null;
        }
        List<String> tableDetails = persistentFileStorage.readFile(tableLocation);
        tableColumnValue = Arrays.stream(tableDetails.get(0).split(Constants.DELIMITER)).toList();

        if (userQueryInputs != null)
        {
            selectAndDeleteOperations.checkDataTypes(userQueryInputs, tableColumnValue, metaTable_location, tableLocation,ValueToDisplay);
        }
        else {
            for (int i = 1; i < tableDetails.size(); i++) {
                ValueToDisplay.add(Arrays.stream(tableDetails.get(i).split(Constants.DELIMITER)).toList());
            }
        }
        if (!allColumns.contains("ALL") && tableColumnValue != null) {

            List<List<String>> finalListOfValues = new ArrayList<>();
            HashMap<String, Integer> columnIndexInTable = new HashMap<>();
            int k = 0;
            for (String columnName : tableColumnValue) {
                columnIndexInTable.put(columnName, k);
                k = k + 1;
            }
            ValueToDisplay.add(0, tableColumnValue);
            List<String> finalColumns = new ArrayList<>();

            for (String columnName : tableColumnValue) {
                if (allColumns.contains(columnName)) {
                    finalColumns.add(columnName);
                }
            }
            finalListOfValues.add(finalColumns);
            for (int i = 1; i < ValueToDisplay.size(); i++) {
                List<String> rowValues = new ArrayList<>();
                for (String column : finalListOfValues.get(0)) {
                    int index = columnIndexInTable.get(column);
                    rowValues.add(ValueToDisplay.get(i).get(index));
                }
                finalListOfValues.add(rowValues);
            }
            System.out.println(finalListOfValues);
            System.out.println("select query executed successfully");
            return finalListOfValues;
        } else {
            System.out.println(ValueToDisplay);
            System.out.println("select query executed successfully");
            return ValueToDisplay;
        }
    }
}

