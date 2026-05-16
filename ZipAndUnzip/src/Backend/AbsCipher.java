package Backend;


import java.util.Base64;
import javax.crypto.SecretKey;

public abstract class AbsCipher {
    protected SecretKey key;

    public SecretKey getKey() {
    	return key;
    	}
    
    // Nạp khóa trực tiếp
    public void loadKey(SecretKey key) {
    	this.key = key; 
    	}

    public abstract SecretKey genKey() throws Exception;
    public abstract void loadKey(String key) throws Exception;
    public abstract byte[] encrypt(String text) throws Exception;
    public abstract String decrypt(byte[] cipherText) throws Exception;
    public abstract byte[] encrypt(byte[] data) throws Exception;
    public abstract byte[] decryptByte(byte[] data) throws Exception;
    
    public String encryptBase64(String text) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(text));
    }

    public String decryptBase64(String base64CipherText) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(base64CipherText);
        return decrypt(decoded);
    }
    
}