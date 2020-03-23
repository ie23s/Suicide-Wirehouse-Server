package com.ie23s.java.suicidewarehouseserver.client;

import java.io.*;
import java.net.Socket;

public class SocketUtil {

	private String ip;
	private int port;

	private BufferedReader in;
	private BufferedWriter out;


	public SocketUtil(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public boolean openConnection() {
		try {
			Socket connection = new Socket(ip, port);

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String readLine() throws IOException, UnsignedExeption {
		String result = null;
		while (result == null || result.equals("null")) {
			result = in.readLine();
		}

		if (result.startsWith("ERROR")) {
			int code = Integer.parseInt(result.substring(5, 7));
			throw new UnsignedExeption(code);
		}
		return result;
	}

	public void sendLine(String s) throws IOException {
		System.out.println(s);
		out.write(s + '\n');
		out.flush();
	}
}
