package dataTransfer;


import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import RMIInterface.ServiceServerInterface;
public class UserOperate {

	private ServiceServerInterface serviceProvider;
	private static FileOptHelper helper;

	/**
	 * constrctor
	 * 
	 * @param hostname
	 */
	public UserOperate(String hostname, int port) {
		try {
			Registry registry = LocateRegistry.getRegistry(hostname, port);
			this.serviceProvider = (ServiceServerInterface) registry
					.lookup("cloudboxRMI");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public String getFilename(String path) {
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String[] splitted = path.split(pattern);
		return splitted[splitted.length - 1];
	}

	/**
	 * user sign in to server
	 * 
	 * @param uname
	 * @param upass
	 * @return
	 * @throws IOException
	 */
	public boolean signIn(String uname, String upass) throws IOException {

		if (serviceProvider.login(uname, upass)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * user sign up in the system
	 * @param uname
	 * @param upass
	 * @return
	 * @throws RemoteException
	 */
	public boolean signUp(String uname, String upass) throws RemoteException{
		if(serviceProvider.signIn(uname, upass)){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @param files
	 * @return
	 */
	public HashMap<String, String> getFileInFolderAddress(){
		HashMap<String, String> res = new HashMap<String, String>();
		helper = new FileOptHelper();
		ArrayList<String> files = helper.getFileInFolder(SessionInfo.getInstance().getWorkFolder());
		try {
			res = serviceProvider.getAddress(files, SessionInfo.getInstance().getUsername());
//			System.out.println("get loacal hashmap length : " + res.size());
			for (int i = 0; i < res.size() ; i++) {
				System.out.println(res.get(files.get(i)));
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public String getOneFileAddress(String filename){
		HashMap<String, String> res = new HashMap<String, String>();
		ArrayList<String> file = new ArrayList<String>();
		String address = "";
		file.add(filename);
		try {
			res = serviceProvider.getAddress(file, SessionInfo.getInstance().getUsername());
			address = res.get(filename);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return address;
	}
	
	/**
	 * get all files visible to a user <file, version>
	 * @param uname
	 * @return
	 */
	public HashMap<String, Integer> getServerVersion(String uname){
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		try {
			res = serviceProvider.getCurrentFiles(uname); 
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * 
	 * @return
	 */
	public String fileContainer(){
		String containerKey = null;
		try {
			containerKey = serviceProvider.getContainer();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return containerKey;
	}
	
	public void shareFile(String[] files, String shareUser){
		HashMap<String, String> shareList = new HashMap<String,String>();
		String username = SessionInfo.getInstance().getUsername();
		for(String fname:files){
			shareList.put(fname, shareUser);
		}
		try {
			serviceProvider.shareFile(shareList, username);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
