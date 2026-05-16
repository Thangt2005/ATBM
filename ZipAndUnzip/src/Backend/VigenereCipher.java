package Backend;

import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class VigenereCipher extends AbsCipher {

	private String key;

	@Override
	public SecretKey genKey() throws Exception {

		Random rd = new Random();

		int len = rd.nextInt(5) + 5;

		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {

			int index = rd.nextInt(chars.length());

			sb.append(chars.charAt(index));
		}

		key = sb.toString();

		return new SecretKeySpec(key.getBytes(), "Vigenere");
	}

	@Override
	public void loadKey(String key) throws Exception {

		if (key == null || key.trim().isEmpty()) {
			throw new Exception("Key không được để trống");
		}

		this.key = key.toUpperCase();
	}

	@Override
	public byte[] encrypt(String text) throws Exception {

		if (text == null) {
			return null;
		}

		text = text.toUpperCase();

		byte[] output = new byte[text.length()];

		int keyIndex = 0;

		for (int i = 0; i < text.length(); i++) {

			char c = text.charAt(i);

			// chỉ mã hóa chữ cái
			if (c >= 'A' && c <= 'Z') {

				int x = c - 'A';

				int k = key.charAt(keyIndex % key.length()) - 'A';

				int y = (x + k) % 26;

				output[i] = (byte) (y + 'A');

				keyIndex++;
			} else {

				output[i] = (byte) c;
			}
		}

		return output;
	}

	@Override
	public String decrypt(byte[] cipherText) throws Exception {

		if (cipherText == null) {
			return null;
		}

		String text = new String(cipherText);

		StringBuilder sb = new StringBuilder();

		int keyIndex = 0;

		for (int i = 0; i < text.length(); i++) {

			char c = text.charAt(i);

			if (c >= 'A' && c <= 'Z') {

				int y = c - 'A';

				int k = key.charAt(keyIndex % key.length()) - 'A';

				int x = (y - k + 26) % 26;

				sb.append((char) (x + 'A'));

				keyIndex++;
			} else {

				sb.append(c);
			}
		}

		return sb.toString();
	}
	@Override
	public byte[] encrypt(byte[] data) throws Exception {

		byte[] result = new byte[data.length];

		for (int i = 0; i < data.length; i++) {

			int x = data[i] & 0xFF;

			int k = key.charAt(i % key.length()) & 0xFF;

			int y = (x + k) % 256;

			result[i] = (byte) y;
		}

		return result;
	}

	@Override
	public byte[] decryptByte(byte[] data) throws Exception {

		byte[] result = new byte[data.length];

		for (int i = 0; i < data.length; i++) {

			int y = data[i] & 0xFF;

			int k = key.charAt(i % key.length()) & 0xFF;

			int x = (y - k + 256) % 256;

			result[i] = (byte) x;
		}

		return result;
	}
}