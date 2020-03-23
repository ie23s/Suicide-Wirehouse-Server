package com.ie23s.java.suicidewarehouseserver.io;

import com.ie23s.java.suicidewarehouseserver.user.MySQL;

import javax.crypto.BadPaddingException;
import javax.crypto.ExemptionMechanismException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

public class Server {
	private static int SOKET_PORT = 6319;
	private HashMap<Integer, ConnectionUtil> connections;


	public Server() {
		connections = new HashMap<>();
	}

	public void start() throws IOException {
		ServerSocket ss = new ServerSocket(SOKET_PORT);
		while (true) {
			Socket socket = ss.accept();
			System.out.println("Client connected!");
			new Thread(() -> {
				Client client = new Client(socket);
				try {
					client.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	void addConnection(int id, ConnectionUtil connectionUtil) {
		if (connections.containsKey(id)) {
			connections.remove(id).close(229, "Connected with a new session");
		}
		connections.put(id, connectionUtil);

	}

	public ConnectionUtil getConnection(int id) {
		return connections.get(id);
	}

	public class Client {
		BufferedReader sin;
		BufferedWriter sout;
		ConnectionUtil connectionUtil;
		private Socket socket;

		public Client(Socket socket) {
			this.socket = socket;
			connectionUtil = new ConnectionUtil(this, Server.this);
		}

		void start() throws IOException {
			sin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sout = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			try {
				connectionUtil.openConnection();
			} catch (BadPaddingException | UnsignedExeption | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchProviderException | InvalidKeyException e) {
				e.printStackTrace();
			}
		}

		void sendInOtherThread(String message) {
			new Thread(() -> send(message)).start();
		}

		public void send(String message) {
			try {
				sout.write(message + '\n');
				sout.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//TODO Remove client after closing connection with server SocketException
		public String read() throws IOException {

			return sin.readLine();
		}

		//TODO Readline works!
		private void reader() throws IOException {
			StringBuilder s = new StringBuilder();
			while (true) {
				char c = (char) sin.read();

				if (c == '\n') {
					System.out.println(s);
					s = new StringBuilder();
				} else {
					s.append(c);
				}
			}
		}

		public void close() {
			try {
				socket.close();
				sin.close();
				sout.close();
			} catch (IOException ignore) {
			}
		}
	}

}