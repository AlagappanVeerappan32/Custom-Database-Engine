package org.DatabaseSystem.databaseCreation;
import org.DatabaseSystem.Constants;
import org.DatabaseSystem.Logger;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class DataBaseImplementation implements Database {
    PersistentFileStorage persistentFileStorage = new PersistentFileStorageImplementation();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * The method prompts user to enter the query to create database
     * once the user inputs, it splits the query into 3 different pieces and checks whether it matches the required constraints
     * if it matches, it extracts the database name from the user input , from this it gets the username of the database created user
     * and checks whether the user already have a database or not, if not it creates a new database, if already exist, it won't allow the user
     * to create the new database, until the current one is deleted.
     *
     * @param userName username of the user to keep track of databases
     * @return it returns the database name of the user, so we can keep track of the database
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public String databaseCreation(String userName) throws IOException {

        while(true) {
            System.out.println("Please enter the query to create DB");
            String createDatabaseQuery = scanner.nextLine();
            String query = createDatabaseQuery.trim().toLowerCase();

            String[] splittingQueryIntoParts = query.split("\\s+");

            String databaseName = splittingQueryIntoParts[splittingQueryIntoParts.length - 1].replace(";", "");

            if (splittingQueryIntoParts.length == 3 && splittingQueryIntoParts[0].equals(Constants.CREATE) && splittingQueryIntoParts[1].equals(Constants.DATABASE)
                    && splittingQueryIntoParts[2].endsWith(";")) {

                if (checkDatabasePerUser(userName)) {
                    System.out.println("You already have a database in progress, delete the current database and create new database. ");
                    Logger.createLogger(userName,databaseName,"User already have a database in progress");

                } else {

                    System.out.println("Creating database " + databaseName);

                    persistentFileStorage.createDirectory(Constants.DB_LOCATION + databaseName);

                    String storingUserDbValues = userName + Constants.DELIMITER + databaseName;

                    persistentFileStorage.writeFile(storingUserDbValues, Constants.USER_DB_FILE);
                    System.out.println("Created Database successfully");
                    Logger.createLogger(userName,databaseName,"Created a new database");

                }
                return databaseName;
            } else {
                System.out.println("Invalid query, Try again, follow this formal 'create database db_name;' ");
                Logger.createLogger(userName,databaseName,"User tried to create a database, failed due to invalid query format");

            }
        }
    }

    /***
     * This method is used to check whether a user has a database or not, if not creates new one.
     * Checks if the username matches the credentials then moves forward.
     * @param userName username of the user to keep track of databases
     * @return true or false, this will let us know whether the user has a database or not
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public boolean checkDatabasePerUser(String userName) throws IOException {
        persistentFileStorage.createUserFile(Constants.USER_DB_FILE);
        List<String> userDbList = persistentFileStorage.readFile(Constants.USER_DB_FILE);
        for (String databaseData : userDbList) {
            String[] columns = databaseData.split(Constants.DELIMITER);
            if (columns[0].equals(userName)) {
                return true;
            }
        }
        return false;
    }

}


