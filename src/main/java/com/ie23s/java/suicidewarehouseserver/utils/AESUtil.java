package com.ie23s.java.suicidewarehouseserver.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {

	private static final String transformation = "AES/CBC/PKCS5Padding";

	private Cipher cipher;
	private byte[] sec_bytes = new byte[16];
	private SecureRandom random = new SecureRandom();

	private SecretKeySpec secretKey;

	public AESUtil(SecretKeySpec secretKey) {
		this.secretKey = secretKey;
	}

	public static SecretKeySpec createSecretKey() {
		SecretKeySpec sks = null;
		byte[] bytes = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(bytes);

		try {
			MessageDigest md;
			byte[] key;
			md = MessageDigest.getInstance("SHA-1");

			key = md.digest(bytes);
			key = Arrays.copyOf(key, 16);
			sks = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException ignored) {
		}
		assert sks != null;
		String encodedKey = Base64.getEncoder().encodeToString(sks.getEncoded());

		return sks;
	}

	public String encrypt(byte[] plainText) {
		return encrypt(plainText, sec_bytes);
	}

	public String encrypt(byte[] plainText, byte[] sec_bytes) {
		try {
			cipher = Cipher.getInstance(transformation);
			IvParameterSpec ivSpec;
			ivSpec = new IvParameterSpec(sec_bytes);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey,
					ivSpec);

			return Base64.getEncoder().encodeToString(cipher.doFinal(plainText));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public byte[] decrypt(String encryptedText, byte[] sec_bytes) {
		try {
			cipher = Cipher.getInstance(transformation);
			IvParameterSpec ivSpec;
			ivSpec = new IvParameterSpec(sec_bytes);
			cipher.init(Cipher.DECRYPT_MODE, secretKey,
					ivSpec);
			return cipher.doFinal(Base64.getDecoder().decode(encryptedText));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public byte[] nextSec() {
		random.nextBytes(sec_bytes);
		return sec_bytes;
	}
}