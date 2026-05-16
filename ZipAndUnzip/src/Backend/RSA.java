package Backend;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSA {
private KeyPair keypair;
PrivateKey privatekey;
PublicKey publickey;

public String encryptBase64(String data ) throws Exception {
	return Base64.getEncoder().encodeToString(encrypt(data));
	
}
public byte[] encrypt(String data) throws Exception{
	Cipher cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding");
	byte in[] = data.getBytes(StandardCharsets.UTF_8);
	cipher.init(cipher.ENCRYPT_MODE, publickey);
	byte[] out = cipher.doFinal(in);
	return out;
	
}
public byte[] encrypt(byte[] data) throws Exception {

	Cipher cipher =
		Cipher.getInstance("RSA/ECB/PKCS1Padding");

	cipher.init(Cipher.ENCRYPT_MODE, publickey);

	return cipher.doFinal(data);
}

public byte[] decrypt(byte[] data) throws Exception {

	Cipher cipher =
		Cipher.getInstance("RSA/ECB/PKCS1Padding");

	cipher.init(Cipher.DECRYPT_MODE, privatekey);

	return cipher.doFinal(data);
}
public String decrypt(String base64) throws Exception {
	Cipher cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding");
	byte in[] = Base64.getDecoder().decode(base64);
	cipher.init(cipher.DECRYPT_MODE, privatekey);
	byte[] out = cipher.doFinal(in);
	return new String (out,StandardCharsets.UTF_8);
}
public void genKey() throws Exception {
	KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	generator.initialize(2048);
	keypair = generator.generateKeyPair();
	publickey = keypair.getPublic();
	privatekey = keypair.getPrivate();
}
public static void main(String[] args) throws Exception {
	RSA rsa = new RSA();
	rsa.genKey();
	String en = rsa.encryptBase64("the default configuration implementation from the SUN provider, as described in the configFile");
	System.out.println(en);
	System.out.println(rsa.decrypt(en));
}
}
