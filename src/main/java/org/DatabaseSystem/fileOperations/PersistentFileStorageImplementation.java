package org.DatabaseSystem.fileOperations;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistentFileStorageImplementation implements PersistentFileStorage {

    /***
     * This method is used to create a file if not exist.
     * @param filename file location
     * @throws IOException as it is handling files, it will throw exception if the file is not present
     */
    @Override
    public  void createUserFile(String filename) throws IOException {

        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /***
     * This method is function to write into the file with the optimized user inputs
     * @param userData the user input values will be appended with custom delimiter and processed
     * @param filename file location
     * @throws IOException  as it is handling files, it will throw exception if the file is not present
     */

    @Override
    public void writeFile(String userData, String filename) throws IOException {

        FileWriter fileWriter = new FileWriter(filename,true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(userData);
        bufferedWriter.newLine();
        bufferedWriter.close();
        fileWriter.close();
    }


    /***
     * This method is used to read the file that is present in the location.
     * @param filename file location
     * @return the list of data which is read from the file
     * @throws IOException as it is handling files, it will throw exception if the file is not present
     */

    @Override
    public List<String> readFile(String filename)  throws IOException{

        List<String> userDataList =new ArrayList<>();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(filename));
        String lines;
        while((lines=bufferedReader.readLine())!=null)
        {
            userDataList.add(lines);
        }
        return userDataList;
    }


    /**
     * This method is used to create directory for the database, if it does not exist
     * @param directory file location
     */
    @Override
    public void createDirectory(String directory) {
        File file=new File(directory);
        if (!file.exists())
        {
            file.mkdir();
        }

    }

    /***
     * This method is used create fie if not exist, if it is present it will delete and create new file for delete query.
     * @param filename file location
     * @throws IOException as it is handling files, it will throw exception if the file is not present
     */
    @Override
    public void createFile(String filename) throws IOException {
        File file=new File(filename);
        if(!file.exists())
        {
            file.createNewFile();
        }
        else {
            file.delete();
            file.createNewFile();
        }
    }

}
