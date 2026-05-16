package Backend;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class RC4Cipher extends AbsCipher {

    @Override
    public SecretKey genKey() throws Exception {

        KeyGenerator kg = KeyGenerator.getInstance("RC4");

        key = kg.generateKey();

        return key;
    }

    @Override
    public void loadKey(String keyStr) throws Exception {

        byte[] keyBytes = keyStr.getBytes();

        key = new SecretKeySpec(keyBytes, "RC4");
    }

    @Override
    public byte[] encrypt(String text) throws Exception {

        Cipher cipher = Cipher.getInstance("RC4");

        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] result = cipher.doFinal(text.getBytes());

        return Base64.getEncoder().encode(result);
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {

        Cipher cipher = Cipher.getInstance("RC4");

        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decode = Base64.getDecoder().decode(cipherText);

        byte[] result = cipher.doFinal(decode);

        return new String(result);
    }
}