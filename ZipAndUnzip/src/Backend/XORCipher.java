package Backend;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class XORCipher extends AbsCipher {

    private String keyText;

    @Override
    public SecretKey genKey() throws Exception {

        keyText = "KEY123";

        return new SecretKeySpec(keyText.getBytes(), "XOR");
    }

    @Override
    public void loadKey(String key) throws Exception {

        if (key == null || key.trim().isEmpty()) {

            throw new Exception("Key không được rỗng");
        }

        keyText = key;
    }

    @Override
    public byte[] encrypt(String text) throws Exception {

        byte[] input = text.getBytes();

        byte[] keyBytes = keyText.getBytes();

        byte[] output = new byte[input.length];

        for (int i = 0; i < input.length; i++) {

            output[i] = (byte) (input[i] ^ keyBytes[i % keyBytes.length]);
        }

        return output;
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {

        byte[] keyBytes = keyText.getBytes();

        byte[] output = new byte[cipherText.length];

        for (int i = 0; i < cipherText.length; i++) {

            output[i] = (byte) (cipherText[i] ^ keyBytes[i % keyBytes.length]);
        }

        return new String(output);
    }
}