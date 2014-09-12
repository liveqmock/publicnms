package com.afunms.common.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {

	private static final String ENCRYPT_ALG = "Blowfish";

	private static final String ENCRYPT_KEY = "#$#@afunms";

	private static String encode(String secret, String encryptKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		if (encryptKey == null || encryptKey.length() == 0) {
			encryptKey = ENCRYPT_KEY;
		}
		byte[] kbytes = encryptKey.getBytes();
		SecretKeySpec key = new SecretKeySpec(kbytes, ENCRYPT_ALG);

		Cipher cipher = Cipher.getInstance(ENCRYPT_ALG);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encoding = cipher.doFinal(secret.getBytes());
		BigInteger n = new BigInteger(encoding);
		return n.toString(16);
	}

	private static String decode(String secret, String encryptKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		if (encryptKey == null || encryptKey.length() == 0) {
			encryptKey = ENCRYPT_KEY;
		}
		byte[] kbytes = encryptKey.getBytes();
		SecretKeySpec key = new SecretKeySpec(kbytes, ENCRYPT_ALG);

		BigInteger n = new BigInteger(secret, 16);
		byte[] encoding = n.toByteArray();

		Cipher cipher = Cipher.getInstance(ENCRYPT_ALG);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decode = cipher.doFinal(encoding);
		return new String(decode);
	}

	public static String encode(String source) throws Exception {
		return encode(source, null);
	}

	public static String decode(String secret) throws Exception {
		return decode(secret, null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			long startTime = System.currentTimeMillis();

			String encoded = EncryptUtil.encode("1");
			String decoded = EncryptUtil.decode("-7d8cc23e0d1861f7");

			System.out.println("encoded = " + encoded+" ³¤¶È "+encoded.length());
			System.out.println("decoded = " + decoded);

			System.out.println("used time = " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
