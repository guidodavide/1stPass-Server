package com.serverSSL.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLSocket;

import com.serverSSL.threads.LoginThread;
import com.serverSSL.threads.RegisterThread;

public class HandlerThread extends Thread {

	private SSLSocket sslskt;
	private static Date now;
	private static SimpleDateFormat time;
	private boolean stop;
	private static String client;

	public HandlerThread (SSLSocket sslskt, String client){
		this.sslskt = sslskt;
		this.stop = false;
		HandlerThread.client = client;
	}

	@Override
	public void run() {
		super.run();
		now = new Date();
		System.out.println(timeNow()+"Avviato");
		do{
			InputStream inputstream;
			try {
				inputstream = sslskt.getInputStream();
				InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

				//Leggo il codice corrispondente alla interazione richiesta
				String opt = bufferedreader.readLine();

				if(opt == null){
					System.out.println(timeNow()+"Applicazione Client Chiusa malamente");
					break;
				}
				switch (opt) {
				case "#L#"://Caso Login

					System.out.println(timeNow()+"Richiesta Login");

					LoginThread lt = new LoginThread(sslskt, sslskt.getInetAddress().toString());
					lt.start();
					try {
						System.out.println(timeNow()+"Attendo LoginThread");
						lt.join();
						System.out.println(timeNow()+"LoginThread chiuso");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case "#R#"://Caso Registrazione

					System.out.println(timeNow()+"Richiesta Registrazione nuovo utente");

					RegisterThread rt = new RegisterThread(sslskt, sslskt.getInetAddress().toString());
					rt.start();
					try {
						System.out.println(timeNow()+"Attendo RegisterThread");
						rt.join();
						System.out.println(timeNow()+"RegisterThread chiuso");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case "#S#"://Caso applicazione Chiusa

					System.out.println(timeNow()+"Applicazione Chiusa");
					stop = true;
					
					break;
				default:
					System.exit(-1);
					break;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(timeNow()+"!ERRORE nella gestione socket!");
				e.printStackTrace();
				
			}

		}while(stop==false);
		System.out.println(timeNow()+"si chiude");
	}

	private static String timeNow(){
		time = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
		return "[" + time.format(now) + " - HandlerThread " + client +"]";
	}



}
