package dataTransfer;

import java.io.File;
import java.io.FileInputStream;
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

import userMetaData.ClientMetaData;

public class FileOptHelper {

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
			if (fileList[i].isFile() && !fileList[i].getName().startsWith(".")
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
		String dir = sessionInfo.getInstance().getWorkFolder();
		UserOperate uopt = new UserOperate(sessionInfo.getInstance()
				.getRemoteDNS(), 12345);
		ClientMetaData cmd = new ClientMetaData();
		cmd.removeRecord(dir, getFileInFolder(dir));
		HashMap<String, String> localFileAndVersion = cmd.readXML(dir,
				getFileInFolder(dir));
		HashMap<String, Integer> remoteFileAndVersion = uopt
				.getServerVersion(sessionInfo.getInstance().getUsername());
		ArrayList<String> remoteFileList = new ArrayList<String>(
				remoteFileAndVersion.keySet());
		for (int i = 0; i < remoteFileList.size(); i++) {
			String fname = remoteFileList.get(i);
			// if file exists on both local and remote
			if (localFileAndVersion.containsKey(fname)) {
				// if local version is not same as remote version
				if (!localFileAndVersion.get(fname).equals(
						remoteFileAndVersion.get(fname).toString())) {
					/******* Simply add the download request into the download queue *******/
					if (remoteFileAndVersion.get(fname).equals(-1)) {
						// file is deleted in the remote folder
						// client will also deleted it.
						deleteFileInFoler(sessionInfo.getInstance()
								.getWorkFolder() + File.separator + fname);
					} else {
						// different version number
						// need to compare version numbers
						// only add to download queue when remote version number
						// is greater than the local version number
						int localVersion = Integer.parseInt(localFileAndVersion
								.get(fname));
						if (remoteFileAndVersion.get(fname) > localVersion) {
							OperationQueue.getInstance().add(
									fname,
									OperationQueue.getInstance()
											.getDownloadQueue());
						}
					}
				}
			} else {
				// new added just download
				OperationQueue.getInstance().add(fname,
						OperationQueue.getInstance().getDownloadQueue());
			}
		}
	}

	/**
	 * initilize uploading queue
	 */
	public void initilizeUploadQueue() {
		// get work space path
		String dir = sessionInfo.getInstance().getWorkFolder();
		ClientMetaData cmd = new ClientMetaData();
		FileOptHelper fopt = new FileOptHelper();
		UserOperate uopt = new UserOperate(sessionInfo.getInstance()
				.getRemoteDNS(), 12345);
		ArrayList<String> filesInfolder = fopt.getFileInFolder(dir);
		HashMap<String, Integer> remoteFileAndVersion = uopt
				.getServerVersion(sessionInfo.getInstance().getUsername());
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
								.toString(newVersion), sessionInfo
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
						OperationQueue.getInstance().add(fname,
								OperationQueue.getInstance().getUploadQueue());
					}
				}
			} else {
				// just upload file
				OperationQueue.getInstance().add(fname,
						OperationQueue.getInstance().getUploadQueue());
				// should be files add when client is off line
				// create meta data in xml file
				cmd.addToXML(fname, dir);
			}
		}
	}

	public void deleteRemoteFile(String filename) {

	}
}