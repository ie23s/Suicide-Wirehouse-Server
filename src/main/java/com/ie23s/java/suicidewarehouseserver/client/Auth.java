package com.ie23s.java.suicidewarehouseserver.client;

import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Auth {
	public static String encodeSession(int id, String session) {
		return "0:" + id + ":" + session;
	}

	public static String encodePassword(String name, String password) {
		return "1:" + name + ":" + encodeSHA256(password);
	}

	public static String encodeRegistration(String name, String password) {
		return "2:" + name + ":" + encodeSHA256(password);
	}

	private static String encodeSHA256(String data) {
		String s = "";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(data.getBytes("UTF-8"));
			s = new String(Hex.encode(hash)).toUpperCase();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException ignore) {
		}
		return s;
	}
}
