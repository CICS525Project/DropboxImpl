package dataTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.microsoft.windowsazure.services.blob.client.CloudBlob;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.ListBlobItem;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

import userGUI.ConflictPopUp;
import userGUI.ToolTip;
import userUtil.ClientMetaData;

public class FileOptHelper {

	private ToolTip myTip;

	/**
	 * hashFile: used for calculating SHA-1 value for a file
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public byte[] hashFile(String filePath) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		FileInputStream fis = new FileInputStream(filePath);
		byte[] dataBytes = new byte[1024];
		int nread = 0;
		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		// DigestInputStream dis = new DigestInputStream(is, md);
		return md.digest();
	}

	/**
	 * getHashCode: used for calculating SHA-1 code from digset
	 * 
	 * @param sha
	 * @return
	 */
	public String getHashCode(byte[] sha) {
		String result = "";
		for (int i = 0; i < sha.length; i++) {
			result += Integer.toString((sha[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return result;
	}

	/**
	 * get all filenames in a folder return an arraylist of strings
	 * 
	 * @param dir
	 * @return
	 */
	public ArrayList<String> getFileInFolder(String dir) {
		File folder = new File(dir);
		File[] fileList = folder.listFiles();
		ArrayList<String> files = new ArrayList<String>();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile() && !fileList[i].getName().startsWith(".") && !fileList[i].getName().startsWith("~")
					&& !fileList[i].getName().endsWith("xml")) {
				files.add(fileList[i].getName());
			}
		}
		return files;
	}

	/**
	 * 
	 * @param dir
	 */
	public void checkFileAndXML(String dir) {
		File folder = new File(dir);
		File[] fileList = folder.listFiles();
		for (File file : fileList) {
			if (file.getName().equals("file.xml")) {
				return;
			}
		}

	}

	/**
	 * Delete a file in a folder
	 * 
	 * @param dir
	 */
	public void deleteFileInFoler(String dir) {
		Path path = Paths.get(dir);
		try {
			Files.deleteIfExists(path);
			System.out.println("delete file: " + dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * compare if there are two different version numbers of a file, yes then
	 * add to download queue initialize download queue when user logs in
	 */
	public void initialDownloadQueue() {
		String dir = SessionInfo.getInstance().getWorkFolder();
		UserOperate uopt = new UserOperate(SessionInfo.getInstance()
				.getRemoteDNS(), SessionInfo.getInstance().getPortNum());
		ClientMetaData cmd = new ClientMetaData();
		cmd.removeRecord(dir, getFileInFolder(dir));
		HashMap<String, String> localFileAndVersion = cmd.readXML(dir,
				getFileInFolder(dir));
		HashMap<String, Integer> remoteFileAndVersion = uopt
				.getServerVersion(SessionInfo.getInstance().getUsername());
		ArrayList<String> remoteFileList = new ArrayList<String>(
				remoteFileAndVersion.keySet());
		for (int i = 0; i < remoteFileList.size(); i++) {
			String fname = remoteFileList.get(i);
			System.out.println("remote file: " + fname + " version " + remoteFileAndVersion.get(fname) + " local version " + localFileAndVersion.get(fname));
			// if file exists on both local and remote
			if (localFileAndVersion.containsKey(fname)) {
				// if local version is not same as remote version
				if (!localFileAndVersion.get(fname).equals(
						remoteFileAndVersion.get(fname).toString())) {
					/******* Simply add the download request into the download queue *******/
					if (remoteFileAndVersion.get(fname).equals(-1)) {
						// file is deleted in the remote folder
						// client will also deleted it.
						deleteFileInFoler(SessionInfo.getInstance()
								.getWorkFolder() + File.separator + fname);
					} else {
						// different version number
						// need to compare version numbers
						// only add to download queue when remote version number
						// is greater than the local version number
						int localVersion = Integer.parseInt(localFileAndVersion
								.get(fname));
						if (remoteFileAndVersion.get(fname) > localVersion) {
							System.out.println("detect file " + fname + "has higher version.");
							downLoadFileControl(fname);
						}
					}
				}
			} else {
				// new added just download
				if (!remoteFileAndVersion.get(fname).equals(-1)) {
					downLoadFileControl(fname);
				}
			}
		}
	}

	/**
	 * initilize uploading queue
	 */
	public void initilizeUploadQueue() {
		// get work space path
		String dir = SessionInfo.getInstance().getWorkFolder();
		ClientMetaData cmd = new ClientMetaData();
		FileOptHelper fopt = new FileOptHelper();
		UserOperate uopt = new UserOperate(SessionInfo.getInstance()
				.getRemoteDNS(), SessionInfo.getInstance().getPortNum());
		ArrayList<String> filesInfolder = fopt.getFileInFolder(dir);
		HashMap<String, Integer> remoteFileAndVersion = uopt
				.getServerVersion(SessionInfo.getInstance().getUsername());
		HashMap<String, String> localFileAndCheckSum = cmd.readHashCode(dir);
		HashMap<String, String> localFileAndVersion = cmd.readXML(dir,
				filesInfolder);
		// compare with local files and checksum and update version numbers
		for (String fname : filesInfolder) {
			try {
				System.out.println("checksum comp in folder.");
				String checks = fopt.getHashCode(fopt.hashFile(dir
						+ File.separator + fname));
				System.out.println("file: " + fname + "check sum: " + checks);
				String originVersion = localFileAndVersion.get(fname);
				if (localFileAndCheckSum.containsKey(fname)) {
					// if two check sum are not same need to update
					if (!localFileAndCheckSum.get(fname).equals(checks)) {
						// modify local version number here
						int newVersion = Integer.parseInt(originVersion) + 1;
						cmd.modifyInfo(fname, checks, Integer
								.toString(newVersion), SessionInfo
								.getInstance().getWorkFolder());
						localFileAndVersion.put(fname,
								Integer.toString(newVersion));
						System.out.println("initial upload: " + fname);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// compare with remote files and versions
		for (String fname : filesInfolder) {
			if (remoteFileAndVersion.containsKey(fname)) {
				// if remote file was deleted
				// just delete it in work space
				// remote it in the list
				if (remoteFileAndVersion.get(fname).equals(-1)) {
					deleteFileInFoler(dir + File.separator + fname);
					filesInfolder.remove(fname);
				} else {
					// need to compare version number to determine if download
					// or upload
					int localVersion = Integer.parseInt(localFileAndVersion
							.get(fname));
					System.out.println("file name : " + fname + " version: "
							+ localVersion);
					// only if local version is greater than the remote version
					// add file in upload queue
					// else do nothing
					if (remoteFileAndVersion.get(fname) < localVersion) {
						uploadFileControl(fname);
//						OperationQueue.getInstance().add(fname,
//								OperationQueue.getInstance().getUploadQueue());
					}
				}
			} else {
				// just upload file
				uploadFileControl(fname);
				// should be files add when client is off line
				// create meta data in xml file
				cmd.addToXML(fname,"1",dir);
			}
		}
	}

	/**
	 * when a file needs to be download, first check the download and upload
	 * queues.
	 * 
	 * @param downloadQ
	 */
	public void downLoadFileControl(String fn) {

		// operation already exists
		// first needs to stop current thread first
		if (OperationQueue.getInstance().containsObj(fn) != 0) {
			if (OperationQueue.getInstance().containsObj(fn) == 2) {
				// operation exists in upload queue
				DownloadFile.stop();
				UploadFile.stop();
				System.out.println("download conflict with upload ***********");
				new ConflictPopUp("Conflict detected. File " + fn
						+ " is current in the Upload queue.", 1, fn);
			} else {
				// operation exists in download queue
				DownloadFile.stop();
				new ConflictPopUp("Conflict detected. File " + fn
						+ " is already in the Download queue.", 2, fn);
			}
		} else {
			// add file name in to download queue if no conflicts
			OperationQueue.getInstance().add(fn,
					OperationQueue.getInstance().getDownloadQueue());
		}
	}

	/**
	 * 
	 * @param fn
	 */
	public void uploadFileControl(String fn) {
		// operation already exists
		// first needs to stop current thread first
		if (OperationQueue.getInstance().containsObj(fn) != 0) {
			
			if (OperationQueue.getInstance().containsObj(fn) == 1) {
				System.out.println("upload conflict with download*********");
				UploadFile.stop();
				DownloadFile.stop();
				// operation exists in download queue
				new ConflictPopUp("Conflict detected. File " + fn
						+ " is current in the download queue.", 1, fn);
			} else {
				UploadFile.stop();
				// operation exists already in the Upload queue
				new ConflictPopUp("Conflict detected. File " + fn
						+ " is already in the Upload queue.", 3, fn);
			}
		} else {
			// add file to the upload queue
			OperationQueue.getInstance().add(fn,
					OperationQueue.getInstance().getUploadQueue());
		}
	}
}
