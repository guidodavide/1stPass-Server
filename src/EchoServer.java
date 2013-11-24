import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;


public class EchoServer {
	private static SSLServerSocketFactory sslServerSocketFactory;
	private static SSLServerSocket sslServerSocket;
	private static SSLSocket sslSocket;
	private static KeyStore keystore;
	private static KeyManager[] keyManagers;
	private static SSLContext sslContext;
	private static SSLSession sslSession;
	private static int port = 9999;
	private static final Character EOL = '\n';
	private static final Character EOF = '\u0017';

	public static void main (String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyManagementException{

		char[] passphrase = "myComplexPass1".toCharArray();
		keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(new FileInputStream("cacerts"), passphrase);

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keystore, passphrase);

		sslContext = SSLContext.getInstance("TLSv1.2");

		keyManagers = keyManagerFactory.getKeyManagers();

		sslContext.init(keyManagers, null, null);

		sslServerSocketFactory = sslContext.getServerSocketFactory();
		sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
		sslServerSocket.setSoTimeout(30000);
		sslServerSocket.setEnabledProtocols(new String [] { "TLSv1", "TLSv1.1", "TLSv1.2" });
		sslServerSocket.setUseClientMode(false);
		sslServerSocket.setWantClientAuth(false);
		sslServerSocket.setNeedClientAuth(false);

		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader input = null;
		OutputStream outputStream = null;
		PrintWriter output = null;

		try
		{
			System.out.println("Server started");

			System.out.println("  Waiting for connection from client...");
			sslSocket = (SSLSocket)sslServerSocket.accept();

			// Connection was accepted
			System.out.println("  Accepted connection from " + sslSocket.getInetAddress().getHostAddress() + ", port " + sslSocket.getPort());

			// set up a SSL session
			sslSession = sslSocket.getSession();
			System.out.println("  Cipher suite used for this session: " + sslSession.getCipherSuite());

			printSocketInfo(sslSocket,sslSession);
			inputStream = (InputStream) sslSocket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			input = new BufferedReader(inputStreamReader);

			outputStream = sslSocket.getOutputStream();
			output = new PrintWriter(outputStream);

			System.out.println("  Server -> receiving...");
			StringBuffer receiver = new StringBuffer();
			Character serverReceived;
			while ((serverReceived = (char)input.read()) != EOF)
			{
				receiver.append(serverReceived);
			}
			System.out.println("    Server received: " + serverReceived);

			System.out.println("  Server -> sending...");

			String serverSendSuccess = "Hello client, how are you?" + EOL + EOF;
			String serverSendFail = "Who are you?" + EOL + EOF;

			if (receiver.toString().contains("Hello server! I am the client!"))
			{
				System.out.println("    Server sent: " + serverSendSuccess);
				output.println(serverSendSuccess);
				output.flush();
			}
			else
			{
				System.out.println("    Server sent: " + serverSendFail);
				output.println(serverSendFail);
				output.flush();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				inputStream.close();
				outputStream.close();
				sslSocket.close();
			}
			catch(Exception ex) {}
			System.out.println("Server ended");
		}
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

