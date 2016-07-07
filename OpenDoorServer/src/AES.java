
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
/**
* AES(Advanced Encryption
* 用AES加密2000年10月，NIST（美国国
* 美国标准与技术研究院（NIST）于2002年5
* AES算法基于排列和置换运算。排列是对数据重新
* AES使用几种不同的方法来执行排列和置换运算。
* 与公共密钥加密使用密钥对不同，对称密钥密码使用
* 
* @author chenlian
* @version 1.0
*
*/
public class AES {
    public static byte[] encrypt(String key, String initVector, byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value);
            System.out.println("encrypted string: "
                    + Base64.encodeBase64String(encrypted)+"/length:"+encrypted.length);

            return encrypted;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
}

    public static byte[] decrypt(String key, String initVector, byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(encrypted);
            return original;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String key = "Bar12345Bar12345"; // 128 bit key
    public static String initVector = "RandomInitVector"; // 16 bytes IV
    
    public static void main(String[] args) throws UnsupportedEncodingException {
    	byte dataEncrypted[] = encrypt(key, initVector, "abAB1234".getBytes("UTF-8"));
        System.out.println(new String(decrypt(key, initVector, dataEncrypted), "UTF-8"));
    }
}