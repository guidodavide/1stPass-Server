package com.serverSSL.threads;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.SSLSocket;


import com.firstpass.model.Site;
import com.serverSSL.utils.*;


public class FirstPassThread extends Thread {

	private SSLSocket sslskt;
	private String usr;
	private static Date now;
	private static SimpleDateFormat time;
	private static String client;
	private String password;
	private static final String PATH = "\\Users1stPass\\";
	private byte[] salt;



	public FirstPassThread(SSLSocket sslskt, String usr, String client, String password, byte[] salt) {
		super();
		this.sslskt = sslskt;
		this.usr = usr;
		FirstPassThread.client = client;
		this.password = password;
		this.salt = salt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		super.run();
		now = new Date();
		System.out.println(timeNow()+"Avviato per " + usr);

		String code="#START#";
		
		String finalPath = new File("").getAbsolutePath() + PATH + usr;
		String finalPathIV = finalPath + ".IV";

		while(!code.equals("#CLOSE#")){
			InputStream is;
			try {
				
				is = sslskt.getInputStream();
				
				//TODO Già qui è cifrato il canale con sicurezza aggiunta
				InputStreamReader inputstreamreader = new InputStreamReader(is);
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

				code = bufferedreader.readLine();
				
				if(code == null){
					System.out.println(timeNow()+"letto null. Termino");
					break;
				}

				if(code.equals("#LOAD_LIST#")){
					//leggo il file dell'utente
					System.out.println(timeNow()+"Richiesta lista per " + usr);
					
					File f = new File(finalPath);
					File fIV = new File(finalPathIV);

					if (!f.exists() || !fIV.exists()){
						FileOutputStream out = new FileOutputStream(f);
						FileOutputStream outIV = new FileOutputStream(fIV);
						ByteObj cosa = EncDecFileSuite.encryptComplex((EncDecFileSuite.fromObjtoByte(new ArrayList<Site>())), password);
						out.write(cosa.getEncriptedData());
						outIV.write(cosa.getIVParameterSpec());
						out.close();
						outIV.close();
						
					}
					ArrayList<Site> res=null;
					try
					{
						Path path = Paths.get(finalPath);
						Path pathIV = Paths.get(finalPathIV);
						byte[] letto = Files.readAllBytes(path);
						byte[] lettoIV = Files.readAllBytes(pathIV);
	
						Object o = EncDecFileSuite.fromBytetoObj(EncDecFileSuite.decryptComplex(new ByteObj(lettoIV, letto), password));
						res = (ArrayList<Site>) o;
					}catch(IOException i)
					{
						i.printStackTrace();
						System.out.println(timeNow()+"!ERRORE! con file");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					System.out.println(timeNow()+"Invio lista per " + usr);
					
					byte[] writable = EncDecSocket.encrypt((EncDecSocket.ObjtoByte(res)), password, salt);
					
					DataOutputStream dos = new DataOutputStream(sslskt.getOutputStream());
					dos.writeInt(writable.length);
//					dos.flush();
					dos.write(writable);
//					dos.flush();
					
//					ObjectOutputStream out  = new ObjectOutputStream(sslskt.getOutputStream());
//					out.writeObject(res);

				}
				else if(code.equals("#UPDATE#")){
					System.out.println(timeNow()+"Richiesta update per " + usr);
//					ObjectInputStream in = new ObjectInputStream(sslskt.getInputStream());
					DataInputStream dis = new DataInputStream(sslskt.getInputStream());
					int size = dis.readInt();
					byte[] bytes = new byte[size];
					dis.readFully(bytes);

					
					ArrayList<Site> res = (ArrayList<Site>)EncDecSocket.BytetoObj((EncDecSocket.decrypt(bytes,password, salt)));
					
					File myFile = new File (finalPath);
					File myFileIV = new File(finalPathIV);
					myFile.delete();
					myFileIV.delete();

					FileOutputStream fos = new FileOutputStream(myFile);
					FileOutputStream fosIV = new FileOutputStream(myFileIV);

					ByteObj cosa = EncDecFileSuite.encryptComplex((EncDecFileSuite.fromObjtoByte(res)), password);
					fos.write(cosa.getEncriptedData());
					fosIV.write(cosa.getIVParameterSpec());
					fos.close();
					fosIV.close();
				}

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				break;
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private static String timeNow(){
		time = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
		return "[" + time.format(now) + " - FirstPass " + client +"]";
	}

}
