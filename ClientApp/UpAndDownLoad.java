package UserControl;

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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
public class UpAndDownLoad {

	private static Socket client;
	private static DataInputStream streamReader;
	private static DataOutputStream streamWriter;
	/**
	 * hashFile: used for calculating SHA-1 value for a file
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static byte[] hashFile(String filename) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		InputStream is = Files.newInputStream(Paths.get(filename));
		DigestInputStream dis = new DigestInputStream(is, md);
		return md.digest();
	}
	
	/**
	 * getHashCode: used for calculating SHA-1 code from digset
	 * @param sha
	 * @return
	 */
	public static String getHashCode(byte[] sha){
		String result = "";
		for (int i=0; i < sha.length; i++) {  
		       result += Integer.toString( ( sha[i] & 0xff ) + 0x100, 16).substring(1); 
		   } 
		return result;
	}
	/**
	 * create TCP socket link
	 * @param host
	 * @param port
	 */
	public static Socket linkToServer(String host, int port){
			try {
				client = new Socket(host, port);
				OutputStream out = client.getOutputStream();
				streamWriter = new DataOutputStream(out);
				InputStream in = client.getInputStream();
				streamReader = new DataInputStream(in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return client;
	}
	public static void upoLoadFile(String FilePath) throws IOException{
		try {
			String msg = "upload";
			streamWriter.writeUTF(msg);
			System.out.println("message sent: "+msg);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String filename = getFilename(FilePath);
		System.out.println("filename is " + filename);
		String addresses = streamReader.readUTF();
		String [] part = addresses.split(",");
		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(part[1]);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			String [] part2 = part[0].split("/");
			String containerName = part2[part2.length-1];
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			container.createIfNotExist();
			CloudBlockBlob blob = container.getBlockBlobReference(filename);
			File source = new File(FilePath);
			blob.upload(new FileInputStream(source), source.length());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getFilename(String path){
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String [] splitted = path.split(pattern);
		return splitted[splitted.length-1];
	}
	public static ArrayList<String> getRemoteList() throws IOException{
		ArrayList<String> list = new ArrayList<String>();
		try {
			String msg = "download";
			streamWriter.writeUTF(msg);
			System.out.println("message sent: "+msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String addresses = streamReader.readUTF();
		String [] part = addresses.split(",");
		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(part[1]);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			String [] part2 = part[0].split("/");
			System.out.println("container name is: "+ part2[part2.length-1]);
			String containerName = part2[part2.length-1];
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			System.out.println("Here is a list of files in the remote container: ");
			for (ListBlobItem blobItem : container.listBlobs()) {
				if(blobItem instanceof CloudBlob)
				{
					CloudBlob blob = (CloudBlob) blobItem;
					list.add(blob.getName());
					System.out.println(blob.getName());
				}
			}
		}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		return list;
	}
	
	public static void downLoadFile(String filePath, String fileName) throws IOException{
		filePath = filePath+"\\"+fileName;
		try {
			String msg = "download";
			streamWriter.writeUTF(msg);
			System.out.println("message sent: "+msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String addresses = streamReader.readUTF();
		String [] part = addresses.split(",");
		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(part[1]);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			String [] part2 = part[0].split("/");
			System.out.println("container name is: "+ part2[part2.length-1]);
			String containerName = part2[part2.length-1];
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			System.out.println("path is " + filePath);
			for (ListBlobItem blobItem : container.listBlobs()) {
				if(blobItem instanceof CloudBlob)
				{
					CloudBlob blob = (CloudBlob) blobItem;
					if(fileName.equals(blob.getName())){
						blob.download(new FileOutputStream(filePath));
						System.out.println("Your file has been downloaded.");
						return;
					}
				}
			}
			System.out.println("Cannot find the file you selected.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void watchFile(Path dir) throws IOException{
		WatchService watcher = FileSystems.getDefault().newWatchService();
		dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		while (true) {
			try {
				WatchKey key = watcher.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind kind = event.kind();
					if(kind == OVERFLOW){
						continue;
					}
					WatchEvent<Path> e = (WatchEvent<Path>)event;
					Path fileName = e.context();
					System.out.println("Event "+kind.name()+ " happened, which filename is " + fileName);
				}
				if(!key.reset()){
					break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static boolean signIn(String uname, String upass) throws IOException{
		//linkToServer("cics525group6.cloudapp.net", 12345);
		String msg = "auth," + uname + ","+upass;
		streamWriter.writeUTF(msg);
		String res = streamReader.readUTF();
		if(res.equals("true")){
			return true;
		}
		return false;
	}
	public static boolean signUp(String uname, String upass) throws IOException{
		String msg = "crea,"+uname+","+upass;
		streamWriter.writeUTF(msg);
		String res = streamReader.readUTF();
		if(res.equals("true")){
			return true;
		}
		return false;
	}
	public static void closeConnection() throws IOException{
		streamWriter.writeUTF("Q");
		client.close();
	}
	public static void main(String[] args) throws IOException {
		Path p = Paths.get("/Users/haonanxu/Desktop/download");
		char file_spliter = File.separatorChar;
		watchFile(p);
	}
}
