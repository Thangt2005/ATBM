package Backend;

public class CRC32Hash {

	public String checkSum(String input) {

		long crc = 0xFFFFFFFF;

		byte[] data = input.getBytes();

		for (int i = 0; i < data.length; i++) {

			crc ^= data[i];

			for (int j = 0; j < 8; j++) {

				if ((crc & 1) == 1) {

					crc = (crc >>> 1) ^ 0xEDB88320;

				} else {

					crc = crc >>> 1;
				}
			}
		}

		crc = crc ^ 0xFFFFFFFF;

		return Integer.toHexString((int) crc);
	}

	public String hash(String file) throws Exception {

		java.io.FileInputStream fis = new java.io.FileInputStream(file);

		int crc = 0xFFFFFFFF;

		int data;

		while ((data = fis.read()) != -1) {

			crc ^= data;

			for (int j = 0; j < 8; j++) {

				if ((crc & 1) == 1) {

					crc = (crc >>> 1) ^ 0xEDB88320;

				} else {

					crc = crc >>> 1;
				}
			}
		}

		fis.close();

		crc = crc ^ 0xFFFFFFFF;

		return Integer.toHexString(crc);
	}

	public static void main(String[] args) throws Exception {

		String s = "123";

		CRC32Hash crc32 = new CRC32Hash();

		System.out.println(crc32.checkSum(s));

		System.out.println(crc32.hash("C:\\Users\\Public\\Desktop\\IntelliJ IDEA 2026.1.lnk"));
	}
}