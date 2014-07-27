package dataTransfer;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import userMetaData.ClientMetaData;

public class FileOptHelper {

	/**
	 * hashFile: used for calculating SHA-1 value for a file
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public byte[] hashFile(String filename) throws Exception
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
	public String getHashCode(byte[] sha){
		String result = "";
		for (int i=0; i < sha.length; i++) {  
		       result += Integer.toString( ( sha[i] & 0xff ) + 0x100, 16).substring(1); 
		   } 
		return result;
	}
	
	
	/**
	 * get all filenames in a folder
	 * return an arraylist of strings
	 * @param dir
	 * @return
	 */
	public ArrayList<String> getFileInFolder(String dir){
		File folder = new File(dir);
		File [] fileList = folder.listFiles();
		ArrayList<String> files = new ArrayList<String>();
		for(int i = 0; i < fileList.length; i++){
			if(fileList[i].isFile() && !fileList[i].getName().startsWith(".") && !fileList[i].getName().endsWith("xml")){
				System.out.println("File name is: " + fileList[i].getName());
				files.add(fileList[i].getName());
			}
		}
		return files;
	}
	
	/**
	 * compare if there are two different version numbers of a file, yes then add to download queue
	 * initialize download queue when user logs in
	 */
	public void initialDownloadQueue(){
		String dir = sessionInfo.getInstance().getWorkFolder();
		UserOperate uopt = new UserOperate(sessionInfo.getInstance().getRemoteDNS(),12345);
		ClientMetaData cmd = new ClientMetaData();
		cmd.removeRecord(dir, getFileInFolder(dir));
		HashMap<String, String> localFileAndVersion = cmd.readXML(dir, getFileInFolder(dir));
		HashMap<String, Integer> remoteFileAndVersion = uopt.getServerVersion(sessionInfo.getInstance().getUsername());
		ArrayList<String> remoteFileList = new ArrayList<String>(remoteFileAndVersion.keySet());
		for (int i = 0; i < remoteFileList.size(); i++) {
			String fname = remoteFileList.get(i);
			//if file exists on both local and remote
			if(localFileAndVersion.containsKey(fname)){
				//if local version is not same as remote version
				if(!localFileAndVersion.get(fname).equals(remoteFileAndVersion.get(fname).toString())){
					/*******Simply add the download request into the download queue*******/
					OperationQueue.getInstance().add(fname, OperationQueue.getInstance().getDownloadQueue());
				}
			}
		}
	}
}
