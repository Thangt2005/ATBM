package Backend;

import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AffineCipher extends AbsCipher {

    private int a;
    private int b;

    @Override
    public SecretKey genKey() throws Exception {

        Random rd = new Random();

        // a phải là số lẻ
        do {
            a = rd.nextInt(256);
        } while (a % 2 == 0);

        b = rd.nextInt(256);

        byte[] key = new byte[2];

        key[0] = (byte) a;
        key[1] = (byte) b;

        return new SecretKeySpec(key, "Affine");
    }

    @Override
    public void loadKey(String key) throws Exception {

        if (key == null || key.trim().isEmpty()) {
            throw new Exception("Key không được để trống");
        }

        String[] arr = key.split(",");

        if (arr.length < 2) {
            throw new Exception("Key phải có dạng a,b");
        }

        int keyA = Integer.parseInt(arr[0].trim());
        int keyB = Integer.parseInt(arr[1].trim());

        if (keyA % 2 == 0) {
            throw new Exception("a phải là số lẻ");
        }

        a = keyA % 256;
        b = keyB % 256;

        // tránh số âm
        if (a < 0) {
            a += 256;
        }

        if (b < 0) {
            b += 256;
        }
    }

    @Override
    public byte[] encrypt(String text) throws Exception {

        if (text == null) {
            return null;
        }

        byte[] data = text.getBytes();

        byte[] output = new byte[data.length];

        for (int i = 0; i < data.length; i++) {

            // ép byte về khoảng 0-255
            int x = data[i] & 0xFF;

            int y = (a * x + b) % 256;

            output[i] = (byte) y;
        }

        return output;
    }

    // tìm nghịch đảo modulo
    private int modInverse(int a) {

        for (int i = 1; i < 256; i++) {

            if ((a * i) % 256 == 1) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {

        if (cipherText == null) {
            return null;
        }

        int aInv = modInverse(a);

        byte[] output = new byte[cipherText.length];

        for (int i = 0; i < cipherText.length; i++) {

            int y = cipherText[i] & 0xFF;

            int x = aInv * (y - b);

            x = (x + 256) % 256;

            output[i] = (byte) x;
        }

        return new String(output);
    }
}