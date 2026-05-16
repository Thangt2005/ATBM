package Backend;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512 {

    public String checkSum(String input){

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger number = new BigInteger(1,messageDigest);

            return number.toString(16);

        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public String hash(String file) throws Exception{

        MessageDigest digest = MessageDigest.getInstance("SHA-512");

        InputStream is = new BufferedInputStream(new FileInputStream(file));

        DigestInputStream dis = new DigestInputStream(is,digest);

        byte[] buffer = new byte[1024];

        int read;

        do{
            read = dis.read(buffer);
        }while(read != -1);

        BigInteger number = new BigInteger(1,dis.getMessageDigest().digest());

        return number.toString(16);
    }

    public static void main(String[] args) throws Exception {

        String s = "123";

        SHA512 sha512 = new SHA512();

        System.out.println(sha512.checkSum(s));

        System.out.println(
                sha512.hash("C:\\Users\\Public\\Desktop\\IntelliJ IDEA 2026.1.lnk")
        );
    }
}