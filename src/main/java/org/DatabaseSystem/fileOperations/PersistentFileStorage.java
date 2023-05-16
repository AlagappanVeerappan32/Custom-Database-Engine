package org.DatabaseSystem.fileOperations;

import java.io.IOException;
import java.util.List;

public interface PersistentFileStorage {

    void createFile(String filename) throws IOException;
    void writeFile( String user_data,String filename) throws IOException;
    List<String> readFile(String filename) throws IOException;
    void createDirectory(String filename);
    void createUserFile(String filename) throws IOException;
}
