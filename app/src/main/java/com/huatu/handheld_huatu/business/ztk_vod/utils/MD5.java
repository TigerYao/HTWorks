package com.huatu.handheld_huatu.business.ztk_vod.utils;

import java.security.MessageDigest;

/**
 * MD5
 */
public class MD5 {
	private static final char hexDigits22[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public final static String digest(String message) {

		try {
			byte[] btInput = message.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits22[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits22[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
