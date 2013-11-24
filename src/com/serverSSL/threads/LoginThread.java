package com.serverSSL.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSocket;

import com.serverSSL.utils.EncDecSocket;
import com.serverSSL.utils.LoginRegisterUtils;

public class LoginThread extends Thread {

	private SSLSocket sslskt;
	private static Date now;
	private static SimpleDateFormat time;
	private static String client;


	public LoginThread(SSLSocket sslskt, String client) {
		super();
		this.sslskt = sslskt;
		LoginThread.client = client;
	}



	@Override
	public void run() {
		super.run();
		now = new Date();
		System.out.println(timeNow()+"Avviato");
		
		InputStream inputstream;
		try {

			inputstream = sslskt.getInputStream();

			InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
			BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

			OutputStream outputstream = sslskt.getOutputStream();
			OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
			BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);

			//Meccanismo sfida risposta
			SecureRandom sfidaRandom = new SecureRandom();
			double sfidaNum = sfidaRandom.nextDouble();
			bufferedwriter.write("" + sfidaNum + "\n");
			bufferedwriter.flush();
			String risposta = bufferedreader.readLine();

			StringTokenizer tokenizer = new StringTokenizer(risposta, "\t");
			double rispostaNum = Double.parseDouble(tokenizer.nextToken());
			String usr = tokenizer.nextToken();
			String psw = tokenizer.nextToken();

			System.out.println(timeNow()+"Richiesta Login per " + usr);
			if(LoginRegisterUtils.isPresent(usr,psw) && !Monitor.getInstance().contains(usr) && (rispostaNum == sfidaNum + 1)){

				bufferedwriter.write("SI\n");
				bufferedwriter.flush();
				System.out.println(timeNow()+usr + " entrato");
				Monitor.getInstance().signInUser(usr);
				System.out.println(timeNow()+ usr + " è LOGGATO");
				FirstPassThread fpt = new FirstPassThread(sslskt, usr, sslskt.getInetAddress().toString(), psw,  EncDecSocket.toByteArray(sfidaNum));
				fpt.start();
				try {
					System.out.println(timeNow()+"Attendo FirstPassThread");
					fpt.join();
					System.out.println(timeNow()+"FirstPassThread chiuso");
					Monitor.getInstance().signOutUser(usr);
					System.out.println(timeNow()+ usr + " è FUORI");
				} catch (InterruptedException e) {
					Monitor.getInstance().signOutUser(usr);
					System.out.println(timeNow()+ usr + " è FUORI");
					e.printStackTrace();
				}
			}
			else {
				System.out.println(timeNow()+usr + " non accettato");
				bufferedwriter.write("NO\n");
				bufferedwriter.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String timeNow(){
		time = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
		return "[" + time.format(now) + " - LoginThread " + client +"]";
	}


}
