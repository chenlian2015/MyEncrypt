package com.yuhuayuan;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
* 3DES是DES加密算法的一种模式，它使用3条64位的密钥对数据进行三次加密。数据加密标准（DES）是美国的一种由来已久的加密标准，它使用对称密钥加密法。
* 3DES（即Triple DES）是DES向AES过渡的加密算法（1999年，NIST将3-DES指定为过渡的加密标准），是DES的一个更安全的变形。它以DES为基本模块，通过组合分组方法设计出分组加密算法。
* 设Ek()和Dk()代表DES算法的加密和解密过程，K代表DES算法使用的密钥，P代表明文，C代表密表，这样，
* 3DES加密过程为：C=Ek3(Dk2(Ek1(P)))
* 3DES解密过程为：P=Dk1((EK2(Dk3(C)))
* K1、K2、K3决定了算法的安全性，若三个密钥互不相同，本质上就相当于用一个长为168位的密钥进行加密。多年来，它在对付强力攻击时是比较安全的。若数据对安全性要求不那么高，K1可以等于K3。在这种情况下，密钥的有效长度为112位。
* 
* @author chenlian
* @version 1.0
*/
public class TripleDES {

    public static void main(String[] args) throws Exception {

    	String text = "kyle boon";

    	byte[] codedtext = new TripleDES().encrypt(text);
    	String decodedtext = new TripleDES().decrypt(codedtext);

    	System.out.println(codedtext); // this is a byte array, you'll just see a reference to an array
    	System.out.println(decodedtext); // This correctly shows "kyle boon"
    }

    /**
     * This method encrypt data by 3des
     * @param message The data to be encrypted
     * @return Encrypted data
     * @exception Exception jre may throws
     * @see DES
     * */
    public byte[] encrypt(String message) throws Exception {
    	final MessageDigest md = MessageDigest.getInstance("md5");
    	final byte[] digestOfPassword = md.digest("HG58YZ3CR9"
    			.getBytes("utf-8"));
    	final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
    	for (int j = 0, k = 16; j < 8;) {
    		keyBytes[k++] = keyBytes[j++];
    	}

    	final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
    	final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
    	final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
    	cipher.init(Cipher.ENCRYPT_MODE, key, iv);

    	final byte[] plainTextBytes = message.getBytes("utf-8");
    	final byte[] cipherText = cipher.doFinal(plainTextBytes);


    	return cipherText;
    }

    public String decrypt(byte[] message) throws Exception {
    	final MessageDigest md = MessageDigest.getInstance("md5");
    	final byte[] digestOfPassword = md.digest("HG58YZ3CR9"
    			.getBytes("utf-8"));
    	final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
    	for (int j = 0, k = 16; j < 8;) {
    		keyBytes[k++] = keyBytes[j++];
    	}

    	final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
    	final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
    	final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
    	decipher.init(Cipher.DECRYPT_MODE, key, iv);

    	// final byte[] encData = new
    	// sun.misc.BASE64Decoder().decodeBuffer(message);
    	final byte[] plainText = decipher.doFinal(message);

    	return new String(plainText, "UTF-8");
    }
}
