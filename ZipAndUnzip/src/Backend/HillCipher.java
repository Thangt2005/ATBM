package Backend;

import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HillCipher extends AbsCipher {
	private int[][] Key = new int[2][2];

	@Override
	public SecretKey genKey() throws Exception {
		Random random = new Random();

		while (true) {
			int a = random.nextInt(26);
			int b = random.nextInt(26);
			int c = random.nextInt(26);
			int d = random.nextInt(26);

			int multiMatrix = a * d - b * c;// công thức nhân 2 ma trận

			if (gcd(multiMatrix, 26) == 1) {
				Key[0][0] = a;
				Key[0][1] = b;
				Key[1][0] = c;
				Key[1][1] = d;

				String keyStr = a + "," + b + "," + c + "," + d;
				return new SecretKeySpec(keyStr.getBytes(), "Hill");
			}
		}
	}


	@Override
	public void loadKey(String key) {
	    try {
	        // Tách chuỗi 
	        String[] parts = key.trim().split("[\\s,]+");
	        
	        if (parts.length == 4) {
	            int a = Integer.parseInt(parts[0]);
	            int b = Integer.parseInt(parts[1]);
	            int c = Integer.parseInt(parts[2]);
	            int d = Integer.parseInt(parts[3]);
	            
	            //Gán giá trị vào mảng Key của class
	            this.Key[0][0] = a;
	            this.Key[0][1] = b;
	            this.Key[1][0] = c;
	            this.Key[1][1] = d;
	        } else {
	            throw new IllegalArgumentException("Key Hill phải có đúng 4 số!");//ma trận chọn làm key 2*2
	        }
	    } catch (NumberFormatException e) {
	        throw new NumberFormatException("Định dạng số trong Key không hợp lệ!");
	    }
	}

	@Override
	public byte[] encrypt(String text) throws Exception {

	    if (text.length() % 2 != 0) {
	        text = text + "X";
	    }

	    StringBuilder result = new StringBuilder();

	    for (int i = 0; i < text.length(); i = i + 2) {

	        char char1 = text.charAt(i);
	        char char2 = text.charAt(i + 1);

	        int x = char1 - 'A';
	        int y = char2 - 'A';

	        // Nhân ma trận
	        int value1 = Key[0][0] * x + Key[0][1] * y;
	        int value2 = Key[1][0] * x + Key[1][1] * y;

	        int c1 = value1 % 26;
	        int c2 = value2 % 26;

	        char encryptedChar1 = (char) (c1 + 'A');
	        char encryptedChar2 = (char) (c2 + 'A');

	        result.append(encryptedChar1);
	        result.append(encryptedChar2);
	    }

	    return result.toString().getBytes();
	}

	@Override
	public String decrypt(byte[] cipherText) throws Exception {

	    String text = new String(cipherText);

	    //tính định thức
	    int a = Key[0][0];
	    int b = Key[0][1];
	    int c = Key[1][0];
	    int d = Key[1][1];

	    int det = a * d - b * c;
	    det = mod(det, 26);

	    //tìm nghịch đảo của det
	    int detInverse = modInverse(det, 26);

	    //tính ma trận nghịch đảo
	    int[][] inverseMatrix = new int[2][2];

	    inverseMatrix[0][0] = mod(d * detInverse, 26);
	    inverseMatrix[0][1] = mod(-b * detInverse, 26);
	    inverseMatrix[1][0] = mod(-c * detInverse, 26);
	    inverseMatrix[1][1] = mod(a * detInverse, 26);

	    StringBuilder result = new StringBuilder();

	    //giải mã từng cặp
	    for (int i = 0; i < text.length(); i = i + 2) {

	        char char1 = text.charAt(i);
	        char char2 = text.charAt(i + 1);

	        int x = char1 - 'A';
	        int y = char2 - 'A';

	        int value1 = inverseMatrix[0][0] * x + inverseMatrix[0][1] * y;
	        int value2 = inverseMatrix[1][0] * x + inverseMatrix[1][1] * y;

	        int p1 = value1 % 26;
	        int p2 = value2 % 26;

	        char decryptedChar1 = (char) (p1 + 'A');
	        char decryptedChar2 = (char) (p2 + 'A');

	        result.append(decryptedChar1);
	        result.append(decryptedChar2);
	    }

	    return result.toString();
	}
	//ucln phụ kiểm tra tính hợp lệ của key
	private int gcd(int a, int b) {
	    if (b == 0) {
	        return Math.abs(a);
	    }
	    return gcd(b, a % b);
	}

	private int mod(int a, int m) {
	    int result = a % m;
	    if (result < 0) {
	        result = result + m;
	    }
	    return result;
	}

	private int modInverse(int a, int m) {

	    a = mod(a, m);

	    for (int x = 1; x < m; x++) {
	        int value = (a * x) % m;

	        if (value == 1) {
	            return x;
	        }
	    }

	    throw new RuntimeException("Không có nghịch đảo");
	}
	@Override
	public byte[] encrypt(byte[] data) throws Exception {

		if (data.length % 2 != 0) {

			byte[] temp = new byte[data.length + 1];

			System.arraycopy(data, 0, temp, 0, data.length);

			temp[data.length] = 0;

			data = temp;
		}

		byte[] result = new byte[data.length];

		for (int i = 0; i < data.length; i += 2) {

			int x = data[i] & 0xFF;
			int y = data[i + 1] & 0xFF;

			int value1 = Key[0][0] * x + Key[0][1] * y;
			int value2 = Key[1][0] * x + Key[1][1] * y;

			result[i] = (byte) (value1 % 256);
			result[i + 1] = (byte) (value2 % 256);
		}

		return result;
	}

	@Override
	public byte[] decryptByte(byte[] data) throws Exception {

		int a = Key[0][0];
		int b = Key[0][1];
		int c = Key[1][0];
		int d = Key[1][1];

		int det = a * d - b * c;

		det = mod(det, 256);

		int detInverse = modInverse(det, 256);

		int[][] inverseMatrix = new int[2][2];

		inverseMatrix[0][0] = mod(d * detInverse, 256);
		inverseMatrix[0][1] = mod(-b * detInverse, 256);
		inverseMatrix[1][0] = mod(-c * detInverse, 256);
		inverseMatrix[1][1] = mod(a * detInverse, 256);

		byte[] result = new byte[data.length];

		for (int i = 0; i < data.length; i += 2) {

			int x = data[i] & 0xFF;
			int y = data[i + 1] & 0xFF;

			int value1 =
				inverseMatrix[0][0] * x +
				inverseMatrix[0][1] * y;

			int value2 =
				inverseMatrix[1][0] * x +
				inverseMatrix[1][1] * y;

			result[i] = (byte) mod(value1, 256);
			result[i + 1] = (byte) mod(value2, 256);
		}

		return result;
	}
}
