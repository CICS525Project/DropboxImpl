package javaRMI;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class serverReg {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			remoteInterface h = new remoteInterfaceImpl("hellow world");
			LocateRegistry.createRegistry(12349);
			Naming.rebind("rmi://127.0.0.1:12349/remoteInterface", h);
			System.out.println("server start.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
