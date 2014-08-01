package dataTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import javafx.scene.control.Tooltip;
import userGUI.ConflictPopUp;
import userMetaData.ClientMetaData;
import userGUI.ToolTip;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

public class UploadFile implements Runnable {

	private OperationQueue optQ;
	private Thread uploader;
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
	public void uploadImpl() throws IOException{
		Thread thisThread = Thread.currentThread();
		while (uploader == thisThread) {
			if (optQ.peekUp() != null) {
				uploadFile(optQ.peekUp());
				OperationQueue.getInstance().pollUp();
			}
		}
	}

	/**
	 * 
	 * @param fn
	 */
	public void uploadFileControl(String fn) {
		// operation already exists
		// first needs to stop current thread first
		if (optQ.containsObj(fn) != 0) {
			stop();
			if (optQ.containsObj(fn) == 1) {
				// operation exists in download queue
				new ConflictPopUp("Conflict detected. File " + fn + " is current in the download queue.", 3, fn);
			} else {
				// operation exists already in the Upload queue
				new ConflictPopUp("Conflict detected. File " + fn + " is already in the Upload queue.", 1, fn);
			}
		}
		else
		{
			//add file to the upload queue
			optQ.add(fn, optQ.getUploadQueue());
		}

	}
	
	//start thread
	public void start(){
		System.out.println("Uploader  running....");
		uploader.start();
	}

	//Stop the thread
	public void stop() {
		uploader = null;
	}

	/**
	 * 
	 * @param fileName
	 */
	public void uploadFile(String fileName){
		//System.out.println("upload queue size is: " + OperationQueue.getInstance().getUploadQueue().size());
		String spliter = File.separator;
		String workSpace = sessionInfo.getInstance().getWorkFolder();
		String upPath  = workSpace + File.separator + fileName;
		String fileRemoteDNS;
		//System.out.println("uploading filename is " + upPath);
		UserOperate uopt = new UserOperate(sessionInfo.getInstance()
					.getRemoteDNS(), 12345);
		if(uopt.getOneFileAddress(fileName)==null){
			fileRemoteDNS = sessionInfo.getInstance().getRemoteDNS();
		}else{
			fileRemoteDNS = uopt.getOneFileAddress(fileName);
		}
		//System.out.println("Upload file DNS is : " + fileRemoteDNS);
		FileOptHelper fopt = new FileOptHelper();
		UserOperate fileDNSOPT = new UserOperate(fileRemoteDNS, 12345);
		String fileContainerString = fileDNSOPT.fileContainer();
		String[] part = fileContainerString.split(",");
		CloudStorageAccount storageAccount;
		try{
			storageAccount = CloudStorageAccount.parse(part[0]);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			String containerName = part[1];
			//System.out.println("Container name is: " + containerName);
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			container.createIfNotExist();
			CloudBlockBlob blob = container.getBlockBlobReference(fileName);
			File source = new File(upPath);
			blob.upload(new FileInputStream(source), source.length());
			
			/**change remote version number as well**/
			ClientMetaData cmd = new ClientMetaData();
			HashMap<String, String> localMeta = cmd.readXML(workSpace, fopt.getFileInFolder(workSpace));
			HashMap<String, String> meta = new HashMap<String, String>();
			//System.out.println("The metadata for the file: " + fileName +" is " + localMeta.get(fileName));
			meta.put("name", sessionInfo.getInstance().getUsername());
			meta.put("version", localMeta.get(fileName));
			blob.setMetadata(meta);
			blob.uploadMetadata();
			System.out.println("File is been uploaded");
			myTip.setToolTip(new ImageIcon(configurationData.UP_IMG), "File "+fileName+" is uploaded successfully!");
		}
		catch(Exception e){
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

