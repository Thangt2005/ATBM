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

        if (gcd(keyA, 26) != 1) {
            throw new Exception("a phải là số lẻ");
        }

        a = keyA % 26;
        b = keyB % 26;

        // tránh số âm
        if (a < 0) {
            a += 256;
        }

        if (b < 0) {
            b += 256;
        }
    }

    private int gcd(int a, int b) {

        if (b == 0) {
            return Math.abs(a);
        }

        return gcd(b, a % b);
    }
	@Override
    public byte[] encrypt(String text) throws Exception {

        if (text == null) {
            return null;
        }

        text = text.toUpperCase();

        String result = "";

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            // chỉ mã hóa chữ cái
            if (c >= 'A' && c <= 'Z') {

                int x = c - 'A';

                int y = (a * x + b) % 26;

                result += (char) (y + 'A');

            } else {

                result += c;
            }
        }

        return result.getBytes();
    }

    // tìm nghịch đảo modulo
    private int modInverse(int a) {

        for (int i = 1; i < 256; i++) {

            if ((a * i) % 26 == 1) {
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

        String text = new String(cipherText);

        text = text.toUpperCase();

        int aInv = modInverse(a);

        String result = "";

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {

                int y = c - 'A';

                int x = (aInv * (y - b + 26)) % 26;

                result += (char) (x + 'A');

            } else {

                result += c;
            }
        }

        return result;
    }
}