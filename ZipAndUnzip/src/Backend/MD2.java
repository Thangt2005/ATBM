package Backend;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD2 {
	public String checkSumMD2(String input){
        try{
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1,messageDigest);

            return number.toString(16);
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public String hashMD2(String file) throws Exception{
        MessageDigest digest = MessageDigest.getInstance("MD2");
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        DigestInputStream dis = new DigestInputStream(is,digest);

        byte[] buffer = new byte[1024];
        int read;
        do {
            read = dis.read(buffer);
        }while(read != -1);
        BigInteger number = new BigInteger(1,dis.getMessageDigest().digest());
        return number.toString(16);
    }

    public static void main(String[] args) throws Exception {
    	 String s = "123";
         MD2 md2 = new MD2();
         System.out.println(md2.checkSumMD2(s));
         System.out.println(md2.hashMD2("C:\\Users\\Public\\Desktop\\IntelliJ IDEA 2026.1.lnk"));
     }
     }


