package Backend;

import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SubCipher extends AbsCipher {

    private String bangChu = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public SecretKey genKey() throws Exception {

        char[] arr = bangChu.toCharArray();

        Random rd = new Random();

        // đảo vị trí ký tự
        for(int i = 0; i < arr.length; i++) {

            int j = rd.nextInt(arr.length);

            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        String khoa = "";

        for(int i = 0; i < arr.length; i++) {
            khoa += arr[i];
        }

        key = new SecretKeySpec(khoa.getBytes(), "Substitution");

        return key;
    }

    @Override
    public void loadKey(String keyStr) throws Exception {

        if(keyStr == null || keyStr.length() != 26) {
            throw new Exception("Key khong hop le");
        }

        key = new SecretKeySpec(keyStr.toUpperCase().getBytes(), "Substitution");
    }

    @Override
    public byte[] encrypt(String text) throws Exception {

        String khoa = new String(key.getEncoded());

        String result = "";

        for(int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            char upper = Character.toUpperCase(c);

            int vt = bangChu.indexOf(upper);

            if(vt != -1) {

                char maHoa = khoa.charAt(vt);

                if(Character.isLowerCase(c)) {
                    maHoa = Character.toLowerCase(maHoa);
                }

                result += maHoa;
            }
            else {
                result += c;
            }
        }

        return result.getBytes();
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {

        String text = new String(cipherText);

        String khoa = new String(key.getEncoded());

        String result = "";

        for(int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            char upper = Character.toUpperCase(c);

            int vt = khoa.indexOf(upper);

            if(vt != -1) {

                char goc = bangChu.charAt(vt);

                if(Character.isLowerCase(c)) {
                    goc = Character.toLowerCase(goc);
                }

                result += goc;
            }
            else {
                result += c;
            }
        }

        return result;
    }
}