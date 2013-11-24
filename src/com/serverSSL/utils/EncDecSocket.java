package com.serverSSL.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class EncDecSocket {
	
	public static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}	

	public static byte[] encrypt(byte[] property, String password, byte[] SALT) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property));
	}

	public static byte[] decrypt(byte[] property, String password, byte[] SALT) throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return pbeCipher.doFinal(base64Decode(property));
	}

	public static byte[] base64Decode(byte[] property) throws IOException {
		return org.apache.commons.codec.binary.Base64.decodeBase64(property);
	}

	public static byte[] base64Encode(byte[] bytes) {
		return org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
	}

	
	public static byte[] ObjtoByte( Serializable o ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return  org.apache.commons.codec.binary.Base64.encodeBase64( baos.toByteArray() );
	}

	public static Object BytetoObj( byte[] s ) throws IOException ,
	ClassNotFoundException {
		byte [] data = org.apache.commons.codec.binary.Base64.decodeBase64( s );
		ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(  data ) );
		Object o  = ois.readObject();
		ois.close();
		return o;
	}
	
	

}