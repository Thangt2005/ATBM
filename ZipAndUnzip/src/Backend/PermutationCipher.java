package Backend;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PermutationCipher extends AbsCipher {
private String keyStr;
private int doDaiKhoa;
	@Override
	public SecretKey genKey() throws Exception {
		this.keyStr = "312";//khóa test
		this.doDaiKhoa = 3;
		return new SecretKeySpec(keyStr.getBytes(), "permutation");
	}

	@Override
	public void loadKey(String key) throws Exception {
		this.keyStr = key;
		this.doDaiKhoa = key.length();
		this.key = new SecretKeySpec(key.getBytes(), "permutation");
		
	}
	

	@Override
	public byte[] encrypt(String text) throws Exception {
		int soCot = doDaiKhoa;
		int soHang = (text.length()+ soCot-1)/soCot;
		
		char[][] matrix = new char[soHang][soCot];
		int pointer = 0;
		
		for (int i = 0; i < soHang; i++) {
			for (int j = 0; j < soCot; j++) {
				if(pointer < text.length()) {
					matrix[i][j] = text.charAt(pointer++);
				}else {
					matrix[i][j] = ' ';
				}
			}
		}
		// Đọc ma trận theo thứ tự hoán vị của khóa
        String ketQua = "";
        for (int i = 0; i < doDaiKhoa; i++) {
            // Chuyển ký tự số của khóa thành chỉ số cột (0, 1, 2...)
            int chiSoCot = Character.getNumericValue(keyStr.charAt(i)) - 1;
            for (int r = 0; r < soHang; r++) {
                ketQua += matrix[r][chiSoCot];
            }
        }
        return ketQua.getBytes();
    }
	@Override
	public String decrypt(byte[] cipherText) throws Exception {
		String data = new String(cipherText);
        int soCot = doDaiKhoa;
        int soHang = data.length() / soCot;
        
        char[][] maTran = new char[soHang][soCot];
        int pointer = 0;

        // Đổ bản mã vào ma trận theo đúng cột đã hoán vị
        for (int i = 0; i < doDaiKhoa; i++) {
            int chiSoCot = Character.getNumericValue(keyStr.charAt(i)) - 1;
            for (int r = 0; r < soHang; r++) {
                if (pointer < data.length()) {
                    maTran[r][chiSoCot] = data.charAt(pointer++);
                }
            }
        }

        // Đọc lại theo hàng để lấy văn bản gốc
        String ketQua = "";
        for (int i = 0; i < soHang; i++) {
            for (int j = 0; j < soCot; j++) {
                ketQua += maTran[i][j];
            }
        }
        return ketQua.trim(); // Xóa khoảng trắng thừa
    }
	@Override
	public byte[] encrypt(byte[] data) throws Exception {

		int soCot = doDaiKhoa;

		int soHang = (data.length + soCot - 1) / soCot;

		byte[][] matrix = new byte[soHang][soCot];

		int pointer = 0;

		for (int i = 0; i < soHang; i++) {

			for (int j = 0; j < soCot; j++) {

				if (pointer < data.length) {

					matrix[i][j] = data[pointer++];

				} else {

					matrix[i][j] = 0;
				}
			}
		}

		byte[] result = new byte[soHang * soCot];

		pointer = 0;

		for (int i = 0; i < doDaiKhoa; i++) {

			int chiSoCot =
				Character.getNumericValue(keyStr.charAt(i)) - 1;

			for (int r = 0; r < soHang; r++) {

				result[pointer++] = matrix[r][chiSoCot];
			}
		}

		return result;
	}

	@Override
	public byte[] decryptByte(byte[] data) throws Exception {

		int soCot = doDaiKhoa;

		int soHang = data.length / soCot;

		byte[][] matrix = new byte[soHang][soCot];

		int pointer = 0;

		for (int i = 0; i < doDaiKhoa; i++) {

			int chiSoCot =
				Character.getNumericValue(keyStr.charAt(i)) - 1;

			for (int r = 0; r < soHang; r++) {

				matrix[r][chiSoCot] = data[pointer++];
			}
		}

		byte[] result = new byte[soHang * soCot];

		pointer = 0;

		for (int i = 0; i < soHang; i++) {

			for (int j = 0; j < soCot; j++) {

				result[pointer++] = matrix[i][j];
			}
		}

		return result;
	}
}