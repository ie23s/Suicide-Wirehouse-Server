package com.ie23s.java.suicidewarehouseserver.io;


import com.ie23s.java.suicidewarehouseserver.user.MySQL;
import com.ie23s.java.suicidewarehouseserver.utils.AESUtil;
import com.ie23s.java.suicidewarehouseserver.utils.RSAUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.ExemptionMechanismException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Base64;


public class ConnectionUtil {
	private static final String privateKeyString = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK7nP9dxhu1kb+g2E" +
			"lqU7SQNhcXE1JlvLccEx+RhCkyXGGcjhxoILZcIji+rZEq4xYwU4lPRK/eljkxYScyoJaaLvl97EtrQpnKiMb5tLL2OjXrHPb3EEtXh4" +
			"P8LblsBBYXC1kPpOEEBeh2KEzcHKjWv+20SQiMTcu5vO9AlhzUlAgMBAAECgYAOKyOL02GHx5QdLowsFFZljkbg74H9b/k4XxXGVWodL" +
			"DxI1qLyI+l1i7bg+7RjLTark2GGQrKaHCo72wcXciOiiJBjjmIeSdHrpHCeLxk1rEkxs36db2RmDXSeBEq+c+tG3bc1H6/snxZ8c3Yya" +
			"iBuPjLc/6G9bzoBo0ns2Wp9PQJBAPYo2bVI8zlWAgjovsBcP1il/dcWyC0bTphwPHlQaO9YDF7+376vuIl3fTWYIq8MhXVHAZIuQRqW1" +
			"96MmuTw/OMCQQC15S0bZRerXRlypfwrLhlbz8OxHQeG90QTQdG3WrcQY4PapuPC5/5qEcojpqDLIM4MEiZs5hIMj3kc1TLfgOxXAkBuX" +
			"wimoSv1VFwbNIh65aG9lMfJTjy5BNprvT9QQb6bOoZpfaxC6rU6ZeotQqaiiGG6oPjSW4zzaBkofzDgYDFzAkEArKcnRKyVZfxNzmxNS" +
			"rNMMMCqMLCsV2jXPiwooxDBWRYMrvvgjz3kWMwgAe0FDSpLSlvkC1Pq5+87d6nKyym1qwJAFzxu/oA4lMVtvseZ4PCi1Ibr9e2J/kz3d" +
			"UvSeGTO4kHw8j+JtBj85OsdbVp+CN00C1LvpIVakNQ6rgPN1oosfw==";
	private static byte[] privateKey;

	static {
		privateKey = privateKeyString.getBytes(StandardCharsets.UTF_8);
	}

	private AESUtil aesUtil;
	private byte[] aesSecBin;
	private byte[] aesSecBinClient = new byte[16];
	private Server.Client client;
	private Server server;


	public ConnectionUtil(Server.Client client, Server server) {
		this.client = client;
		this.server = server;
	}

	public void openConnection() throws BadPaddingException, InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException,
            IOException, UnsignedExeption {

		initAES();
		sendServerAESSec();

		if (!new String(getData(), StandardCharsets.UTF_8).equals("OK"))
			throw new UnsignedExeption(1);
		sendData("OK".getBytes(StandardCharsets.UTF_8));
		System.out.println("Connection success!");

		Auth auth = new Auth(this);
		runAuth(auth);


	}

	void close(int code, String s) {
		sendData(String.valueOf(code).concat(":").concat(s).getBytes());
		client.close();
	}

	private void runAuth(Auth auth) throws IOException {
		byte[] data = getData();
		int code = 229;
		try {
			code = auth.runAuth(data);
		} catch (SQLException ignored) {
		}
		sendData(auth.prepareData(code).getBytes());
		if (code % 100 / 10 == 2 || code == 299)
			runAuth(auth);
		else {
			server.addConnection(auth.getUser().getId(), this);
		}
	}

	private byte[] getAESKey() throws BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IOException {
		byte[] ans = RSAUtil.decrypt(privateKey, client.read());
		byte[] key = new byte[16];
		System.arraycopy(ans, 0, aesSecBinClient, 0, 16);
		System.arraycopy(ans, 16, key, 0, 16);
		return key;
	}

	private void initAES() throws BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
		byte[] key = getAESKey();
		SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
		aesUtil = new AESUtil(aesKey);
		aesSecBin = aesUtil.nextSec();
	}

	private void sendServerAESSec() {
		client.send(aesUtil.encrypt(aesSecBin, aesSecBinClient));
	}

	private byte[] addPrefix(byte[] data) {
		aesSecBin = aesUtil.nextSec();
		byte[] outputData = new byte[16 + data.length];

		System.arraycopy(aesSecBin, 0, outputData, 0, 16);
		System.arraycopy(data, 0, outputData, 16, data.length);

		return outputData;
	}

	void sendData(byte[] data) {
		String message = aesUtil.encrypt(addPrefix(data));
		client.send(message);
	}

	byte[] getData() throws IOException {
		String message = client.read();
		System.out.println(message);
		byte[] data = aesUtil.decrypt(message, aesSecBinClient);
		byte[] decoded = new byte[data.length - 16];
		System.arraycopy(data, 0, aesSecBinClient, 0, 16);
		System.arraycopy(data, 16, decoded, 0, decoded.length);
		return decoded;
	}

    public Server.Client getClient() {
        return client;
    }

    public Server getServer() {
		return server;
	}
}
