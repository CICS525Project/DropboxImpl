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
import dataTransfer.*;
public class userOperate {

	private ServiceServerInterface serviceProvider;
	private static fileOptHelper helper;
	private ArrayList<String> filenames;
	private static userOperate singleton = null;
	private static String hostname;
	private static int port;
	private static String folder;
	private userOperate(){}
	public static userOperate getInstance(){
		if(singleton == null){
			singleton = new userOperate(hostname,port,folder);
		}
		return singleton;
	}
	/**
	 * constrctor
	 * 
	 * @param hostname
	 */
	public userOperate(String hostname, int port, String folderPath) {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
			this.serviceProvider = (ServiceServerInterface) registry
					.lookup("cloudboxRMI");
			helper = new fileOptHelper();
			filenames = helper.getFileInFolder(folderPath);
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
	 * 
	 * @return
	 * @throws IOException
	 */
	public ArrayList<String> getRemoteFileList() throws IOException {
		ArrayList<String> list = new ArrayList<String>();
//		try {
//			String msg = "download";
//			streamWriter.writeUTF(msg);
//			System.out.println("message sent: " + msg);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		String addresses = streamReader.readUTF();
//		String[] part = addresses.split(",");
//		CloudStorageAccount storageAccount;
//		try {
//			storageAccount = CloudStorageAccount.parse(part[1]);
//			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
//			String[] part2 = part[0].split("/");
//			System.out.println("container name is: " + part2[part2.length - 1]);
//			String containerName = part2[part2.length - 1];
//			CloudBlobContainer container = blobClient
//					.getContainerReference(containerName);
//			System.out
//					.println("Here is a list of files in the remote container: ");
//			for (ListBlobItem blobItem : container.listBlobs()) {
//				if (blobItem instanceof CloudBlob) {
//					CloudBlob blob = (CloudBlob) blobItem;
//					list.add(blob.getName());
//					System.out.println(blob.getName());
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
		return list;
	}

	/**
	 * download file from Azure container
	 * 
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	public void downLoadFile(String filePath, String fileName)
			throws IOException {
//		String spliter = File.separator;
//		filePath = filePath + spliter + fileName;
//		try {
//			String msg = "download";
//			streamWriter.writeUTF(msg);
//			System.out.println("message sent: " + msg);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		String addresses = streamReader.readUTF();
//		String[] part = addresses.split(",");
//		CloudStorageAccount storageAccount;
//		try {
//			storageAccount = CloudStorageAccount.parse(part[1]);
//			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
//			String[] part2 = part[0].split("/");
//			System.out.println("container name is: " + part2[part2.length - 1]);
//			String containerName = part2[part2.length - 1];
//			CloudBlobContainer container = blobClient
//					.getContainerReference(containerName);
//			System.out.println("path is " + filePath);
//			for (ListBlobItem blobItem : container.listBlobs()) {
//				if (blobItem instanceof CloudBlob) {
//					CloudBlob blob = (CloudBlob) blobItem;
//					if (fileName.equals(blob.getName())) {
//						blob.download(new FileOutputStream(filePath));
//						System.out.println("Your file has been downloaded.");
//						return;
//					}
//				}
//			}
//			System.out.println("Cannot find the file you selected.");
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
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
	public boolean signUp(String uname, String upass) throws RemoteException{
		if(serviceProvider.signIn(uname, upass)){
			return true;
		}
		return false;
	}
	public HashMap<String, String> getFileAddress(ArrayList<String> files){
		HashMap<String, String> res = new HashMap<String, String>();
		try {
			res = serviceProvider.getAddress(files, "jitin");
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
	
	public void startWatcher(Path dir){
		try {
			helper.watchFile(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
	public ArrayList<String> getFileList(){
		return filenames;
	}

	public static String getHostname() {
		return hostname;
	}
	public static void setHostname(String hostname) {
		userOperate.hostname = hostname;
	}
	public static int getPort() {
		return port;
	}
	public static void setPort(int port) {
		userOperate.port = port;
	}
	public static String getFolder() {
		return folder;
	}
	public static void setFolder(String folder) {
		userOperate.folder = folder;
	}
	
}
