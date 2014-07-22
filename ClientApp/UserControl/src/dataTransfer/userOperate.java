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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
public class userOperate {

	private static Socket client;
	private static DataInputStream streamReader;
	private static DataOutputStream streamWriter;

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
	
	/**
	 * upload file to Azure container
	 * filePath is the path for file that needs to be uploaded.
	 * @param FilePath
	 * @throws IOException
	 */
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
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String getFilename(String path){
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String [] splitted = path.split(pattern);
		return splitted[splitted.length-1];
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
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
	
	/**
	 * download file from Azure container
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	public static void downLoadFile(String filePath, String fileName) throws IOException{
		String spliter = File.separator;
		filePath = filePath+spliter+fileName;
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
	
	/**
	 * user sign in to server
	 * @param uname
	 * @param upass
	 * @return
	 * @throws IOException
	 */
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
	
	/**
	 * user sign up on the server
	 * @param uname
	 * @param upass
	 * @return
	 * @throws IOException
	 */
	public static boolean signUp(String uname, String upass) throws IOException{
		String msg = "crea,"+uname+","+upass;
		streamWriter.writeUTF(msg);
		String res = streamReader.readUTF();
		if(res.equals("true")){
			return true;
		}
		return false;
	}
	
	/**
	 * close tcp socket connections between client and user
	 * @throws IOException
	 */
	public static void closeConnection() throws IOException{
		streamWriter.writeUTF("Q");
		client.close();
	}
}
