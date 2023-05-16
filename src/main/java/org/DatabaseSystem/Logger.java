package org.DatabaseSystem;

import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    static PersistentFileStorage persistentFileStorage =new PersistentFileStorageImplementation();


    /**
     * This method is used to generate the logs for the all the user and database activity.
     * We are also using java. Time library to get the record of the accurate time of activity
     * It is calling fileOperations to create a file and write into the file
     * @param username Username of the user
     * @param database database name created by the user
     * @param query the input given by the user in console
     * @throws IOException as it is handling files, it will to throw exception if the file is not present.
     */
    public static void createLogger(String username,String database, String query) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        persistentFileStorage.createUserFile(Constants.LOGGER_FILE);
        String log = username+Constants.DELIMITER+database+Constants.DELIMITER+query+Constants.DELIMITER+formattedDateTime;
        persistentFileStorage.writeFile(log,Constants.LOGGER_FILE);

    }
}
