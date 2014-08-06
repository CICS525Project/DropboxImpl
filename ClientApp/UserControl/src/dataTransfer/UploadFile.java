package dataTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import javafx.scene.control.Tooltip;
import userGUI.ConflictPopUp;
import userUtil.ClientMetaData;
import userGUI.ToolTip;

import com.microsoft.windowsazure.services.blob.client.CloudBlob;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.blob.client.ListBlobItem;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

public class UploadFile implements Runnable {

	private OperationQueue optQ;
	public static Thread uploader;
	private ToolTip myTip;

	public UploadFile() {
		optQ = OperationQueue.getInstance();
		myTip = new ToolTip();
		uploader = new Thread(this);
		start();
	}

	/***
	 * Upload file to the queue
	 * 
	 */
	public void uploadImpl() throws IOException {
		Thread thisThread = Thread.currentThread();
		while (uploader == thisThread) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (optQ.peekUp() != null) {
				uploadFile(optQ.peekUp());
				System.out.println("upload queue size is " + OperationQueue.getInstance().getUploadQueue().size());
				OperationQueue.getInstance().pollUp();
				System.out.println("upload queue size is " + OperationQueue.getInstance().getUploadQueue().size());
			}
		}
	}

	/**
	 *  start thread
	 */
	public void start() {
		System.out.println("Uploader  running....");
		uploader.start();
	}

	/**
	 *  Stop the thread
	 */
	public static void stop() {
		System.out.println("upload thread stopping ... ");
		uploader = null;
	}

	/**
	 * upload a file 
	 * @param fileName file name need to be uploaded
	 */
	public void uploadFile(String fileName) {
		boolean blobExistFlag = false;
		// System.out.println("upload queue size is: " +
		// OperationQueue.getInstance().getUploadQueue().size());
		String spliter = File.separator;
		String workSpace = SessionInfo.getInstance().getWorkFolder();
		String upPath = workSpace + File.separator + fileName;
		String fileRemoteDNS;
		// System.out.println("uploading filename is " + upPath);
		UserOperate uopt = new UserOperate(SessionInfo.getInstance()
				.getRemoteDNS(), 12345);
		if (uopt.getOneFileAddress(fileName) == null) {
			fileRemoteDNS = SessionInfo.getInstance().getRemoteDNS();
		} else {
			fileRemoteDNS = uopt.getOneFileAddress(fileName);
		}
		// System.out.println("Upload file DNS is : " + fileRemoteDNS);
		FileOptHelper fopt = new FileOptHelper();
		UserOperate fileDNSOPT = new UserOperate(fileRemoteDNS, 12345);
		String fileContainerString = fileDNSOPT.fileContainer();
		String[] part = fileContainerString.split(",");
		CloudStorageAccount storageAccount;
		try {
			ClientMetaData cmd = new ClientMetaData();
			HashMap<String, String> localMeta = cmd.readXML(workSpace,
					fopt.getFileInFolder(workSpace));
			HashMap<String, String> localCheck = cmd.readHashCode(workSpace);
			
			HashMap<String, String> meta = new HashMap<String, String>();
			storageAccount = CloudStorageAccount.parse(part[0]);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			String containerName = part[1];
			// System.out.println("Container name is: " + containerName);
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			container.createIfNotExist();
			for (ListBlobItem blobItem : container.listBlobs()) {
				if (blobItem instanceof CloudBlob) {
					CloudBlob blob = (CloudBlob) blobItem;
					if (fileName.equals(blob.getName())) {
						System.out.println("upload exist file: " + fileName + " version " + localMeta.get(fileName));
						CloudBlockBlob blob1 = container.getBlockBlobReference(fileName);
						blob.downloadAttributes();
						HashMap<String, String> res = blob.getMetadata();
						meta.put("name", res.get("name"));
						meta.put("version", localMeta.get(fileName));
						meta.put("checkSum", localCheck.get(fileName));
						blob1.setMetadata(meta);
						File source = new File(upPath);
						FileInputStream fin = new FileInputStream(source);
						blob1.upload(fin, source.length());
						fin.close();
						blobExistFlag = true;
						break;
					}
				}
			}
			// if blob not already exist
			if (!blobExistFlag) {
				System.out.println("upload not remote exist file: " + fileName + " version " + localMeta.get(fileName));
				CloudBlockBlob blob = container.getBlockBlobReference(fileName);
				meta.put("name", SessionInfo.getInstance().getUsername());
				meta.put("version", localMeta.get(fileName));
				meta.put("checkSum", localCheck.get(fileName));
				blob.setMetadata(meta);
				File source = new File(upPath);
				FileInputStream fin = new FileInputStream(source);
				blob.upload(fin, source.length());
				fin.close();
				blob.uploadMetadata();
			}
			System.out.println("File is been uploaded");
			myTip.setToolTip(new ImageIcon(ConfigurationData.UP_IMG), "File "
					+ fileName + " is uploaded successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			uploadImpl();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
