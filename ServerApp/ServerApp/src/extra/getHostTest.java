package extra;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class getHostTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try {
		      InetAddress address = InetAddress.getLocalHost();
		      System.out.println("My name is " + address.getHostName());
		    } catch (UnknownHostException e) {
		      System.out.println("I'm sorry. I don't know my own name.");
		    }

	}

}
