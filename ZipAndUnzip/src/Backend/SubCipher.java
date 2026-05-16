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
    @Override
    public byte[] encrypt(byte[] data) throws Exception {

    	String khoa = new String(key.getEncoded());

    	byte[] subTable = new byte[256];

    	for (int i = 0; i < 256; i++) {
    		subTable[i] = (byte) i;
    	}

    	for (int i = 0; i < 26; i++) {

    		char plain = bangChu.charAt(i);

    		char sub = khoa.charAt(i);

    		subTable[(byte) plain & 0xFF] = (byte) sub;
    		subTable[(byte) Character.toLowerCase(plain) & 0xFF]
    				= (byte) Character.toLowerCase(sub);
    	}

    	byte[] result = new byte[data.length];

    	for (int i = 0; i < data.length; i++) {

    		int value = data[i] & 0xFF;

    		result[i] = subTable[value];
    	}

    	return result;
    }

    @Override
    public byte[] decryptByte(byte[] data) throws Exception {

    	String khoa = new String(key.getEncoded());

    	byte[] reverseTable = new byte[256];

    	for (int i = 0; i < 256; i++) {
    		reverseTable[i] = (byte) i;
    	}

    	for (int i = 0; i < 26; i++) {

    		char plain = bangChu.charAt(i);

    		char sub = khoa.charAt(i);

    		reverseTable[(byte) sub & 0xFF] = (byte) plain;

    		reverseTable[(byte) Character.toLowerCase(sub) & 0xFF]
    				= (byte) Character.toLowerCase(plain);
    	}

    	byte[] result = new byte[data.length];

    	for (int i = 0; i < data.length; i++) {

    		int value = data[i] & 0xFF;

    		result[i] = reverseTable[value];
    	}

    	return result;
    }
}