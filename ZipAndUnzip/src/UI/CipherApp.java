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
	JFrame jframe = new JFrame("Cipher App ATBM");
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
	String[] symmetric = {"AES","DES","Blowfish","RC4","Twofish"};
	String[] asymmetric = { "RSA" };
	String hashing[] = { "MD5", "MD2", "SHA-1", "SHA-256", "SHA-512", "CRC-32" };

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

			// Hash
			if (algo.equals("MD5")
					|| algo.equals("MD2")
					|| algo.equals("SHA-1")
					|| algo.equals("SHA-256")
					|| algo.equals("SHA-512")
					|| algo.equals("CRC-32")) {

				jtextOutput.setText(HashManager.hash(algo, input));

				return;
			}

			// Kiểm tra save key
			if (!keySave) {

				jtextOutput.setText("Vui lòng Save Key trước khi Encrypt!");

				return;
			}

			String result = "";

			try {

				// RSA
				if (algo.equals("RSA")) {

					if (!rsaReady) {

						rsa.genKey();

						rsaReady = true;
					}

					result = rsa.encryptBase64(input);

					jtextOutput.setText(result);

					return;
				}

				// Lấy cipher
				ab = CipherFactory.getCipher(algo);

				if (ab == null) {

					jtextOutput.setText("Chưa hỗ trợ thuật toán này!");

					return;
				}

				// Kiểm tra Hill Key
				if (algo.equals("Hill")) {

					String keyText = txtKey.getText().trim();

					String[] parts = keyText.split(" ");

					if (parts.length != 4) {

						jtextOutput.setText("Key Hill phải gồm 4 số!");

						return;
					}

					int a = Integer.parseInt(parts[0]);

					int b = Integer.parseInt(parts[1]);

					int c = Integer.parseInt(parts[2]);

					int d = Integer.parseInt(parts[3]);

					int det = (((a * d - b * c) % 26) + 26) % 26;

					if (gcd(det, 26) != 1) {

						jtextOutput.setText("Key Hill không hợp lệ!");

						return;
					}
				}

				// Kiểm tra Hoán vị
				if (algo.equals("Hoán vị")) {

					if (txtKey.getText().trim().isEmpty()) {

						jtextOutput.setText("Vui lòng nhập key hoán vị!");

						return;
					}
				}

				// Load key
				ab.loadKey(txtKey.getText().trim());

				// Encrypt
				result = new String(ab.encrypt(input));

			} catch (Exception ex) {

				ex.printStackTrace();

				result = "Lỗi Encrypt!";
			}

			jtextOutput.setText(result);

		});
		btnDecrypt.addActionListener(e -> {

			// Kiểm tra save key
			if (!keySave) {

				jtextOutput.setText("Vui lòng Save Key trước khi Decrypt!");

				return;
			}

			String input = jtextInput.getText();

			String algo = (String) cbAlgorithm.getSelectedItem();

			String result = "";

			try {

				// RSA
				if (algo.equals("RSA")) {

					result = rsa.decrypt(input);

					jtextOutput.setText(result);

					return;
				}

				// Lấy cipher
				ab = CipherFactory.getCipher(algo);

				if (ab == null) {

					jtextOutput.setText("Chưa hỗ trợ thuật toán này!");

					return;
				}

				// Load key
				ab.loadKey(txtKey.getText().trim());

				// Decrypt
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

		txtKey.setBorder(BorderFactory.createTitledBorder("Key (Có thể tự nhập tay)"));

		// Nút tạo khóa ngẫu nhiên
		JButton btnCreateKey = new JButton("Create Key");

		btnCreateKey.addActionListener((ActionEvent event) -> {

		    String algo = (String) cbAlgorithm.getSelectedItem();
		    Random random = new Random();

		    if (algo.equals("Hill")) {
		        // Tạo một key Hill mẫu
		        txtKey.setText("3 3 2 5");
		    } 
		    else if (algo.equals("Thay thế")) {
		        try {
		            SubCipher sub = new SubCipher();
		            javax.crypto.SecretKey generatedKey = sub.genKey();
		            
		            //lấy chuỗi 26 kí tự đã đảo vị trí gán lên
		            txtKey.setText(new String(generatedKey.getEncoded()));
		        } catch (Exception ex) {
		            jtextOutput.setText("Lỗi sinh khóa Thay thế: " + ex.getMessage());
		        }
		    } 
		    else if (algo.equals("Hoán vị")) {
		        // Tạo key hoán vị mẫu cho sinh viên
		        txtKey.setText("312");
		    } 
		    else if (algo.equals("RSA")) {
		        txtKey.setText("RSA tự tạo khóa khi Encrypt");
		    } 
		    else if (algo.equals("Affine")) {

		        int a;
		        do {
		            a = random.nextInt(255);
		        } while (a % 2 == 0);

		        int b = random.nextInt(255);
		        txtKey.setText(a + "," + b);
		    } 
		    else if (algo.equals("Vigenere")) {

		        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		        String key = "";
		        for (int i = 0; i < 5; i++) {
		            int index = random.nextInt(s.length());
		            key += s.charAt(index);
		        }
		        txtKey.setText(key);
		    } 
		    else if (algo.equals("AES")|| algo.equals("DES")|| algo.equals("Blowfish")|| algo.equals("RC4")|| algo.equals("Twofish")) {

		        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		        String key = "";
		        for (int i = 0; i < 16; i++) {
		            int index = random.nextInt(chars.length());
		            key += chars.charAt(index);
		        }
		        txtKey.setText(key);
		    }

		    keySave = false; // Reset 
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

				String keyText = txtKey.getText().trim() ;

				// Kiểm tra key rỗng
				if (keyText == null || keyText.trim().length() == 0) {
					jtextOutput.setText("Chưa nhập key!");
					return;
				}
				if (keyText.equals("Thay thế") && keyText.length() != 26) {
		            jtextOutput.setText("Lỗi: Khóa của thuật toán Thay thế phải có độ dài đúng 26 ký tự!");
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