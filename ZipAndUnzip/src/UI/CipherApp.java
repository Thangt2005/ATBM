package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Backend.*;

public class CipherApp {

	JFrame jframe = new JFrame("Cipher App ATBM");

	AbsCipher cipher = new ShiftCipher();
	RSA rsa = new RSA();
	boolean daCoKhoaRSA = false;

	String[] dsTruyenThong = { "Dịch chuyển", "Thay thế", "Hill", "Hoán vị", "Affine", "Vigenere" };
	String[] dsDoiXung = { "AES", "DES", "Blowfish", "RC4", "Twofish" };
	String[] dsBatDoiXung = { "RSA" };
	String[] dsHamBam = { "MD5", "MD2", "SHA-1", "SHA-256", "SHA-512", "CRC-32" };

	JComboBox<String> cbLoai, cbThuat, cbMode, cbPadding;
	JLabel lblMode = new JLabel("Mode :");
	JLabel lblPadding = new JLabel("Padding :");
	JTextArea txtInput = new JTextArea();
	JTextArea txtOutput = new JTextArea();
	JTextField txtKey = new JTextField("3");
	JButton btnMaHoa = new JButton("Encrypt");
	JButton btnGiaiMa = new JButton("Decrypt");
	JButton btnMaHoaFile = new JButton("Encrypt File");
	JButton btnGiaiMaFile = new JButton("Decrypt File");
	JButton btnTaoKhoa = new JButton("Create Key");
	JButton btnNhapKhoa = new JButton("Import Key");
	JButton btnLuuKhoa = new JButton("Save Key");
//hàm khởi tạo(constructor)
	public CipherApp() {
		xayDungGiaoDien();
		ganSuKien();

		jframe.setSize(800, 600);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);
	}
