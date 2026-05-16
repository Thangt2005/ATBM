package Backend;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class TwofishCipher extends AbsCipher {

    static {

        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public SecretKey genKey() throws Exception {

        KeyGenerator kg = KeyGenerator.getInstance("Twofish", "BC");

        kg.init(128);

        key = kg.generateKey();

        return key;
    }

    @Override
    public void loadKey(String keyStr) throws Exception {

        byte[] keyBytes = keyStr.getBytes("UTF-8");

        byte[] data = new byte[16];

        for (int i = 0; i < data.length; i++) {

            if (i < keyBytes.length) {

                data[i] = keyBytes[i];

            } else {

                data[i] = 0;
            }
        }

        key = new SecretKeySpec(data, "Twofish");
    }

    @Override
    public byte[] encrypt(String text) throws Exception {

        Cipher cipher =
                Cipher.getInstance("Twofish/ECB/PKCS5Padding", "BC");

        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(text.getBytes("UTF-8"));
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {

        Cipher cipher =
                Cipher.getInstance("Twofish/ECB/PKCS5Padding", "BC");

        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] result = cipher.doFinal(cipherText);

        return new String(result, "UTF-8");
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {

        Cipher cipher =
                Cipher.getInstance("Twofish/ECB/PKCS5Padding", "BC");

        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    @Override
    public byte[] decryptByte(byte[] data) throws Exception {

        Cipher cipher =
                Cipher.getInstance("Twofish/ECB/PKCS5Padding", "BC");

        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }
}