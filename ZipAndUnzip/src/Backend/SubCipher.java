package Backend;

import java.util.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SubCipher extends AbsCipher {
    private final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public SecretKey genKey() throws Exception {
        // Tạo danh sách ký tự và xáo trộn
        List<String> letters = Arrays.asList(ALPHABET.split(""));
        Collections.shuffle(letters);
        String shuffled = String.join("", letters);
        return new SecretKeySpec(shuffled.getBytes(), "Substitution");
    }

    @Override
    public void loadKey(String keyStr) throws Exception {
        this.key = new SecretKeySpec(keyStr.toUpperCase().getBytes(), "Substitution");
    }

    @Override
    public byte[] encrypt(String text) throws Exception {
        String keyStr = new String(key.getEncoded());
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            char upperC = Character.toUpperCase(c);
            int index = ALPHABET.indexOf(upperC);
            
            if (index != -1) {
                char mappedChar = keyStr.charAt(index);
                // Giữ nguyên kiểu chữ hoa/thường
                result.append(Character.isLowerCase(c) ? Character.toLowerCase(mappedChar) : mappedChar);
            } else {
                result.append(c); // Ký tự đặc biệt giữ nguyên
            }
        }
        return result.toString().getBytes();
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {
        String keyStr = new String(key.getEncoded());
        String text = new String(cipherText);
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            char upperC = Character.toUpperCase(c);
            int index = keyStr.indexOf(upperC); // Tìm trong bảng khóa
            
            if (index != -1) {
                char originalChar = ALPHABET.charAt(index); // Lấy từ bảng chuẩn
                result.append(Character.isLowerCase(c) ? Character.toLowerCase(originalChar) : originalChar);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}