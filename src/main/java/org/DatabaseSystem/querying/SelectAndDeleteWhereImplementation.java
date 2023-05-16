package org.DatabaseSystem.querying;
import org.DatabaseSystem.Constants;
import org.DatabaseSystem.querying.select.UserQueryInputs;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectAndDeleteWhereImplementation implements SelectAndDeleteOperations{
    static PersistentFileStorage persistentFileStorage = new PersistentFileStorageImplementation();

    /***
     * This method is used to check with 2 operators and gets the column name and value from the where claus
     * @param userQuery user input query
     * @return column name and value from where condition
     */
    public UserQueryInputs operatorConditions(String userQuery) {
        UserQueryInputs userQueryInputs = null;
        String[] availableOperators = {"=", "<>"};

        for (String operator : availableOperators) {
            String regex = "WHERE(.*)[\\s\\n\\t]*" + Pattern.quote(operator) + "[\\s\\n\\t]*(.*);";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userQuery);
            if (matcher.find()) {
                String columnName = matcher.group(1).trim();
                String columnValue = matcher.group(2).trim();
                userQueryInputs = new UserQueryInputs(columnName, columnValue, operator);
                break;
            }
        }
        return userQueryInputs;
    }

    /***
     * This method is used to retrieve the table name from the user query using regex
     * @param userQuery user input query
     * @return table name
     */
    public String extractTableName(String userQuery) {
        if (userQuery.trim().toLowerCase().contains("where")) {
            String regex = "FROM(.*)WHERE";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userQuery);
            if (matcher.find()) {
                String tableName = matcher.group(1).trim();
//                System.out.println("The table name is " + tableName);
                return tableName;
            }
        }
        else {
            String regex = "FROM(.*);";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher_val = pattern.matcher(userQuery);
            if (matcher_val.find()) {
                String tableName = matcher_val.group(1).trim();
                return tableName;
            }
        }
        return "Incorrect table Name";
    }

    /***
     * This method is used to check whether the column are present in the table, if so
     * It checks the data types whether the value is of same type along with the operators.
     * @param userQueryInputs user query
     * @param tableColumnValue values from the table
     * @param metaTableLocation meta table location
     * @param tableLocation table location
     * @param AddedValues the values that need to be analyzed
     * @return This returns the checked values back to the select and delete function
     * @throws IOException
     */
    public List<List<String>> checkDataTypes(UserQueryInputs userQueryInputs, List<String>tableColumnValue, String metaTableLocation, String tableLocation,
                                             List<List<String>> AddedValues) throws IOException {
        List<String> tableDetails = persistentFileStorage.readFile(tableLocation);

        String getWhereColumnName = userQueryInputs.getWhere_Column_name();
        String getWhereTableValue = userQueryInputs.getValue();
        String getWhereTableOperator = userQueryInputs.getOperator();

        if (!tableColumnValue.contains(getWhereColumnName)) {
            System.out.println("The column is not found");
            return null;
        }

        List<String> getValue = null;
        List<String> checkMetaTable = persistentFileStorage.readFile(metaTableLocation);
        for (String valueInMetaTable : checkMetaTable) {
            checkMetaTable = List.of(valueInMetaTable.split(Constants.DELIMITER));
            if (checkMetaTable.contains(getWhereColumnName)) {
                getValue = checkMetaTable;
            }
        }
        String dataType = getValue.get(1);

        int valueIndex = 0;


        for (String tableValueFromList : tableDetails) {
            List<String> filteredTableDetailsColumnCheck = List.of(tableValueFromList.trim().split(Constants.DELIMITER));

            for (int i = 0; i < filteredTableDetailsColumnCheck.size(); i++) {
                if (filteredTableDetailsColumnCheck.get(i).equals(getWhereColumnName)) {
                    valueIndex = i;
                }
            }
            String valueToCheck;
            valueToCheck = filteredTableDetailsColumnCheck.get(valueIndex);

            if (dataType.equals("int")) {
                try {
                    Integer.parseInt(getWhereTableValue);
                    if (getWhereTableOperator.equals("=")) {
                        if (valueToCheck.equals(getWhereTableValue)) {
                            AddedValues.add(filteredTableDetailsColumnCheck);
                        }
                    } else if (getWhereTableOperator.equals("<>")) {
                        if (!Objects.equals(valueToCheck, getWhereTableValue)) {
                            AddedValues.add(filteredTableDetailsColumnCheck);
                        }
                    } else {
                        System.out.println("Incorrect value");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Incorrect Value");
                    return null;
                }
            } else if (dataType.equals("float")) {
                try {
                    Float.parseFloat(getWhereTableValue);
                    if (getWhereTableOperator.equals("=")) {
                        if (valueToCheck.equals(getWhereTableValue)) {
                            AddedValues.add(filteredTableDetailsColumnCheck);
                        }
                    } else if (getWhereTableOperator.equals("<>")) {
                        if (!Objects.equals(valueToCheck, getWhereTableValue)) {
                            AddedValues.add(filteredTableDetailsColumnCheck);
                        }
                    } else {
                        System.out.println("Incorrect value");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Incorrect Value");
                    return AddedValues;
                }
            } else if (dataType.equals("boolean")) {
                if (!getWhereTableValue.equalsIgnoreCase("false") && !getWhereTableValue.equalsIgnoreCase("true")) {
                    System.out.println("Incorrect value for column " + valueToCheck);
                    return AddedValues;
                }
                boolean conditionValue = Boolean.parseBoolean(getWhereTableValue);
                if (!conditionValue) {
                    if (getWhereTableOperator.equals("=")) {
                        if (!Boolean.parseBoolean(valueToCheck)) {
                            AddedValues.add(filteredTableDetailsColumnCheck);
                        }
                    } else if (getWhereTableOperator.equals("<>")) {
                        if (Boolean.parseBoolean(valueToCheck)) {
                            AddedValues.add(filteredTableDetailsColumnCheck);
                        }
                    } else {
                        System.out.println("Invalid conditional operator");
                        return AddedValues;
                    }
                }
            } else if (dataType.equals("varchar")) {
                if (getWhereTableOperator.equals("=")) {
                    if (valueToCheck.equalsIgnoreCase(getWhereTableValue)) {
                        AddedValues.add(filteredTableDetailsColumnCheck);
                    }
                } else if (getWhereTableOperator.equals("<>"))
                    if (!valueToCheck.equalsIgnoreCase(getWhereTableValue)) {
                        AddedValues.add(filteredTableDetailsColumnCheck);
                    }
            } else {
                System.out.println("Invalid conditional operator");
                return AddedValues;
            }
        }
        return AddedValues;
    }
}
