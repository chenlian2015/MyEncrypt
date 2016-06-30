package com.yuhuayuan.asymmetric;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;


public class RSA {
	public static void main(String [] args) throws Exception
	{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		// Generate the keys — might take sometime on slow computers
		KeyPair myPair = kpg.generateKeyPair();
		PrivateKey priKey = myPair.getPrivate();
		PublicKey pubKey = myPair.getPublic();
		
		System.out.println(priKey.getEncoded());

		System.out.println(pubKey.getEncoded());
		
		final Cipher cipher = Cipher.getInstance("RSA");
        final String plaintext = "javacirecep你好基督 佛教 伊斯兰";

        // ENCRYPT using the PUBLIC key
        cipher.init(Cipher.ENCRYPT_MODE, myPair.getPublic());
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        String chipertext = new String(Base64.getEncoder().encode(encryptedBytes));
        System.out.println("encrypted (chipertext) = " + chipertext);

        // DECRYPT using the PRIVATE key
        cipher.init(Cipher.DECRYPT_MODE, myPair.getPrivate());
        byte[] ciphertextBytes = Base64.getDecoder().decode(chipertext.getBytes());
        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
        String decryptedString = new String(decryptedBytes);
        System.out.println("decrypted (plaintext) = " + decryptedString);
	}
}