//chia nhỏ hàm chính
	private void xayDungGiaoDien() {
		jframe.setLayout(new BorderLayout(5, 5));

		// --- Thanh menu trên ---
		JPanel panelMenu = new JPanel(new BorderLayout());
		JPanel panelOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));

		cbLoai = new JComboBox<>(new String[] { "Truyền thống", "Đối xứng", "Bất đối xứng", "Hàm băm" });
		cbThuat = new JComboBox<>(dsTruyenThong);
		cbMode = new JComboBox<>(new String[] { "ECB", "CBC" });
		cbPadding = new JComboBox<>(new String[] { "PKCS5Padding", "NoPadding" });

		panelOptions.add(new JLabel("Type:"));
		panelOptions.add(cbLoai);
		panelOptions.add(new JLabel("Algorithm:"));
		panelOptions.add(cbThuat);
		panelOptions.add(lblMode);
		panelOptions.add(cbMode);
		panelOptions.add(lblPadding);
		panelOptions.add(cbPadding);

		panelMenu.add(panelOptions, BorderLayout.CENTER);
		jframe.add(panelMenu, BorderLayout.NORTH);

		// --- Panel chính 2x2 ---
		JPanel panelChinh = new JPanel(new GridLayout(2, 2, 10, 10));

		// Ô input (trái trên)
		JPanel oInput = new JPanel(new BorderLayout());
		oInput.setBorder(BorderFactory.createTitledBorder("Input (plainText/cipherText)"));
		oInput.add(new JScrollPane(txtInput), BorderLayout.CENTER);

		// Ô output (phải trên)
		JPanel oOutput = new JPanel(new BorderLayout());
		oOutput.setBorder(BorderFactory.createTitledBorder("Output (result)"));
		txtOutput.setEditable(false);
		txtOutput.setBackground(new Color(245, 245, 245));
		oOutput.add(new JScrollPane(txtOutput), BorderLayout.CENTER);

		// Ô nút bấm (trái dưới)
		JPanel oNut = new JPanel(new GridLayout(2, 2, 5, 5));
		oNut.setBorder(BorderFactory.createTitledBorder("Actions"));
		oNut.add(btnMaHoa);
		oNut.add(btnGiaiMa);
		oNut.add(btnMaHoaFile);
		oNut.add(btnGiaiMaFile);

		// Ô cấu hình (phải dưới)
		JPanel oCauHinh = new JPanel(new GridLayout(4, 1, 5, 5));
		oCauHinh.setBorder(BorderFactory.createTitledBorder("Configurations"));
		txtKey.setBorder(BorderFactory.createTitledBorder("Key (Có thể tự nhập tay)"));
		oCauHinh.add(txtKey);
		oCauHinh.add(btnTaoKhoa);
		oCauHinh.add(btnNhapKhoa);
		oCauHinh.add(btnLuuKhoa);

		panelChinh.add(oInput);
		panelChinh.add(oOutput);
		panelChinh.add(oNut);
		panelChinh.add(oCauHinh);

		jframe.add(panelChinh, BorderLayout.CENTER);
	}

	private void ganSuKien() {
		cbLoai.addActionListener(e -> capNhatDanhSachThuatToan());
		cbLoai.setSelectedIndex(0);
		capNhatDanhSachThuatToan();

		btnMaHoa.addActionListener(e -> xuLyVanBan(true));
		btnGiaiMa.addActionListener(e -> xuLyVanBan(false));
		btnMaHoaFile.addActionListener(e -> xuLyFile(true));
		btnGiaiMaFile.addActionListener(e -> xuLyFile(false));

		btnTaoKhoa.addActionListener(e -> taoKhoaNgauNhien());
		btnNhapKhoa.addActionListener(e -> nhapKhoaTuFile());
		btnLuuKhoa.addActionListener(e -> luuKhoa());
	}

	// Cập nhật danh sách thuật toán khi đổi loại
	private void capNhatDanhSachThuatToan() {
		cbThuat.removeAllItems();
		String loai = (String) cbLoai.getSelectedItem();

		boolean isDoiXung = loai.equals("Đối xứng");
		cbMode.setVisible(isDoiXung);
		lblMode.setVisible(isDoiXung);
		cbPadding.setVisible(isDoiXung);
		lblPadding.setVisible(isDoiXung);

		String[] dsHienTai = dsTruyenThong;
		if (loai.equals("Đối xứng"))
			dsHienTai = dsDoiXung;
		else if (loai.equals("Bất đối xứng"))
			dsHienTai = dsBatDoiXung;
		else if (loai.equals("Hàm băm"))
			dsHienTai = dsHamBam;

		for (String ten : dsHienTai)
			cbThuat.addItem(ten);
	}

	private void xuLyVanBan(boolean laMaHoa) {
		String thuat = (String) cbThuat.getSelectedItem();
		String loai = (String) cbLoai.getSelectedItem();
		String vanBan = txtInput.getText().trim();
		String khoa = txtKey.getText().trim();

		if (vanBan.isEmpty()) {
			txtOutput.setText("Vui lòng nhập văn bản!");
			return;
		}

		// Hàm băm chỉ mã hóa 1 chiều
		boolean laHamBam = thuat.startsWith("SHA") || thuat.startsWith("MD") || thuat.equals("CRC-32");
		if (laHamBam) {
			if (!laMaHoa) {
				txtOutput.setText("Hàm băm không thể giải mã!");
				return;
			}
			txtOutput.setText(HashManager.hash(thuat, vanBan));
			return;
		}

		if (!thuat.equals("RSA") && khoa.isEmpty()) {
			txtOutput.setText("Vui lòng nhập Key!");
			return;
		}

		if (thuat.equals("RC4") && khoa.length() < 5) {
			txtOutput.setText("Lỗi: Key RC4 phải từ 5 ký tự trở lên!");
			return;
		}

		try {
			if (thuat.equals("RSA")) {

				if (!daCoKhoaRSA) {
					rsa.genKey();
					daCoKhoaRSA = true;
				}

				if (laMaHoa) {

					// encrypt -> trả Base64
					String cipherText = rsa.encryptBase64(vanBan);
					txtOutput.setText(cipherText);

				} else {

					try {

						String sach = vanBan.replaceAll("\\s+", "");

						String plain = rsa.decrypt(sach);

						txtOutput.setText(plain);

					} catch (Exception ex) {
						txtOutput.setText("CipherText RSA không đúng Base64!");
					}
				}

				return;
			}

			cipher = CipherFactory.getCipher(thuat);
			if (cipher == null) {
				txtOutput.setText("Chưa hỗ trợ thuật toán này!");
				return;
			}
			cipher.loadKey(khoa);

			if (laMaHoa) {
				// chọn loại mã hóa
				if (loai.equals("Truyền thống")) {
					// Truyền thống: encrypt trả về text bình thường
					txtOutput.setText(new String(cipher.encrypt(vanBan)));
				} else {
					// Đối xứng: dùng UTF-8 rồi bọc Base64 để tránh lỗi ký tự đặc biệt
					byte[] ketQua = cipher.encrypt(vanBan.getBytes("UTF-8"));
					txtOutput.setText(java.util.Base64.getEncoder().encodeToString(ketQua));
				}
			} else {
				// chọn loại giải mã phù hợp
				if (loai.equals("Truyền thống")) {
					// Truyền thống: decrypt từ bytes của text
					txtOutput.setText(cipher.decrypt(vanBan.getBytes("UTF-8")));
				} else {
					// Đối xứng: decode Base64 trước khi decrypt
					String sach = vanBan.replaceAll("\\s+", "");
					byte[] decoded = java.util.Base64.getDecoder().decode(sach);
					txtOutput.setText(cipher.decrypt(decoded));
				}
			}

		} catch (IllegalArgumentException ex) {
			txtOutput.setText("Lỗi: Dữ liệu không đúng định dạng Base64!");
		} catch (Exception ex) {
			txtOutput.setText("Lỗi: " + ex.getMessage());
		}
	}

	private void xuLyFile(boolean laMaHoa) {
		String thuat = (String) cbThuat.getSelectedItem();
		String loai = (String) cbLoai.getSelectedItem();
		String khoa = txtKey.getText().trim();
		if (loai.equals("Truyền thống")) {
			txtOutput.setText("Thuật toán truyền thống chỉ hỗ trợ text, không hỗ trợ file!");
			return;
		}
		if (!thuat.equals("RSA") && khoa.isEmpty()) {
			txtOutput.setText("Vui lòng nhập Key trước khi thao tác file!");
			return;
		}

		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(jframe) != JFileChooser.APPROVE_OPTION)
			return;

		try {
			File fileDauVao = fc.getSelectedFile();
			byte[] duLieu = Files.readAllBytes(fileDauVao.toPath());
			byte[] ketQua;
			String duongDanRa = fileDauVao.getAbsolutePath();

			if (thuat.equals("RSA")) {
				if (laMaHoa) {
					if (!daCoKhoaRSA) {
						rsa.genKey();
						daCoKhoaRSA = true;
					}
					ketQua = rsa.encryptBase64(new String(duLieu, "UTF-8")).getBytes("UTF-8");
					duongDanRa += ".enc";
				} else {
					ketQua = rsa.decrypt(new String(duLieu, "UTF-8")).getBytes("UTF-8");
					duongDanRa = tenFileGiaiMa(fileDauVao);
				}
			} else {
				cipher = CipherFactory.getCipher(thuat);
				if (cipher == null) {
					txtOutput.setText("Thuật toán không hỗ trợ file!");
					return;
				}
				cipher.loadKey(khoa);

				if (laMaHoa) {
					ketQua = cipher.encrypt(duLieu);
					duongDanRa += ".enc";
				} else {
					ketQua = cipher.decryptByte(duLieu);
					duongDanRa = tenFileGiaiMa(fileDauVao);
				}
			}

			Files.write(new File(duongDanRa).toPath(), ketQua);
			txtOutput.setText((laMaHoa ? "Encrypt" : "Decrypt") + " file thành công!\nLưu tại: " + duongDanRa);

		} catch (Exception ex) {
			txtOutput.setText("Lỗi xử lý file: " + ex.getMessage());
		}
	}

	// Tạo tên file output khi giải mã
	private String tenFileGiaiMa(File file) {
		String ten = file.getName();
		if (ten.endsWith(".enc"))
			ten = ten.substring(0, ten.length() - 4);
		return new File(file.getParent(), "decrypted_" + ten).getAbsolutePath();
	}

	private void taoKhoaNgauNhien() {
		String thuat = (String) cbThuat.getSelectedItem();
		Random rd = new Random();

		try {
			switch (thuat) {
			case "Dịch chuyển":
				txtKey.setText(String.valueOf(rd.nextInt(25) + 1));
				break;

			case "Hill":
				int[] m;
				do {
					m = new int[] { rd.nextInt(26), rd.nextInt(26), rd.nextInt(26), rd.nextInt(26) };
				} while (!isInvertibleMod26(m));
				txtKey.setText(m[0] + " " + m[1] + " " + m[2] + " " + m[3]);
				break;

			case "Hoán vị":
				List<Integer> ds = new ArrayList<>();
				for (int i = 1; i <= 5; i++)
					ds.add(i);
				Collections.shuffle(ds, rd);
				StringBuilder sb = new StringBuilder();
				for (int i : ds)
					sb.append(i);
				txtKey.setText(sb.toString());
				break;

			case "Affine":
				int[] hopLe = { 1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25 };
				txtKey.setText(hopLe[rd.nextInt(hopLe.length)] + "," + rd.nextInt(26));
				break;

			case "Vigenere":
				StringBuilder khoaVig = new StringBuilder();
				for (int i = 0; i < 5; i++)
					khoaVig.append((char) ('A' + rd.nextInt(26)));
				txtKey.setText(khoaVig.toString());
				break;

			case "Thay thế":
				List<Character> kyTu = new ArrayList<>();
				for (char c = 'A'; c <= 'Z'; c++)
					kyTu.add(c);
				Collections.shuffle(kyTu, rd);
				StringBuilder khoaSub = new StringBuilder();
				for (char c : kyTu)
					khoaSub.append(c);
				txtKey.setText(khoaSub.toString());
				break;

			case "DES":
				txtKey.setText(chuoiNgauNhien(rd, 8));
				break;

			case "AES":
			case "Blowfish":
			case "RC4":
			case "Twofish":
				txtKey.setText(chuoiNgauNhien(rd, 16));
				break;

			case "RSA":
				txtKey.setText("RSA tự sinh khóa khi mã hóa");
				break;

			default:
				txtKey.setText(chuoiNgauNhien(rd, 10));
			}
		} catch (Exception ex) {
			txtOutput.setText("Lỗi tạo khóa: " + ex.getMessage());
		}
	}

	private void nhapKhoaTuFile() {
		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(jframe) != JFileChooser.APPROVE_OPTION)
			return;
		try {
			txtKey.setText(Files.readString(fc.getSelectedFile().toPath()).trim());
			txtOutput.setText("Đã import key!");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(jframe, "Lỗi đọc file: " + ex.getMessage());
		}
	}

	private void luuKhoa() {
		String khoa = txtKey.getText().trim();
		String thuat = (String) cbThuat.getSelectedItem();

		if (khoa.isEmpty()) {
			txtOutput.setText("Chưa nhập key!");
			return;
		}

		if (thuat.equals("Thay thế") && khoa.length() != 26) {
			txtOutput.setText("Lỗi: Key Thay thế phải đúng 26 ký tự!");
			return;
		}
		if (thuat.equals("Dịch chuyển")) {
			try {
				Integer.parseInt(khoa);
			} catch (NumberFormatException e) {
				txtOutput.setText("Lỗi: Key Dịch chuyển phải là số!");
				return;
			}
		}
		if (thuat.equals("Affine")) {
			String[] p = khoa.split(",");
			if (p.length != 2) {
				txtOutput.setText("Lỗi: Key Affine phải dạng a,b");
				return;
			}
			try {
				int a = Integer.parseInt(p[0].trim());
				if (gcd(a, 26) != 1) {
					txtOutput.setText("Lỗi: a phải coprime với 26!");
					return;
				}
				Integer.parseInt(p[1].trim());
			} catch (NumberFormatException e) {
				txtOutput.setText("Lỗi: Key Affine phải là số nguyên!");
				return;
			}
		}
		if (thuat.equals("Hill")) {
			String[] p = khoa.split(" ");
			if (p.length != 4) {
				txtOutput.setText("Lỗi: Key Hill cần đúng 4 số!");
				return;
			}
			try {
				int a = Integer.parseInt(p[0]), b = Integer.parseInt(p[1]);
				int c = Integer.parseInt(p[2]), d = Integer.parseInt(p[3]);
				int det = (((a * d - b * c) % 26) + 26) % 26;
				if (gcd(det, 26) != 1) {
					txtOutput.setText("Lỗi: Ma trận Hill không khả nghịch mod 26!");
					return;
				}
			} catch (NumberFormatException e) {
				txtOutput.setText("Lỗi: Key Hill phải là 4 số nguyên!");
				return;
			}
		}

		txtOutput.setText("Đã lưu key: " + khoa);
	}

	private String chuoiNgauNhien(Random rd, int length) {
		String kyTu = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
			sb.append(kyTu.charAt(rd.nextInt(kyTu.length())));
		return sb.toString();
	}

	private boolean isInvertibleMod26(int[] m) {
		int det = (m[0] * m[3] - m[1] * m[2]) % 26;
		if (det < 0)
			det += 26;
		return gcd(det, 26) == 1;
	}

	private int gcd(int a, int b) {
		return b == 0 ? Math.abs(a) : gcd(b, a % b);
	}
}