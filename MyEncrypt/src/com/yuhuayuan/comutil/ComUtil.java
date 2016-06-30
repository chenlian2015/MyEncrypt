package com.yuhuayuan.comutil;

public class ComUtil {
	public static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +="0x"+
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 )+"-";
		  }
		  return result;
		}
}
