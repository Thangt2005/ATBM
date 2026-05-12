package Backend;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCipher extends AbsCipher {

	@Override
	public SecretKey genKey() throws Exception {

		KeyGenerator kg = KeyGenerator.getInstance("AES");

		kg.init(128);

		key = kg.generateKey();

		return key;
	}

	@Override
	public void loadKey(String keyStr) throws Exception {

		byte[] keyBytes = keyStr.getBytes();

		byte[] data = new byte[16];

		for (int i = 0; i < data.length; i++) {

			if (i < keyBytes.length) {
				data[i] = keyBytes[i];
			} else {
				data[i] = 0;
			}
		}

		key = new SecretKeySpec(data, "AES");
	}

	@Override
	public byte[] encrypt(String text) throws Exception {

		Cipher cipher = Cipher.getInstance("AES");

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] data = cipher.doFinal(text.getBytes());

		return Base64.getEncoder().encode(data);
	}

	@Override
	public String decrypt(byte[] cipherText) throws Exception {

		Cipher cipher = Cipher.getInstance("AES");

		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] decode = Base64.getDecoder().decode(cipherText);

		byte[] result = cipher.doFinal(decode);

		return new String(result);
	}

}