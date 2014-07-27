package dataTransfer;

import com.microsoft.windowsazure.services.blob.client.*;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.file.Path;
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
	 * upload file to Azure container filePath is the path for file that needs
	 * to be uploaded.
	 * 
	 * @param FilePath
	 * @throws IOException
	 */
	public void upoLoadFile(String FilePath) throws IOException {

//		String filename = getFilename(FilePath);
//		System.out.println("filename is " + filename);
//		String addresses = streamReader.readUTF();
//		String[] part = addresses.split(",");
//		CloudStorageAccount storageAccount;
//		try {
//			storageAccount = CloudStorageAccount.parse(part[1]);
//			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
//			String[] part2 = part[0].split("/");
//			String containerName = part2[part2.length - 1];
//			CloudBlobContainer container = blobClient
//					.getContainerReference(containerName);
//			container.createIfNotExist();
//			CloudBlockBlob blob = container.getBlockBlobReference(filename);
//			File source = new File(FilePath);
//			blob.upload(new FileInputStream(source), source.length());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
	public HashMap<String, String> getFileAddress(){
		HashMap<String, String> res = new HashMap<String, String>();
		helper = new FileOptHelper();
		ArrayList<String> files = helper.getFileInFolder(sessionInfo.getInstance().getWorkFolder());
		try {
			res = serviceProvider.getAddress(files, sessionInfo.getInstance().getUsername());
			System.out.println("get hashmap length : " + res.size());
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
}
