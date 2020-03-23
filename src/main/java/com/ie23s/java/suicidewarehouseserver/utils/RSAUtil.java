package com.ie23s.java.suicidewarehouseserver.utils;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class RSAUtil {

	private static final String RSA_ALGORITHM = "RSA";
	private static final String CIPHER_RSA_WITH_PADDING = "RSA/ECB/PKCS1Padding";
	private static final String PROVIDER = "BC";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Encrypt a string with RSA using a public key and handling base64 decoding/encoding
	 *
	 * @param publicKeyBase64 the public key base64 encoded
	 * @param inputData       the data to encrypt
	 * @return the data encrypted and base64 encoded
	 */
	public static String encrypt(byte[] publicKeyBase64, byte[] inputData) throws InvalidKeySpecException, NoSuchAlgorithmException,
			BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
		byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

		PublicKey publicKey = getPublicKey(publicKeyBytes);
		byte[] encrypted = encrypt(publicKey, inputData);

		return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 * Encrypt a string with RSA using a public key
	 *
	 * @param publicKey the public key
	 * @param inputData the data to encrypt
	 * @return the data encrypted
	 */
	private static byte[] encrypt(PublicKey publicKey, byte[] inputData) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException,
			NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
		Cipher cipher = getEncryptionCipher(publicKey);

		return cipher.doFinal(inputData, 0, inputData.length);
	}

	public static byte[] decrypt(byte[] privateKeyBase64, String inputData) throws InvalidKeySpecException, NoSuchAlgorithmException,
			BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException {
		byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
		System.out.println(inputData);
		byte[] data = Base64.getDecoder().decode(inputData);
		PrivateKey privateKey = getPrivateKey(privateKeyBytes);

		return decrypt(privateKey, data);
	}

	private static byte[] decrypt(PrivateKey privateKey, byte[] inputData) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
		Cipher rsa = getDeryptionCipher(privateKey);
		//System.out.println(inputData.getBytes(StandardCharsets.UTF_8).length);
		return rsa.doFinal(inputData);
	}

	public static byte[] encryptWithPrivateKey(byte[] privateKeyBase64, byte[] inputData) throws InvalidKeySpecException, NoSuchAlgorithmException,
			BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
		byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

		PrivateKey privateKey = getPrivateKey(privateKeyBytes);
		byte[] encrypted = encryptWithPrivateKey(privateKey, inputData);

		return Base64.getEncoder().encode(encrypted);
	}

	public static byte[] encryptWithPrivateKey(PrivateKey privateKey, byte[] inputData) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException,
			NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
		Cipher cipher = getEncryptionCipherWithPrivateKey(privateKey);

		return cipher.doFinal(inputData, 0, inputData.length);
	}

	public static byte[] decryptWithPublicKey(byte[] publicKeyBase64, String inputData) throws InvalidKeySpecException, NoSuchAlgorithmException,
			BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException {
		byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
		byte[] data = Base64.getDecoder().decode(inputData.getBytes());
		PublicKey publicKey = getPublicKey(publicKeyBytes);

		return decryptWithPublicKey(publicKey, data);
	}

	private static byte[] decryptWithPublicKey(PublicKey publicKey, byte[] inputData) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
		Cipher rsa = getDecryptionCipherWithPublicKey(publicKey);
		//System.out.println(inputData.getBytes(StandardCharsets.UTF_8).length);
		byte[] data = rsa.doFinal(inputData);
		return data;
	}

	/**
	 * Create and return the encryption cipher
	 */
	private static Cipher getEncryptionCipher(PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeyException {
		Cipher cipher = Cipher.getInstance(CIPHER_RSA_WITH_PADDING, PROVIDER);
		cipher.init(ENCRYPT_MODE, publicKey);
		return cipher;
	}

	private static Cipher getDeryptionCipher(PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeyException {
		Cipher cipher = Cipher.getInstance(CIPHER_RSA_WITH_PADDING, PROVIDER);
		cipher.init(DECRYPT_MODE, privateKey);
		return cipher;
	}

	/**
	 * Create and return the encryption cipher
	 */
	private static Cipher getEncryptionCipherWithPrivateKey(PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeyException {
		Cipher cipher = Cipher.getInstance(CIPHER_RSA_WITH_PADDING, PROVIDER);
		cipher.init(ENCRYPT_MODE, privateKey);
		return cipher;
	}

	private static Cipher getDecryptionCipherWithPublicKey(PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeyException {
		Cipher cipher = Cipher.getInstance(CIPHER_RSA_WITH_PADDING, PROVIDER);
		cipher.init(DECRYPT_MODE, publicKey);
		return cipher;
	}

	/**
	 * /**
	 * Create and return the PublicKey object from the public key bytes
	 */
	private static PublicKey getPublicKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
		return keyFactory.generatePublic(keySpec);
	}

	public static PrivateKey getPrivateKey(byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
		return kf.generatePrivate(spec);
	}
}