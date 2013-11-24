package com.serverSSL.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.serverSSL.model.User;

public class LoginRegisterUtils 
{

	private static LoginRegisterUtils instance;
	private static LinkedHashSet<User> userList;
	private static final String password = ">,e=|&UPHV&}&4B=OaN9Xo[4O9a@aHCAU&Lb*m>p66BE-<JV>$9_s{$u~y";

	@SuppressWarnings("unchecked")
	private LoginRegisterUtils(){
		super();
		userList = new LinkedHashSet<User> ();
		File f = new File("PSW-LIST");
		if (!f.exists())
			userList = new LinkedHashSet<User>();
		else{
			try {
				Path path = Paths.get("PSW-LIST");
				Path pathIV = Paths.get("PSW-LIST.IV");
				byte[] letto = Files.readAllBytes(path);
				byte[] lettoIV = Files.readAllBytes(pathIV);
				
				Object o = EncDecFileSuite.fromBytetoObj(EncDecFileSuite.decryptComplex(new ByteObj(lettoIV,letto), password));
				userList = (LinkedHashSet<User>) o;
			} catch (ClassNotFoundException | IOException | GeneralSecurityException e) {
				e.printStackTrace();
				System.out.println("Errore nell'apertura del file utenti");
			}
		}

	}

	public static LoginRegisterUtils getInstance(){
		if(instance == null)
			instance = new LoginRegisterUtils();
		return instance;
	}

	public static synchronized int register(String userText,String pswText) throws NoSuchAlgorithmException
	{
		LoginRegisterUtils.getInstance();

		int salt= (int) (Math.random()*1000); //salt con 4 cifre

		String toSave=hash("sha", pswText,""+salt);	

		createFile(userText,salt,toSave);

		return 1;

	}

	public static synchronized boolean isPresent(String user, String psw) throws IOException 
	{
		LoginRegisterUtils.getInstance();
		Iterator<User> it = userList.iterator();
		while(it.hasNext()){
			User temp = it.next();
			if(temp.getName().equals(user)){
				String hashCalculated=hash("sha", psw, temp.getSalt());
				if(hashCalculated.equals(temp.getHash()))
					return true;
				else
					return false;
			}
		}
		return false;
	}

	public static synchronized String hash(String algorithm, String input,String salt) {
		LoginRegisterUtils.getInstance();
		String res = null;

		if(null == input) return null;

		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			digest.update(input.getBytes(), 0, input.length());

			res = new BigInteger(1, digest.digest()).toString(16);

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		return res;
	}

	private static synchronized void createFile (String user,int salt, String digest){
		LoginRegisterUtils.getInstance();
		User u = new User(user, ""+salt, digest);
		userList.add(u);

		File f = new File("PSW-LIST");
		f.delete();
		File fIV = new File("PSW-LIST.IV");
		fIV.delete();

		FileOutputStream fos;
		FileOutputStream fosIV;
		try {
			fos = new FileOutputStream(f);
			fosIV = new FileOutputStream(fIV);
			ByteObj cosa = EncDecFileSuite.encryptComplex(EncDecFileSuite.fromObjtoByte(userList), password);
			fosIV.write(cosa.getIVParameterSpec());
			fos.write(cosa.getEncriptedData());
			fos.close();
			fosIV.close();
		} catch ( IOException | GeneralSecurityException e) {
			e.printStackTrace();
			System.out.println("Errore nel salvataggio nuovo utente su disco");
		}

	}

	public static synchronized boolean usrExists(String usr) throws IOException {
		LoginRegisterUtils.getInstance();
		Iterator<User> it = userList.iterator();
		while(it.hasNext()){
			User temp = it.next();
			if(temp.getName().equals(usr))
				return true;
		}
		return false;
	}
}