package javaRMI;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
public class client {

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		remoteInterface hello = (remoteInterface)Naming.lookup("rmi://127.0.0.1:12349/remoteInterface");
		String msg = hello.say();
		System.out.println(msg);
	}

}
