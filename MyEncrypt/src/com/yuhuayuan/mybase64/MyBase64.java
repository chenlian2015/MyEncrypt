package com.yuhuayuan.mybase64;

import java.util.Base64;

import com.yuhuayuan.comutil.ComUtil;
import com.yuhuayuan.hash.MD5;

/**
 * Base64是一种基于64个可打印字符来表示二进制数据的表示方法。由于2的6次方等于64，所以每6个比特为一个单元，
 * 对应某个可打印字符。三个字节有24个比特，对应于4个Base64单元，即3个字节需要用4个可打印字符来表示。
 * 它可用来作为电子邮件的传输编码。在Base64中的可打印字符包括字母A-Z、a-z、数字0-9，这样共有62个字符，
 * 此外两个可打印符号在不同的系统中而不同。一些如uuencode的其他编码方法，和之后binhex的版本使用不同的64字符集来代表6个二进制数字，
 * 但是它们不叫Base64。
 * Base64常用于在通常处理文本数据的场合，表示、传输、存储一些二进制数据。包括MIME的email、在XML中存储复杂数据。
 */

public class MyBase64 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte [] data = null;
		System.out.println(ComUtil.byteArrayToHexString(Base64.getEncoder().encode(MD5.strData.getBytes())));
		System.out.println(ComUtil.byteArrayToHexString(Base64.getMimeEncoder().encode(MD5.strData.getBytes())));
		System.out.println(ComUtil.byteArrayToHexString(data = Base64.getMimeEncoder().encode(MD5.strData.getBytes())));
		
		System.out.println( new String((Base64.getDecoder().decode(data))));
	}

}
