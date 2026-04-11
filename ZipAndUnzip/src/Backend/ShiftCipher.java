package Backend;

import java.util.Random;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ShiftCipher extends AbsCipher {

    @Override
    public SecretKey genKey() throws Exception {
    	Random random = new Random();
    	int k = random.nextInt(26);
        return new SecretKeySpec(String.valueOf(k).getBytes(), "Shift");
    }

    @Override
    public void loadKey(String keyStr) throws Exception {
        this.key = new SecretKeySpec(keyStr.getBytes(), "Shift");
    }

    @Override
    public byte[] encrypt(String text) throws Exception {
        int k = Integer.parseInt(new String(key.getEncoded()));
        StringBuilder sb = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append((char) ((c - 'A' + k) % 26 + 'A'));
            } else if (Character.isLowerCase(c)) {
                sb.append((char) ((c - 'a' + k) % 26 + 'a'));
            } else {
                sb.append(c);
            }
        }
        return sb.toString().getBytes();
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {
        int k = Integer.parseInt(new String(key.getEncoded()));
        String text = new String(cipherText);
        
        // Giải mã bằng cách dịch chuyển ngược lại (26 - k)
        int decryptKey = 26 - (k % 26);
        
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append((char) ((c - 'A' + decryptKey) % 26 + 'A'));
            } else if (Character.isLowerCase(c)) {
                sb.append((char) ((c - 'a' + decryptKey) % 26 + 'a'));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}