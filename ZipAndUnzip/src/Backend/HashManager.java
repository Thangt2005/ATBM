package Backend;

import java.io.File;

public class HashManager {

	public static String hash(String algo, String input) {

		try {

			File file = new File(input.trim());

			switch (algo) {

			case "MD5":

				MD5 md5 = new MD5();

				if (file.exists() && file.isFile()) {

					return md5.hash(input.trim());

				} else {

					return md5.checkSum(input);
				}

			case "MD2":

				MD2 md2 = new MD2();

				if (file.exists() && file.isFile()) {

					return md2.hashMD2(input.trim());

				} else {

					return md2.checkSumMD2(input);
				}

			case "SHA-1":

				SHA1 sha1 = new SHA1();

				if (file.exists() && file.isFile()) {

					return sha1.hash(input.trim());

				} else {

					return sha1.checkSum(input);
				}

			case "SHA-256":

				SHA256 sha256 = new SHA256();

				if (file.exists() && file.isFile()) {

					return sha256.hash(input.trim());

				} else {

					return sha256.checkSum(input);
				}

			case "SHA-512":

				SHA512 sha512 = new SHA512();

				if (file.exists() && file.isFile()) {

					return sha512.hash(input.trim());

				} else {

					return sha512.checkSum(input);
				}

			case "CRC-32":

				CRC32Hash crc32 = new CRC32Hash();

				if (file.exists() && file.isFile()) {

					return crc32.hash(input.trim());

				} else {

					return crc32.checkSum(input);
				}
			}

		} catch (Exception ex) {

			return "Lỗi Hash!";
		}

		return "Không hỗ trợ Hash!";
	}
}