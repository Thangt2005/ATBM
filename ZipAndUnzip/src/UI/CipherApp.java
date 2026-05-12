package UI;

import java.awt.BorderLayout;
import Backend.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CipherApp {
	JFrame jframe = new JFrame("Zip and Unzip");
	AbsCipher ab = new ShiftCipher();
	RSA rsa = new RSA();
	boolean rsaReady = false;
	boolean keySave = false;

//vùng panel chọn các giải thuật mã hóa
	JPanel pnlMenu = new JPanel(new BorderLayout());

// Panel chính chia 2x2 cho 4 vùng chức năng
	JPanel pnlCenter = new JPanel(new GridLayout(2, 2, 10, 10));
//các thành phần trong cửa sổ 
	JTextArea jtextInput = new JTextArea();
	JTextArea jtextOutput = new JTextArea();
	JTextField txtKey = new JTextField();
	JLabel mode = new JLabel("Mode :");
	JLabel padding = new JLabel("Padding :");
	JPanel Config = new JPanel((new BorderLayout()));
	JPanel buttons = new JPanel((new GridLayout(2, 2, 5, 5)));
	String[] traditional = { "Dịch chuyển", "Thay thế", "Hill", "Hoán vị", "Affine", "Vigenere" };
	String[] symmetric = { "AES", "DES" };
	String[] asymmetric = { "RSA" };
	String hashing[] = { "MD5" };

	public CipherApp() {
		txtKey.setText("3");
		// phần khung chính
		jframe.setSize(800, 600);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setLocationRelativeTo(null);
		jframe.setLayout(new BorderLayout(5, 5));

		// phần tab option
		JPanel pnlOptions = new JPanel();
		pnlOptions.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
		// type
		JComboBox<String> cbType = new JComboBox<>(
				new String[] { "Truyền thống", "Đối xứng", "Bất đối xứng", "Hàm băm" });
		// algorithms
		JComboBox<String> cbAlgorithm = new JComboBox<>(traditional);

		// Mode
		JComboBox<String> cbMode = new JComboBox<>();
		cbMode.addItem("ECB");
		cbMode.addItem("CBC");

		
		// Padding
		JComboBox<String> cbPadding = new JComboBox<>();
		cbPadding.addItem("PKCS5Padding");
		cbPadding.addItem("NoPadding");
		
		pnlOptions.add(new JLabel("Type:"));
		pnlOptions.add(cbType);

		pnlOptions.add(new JLabel("Algorithm:"));
		pnlOptions.add(cbAlgorithm);

		pnlOptions.add(mode);
		pnlOptions.add(cbMode);

		pnlOptions.add(padding);
		pnlOptions.add(cbPadding);

		pnlMenu.add(pnlOptions, BorderLayout.CENTER);
		cbType.addActionListener(e -> {

			cbAlgorithm.removeAllItems();

			String type = (String) cbType.getSelectedItem();

			// truyền thống
			if (type.equals("Truyền thống")) {

				for (String s : traditional) {
					cbAlgorithm.addItem(s);
				}

				// ẩn mode + padding
				cbMode.setVisible(false);
				cbPadding.setVisible(false);

				mode.setVisible(false);
				padding.setVisible(false);

			}

			// đối xứng
			else if (type.equals("Đối xứng")) {

				for (String s : symmetric) {
					cbAlgorithm.addItem(s);
				}

				cbMode.setVisible(true);
				cbPadding.setVisible(true);

				mode.setVisible(true);
				padding.setVisible(true);
			}

			// bất đối xứng
			else if (type.equals("Bất đối xứng")) {

				for (String s : asymmetric) {
					cbAlgorithm.addItem(s);
				}

				// RSA không cần mode padding kiểu AES
				cbMode.setVisible(false);
				cbPadding.setVisible(false);

				mode.setVisible(false);
				padding.setVisible(false);
			}

			// hash
			else if (type.equals("Hàm băm")) {

				for (String s : hashing) {
					cbAlgorithm.addItem(s);
				}

				cbMode.setVisible(false);
				cbPadding.setVisible(false);

				mode.setVisible(false);
				padding.setVisible(false);
			}
		});

		// load lần đầu
		cbType.setSelectedIndex(0);
		cbType.getActionListeners()[0].actionPerformed(null);

		// vùng chính ở giữa
		// input(trái-trên)
		JPanel p1 = new JPanel(new BorderLayout());
		p1.setBorder(BorderFactory.createTitledBorder("Input(plainText/cipherText"));
		p1.add(new JScrollPane(jtextInput), BorderLayout.CENTER);

		// output(phải-trên)
		JPanel p2 = new JPanel(new BorderLayout());
		p2.setBorder(BorderFactory.createTitledBorder("Output(result"));
		jtextOutput.setEditable(false);// không cho người dùng chỉnh sửa nội dung trong output
		jtextOutput.setBackground(new Color(245, 245, 245));
		p2.add(new JScrollPane(jtextOutput), BorderLayout.CENTER);

		// button(trái-dưới)
		buttons.setBorder(BorderFactory.createTitledBorder("Button"));
		JButton btnEncrypt = new JButton("Encrypt");
		JButton btnDecrypt = new JButton("Decrypt");

		btnEncrypt.addActionListener(e -> {
			String algo = (String) cbAlgorithm.getSelectedItem();
			String input = jtextInput.getText();

			if (algo.equals("MD5")) {
				try {
					MD5 md5 = new MD5();
					File file = new File(input.trim());

					// input là file hash file ngược lại hash chuỗi
					if (file.exists() && file.isFile()) {
						jtextOutput.setText(md5.hash(input.trim()));
					} else {
						jtextOutput.setText(md5.checkSum(input));
					}
				} catch (Exception ex) {
					jtextOutput.setText("Lỗi xử lý Hash: " + ex.getMessage());
				}
				return;
			}

			if (!keySave) {
				jtextOutput.setText("Vui lòng Save Key trước khi Encrypt!");
				return;
			}

			String result = "";
			try {
				if (algo.equals("Dịch chuyển")) {
					ab = new ShiftCipher();
				} else if (algo.equals("Hill")) {
					String keyText = txtKey.getText().trim();
					String[] parts = keyText.split(" ");
					if (parts.length != 4) {
						jtextOutput.setText("Key Hill phải gồm 4 số! Ví dụ: 3 3 2 5");
						return;
					}
					int a = Integer.parseInt(parts[0]);
					int b = Integer.parseInt(parts[1]);
					int c = Integer.parseInt(parts[2]);
					int d = Integer.parseInt(parts[3]);
					int det = (((a * d - b * c) % 26) + 26) % 26;
					if (gcd(det, 26) != 1) {
						jtextOutput.setText("Key Hill không hợp lệ (không có nghịch đảo)!");
						return;
					}
					ab = new HillCipher();
				} else if (algo.equals("Hoán vị")) {

					ab = new PermutationCipher();

					if (txtKey.getText().trim().isEmpty()) {
						jtextOutput.setText("Vui lòng nhập key hoán vị!");
						return;
					}

				} else if (algo.equals("AES")) {

					ab = new AESCipher();

				} else if (algo.equals("DES")) {

					ab = new DESCipher();

				} else if (algo.equals("RSA")) {
					if (!rsaReady) {
						rsa.genKey();
						rsaReady = true;
					}
					jtextOutput.setText(rsa.encryptBase64(input));
					return;
				} else if (algo.equals("Vigenere")) {
					ab = new VigenereCipher();
				} else if (algo.equals("Affine")) {
					ab = new AffineCipher();
				} else {
					jtextOutput.setText("Chưa hỗ trợ thuật toán này!");
					return;
				}

				// Encrypt cho các thuật toán kế thừa AbsCipher
				ab.loadKey(txtKey.getText().trim());
				result = new String(ab.encrypt(input));

			} catch (Exception ex) {
				ex.printStackTrace();
				result = "Lỗi Encrypt!";
			}

			jtextOutput.setText(result);
		});
		btnDecrypt.addActionListener(e -> {
			if (!keySave) {
				jtextOutput.setText("Vui lòng Save Key trước khi Decrypt!");
				return;
			}

			// Lấy input từ ô văn bản (Thường là giải mã cái mình vừa dán vào)
			String input = jtextInput.getText();
			String algo = (String) cbAlgorithm.getSelectedItem();
			String result = "";

			try {
				// --- 1. Khởi tạo đối tượng thuật toán tương ứng ---
				if (algo.equals("Dịch chuyển")) {
					ab = new ShiftCipher();
				} else if (algo.equals("Hill")) {
					ab = new HillCipher();
				} else if (algo.equals("Hoán vị")) {
					ab = new PermutationCipher();
				} else if (algo.equals("AES")) {
					ab = new AESCipher();
				} else if (algo.equals("DES")) {
					ab = new DESCipher();

				} else if (algo.equals("RSA")) {
					result = rsa.decrypt(input);
					jtextOutput.setText(result);
					return; // RSA return luôn
				} else if (algo.equals("Vigenere")) {
					ab = new VigenereCipher();
				} else if (algo.equals("Affine")) {
					ab = new AffineCipher();
				} else {
					jtextOutput.setText("Chưa hỗ trợ thuật toán này!");
					return;
				}

				// --- 2. Nạp Key và thực hiện Decrypt ---
				ab.loadKey(txtKey.getText().trim());
				result = ab.decrypt(input.getBytes());

			} catch (Exception ex) {
				ex.printStackTrace();
				result = "Lỗi Decrypt!";
			}

			jtextOutput.setText(result);
		});
		buttons.add(btnEncrypt);
		buttons.add(btnDecrypt);

		// config
		Config.setBorder(BorderFactory.createTitledBorder("Configurations"));
		Config.setLayout(new GridLayout(4, 1, 5, 5));

		txtKey.setBorder(BorderFactory.createTitledBorder("Key (Manual Input)"));

		// Nút tạo khóa ngẫu nhiên
		JButton btnCreateKey = new JButton("Create Key");

		btnCreateKey.addActionListener((ActionEvent event) -> {

			String algo = (String) cbAlgorithm.getSelectedItem();
			Random random = new Random();

			if (algo.equals("Hill")) {
				// Tạo một key Hill mẫu dễ tính toán
				txtKey.setText("3 3 2 5");
			} else if (algo.equals("Hoán vị")) {
				// Tạo key hoán vị mẫu cho sinh viên
				txtKey.setText("312");
			} else if (algo.equals("RSA")) {
				txtKey.setText("RSA tự tạo khóa khi Encrypt");
			} else if (algo.equals("Affine")) {

				int a;

				do {
					a = random.nextInt(255);
				} while (a % 2 == 0);

				int b = random.nextInt(255);

				txtKey.setText(a + "," + b);
			} else if (algo.equals("Vigenere")) {

				String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

				String key = "";

				for (int i = 0; i < 5; i++) {

					int index = random.nextInt(s.length());

					key += s.charAt(index);
				}

				txtKey.setText(key);
			} else if (algo.equals("AES") || algo.equals("DES")) {

				String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

				String key = "";

				for (int i = 0; i < 16; i++) {

					int index = random.nextInt(chars.length());

					key += chars.charAt(index);
				}

				txtKey.setText(key);
			}

			keySave = false; // Reset trạng thái để nhắc người dùng bấm Save Key mới
		});

		// Nút ImportKey từ file
		JButton btnImportKey = new JButton("Import Key");

		btnImportKey.addActionListener((ActionEvent event) -> {

			JFileChooser fileChooser = new JFileChooser();

			int result = fileChooser.showOpenDialog(jframe);

			if (result == JFileChooser.APPROVE_OPTION) {

				File file = fileChooser.getSelectedFile();

				try {
					FileReader fileReader = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(fileReader);

					String line = bufferedReader.readLine();

					if (line != null) {
						txtKey.setText(line.trim());
					}

					bufferedReader.close();
					fileReader.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		// nút savekey
		JButton btnSaveKey = new JButton("Save Key");

		btnSaveKey.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent event) {

				String keyText = txtKey.getText();

				// Kiểm tra key rỗng
				if (keyText == null || keyText.trim().length() == 0) {
					jtextOutput.setText("Chưa nhập key!");
					return;
				}

				// Đánh dấu đã lưu key
				keySave = true;

				// Hiển thị thông báo
				jtextOutput.setText("Đã lưu key tạm thời!");
			}
		});

		// Thêm theo thứ tự từ trên xuống
		Config.add(txtKey);
		Config.add(btnCreateKey);
		Config.add(btnImportKey);
		Config.add(btnSaveKey);

		pnlCenter.add(p1);
		pnlCenter.add(p2);
		pnlCenter.add(buttons);
		pnlCenter.add(Config);

		// thêm menu vào jframe
		jframe.add(pnlMenu, BorderLayout.NORTH);
		// Thêm panel vào jframe
		jframe.add(pnlCenter, BorderLayout.CENTER);
		// cho phép hiển thị
		jframe.setVisible(true);

	}

	private int gcd(int a, int b) {
		if (b == 0) {
			return Math.abs(a);
		}
		return gcd(b, a % b);
	}

}