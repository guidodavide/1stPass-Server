package com.serverSSL.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLSocket;

import com.serverSSL.utils.LoginRegisterUtils;

public class RegisterThread extends Thread {

	private String psw,usr;
	private SSLSocket skt;
	private static Date now;
	private static SimpleDateFormat time;
	private static String client;

	public RegisterThread(SSLSocket sslsocket, String client) {
		this.skt = sslsocket;
		RegisterThread.client = client;
	}

	@Override
	public void run() {
		super.run();
		now = new Date();
		System.out.println(timeNow()+"Avviato");
		
		InputStream inputstream;
		try {

			inputstream = skt.getInputStream();

			InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
			BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
			
			OutputStream outputstream = skt.getOutputStream();
			OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
			BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);
			
			
			usr = bufferedreader.readLine();
			psw = bufferedreader.readLine();
			
			System.out.println(timeNow()+"Richiesta Registrazione per " + usr);
			
			if(!LoginRegisterUtils.usrExists(usr)){
				
				LoginRegisterUtils.register(usr, psw);
//				System.out.println(timeNow()+usr + " inserito tra i logged");
//				Monitor.getInstance().signInUser(usr);
				
				bufferedwriter.write("SI\n");
				bufferedwriter.flush();
				
				System.out.println(timeNow()+usr + " registrato");
				SecureRandom sfidaRandom = new SecureRandom();
				double sfidaNum = sfidaRandom.nextDouble();
				bufferedwriter.write("" + sfidaNum + "\n");
				bufferedwriter.flush();
				//TODO mandare al client il random altrimenti come fa dopo?
//				FirstPassThread fpt = new FirstPassThread(skt, usr, skt.getInetAddress().toString(), psw, EncDecSocket.toByteArray(sfidaNum));
////				fpt.start();
//				try {
//					System.out.println(timeNow()+"Attendo FirstPassThread");
////					fpt.join();
//					System.out.println(timeNow()+"FirstPassThread chiuso");
//					Monitor.getInstance().signOutUser(usr);
//					System.out.println(timeNow()+ usr + " rimosso da i logged");
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//					Monitor.getInstance().signOutUser(usr);
//					System.out.println(timeNow()+ usr + " rimosso da i logged");
//				}
				
			}
			else {
				System.out.println(timeNow()+usr + " utente già esistente");
				bufferedwriter.write("NO\n");
				bufferedwriter.flush();
				
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String timeNow(){
		time = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
		return "[" + time.format(now) + " - RegisterThread " + client +"]";
	}

}
