package com.serverSSL.main;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class AcceptServer {
	private static Date now;
	//nuovo
	private static SimpleDateFormat time;
	private static KeyStore keystore;
	private static SSLContext sslContext;
	private static KeyManager[] keyManagers;
	private static SSLServerSocketFactory sslServerSocketFactory;
	private static SSLServerSocket sslServerSocket;
	//nuovo
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		now = new Date();
		System.out.println(timeNow()+"Avviato");
		
//		SecureRandom secure = new SecureRandom();
//		
//		byte [] howMuch = new byte[8];
//		secure.nextBytes(howMuch);
//		
//		for (byte b : howMuch) {
//			   System.out.format("0x%x ", b);
//			}
		
		//nuovo
		char[] passphrase = "myComplexPass1".toCharArray();
		try {
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(new FileInputStream("cacerts"), passphrase);
			System.out.println(timeNow()+"Keystore inizializzato");

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keystore, passphrase);
			//^nuovo

			//			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			//			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(9999);

			sslContext = SSLContext.getInstance("TLSv1.2");
			keyManagers = keyManagerFactory.getKeyManagers();
			sslContext.init(keyManagers, null, null);
			sslServerSocketFactory = sslContext.getServerSocketFactory();
			sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(8080);
			
			sslServerSocket.setEnabledProtocols(new String [] { "TLSv1", "TLSv1.1", "TLSv1.2" });
			sslServerSocket.setUseClientMode(false);
			sslServerSocket.setWantClientAuth(false);
			sslServerSocket.setNeedClientAuth(false);
			
			System.out.println(timeNow()+"Server Socket creata");
			
			while (true) {
				SSLSocket sslsocket = (SSLSocket) sslServerSocket.accept();
				System.out.println(timeNow()+"Accettata richiesta");
				SSLSession ss = sslsocket.getSession();
				printSocketInfo(sslsocket,ss);

				if(!ss.getCipherSuite().equals("SSL_NULL_WITH_NULL_NULL")){
					HandlerThread ht = new HandlerThread(sslsocket, sslsocket.getInetAddress().toString());
					ht.start();
				}

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static String timeNow(){
		time = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
		return "[" + time.format(now) + " - AcceptServer] ";
	}

	private static void printSocketInfo(SSLSocket s, SSLSession ss) {
		System.out.println("Socket class: "+s.getClass());
		System.out.println("   Remote address = "
				+s.getInetAddress().toString());
		System.out.println("   Remote port = "+s.getPort());
		System.out.println("   Local socket address = "
				+s.getLocalSocketAddress().toString());
		System.out.println("   Local address = "
				+s.getLocalAddress().toString());
		System.out.println("   Local port = "+s.getLocalPort());
		System.out.println("   Need client authentication = "
				+s.getNeedClientAuth());
		System.out.println("   Cipher suite = "+ss.getCipherSuite());
		System.out.println("   Protocol = "+ss.getProtocol());
	}

}
