package org.DatabaseSystem.querying.insert;

import org.DatabaseSystem.Constants;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertStatement implements InsertQuery {


    PersistentFileStorage persistentFileStorage =new PersistentFileStorageImplementation();

    /***
     * This method sole purpose is to analyze the query using regex operation
     * @param userQuery the user query given by the user
     * @return true or false based on analyzing query
     */
    @Override
    public boolean validate_query(String userQuery) {
        String regex = "INSERT INTO (.*)[\\s\\n\\r]*[(](.*)[)] VALUES[\\s\\n\\r]*[(](.*)[)];";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userQuery);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * The method is used for inserting values into the table
     * It checks whether the table is present if so checks the column name are same
     * Also checks if the data types are similar in the table meta location
     * Looks for primary and foreign key constraints , collects all and if the conditions are successfully mey
     * it adds the values into the table.
     * Note : If the user try to insert same primary key value again it won't let the user insert.
     *
     * @param database User current database
     * @param tableName User Query Table name
     * @param columns User Query columns to be inserted
     * @param values User Query values to be inserted
     * @return
     * @throws IOException
     */
    @Override
    public boolean checkInsertConditionQuery(String database, String tableName, String columns, String values) throws IOException {

        String tableLocation =Constants.TABLE_LOCATIONS.replace("{db}",database).replace("{table}",tableName);
        String metaLocation = Constants.META_TABLE_LOCATIONS.replace("{db}", database).replace("{table}", tableName);

        if (!new File(tableLocation).exists()) {
            System.out.println("Table doesn't exist");
            return false;
        } else {
            List<String> checkingTable = persistentFileStorage.readFile(tableLocation);
            List<String> tableColumnValue = Arrays.stream(checkingTable.get(0).split(Constants.DELIMITER)).toList();
            for (String column : columns.trim().split(",")) {
                if (!tableColumnValue.contains(column.trim())) {
                    System.out.println("column " + column + " does not exist in " + tableName);
                    return false;
                }
            }
            List<String> checkMetaTable = persistentFileStorage.readFile(metaLocation);
            String[] valueListFromTable = (values.trim().split(","));
            String[] columnListFromTable = columns.trim().split(",");


            if (columnListFromTable.length != valueListFromTable.length) {
                System.out.println("Query does not have proper columns and values count");
                return false;
            }

            Map<String, String> metadataMap = new HashMap<>();
            for (String metadataString : checkMetaTable) {
                String[] metadataArray = metadataString.split("/@/");
                metadataMap.put(metadataArray[0], metadataString);
            }
            for (int i = 0; i < columnListFromTable.length; i++) {
                String columnName = columnListFromTable[i].trim();
                if (metadataMap.containsKey(columnName)) {
                    String[] columnType = metadataMap.get(columnName).split("/@/");
                    String dataType = columnType[1];
                    int lengthOfTheValue = Integer.parseInt(columnType[2]);
                    boolean isPrimaryKey = Boolean.parseBoolean(columnType[3]);
                    boolean isForeignKey = Boolean.parseBoolean(columnType[4]);
                    String foreignTable = "";
                    String foreignColumn = "";
                    if (isForeignKey) {
                        if (columnType.length >= 6) {
                            foreignTable = columnType[5];
                        }
                        if (columnType.length >= 7) {
                            foreignColumn = columnType[6];
                        }
                    }
                    String columnValue = valueListFromTable[i];
                    if (dataType.equals("float")) {
                        try {
                            Float.parseFloat(columnValue);
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect value for column " + columnName);
                            return false;
                        }
                    } else if (dataType.equals("int")) {
                        try {
                            Integer.parseInt(columnValue);
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect value for column " + columnName);
                            return false;
                        }
                    } else if (dataType.equals("boolean")) {
                        if (!(columnValue.toUpperCase().equals("FALSE") || columnValue.toUpperCase().equals("TRUE"))) {
                            System.out.println("Incorrect value for column " + columnName);
                            return false;
                        }
                    } else if (dataType.equals("varchar")) {
                        if (columnValue.length() > lengthOfTheValue) {
                            System.out.println("Incorrect value, the value length is exceeding the column " + columnName + " by length " + lengthOfTheValue);
                            return false;
                        }
                    }
                    List<String> primary_table = null;
                    if (isPrimaryKey) {
                        int value_index = 0;
                        for (String keyCheck : checkingTable) {
                            primary_table = List.of(keyCheck.trim().split(Constants.DELIMITER));
                            for (int j = 0; j < primary_table.size(); j++) {
                                if (primary_table.get(j).equals(columnName)) {
                                    value_index = j;
                                }
                            }
                            String valueToCheck = primary_table.get(value_index);
                            if(valueToCheck.equals(columnValue))
                            {
                                System.out.println("Cant insert the elements, violates primary key");
                                return false;
                            }
                        }
                    }
                }

            }
        }

        return true;
    }

    /***
     * This method works only if the user query is valid, it collects the table name, columns and values from the query
     * and checks if the user inputs can be inserted into the table using "checkInsertConditionQuery" method
     * if it is true, the user value is written in the table.
     * @param database current database name of the user
     * @param userQuery the user query given by the user
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void insertTableQuery(String database, String userQuery) throws IOException {

        if(!validate_query(userQuery))
        {
            System.out.println("Incorrect query, please follow the correct type");
            return;
        }
        Pattern pattern = Pattern.compile("INSERT INTO (.*)[\\s\\n\\r]*[(](.*)[)] VALUES[\\s\\n\\r]*[(](.*)[)];", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userQuery);
        if (matcher.find()) {
            String tableName = matcher.group(1).trim();
            String columns = matcher.group(2).trim();
            String values = matcher.group(3).trim();
            boolean valid= checkInsertConditionQuery(database,tableName, columns, values);
            String valueToWrite=values.replace(",", Constants.DELIMITER).trim();
            String tableFilePath =Constants.TABLE_LOCATIONS.replace("{db}",database).replace("{table}",tableName);
            if(valid)
            {
                persistentFileStorage.writeFile(valueToWrite,tableFilePath);
                System.out.println("insert query executed successfully");

            }else
            {
                System.out.println("Query values does not match the table requirements");
                return;
            }

        }
    }





    }

