package org.DatabaseSystem;
import org.DatabaseSystem.databaseCreation.DataBaseImplementation;
import org.DatabaseSystem.databaseCreation.Database;
import org.DatabaseSystem.querying.query.Query;
import org.DatabaseSystem.querying.query.QueryImplementation;
import org.DatabaseSystem.login.Login;
import org.DatabaseSystem.login.LoginImplementation;
import org.DatabaseSystem.signUp.SignUp;
import org.DatabaseSystem.signUp.SignUpImplementation;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private  static final Scanner scanner=new Scanner(System.in);
    static Login loginInterface=new LoginImplementation();
    static SignUp signUpInterface=new SignUpImplementation();
    static Database dataBaseImplementation=new DataBaseImplementation();
    static Query query=new QueryImplementation();
    public static void main(String[] args) throws IOException {

        while (true)
        {
            System.out.println("Select an option");
            System.out.println(" 1. Sign Up");
            System.out.println(" 2. Login In");
            System.out.println(" 3. Quit");


            int option = scanner.nextInt();
            switch (option)
            {
                case 1:
                    signUpInterface.userSignUp();
                    break;
                case 2:
                    String user =loginInterface.userLogin();
                    if(user!=null)
                    {
                        String database = dataBaseImplementation.databaseCreation(user);
                        if(database!=null)
                        {
                            while(true)
                            {
                                System.out.println("Select an option");
                                System.out.println("1. Write a Query");
                                System.out.println("2. Quit");

                                int subOption = scanner.nextInt();
                                switch (subOption)
                                {
                                    case 1:
                                        query.writingQuery(database,user);
                                        break;
                                    case 2:
                                        System.exit(0);
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option, please chose the correct option");
            }

        }
    }
}