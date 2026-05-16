package Backend;

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

        byte[] keyBytes = keyStr.getBytes("UTF-8");

        key = new SecretKeySpec(keyBytes, "RC4");
    }

    @Override
    public byte[] encrypt(String text) throws Exception {

        Cipher cipher = Cipher.getInstance("RC4");

        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(text.getBytes("UTF-8"));
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {

        Cipher cipher = Cipher.getInstance("RC4");

        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] result = cipher.doFinal(cipherText);

        return new String(result, "UTF-8");
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {

        Cipher cipher = Cipher.getInstance("RC4");

        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    @Override
    public byte[] decryptByte(byte[] data) throws Exception {

        Cipher cipher = Cipher.getInstance("RC4");

        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }
}