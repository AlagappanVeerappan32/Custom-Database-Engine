package org.DatabaseSystem.hashing;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashImplementation implements Hashing {

    /**
     * This method is used to hash the values as it contains sensitive information of the user
     * this method uses md5 algorithm with message digest library to hash the values
     * @param userValue User password and security answer
     * @return hashed value of the user password and security answer
     */
    @Override
    public String hashing(String userValue) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digestValue = md5.digest(userValue.getBytes());
            StringBuilder stringBuilder=new StringBuilder();
            for (byte value:digestValue) {
                stringBuilder.append(String.format("%02x",value));
            }
            return stringBuilder.toString();
        }catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }
}
