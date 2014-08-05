package userGUI;

import java.io.File;

import dataTransfer.*;

public class StartGUI {

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
		}else{
			SessionInfo.getInstance().setWorkFolder(workFolder);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String initialDNS = ConfigurationData.SERVICE_S3;
		SessionInfo.getInstance().setRemoteDNS(
				initialDNS);
		SessionInfo.getInstance().setPortNum(ConfigurationData.PORT_NUM);
		SignIn frame = new SignIn();
		frame.setVisible(true);
		setUpWorkFolder();
	}

}