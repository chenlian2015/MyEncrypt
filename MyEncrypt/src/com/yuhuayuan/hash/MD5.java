package com.yuhuayuan.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.yuhuayuan.comutil.ComUtil;

/**
 * MD5是输入不定长度信息，输出固定长度128-bits的算法
 * 1996年后被证实存在弱点，可以被加以破解，对于需要高度安全性的数据，专家一般建议改用其他算法，
 * 如SHA-1。2004年，证实MD5算法无法防止碰撞，因此无法适用于安全性认证，如SSL公开密钥认证或是数字签名等用途。
 * 
 * 补充： 数字签名，就是只有信息的发送者才能产生的别人无法伪造的一段数字串， 这段数字串同时也是对信息的发送者发送信息真实性的一个有效证明
 */

public class MD5 {
	public static final String strData = "hello world 你好伊斯兰 基督 佛教";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long tick = System.currentTimeMillis();
		int nCount = 10000;
		while (nCount-- >= 0) {
			byte[] thedigest = md.digest(strData.getBytes());
			System.out.println(ComUtil.byteArrayToHexString(thedigest));
		}

		System.out.println("costume:" + (System.currentTimeMillis() - tick));
	}

}
