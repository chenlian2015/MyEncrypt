package com.yuhuayuan.base64;

import java.util.Base64;

import com.yuhuayuan.comutil.ComUtil;
import com.yuhuayuan.hash.MD5;

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
