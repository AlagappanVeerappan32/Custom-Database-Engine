package org.DatabaseSystem.signUp;
import org.DatabaseSystem.Constants;
import org.DatabaseSystem.Logger;
import org.DatabaseSystem.fileOperations.PersistentFileStorage;
import org.DatabaseSystem.fileOperations.PersistentFileStorageImplementation;
import org.DatabaseSystem.hashing.Hashing;
import org.DatabaseSystem.hashing.HashImplementation;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class SignUpImplementation implements SignUp {
    PersistentFileStorage persistentFileStorage =new PersistentFileStorageImplementation();
    Hashing hashingInterface=new HashImplementation();
    private final Scanner scanner = new Scanner(System.in);
    private String userName;


    /***
     *This method prompts for user inputs like name,password, security question and security answer
     *Then the password and security answer are hashed using hashingInterface and
     *checks whether the user is already present or not.
     *If the user data is not present, it will write the data with delimiter into the file
     *Writing is done using calling the method from persistentFileStorageImplementation class
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    @Override
    public void userSignUp() throws IOException {

        System.out.println("Enter the username");
        userName = scanner.nextLine();
        System.out.println("Enter the password");
        String password = scanner.nextLine();
        System.out.println("Enter the security question");
        String securityQuestion = scanner.nextLine();
        System.out.println("Enter the security answer");
        String securityAnswer = scanner.nextLine();
        String hashedPassword = hashingInterface.hashing(password);
        String hashedSecurityAnswer = hashingInterface.hashing(securityAnswer);
        boolean currentUserStatus = checkUserExist();
        if (currentUserStatus) {
            System.out.println("User already exist,Please Log in");
            Logger.createLogger(userName,null,"Already exist, the user is trying to SignUp gain");

        } else {
            String writeUserData = userName + Constants.DELIMITER + hashedPassword + Constants.DELIMITER + securityQuestion + Constants.DELIMITER +
                    hashedSecurityAnswer + Constants.DELIMITER;
            persistentFileStorage.writeFile(writeUserData,Constants.USER_INFO_FILE);
            System.out.println("Successfully signed up");
            Logger.createLogger(userName,null,"User SignedUp");

        }
    }

    /***
     * This method is used to read if the user data is present in the storage if not
     * it creates the new file.
     * @return True or False (checks user data is present in the file)
     * @throws IOException as it is handling files, it will to throw exception if the file is not present
     */
    public boolean checkUserExist() throws IOException {

        persistentFileStorage.createUserFile(Constants.USER_INFO_FILE);
        List<String> userDataList = persistentFileStorage.readFile(Constants.USER_INFO_FILE);
        for (String singleData:userDataList) {
            String[] columns = singleData.split(Constants.DELIMITER);
            if(columns[0].equals(userName))
            {
                return true;
            }
        }
        return false;
    }

}

