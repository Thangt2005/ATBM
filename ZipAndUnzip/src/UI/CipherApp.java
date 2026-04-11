package UI;

import java.awt.BorderLayout;
import Backend.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
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
import javax.swing.border.Border;

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
	JPanel Config = new JPanel((new BorderLayout()));
	JPanel buttons = new JPanel((new GridLayout(2, 2, 5, 5)));
	String[] traditional = { "Dịch chuyển", "Thay thế", "Hill", "Hoán vị", "Affine", "Vigenere" };
	String[] modern = { "AES", "DES", "RSA" };

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
		JComboBox<String> cbType = new JComboBox<>(new String[] { "Truyền thống", "Hiện đại" });
		// algorithms
		JComboBox<String> cbAlgorithm = new JComboBox<>(traditional);

		// Mode
		JComboBox<String> cbMode = new JComboBox<>();

		// Padding
		JComboBox<String> cbPadding = new JComboBox<>();

		pnlOptions.add(new JLabel("Type:"));
		pnlOptions.add(cbType);

		pnlOptions.add(new JLabel("Algorithm:"));
		pnlOptions.add(cbAlgorithm);

		pnlOptions.add(new JLabel("Mode:"));
		pnlOptions.add(cbMode);

		pnlOptions.add(new JLabel("Padding:"));
		pnlOptions.add(cbPadding);

		pnlMenu.add(pnlOptions, BorderLayout.CENTER);
		cbType.addActionListener(e -> {
			cbAlgorithm.removeAllItems();

			if (cbType.getSelectedItem().equals("Truyền thống")) {
				for (String s : traditional)
					cbAlgorithm.addItem(s);
			} else {
				for (String s : modern)
					cbAlgorithm.addItem(s);
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

		    if (!keySave) {
		        jtextOutput.setText("Vui lòng Save Key trước khi Encrypt!");
		        return;
		    }

		    String input = jtextInput.getText();
		    String algo = (String) cbAlgorithm.getSelectedItem();
		    String result = "";

		    try {

		        // ===== Chọn thuật toán =====
		        if (algo.equals("Dịch chuyển")) {

		            ab = new ShiftCipher();

		        } else if (algo.equals("Hill")) {

		            String keyText = txtKey.getText().trim();
		            String[] parts = keyText.split(" ");

		            // kiểm tra đủ 4 số
		            if (parts.length != 4) {
		                jtextOutput.setText("Key Hill phải gồm 4 số! Ví dụ: 3 3 2 5");
		                return;
		            }

		            int a = Integer.parseInt(parts[0]);
		            int b = Integer.parseInt(parts[1]);
		            int c = Integer.parseInt(parts[2]);
		            int d = Integer.parseInt(parts[3]);

		            int det = a * d - b * c;
		            det = ((det % 26) + 26) % 26;

		            if (gcd(det, 26) != 1) {
		                jtextOutput.setText("Key Hill không hợp lệ!");
		                return;
		            }

		            ab = new HillCipher();

		        } else if (algo.equals("RSA")) {

		            if (!rsaReady) {
		                rsa.genKey();
		                rsaReady = true;
		            }

		            result = rsa.encryptBase64(input);
		            jtextOutput.setText(result);
		            return;

		        } else {

		            jtextOutput.setText("Chưa hỗ trợ thuật toán này!");
		            return;
		        }

		        // ===== Load key + encrypt =====
		        ab.loadKey(txtKey.getText());
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

		    String input = jtextOutput.getText(); // lấy từ output
		    String algo = (String) cbAlgorithm.getSelectedItem();
		    String result = "";

		    try {

		        // ===== Chọn thuật toán =====
		        if (algo.equals("Dịch chuyển")) {

		            ab = new ShiftCipher();

		        } else if (algo.equals("Hill")) {

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

		            int det = a * d - b * c;
		            det = ((det % 26) + 26) % 26;

		            if (gcd(det, 26) != 1) {
		                jtextOutput.setText("Key Hill không hợp lệ!");
		                return;
		            }

		            ab = new HillCipher();

		        } else if (algo.equals("RSA")) {

		            result = rsa.decrypt(input);
		            jtextOutput.setText(result);
		            return;

		        } else {

		            jtextOutput.setText("Chưa hỗ trợ thuật toán này!");
		            return;
		        }

		        // ===== Load key + decrypt =====
		        ab.loadKey(txtKey.getText());
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

		        // tạo key 2x2 hợp lệ
		        int a = 3;
		        int b = 3;
		        int c = 2;
		        int d = 5;

		        txtKey.setText(a + " " + b + " " + c + " " + d);

		    } else {

		        int randomKey = random.nextInt(25) + 1;
		        txtKey.setText(String.valueOf(randomKey));
		    }

		    keySave = false;
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

	private String decrypt(String input, int key) {
		return encrypt(input, 26 - key);
	}

	private String substituteDecrypt(String input) {
		String result = "";

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			char lower = Character.toLowerCase(c);

			int index = cipher.indexOf(lower);

			if (index != -1) {
				char newChar = plain.charAt(index);

				if (Character.isUpperCase(c))
					newChar = Character.toUpperCase(newChar);

				result += newChar;
			} else {
				result += c;
			}
		}
		return result;
	}

	private final String plain = "abcdefghijklmnopqrstuvwxyz";
	private final String cipher = "qwertyuiopasdfghjklzxcvbnm";

	private String substituteEncrypt(String input) {
		String result = "";

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			char lower = Character.toLowerCase(c);

			int index = plain.indexOf(lower);

			if (index != -1) {
				char newChar = cipher.charAt(index);

				if (Character.isUpperCase(c))
					newChar = Character.toUpperCase(newChar);

				result += newChar;
			} else {
				result += c;
			}
		}
		return result;
	}

	private String encrypt(String text, int key) {
		String result = "";

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (Character.isLetter(c)) {
				char base;
				if (Character.isUpperCase(c))
					base = 'A';
				else
					base = 'a';

				c = (char) ((c - base + key) % 26 + base);
			}

			result += c;
		}
		return result;
	}

}