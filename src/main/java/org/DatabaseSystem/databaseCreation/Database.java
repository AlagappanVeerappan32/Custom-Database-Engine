package org.DatabaseSystem.databaseCreation;
import java.io.IOException;

public interface Database {

    String databaseCreation(String UserName) throws IOException;

    boolean checkDatabasePerUser(String userName) throws IOException;
}
