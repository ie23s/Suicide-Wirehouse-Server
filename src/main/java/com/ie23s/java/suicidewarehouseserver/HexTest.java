package com.ie23s.java.suicidewarehouseserver;

import org.bouncycastle.util.encoders.Hex;

public class HexTest {
	public static void main(String[] args) {

		byte[] bytes = {11, 34, 0, 32,-45, 34, -125, 53, 43};

		System.out.println(new String(Hex.encode(bytes)));
		System.out.println(toHex(bytes));

	}

	private static String toHex(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder();

		for(byte b  : bytes) {
			stringBuilder.append(String.format("%02x", b&0xff));
		}
		return stringBuilder.toString();
	}
}
