package com.serverSSL.utils;

public class ByteObj {

	public ByteObj(byte[] iVParameterSpec, byte[] encriptedData) {
		super();
		IVParameterSpec = iVParameterSpec;
		EncriptedData = encriptedData;
	}
	private byte[] IVParameterSpec;
	private byte[] EncriptedData;
	public byte[] getIVParameterSpec() {
		return IVParameterSpec;
	}
	public byte[] getEncriptedData() {
		return EncriptedData;
	}

}
