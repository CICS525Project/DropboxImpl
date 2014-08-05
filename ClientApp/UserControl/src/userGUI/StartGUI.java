package userGUI;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import RMIInterface.ServiceServerInterface;
import dataTransfer.*;

public class StartGUI {

	private static String remoteDNS = null;

	public static void setUpWorkFolder() {
		String homePath = System.getProperty("user.home");
		String workFolder = homePath + File.separator + "Cloudbox";
		File thedir = new File(workFolder);
		if (!thedir.exists()) {
			try {
				thedir.mkdir();
				SessionInfo.getInstance().setWorkFolder(workFolder);
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		} else {
			SessionInfo.getInstance().setWorkFolder(workFolder);
		}
	}

	public static String selectServer() {
		ArrayList<String> rDNS = new ArrayList<String>();
		rDNS.add(ConfigurationData.SERVICE_S1);
		rDNS.add(ConfigurationData.SERVICE_S2);
		rDNS.add(ConfigurationData.SERVICE_S3);
		rDNS.add(ConfigurationData.SERVICE_S4);
		rDNS.add(ConfigurationData.SERVICE_B1);
		rDNS.add(ConfigurationData.SERVICE_B2);
		try {
			ServiceServerInterface ssi = null;
			for (String DNS : rDNS) {
				Registry registry = LocateRegistry.getRegistry(DNS,
						ConfigurationData.PORT_NUM);
				ssi = (ServiceServerInterface) registry.lookup("cloudboxRMI");
				if (ssi != null) {
					remoteDNS = DNS;
					return remoteDNS;
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("1");
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("2");
		}
		return remoteDNS;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String initialDNS = selectServer();
		if (initialDNS == null) {
			JOptionPane.showMessageDialog(null,
					"Sorry! All server is down!!");
		} else {
			SessionInfo.getInstance().setRemoteDNS(initialDNS);
			SessionInfo.getInstance().setPortNum(ConfigurationData.PORT_NUM);
			SignIn frame = new SignIn();
			frame.setVisible(true);
			setUpWorkFolder();
		}
	}

}
