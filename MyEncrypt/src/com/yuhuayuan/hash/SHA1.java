package com.yuhuayuan.hash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.yuhuayuan.comutil.ComUtil;

public class SHA1 {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
		MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
	    
	    long tick = System.currentTimeMillis();
	    int nCount = 10000;
	    while(nCount-->=0)
	    {
	     byte[] thedigest = md.digest(MD5.strData.getBytes("UTF-8"));
	     System.out.println(ComUtil.byteArrayToHexString(thedigest));
	    }
	    
	    System.out.println("costume:"+(System.currentTimeMillis()-tick));
	}

}
