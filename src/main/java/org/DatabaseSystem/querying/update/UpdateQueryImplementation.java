package org.DatabaseSystem.querying.update;

import org.DatabaseSystem.Constants;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateQueryImplementation implements UpdateQuery {

    static PersistentFileStorage persistentFileStorage = new PersistentFileStorageImplementation();

    /***
     * This method sole purpose is to analyze the query using regex operation
     * @param userQuery the user query given by the user
     * @return true or false based on analyzing query
     */
    @Override
    public boolean validate_query(String userQuery) {
        if (userQuery.trim().toLowerCase().contains("where")) {
            String regex = "UPDATE (.*) SET (.*) WHERE (.*);";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userQuery);
            if (matcher.find()) {
                return true;
            } else {
                return false;
            }
        } else {
            String regex = "UPDATE (.*) SET (.*);";
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
     * This method is used to get the tableName from the query
     * @param userQuery the user query given by the user
     * @return tableName from the query
     */
    @Override
    public String getTableName(String userQuery) {
        String tableName;
        String regex = "UPDATE (.*) SET ";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userQuery);
        if (matcher.find()) {
            tableName = matcher.group(1).trim();
            return tableName;
        }
        return "Incorrect table name";
    }

    /***
     * This method is used to collect all column and values form the user input query and stores them in UserUpdateInputs
     * class object so that it can be used further to check with table
     * @param userQuery the user query given by the user
     * @return column and values from the query
     */
    @Override
    public UserUpdateInputs getSetColumnAndValues(String userQuery) {
        UserUpdateInputs userUpdateInputs = null;
        String[] availableOperators = {"="};
        for (String conditionalOperator : availableOperators) {
            if (userQuery.trim().toLowerCase().contains("where")) {
                String regex = "SET\\s+(\\w+)\\s*" + conditionalOperator + "\\s*(\\w+)\\s* WHERE (\\w+)[\\s\\n\\t]*" + conditionalOperator + "[\\s\\n\\t]*(.*);";
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(userQuery);
                if (matcher.find()) {
                    String columnName = matcher.group(1).trim();
                    String columnValue = matcher.group(2).trim();
                    String WhereColumnName = matcher.group(3).trim();
                    String WhereColumnValue = matcher.group(4).trim();
                    if (WhereColumnValue.isEmpty()) {
                        System.out.println("value in where condition is not valid");
                        return null;
                    }
                    userUpdateInputs = new UserUpdateInputs(columnName, columnValue, WhereColumnName, conditionalOperator, WhereColumnValue);
                    break;
                }
            } else {
                String regex = "SET\\s+(\\w+)\\s*" + conditionalOperator + "\\s*(\\w+)\\s*;";
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(userQuery);
                if (matcher.find()) {
                    String columnName = matcher.group(1).trim();
                    String columnValue = matcher.group(2).trim();
                    userUpdateInputs = new UserUpdateInputs(columnName, columnValue);
                    break;
                }
            }
            System.out.println("The operator is not valid, must be query error check and try again");
        }
        return userUpdateInputs;
    }


    /***
     * This method is used to call the functions , it validates if query is valid if so, performs update operations
     * @param userQuery the user query given by the user
     * @param database the database name
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void updateQueryImp(String userQuery, String database) throws IOException {
        if (!validate_query(userQuery)) {
            System.out.println("invalid query format");
            return;
        }

        String tableName = getTableName(userQuery);
        UserUpdateInputs WhereColumnAndValues = getSetColumnAndValues(userQuery);
        updateWhereConditionChecks(tableName, WhereColumnAndValues, database);

    }

    /***
     * This method checks the table exist , if so checks is there any where condition values, if so
     * it checks the value is in correct data type by searching from meta table file, and calls the inside function to update values
     * if there is no where condition then checks what is the set condition and proceeds further.
     * @param Table_name table name from the user query
     * @param WhereColumnAndValues column name and values from set and where in the query
     * @param database database name
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void updateWhereConditionChecks(String Table_name, UserUpdateInputs WhereColumnAndValues, String database) throws IOException {

        String tableLocation = Constants.TABLE_LOCATIONS.replace("{db}", database).replace("{table}", Table_name);
        String metaTable_location = Constants.META_TABLE_LOCATIONS.replace("{db}", database).replace("{table}", Table_name);

        List<List<String>> addTableValueListToUpdate = new ArrayList<>();
        List<String> checkingTableDetails = persistentFileStorage.readFile(tableLocation);
        List<String> checkingMetaTable = persistentFileStorage.readFile(metaTable_location);
        List<String> tableColumnFirstIndexValue;
        String valueToCheck;
        List<String> valueList = new ArrayList<>();

        String columnName = WhereColumnAndValues.getColumnName();
        String columnValue = WhereColumnAndValues.getColumnValue();
        String whereColumnName = WhereColumnAndValues.getWhereColumn_name();
        String whereColumnValue = WhereColumnAndValues.getWhereColumn_value();
        String whereOperator = WhereColumnAndValues.getOperator();

        if (!new File(tableLocation).exists()) {
            System.out.println("Table doesn't exist");
            return;
        }

        if (whereColumnName != null) {
            tableColumnFirstIndexValue = Arrays.stream(checkingTableDetails.get(0).split(Constants.DELIMITER)).toList();

            if (!tableColumnFirstIndexValue.contains(whereColumnName)) {
                System.out.println("The column is not found");
                return;
            }

            List<String> getValue = null;
            for (String isValuePresent : checkingMetaTable) {
                checkingMetaTable = List.of(isValuePresent.split(Constants.DELIMITER));
                if (checkingMetaTable.contains(whereColumnName)) {
                    getValue = checkingMetaTable;
                }
            }
            String dataType = getValue.get(1);
            int valueIndex = 0;
            for (String tableValue : checkingTableDetails) {
                List<String> filteredTableDetailsColumnCheck = List.of(tableValue.trim().split(Constants.DELIMITER));


                for (int i = 0; i < filteredTableDetailsColumnCheck.size(); i++) {
                    if (filteredTableDetailsColumnCheck.get(i).equals(whereColumnName)) {
                        valueIndex = i;
                    }
                }
                valueToCheck = filteredTableDetailsColumnCheck.get(valueIndex);
                valueList.add(filteredTableDetailsColumnCheck.get(valueIndex));

                if (dataType.equals("int")) {
                    try {
                        Integer.parseInt(whereColumnValue);
                        if (whereOperator.equals("=")) {
                            if (valueToCheck.equals(whereColumnValue)) {
                                addTableValueListToUpdate.add(filteredTableDetailsColumnCheck);
                            }
                        } else {
                            System.out.println("Incorrect value");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect Value");
                        return;
                    }
                } else if (dataType.equals("float")) {
                    try {
                        Float.parseFloat(whereColumnValue);
                        if (whereOperator.equals("=")) {
                            if (valueToCheck.equals(whereColumnValue)) {
                                addTableValueListToUpdate.add(filteredTableDetailsColumnCheck);
                            }
                        } else {
                            System.out.println("Incorrect value");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect Value");
                        return;
                    }
                } else if (dataType.equals("boolean")) {
                    if (!whereColumnValue.equalsIgnoreCase("false") && !whereColumnValue.equalsIgnoreCase("true")) {
                        System.out.println("Incorrect value for column " + valueToCheck);
                        return;
                    }
                    boolean condition_value = Boolean.parseBoolean(whereColumnValue);
                    if (!condition_value) {
                        if (whereOperator.equals("=")) {
                            if (!Boolean.parseBoolean(valueToCheck)) {
                                addTableValueListToUpdate.add(filteredTableDetailsColumnCheck);
                            }
                        } else {
                            System.out.println("Incorrect value");
                            return;
                        }
                    }
                } else if (dataType.equals("varchar")) {
                    if (whereOperator.equals("=")) {
                        if (valueToCheck.equalsIgnoreCase(whereColumnValue)) {
                            addTableValueListToUpdate.add(filteredTableDetailsColumnCheck);
                        }
                    } else {
                        System.out.println("Incorrect value");
                        return;
                    }
                }
            }
            List<List<String>> FinalUpdatedValuesList = setUpdateCondition(addTableValueListToUpdate, checkingTableDetails, columnName, columnValue, metaTable_location);
            updateRecordsInTable(FinalUpdatedValuesList, whereColumnName, checkingTableDetails, whereColumnValue, database, Table_name);

        } else {
            List<String> columnListToChange;
            for (String allColumnChangedValues : checkingTableDetails) {
                columnListToChange = List.of(allColumnChangedValues.split(Constants.DELIMITER));
                addTableValueListToUpdate.add(columnListToChange);
            }
            addTableValueListToUpdate.remove(0);
            List<List<String>> FinalUpdatedValuesList = setUpdateCondition(addTableValueListToUpdate, checkingTableDetails, columnName, columnValue, metaTable_location);
            updateRecordsInTable(FinalUpdatedValuesList, null, checkingTableDetails, whereColumnValue, database, Table_name);

        }
    }

    /***
     * This method performs update of the values in a list by checking if the set update column exist in the table
     * checks if the set value is in right data type, if so it updates the values
     * @param addTableValueListToUpdate This is a list which contains the value to be updated
     * @param tableColumnDetails column of the table
     * @param columnName column name
     * @param columnValue column value
     * @param metaTableLocation table location
     * @return the value to be changed in the table
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public List<List<String>> setUpdateCondition(List<List<String>> addTableValueListToUpdate, List<String> tableColumnDetails, String columnName, String columnValue, String metaTableLocation) throws IOException {

        List<String> checkMetaTable = persistentFileStorage.readFile(metaTableLocation);

        List<String> tableColumnValue = Arrays.stream(tableColumnDetails.get(0).split(Constants.DELIMITER)).toList();

        if (!tableColumnValue.contains(columnName)) {
            System.out.println("The column is not found");
            return null;
        }
        List<String> getValue = null;
        for (String value : checkMetaTable) {
            checkMetaTable = List.of(value.split(Constants.DELIMITER));
            if (checkMetaTable.contains(columnName)) {
                getValue = checkMetaTable;
            }
        }
        String dataType = getValue.get(1);

        if (dataType.equals("int")) {
            try {
                Integer.parseInt(columnValue);
            } catch (NumberFormatException e) {
                System.out.println("Datatype of value is wrong");
                return null;
            }
        } else if (dataType.equals("float")) {
            try {
                Float.parseFloat(columnValue);
            } catch (NumberFormatException e) {
                System.out.println("Datatype of value is wrong");
                return null;
            }
        } else if (dataType.equals("boolean")) {
            if (!columnValue.equalsIgnoreCase("false") && !columnValue.equalsIgnoreCase("true")) {
                System.out.println("Incorrect value for column " + columnValue);
                return null;
            }
        } else {
            columnValue.toString();
        }
        int columIndex = 0;

        for (int i = 0; i < tableColumnValue.size(); i++) {
            if (tableColumnValue.get(i).equals(columnName)) {
                columIndex = i;
            }
        }
        List<List<String>> FinalUpdatedList = new ArrayList<>();
        for (List<String> innerList : addTableValueListToUpdate) {
            List<String> modifiedInnerList = new ArrayList<>(innerList);
            modifiedInnerList.set(columIndex, columnValue);
            FinalUpdatedList.add(modifiedInnerList);
        }

        return FinalUpdatedList;
    }

    /***
     * This method is used to update records in the table
     * it checks if there is any where condition if so, with condition appropriate elements it updates
     * else it updates all the records in the field.
     * @param FinalUpdatedValuesList contains the updated records that needs to be updated in table
     * @param getWhereColumnName where condition column names
     * @param tableDetails table location
     * @param getWhereColumnValue  where condition values
     * @param database database of the user
     * @param tableName tableName that is being updated
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void updateRecordsInTable(List<List<String>> FinalUpdatedValuesList, String getWhereColumnName, List<String> tableDetails, String getWhereColumnValue, String database, String tableName) throws IOException {
        String tableLocation = Constants.TABLE_LOCATIONS.replace("{db}", database).replace("{table}", tableName);
//        String metaTable_location = Constants.META_TABLE_LOCATIONS.replace("{db}", database).replace("{table}", tableName);
        List<String> finalStringList;
        List<List<String>> TemporaryListToHoldsValue = new ArrayList<>();

        if (getWhereColumnName != null) {
            List<String> tableData = new ArrayList<>();
            for (List<String> addUpdateValue : FinalUpdatedValuesList) {
                tableData.add(String.join(Constants.DELIMITER, addUpdateValue));
            }

            for (String valueInTable : tableDetails) {
                finalStringList = List.of(valueInTable.split(Constants.DELIMITER));
                if (!finalStringList.contains(getWhereColumnValue)) {
                    TemporaryListToHoldsValue.add(finalStringList);
                }
            }

            for (List<String> breakList : FinalUpdatedValuesList) {
                TemporaryListToHoldsValue.add(breakList);
            }

            List<String> updateValue = new ArrayList<>();
            for (List<String> value : TemporaryListToHoldsValue) {
                updateValue.add(String.join(Constants.DELIMITER, value));
            }

            persistentFileStorage.createFile(tableLocation);
            for (String data : updateValue) {
                persistentFileStorage.writeFile(data, tableLocation);
            }
            System.out.println("The table rows has been updated");
            System.out.println("update query executed successfully");

        }
        else {
            List<String> tableColumnValue = Arrays.stream(tableDetails.get(0).split(Constants.DELIMITER)).toList();

            FinalUpdatedValuesList.add(0, tableColumnValue);

            List<String> updateValueWithoutWhere = new ArrayList<>();
            for (List<String> value : FinalUpdatedValuesList) {
                updateValueWithoutWhere.add(String.join(Constants.DELIMITER, value));
            }

            persistentFileStorage.createFile(tableLocation);
            for (String data : updateValueWithoutWhere) {
                persistentFileStorage.writeFile(data, tableLocation);
            }
            System.out.println("The table rows has been updated");
            System.out.println("update query executed successfully");


        }
    }
}
