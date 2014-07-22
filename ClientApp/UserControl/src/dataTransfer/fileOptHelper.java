package dataTransfer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

public class fileOptHelper {

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
	 * watch operations on files in the folder
	 * @param dir
	 * @throws IOException
	 */
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
	
	/**
	 * get all filenames in a folder
	 * return an arraylist of strings
	 * @param dir
	 * @return
	 */
	public static ArrayList<String> getFileInFolder(String dir){
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
}
