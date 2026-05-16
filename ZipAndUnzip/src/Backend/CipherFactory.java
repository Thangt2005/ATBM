package Backend;

public class CipherFactory {

	public static AbsCipher getCipher(String algo) {

		switch (algo) {

		case "Dịch chuyển":
			return new ShiftCipher();

		case "Thay thế":
			return new SubCipher();

		case "Hill":
			return new HillCipher();

		case "Hoán vị":
			return new PermutationCipher();

		case "AES":
			return new AESCipher();

		case "DES":
			return new DESCipher();

		case "Blowfish":
			return new BlowfishCipher();

		case "RC4":
			return new RC4Cipher();

		case "Twofish":
			return new TwofishCipher();

		case "Vigenere":
			return new VigenereCipher();

		case "Affine":
			return new AffineCipher();

		case "XOR":
			return new XORCipher();

		default:
			return null;
		}
	}
}