package org.DatabaseSystem.querying.query;
import org.DatabaseSystem.Constants;
import org.DatabaseSystem.Logger;
import org.DatabaseSystem.querying.insert.InsertQuery;
import org.DatabaseSystem.querying.insert.InsertStatement;
import org.DatabaseSystem.querying.select.SelectQuery;
import org.DatabaseSystem.querying.select.SelectQueryImplementation;
import org.DatabaseSystem.querying.create.CreateQuery;
import org.DatabaseSystem.querying.create.CreateStatement;
import org.DatabaseSystem.querying.delete.DeleteQuery;
import org.DatabaseSystem.querying.delete.DeleteStatement;
import org.DatabaseSystem.querying.update.UpdateQuery;
import org.DatabaseSystem.querying.update.UpdateQueryImplementation;
import java.io.IOException;
import java.util.Scanner;

public class QueryImplementation implements Query {

    private final Scanner scanner = new Scanner(System.in);
    CreateQuery createQuery = new CreateStatement();

    InsertQuery insertTableQuery = new InsertStatement();

    SelectQuery selectQuery = new SelectQueryImplementation();

    DeleteQuery deleteQuery=new DeleteStatement();

    UpdateQuery updateQuery=new UpdateQueryImplementation();

    /***
     * This method represents the query executions, it prompts user to enter the query
     * And checks whether the query matches the certain conditions that are given
     * executes the function based on the query statements
     * @param databaseName  current database name of the user
     * @param userName Username of the user
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void writingQuery(String databaseName, String userName) throws IOException {

        System.out.println("Enter the query");
        String userQuery = scanner.nextLine();
        userQuery = userQuery.trim().toLowerCase();
        if(userQuery.startsWith(Constants.CREATE.toLowerCase()))
        {
         createTable(databaseName,userQuery);
         Logger.createLogger(userName,databaseName,userQuery);

        }
        if(userQuery.startsWith(Constants.INSERT.toLowerCase()))
        {
            insertIntoTable(databaseName,userQuery);
            Logger.createLogger(userName,databaseName,userQuery);

        }
        if(userQuery.startsWith(Constants.SELECT.toLowerCase()))
        {
            selectTable(userQuery,databaseName);
            Logger.createLogger(userName,databaseName,userQuery);

        }
        if(userQuery.startsWith(Constants.DELETE.toLowerCase()))
        {
            deleteTable(userQuery,databaseName);
            Logger.createLogger(userName,databaseName,userQuery);

        }
        if(userQuery.startsWith(Constants.UPDATE.toLowerCase()))
        {
            updateTable(userQuery,databaseName);
            Logger.createLogger(userName,databaseName,userQuery);

        }

    }

    /***
     * This method represents to create a table based on the value given by the user
     * This method should meet necessary conditions to be executed, all the operation are done in
     * CreateTableQuery method, which acts as a backend.
     * @param database current database name of the user
     * @param user_query the user input inform of query
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void createTable(String database, String user_query) throws IOException {
        createQuery.createTableQuery(database,user_query);
    }

    /***
     * This method represents to insert values into a table based on the value given by the user
     * This method should meet necessary conditions to be executed, all the operation are done in
     * insertTableQuery method, which acts as a backend.
     * @param database current database name of the user
     * @param query the user input inform of query
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void insertIntoTable(String database, String query)throws IOException {
        insertTableQuery.insertTableQuery(database,query);

    }

    /***
     * This method represents to display values from a table based on the value given by the user
     * This method should meet necessary conditions to be executed, all the operation are done in
     * extractSelectColumns method, which acts as a backend.
     * @param query the user input inform of query
     * @param database current database name of the user
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void selectTable(String query, String database) throws IOException {
        selectQuery.extractSelectColumns(query,database);

    }

    /***
     * This method represents to delete values from a table or a contents in a table based on the value given by the user
     * This method should meet necessary conditions to be executed, all the operation are done in
     * extractDeleteColumns method, which acts as a backend.
     * @param query the user input inform of query
     * @param database current database name of the user
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void deleteTable(String query, String database) throws IOException  {
        deleteQuery.extractDeleteColumns(query,database);

    }

    /***
     * This method represents to update values in a table based on the value given by the user
     * This method should meet necessary conditions to be executed, all the operation are done in
     * updateQueryImp method, which acts as a backend.
     * @param query the user input inform of query
     * @param database current database name of the user
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void updateTable(String query, String database) throws IOException {
        updateQuery.updateQueryImp(query,database);

    }
}
