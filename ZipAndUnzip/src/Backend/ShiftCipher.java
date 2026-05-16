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

		String result = "";

		for (int i = 0; i < text.length(); i++) {

			char c = text.charAt(i);

			if (c >= 'A' && c <= 'Z') {

				int x = c - 65;

				x = (x + k) % 26;

				result += (char) (x + 65);
			} else if (c >= 'a' && c <= 'z') {

				int x = c - 97;

				x = (x + k) % 26;

				result += (char) (x + 97);
			} else {
				result += c;
			}
		}

		return result.getBytes();
	}

	@Override
	public String decrypt(byte[] cipherText) throws Exception {

		int k = Integer.parseInt(new String(key.getEncoded()));

		String text = new String(cipherText);

		String result = "";

		for (int i = 0; i < text.length(); i++) {

			char c = text.charAt(i);

			if (c >= 'A' && c <= 'Z') {

				int y = c - 65;

				y = (y - k + 26) % 26;

				result += (char) (y + 65);
			} else if (c >= 'a' && c <= 'z') {

				int y = c - 97;

				y = (y - k + 26) % 26;

				result += (char) (y + 97);
			} else {
				result += c;
			}
		}

		return result;
	}
	@Override
	public byte[] encrypt(byte[] data) throws Exception {

		int k = Integer.parseInt(new String(key.getEncoded()));

		byte[] result = new byte[data.length];

		for (int i = 0; i < data.length; i++) {

			int x = data[i] & 0xFF;

			x = (x + k) % 256;

			result[i] = (byte) x;
		}

		return result;
	}

	@Override
	public byte[] decryptByte(byte[] data) throws Exception {

		int k = Integer.parseInt(new String(key.getEncoded()));

		byte[] result = new byte[data.length];

		for (int i = 0; i < data.length; i++) {

			int y = data[i] & 0xFF;

			y = (y - k + 256) % 256;

			result[i] = (byte) y;
		}

		return result;
	}
}