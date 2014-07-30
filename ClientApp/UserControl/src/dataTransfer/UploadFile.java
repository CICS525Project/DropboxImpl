package dataTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import userGUI.ConflictPopUp;
import userMetaData.ClientMetaData;

import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
/*
 * @author Sashiraj
 * 
 */
import com.sun.xml.internal.ws.handler.ClientMessageHandlerTube;

public class UploadFile implements Runnable {

	private OperationQueue optQ;
	private Thread uploader;

	public UploadFile() {
		optQ = OperationQueue.getInstance();
		System.out.println("New thread goin to start...");
		uploader = new Thread(this);
		System.out.println("New thread started...");
		try {
			uploadImpl();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * Upload file to the queue
	 * 
	 */
	public void uploadImpl() throws IOException{
		Thread thisThread = Thread.currentThread();
		System.out.println("Same thread");
		while (uploader == thisThread) {
			if (optQ.peekUp() != null) {
				uploadFile(optQ.peekUp());

				OperationQueue.getInstance().pollUp();
			}
		}
	}

	/**
	 * first check the upload and download before uploading in queues.
	 * 
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
		System.out.println("upload thread running....");
		uploader.start();
	}

	//Stop the thread
	public void stop() {
		uploader = null;
	}

	public void uploadFile(String fileName){
		System.out.println("Inside UploadFile");
		String spliter = File.separator;
		String workSpace = sessionInfo.getInstance().getWorkFolder();
		String upPath  = workSpace + File.separator + fileName;
		UserOperate uopt = new UserOperate(sessionInfo.getInstance()
					.getRemoteDNS(), 12345);
		String fileRemoteDNS = uopt.getOneFileAddress(fileName);
		FileOptHelper fopt = new FileOptHelper();
		UserOperate fileDNSOPT = new UserOperate(fileRemoteDNS, 12345);
		String fileContainerString = fileDNSOPT.fileContainer();
		String[] part = fileContainerString.split(",");
		CloudStorageAccount storageAccount;
		try{
			storageAccount = CloudStorageAccount.parse(part[0]);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			String containerName = part[0];
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			container.createIfNotExist();
			CloudBlockBlob blob = container.getBlockBlobReference(fileName);
			File source = new File(upPath);
			blob.upload(new FileInputStream(source), source.length());
			
			/**change remote version number as well**/
			/**may be error here**/
			ClientMetaData cmd = new ClientMetaData();
			HashMap<String, String> localMeta = cmd.readXML(workSpace, fopt.getFileInFolder(workSpace));
			HashMap<String, String> meta = new HashMap<String, String>();
			System.out.println("The metadata for the file: " + fileName +" is " + localMeta.get(fileName));
			meta.put("version", localMeta.get(fileName));
			blob.setMetadata(meta);
			blob.uploadMetadata();
			
			System.out.println("File is been uploaded");
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

	public static void main(String[] args){

		UploadFile upf = new UploadFile();
		upf.start();
		upf.uploadFile("sas_test2");
	}
}

