package dataTransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.microsoft.windowsazure.services.blob.client.CloudBlob;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.ListBlobItem;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

import userGUI.ConflictPopUp;
import userMetaData.ClientMetaData;

public class DownloadFile implements Runnable {

	private OperationQueue opt;
	private volatile Thread downloader;

	public DownloadFile() {
		opt = OperationQueue.getInstance();
		downloader = new Thread(this);
		// downLoadFileControl(opt.getDownloadQueue());
		//downloadImpl();
		start();
	}

	/**
	 * when a file needs to be downloaded, first check the download and upload
	 * queues.
	 * 
	 * @param downloadQ
	 */
	public void downLoadFileControl(String fn) {

		// operation already exists
		// first needs to stop current thread first
		if (opt.containsObj(fn) != 0) {
			stop();
			if (opt.containsObj(fn) == 2) {
				// operation exists in upload queue
				new ConflictPopUp("Conflict detected. File " + fn
						+ " is current in the Upload queue.", 1, fn);
			} else {
				// operation exists in download queue
				new ConflictPopUp("Conflict detected. File " + fn
						+ " is already in the Download queue.", 2, fn);
			}
		} else {
			// add file name in to download queue if no conflicts
			opt.add(fn, opt.getDownloadQueue());
		}
	}

	/**
	 * downloads files in the download queue
	 * 
	 * @param downloadQ
	 */
	public void downloadImpl() {
		Thread thisThread = Thread.currentThread();
		try {
			while (downloader == thisThread) {
				if (opt.peekDown() != null) {
					// get first download object from download queue
					downLoadFile(opt.peekDown());
					// delete first download object from download quque after
					// successfully downloading
					OperationQueue.getInstance().pollDown();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * download a file from Azure container
	 * 
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	public void downLoadFile(String fileName) throws IOException {
		
		ClientMetaData cmd = new ClientMetaData();
		FileOptHelper fopt = new FileOptHelper();
		String spliter = File.separator;
		String downPath = sessionInfo.getInstance().getWorkFolder() + spliter
				+ fileName;
		UserOperate uopt = new UserOperate(sessionInfo.getInstance()
				.getRemoteDNS(), 12345);
		HashMap<String, String> fileAndDNS = uopt.getFileAddress();
		String fileRemoteDNS = fileAndDNS.get(fileName);
		UserOperate fileDNSOPT = new UserOperate(fileRemoteDNS, 12345);
		String fileContainerString = fileDNSOPT.fileContainer();
		String[] part = fileContainerString.split(",");
		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(part[0]);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			String containerName = part[1];
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			System.out.println("download path is " + downPath);
			for (ListBlobItem blobItem : container.listBlobs()) {
				if (blobItem instanceof CloudBlob) {
					CloudBlob blob = (CloudBlob) blobItem;
					blob.downloadAttributes();
					if (fileName.equals(blob.getName())) {
						blob.download(new FileOutputStream(downPath));
						/*****need to change version number of the file download as meta in container******/
						HashMap<String, String> res = blob.getMetadata();
						String latestVersion = res.get("version");
						String checkSum = fopt.getHashCode(fopt.hashFile(sessionInfo.getInstance().getWorkFolder()+File.separator+fileName));
						cmd.modifyInfo(fileName, checkSum, latestVersion, sessionInfo.getInstance().getWorkFolder());
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

	public void start() {
		System.out.println("Start downloading...");
		downloader.start();
	}

	public void stop() {
		downloader = null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		downloadImpl();
	}

//	 public static void main(String[] args) {
//	 new DownloadFile();
//	 }
}
