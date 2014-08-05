package userMetaData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import dataTransfer.ConfigurationData;

public class ServerConnectionTest {

	private static ArrayList<String> rDNS = new ArrayList<String>();

	public ServerConnectionTest() {
		rDNS.add(ConfigurationData.SERVICE_S1);
		rDNS.add(ConfigurationData.SERVICE_S2);
		rDNS.add(ConfigurationData.SERVICE_S3);
		rDNS.add(ConfigurationData.SERVICE_S4);
	}

	@SuppressWarnings("finally")
	public boolean testConnection(String dns) {
		boolean flag = false;
		try {
			Socket telnetClient = new Socket();
			telnetClient.setSoTimeout(200);
			telnetClient.connect(new InetSocketAddress(dns, 12345), 200);
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

	public String testDNSCtrl() {
		for (String d : rDNS) {
			System.out.println("inside control " + d);
			if (testConnection(d)) {
				return d;
			}
		}
		return null;
	}
	
	public String initialLink(){
		ArrayList<String> address = rDNS;
		int index = address.size();
		while(index > 0){
			Random rand = new Random();
			int accessInt = rand.nextInt(index);
			if(!testConnection(address.get(accessInt))){
				address.remove(accessInt);
				index = address.size();
			}else{
				return address.get(accessInt);
			}
		}
		return null;
	}
//	public static void main(String[] args) {
//		ServerConnectionTest sct = new ServerConnectionTest();
//		String res = sct.initialLink();
//		if (res != null) {
//			System.out.println("test server " + res + " is running.");
//		} else {
//			System.out.println("nothing is running..");
//		}
//	}
}
