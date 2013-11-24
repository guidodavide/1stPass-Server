package com.serverSSL.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncDecFileSuite {

	private static Cipher complexCipher;
	private static byte[] iv;
	private static byte[] utf8EncryptedData;
	private static SecretKeyFactory keyFactory;
	private static KeySpec spec;
	private static SecretKey tmp;
	private static SecretKey key;

	private static final byte[] SALT = {
		(byte) 0x1a, (byte) 0x23, (byte) 0x26, (byte) 0x25,
		(byte) 0xed, (byte) 0xc7, (byte) 0x63, (byte) 0xad,
	};

	public static byte[] decryptComplex(ByteObj obj, String password) throws GeneralSecurityException, IOException {
		keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		spec = new PBEKeySpec(password.toCharArray(), SALT, 65536, 256);
		tmp = keyFactory.generateSecret(spec);
		key = new SecretKeySpec(tmp.getEncoded(), "AES");
		complexCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		complexCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(obj.getIVParameterSpec()));
		return complexCipher.doFinal(obj.getEncriptedData());
	}

	public static ByteObj encryptComplex(byte[] property, String password) throws GeneralSecurityException, IOException {
		keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		spec = new PBEKeySpec(password.toCharArray(), SALT, 65536, 256);
		tmp = keyFactory.generateSecret(spec);
		key = new SecretKeySpec(tmp.getEncoded(), "AES");
		complexCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		complexCipher.init(Cipher.ENCRYPT_MODE, key);
		AlgorithmParameters params = complexCipher.getParameters();
		iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		utf8EncryptedData = complexCipher.doFinal(property);
		return new ByteObj(iv, utf8EncryptedData);
	}

	public static byte[] base64Decode(byte[] property) throws IOException {
		return org.apache.commons.codec.binary.Base64.decodeBase64(property);
	}

	public static byte[] fromObjtoByte( Serializable o ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return  org.apache.commons.codec.binary.Base64.encodeBase64( baos.toByteArray() );
	}

	public static byte[] base64Encode(byte[] bytes) {
		return org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
	}

	public static Object fromBytetoObj( byte[] s ) throws IOException ,
	ClassNotFoundException {
		byte [] data = org.apache.commons.codec.binary.Base64.decodeBase64( s );
		ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(  data ) );
		Object o  = ois.readObject();
		ois.close();
		return o;
	}


}