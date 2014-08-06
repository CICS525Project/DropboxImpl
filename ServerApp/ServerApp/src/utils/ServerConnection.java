package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;



public class ServerConnection {

	@SuppressWarnings("finally")
	public boolean testConnection(String dns) {
		boolean flag = false;
		try {
			Socket telnetClient = new Socket();
			telnetClient.setSoTimeout(800);
			telnetClient.connect(new InetSocketAddress(dns, 9999), 800);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			BufferedReader stdin = new BufferedReader(new InputStreamReader(
					telnetClient.getInputStream()));
			PrintWriter out = new PrintWriter(telnetClient.getOutputStream());
			flag = true;
			input.close();
			stdin.close();
			out.close();
			telnetClient.close();
		} catch (Exception e) {
			// TODO: handle exception
			flag = false;
		} finally {
			return flag;
		}
	}

}
