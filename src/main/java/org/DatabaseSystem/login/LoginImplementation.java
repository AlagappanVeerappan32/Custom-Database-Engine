package org.DatabaseSystem.login;
import org.DatabaseSystem.Constants;
import org.DatabaseSystem.Logger;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;
import org.DatabaseSystem.hashing.Hashing;
import org.DatabaseSystem.hashing.HashImplementation;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class LoginImplementation implements Login {
    private final Scanner scanner = new Scanner(System.in);
    PersistentFileStorage persistentFileStorage = new PersistentFileStorageImplementation();
    Hashing hashingInterface = new HashImplementation();

    /***
     *Method is used for logging in,It prompts user for name and password and hash the password.
     * checks the name and hashed password with stored value in the file, if matches it asks security question and answer
     * checks the stored value again, if matches login successful, else it won't allow the user to log in
     *
     * @return UserName
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public String userLogin() throws IOException {


        System.out.println("Enter the username");
        String userName = scanner.nextLine();
        System.out.println("Enter the password");
        String password = scanner.nextLine();
        String hashedPassword = hashingInterface.hashing(password);

        persistentFileStorage.createUserFile(Constants.USER_INFO_FILE);
        List<String> userDataList = persistentFileStorage.readFile(Constants.USER_INFO_FILE);
        for (String singleValue : userDataList) {
            String[] userDetailsValue = singleValue.split(Constants.DELIMITER);
            if (userDetailsValue[0].equals(userName) && userDetailsValue[1].equals(hashedPassword)) {
                System.out.println("Security Question : " + userDetailsValue[2]);
                String securityAnswer = scanner.nextLine();
                String hashedSecurityAnswer = hashingInterface.hashing(securityAnswer);
                if (userDetailsValue[3].equals(hashedSecurityAnswer)) {
                    System.out.println("Successfully logged in");
                    Logger.createLogger(userName,null,"User has logged in");
                    return userDetailsValue[0];
                } else {
                    System.out.println("Incorrect security answer");
                    return null;
                }
            }
        }
        System.out.println("Invalid userName or password");
        return null;
    }
}