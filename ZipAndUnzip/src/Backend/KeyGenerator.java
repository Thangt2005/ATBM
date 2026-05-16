package Backend;

import java.util.Random;

public class KeyGenerator {

	public static String createKey(String algo) {

		Random random = new Random();

		if (algo.equals("Hill")) {

			return "3 3 2 5";

		}

		else if (algo.equals("Hoán vị")) {

			return "312";
		}

		else if (algo.equals("RSA")) {

			return "RSA tự tạo khóa khi Encrypt";
		}

		else if (algo.equals("Affine")) {

			int a;

			do {

				a = random.nextInt(255);

			} while (a % 2 == 0);

			int b = random.nextInt(255);

			return a + "," + b;
		}

		else if (algo.equals("Vigenere")) {

			String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

			String key = "";

			for (int i = 0; i < 5; i++) {

				int index = random.nextInt(s.length());

				key += s.charAt(index);
			}

			return key;
		}

		else if (algo.equals("AES") || algo.equals("DES")) {

			String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

			String key = "";

			for (int i = 0; i < 16; i++) {

				int index = random.nextInt(chars.length());

				key += chars.charAt(index);
			}

			return key;
		}

		return "";
	}
}