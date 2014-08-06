package dataTransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import com.microsoft.windowsazure.services.blob.client.CloudBlob;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.ListBlobItem;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

import userGUI.ToolTip;
import userUtil.ClientMetaData;

public class DownloadFile implements Runnable {

	private OperationQueue opt;
	public volatile static Thread downloader;
	private ToolTip myTip;

	/**
	 * download thread constructor
	 */
	public DownloadFile() {
		opt = OperationQueue.getInstance();
		myTip = new ToolTip();
		downloader = new Thread(this);
		start();
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
				Thread.sleep(2000);
				if (opt.peekDown() != null) {
					// get first download object from download queue
					downLoadFile(opt.peekDown());
					// delete first download object from download quque after
					// successfully downloading
					System.out.println("download queue size is " + OperationQueue.getInstance().getDownloadQueue().size());
					OperationQueue.getInstance().pollDown();
					System.out.println("download queue size is " + OperationQueue.getInstance().getDownloadQueue().size());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
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
		String downPath = SessionInfo.getInstance().getWorkFolder() + spliter
				+ fileName;
		UserOperate uopt = new UserOperate(SessionInfo.getInstance()
				.getRemoteDNS(), SessionInfo.getInstance().getPortNum());
		String fileRemoteDNS = uopt.getOneFileAddress(fileName);
		UserOperate fileDNSOPT = new UserOperate(fileRemoteDNS, SessionInfo.getInstance().getPortNum());
		String fileContainerString = fileDNSOPT.fileContainer();
		String[] part = fileContainerString.split(",");
		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(part[0]);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			String containerName = part[1];
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			for (ListBlobItem blobItem : container.listBlobs()) {
				if (blobItem instanceof CloudBlob) {
					CloudBlob blob = (CloudBlob) blobItem;
					blob.downloadAttributes();
					if (fileName.equals(blob.getName())) {
						ArrayList<String> originFileInfolder = fopt
								.getFileInFolder(SessionInfo.getInstance()
										.getWorkFolder());
						FileOutputStream fout = new FileOutputStream(downPath);
						blob.download(fout);
						fout.close();
//						 need to change version number of the file download as
//						 meta in container
						HashMap<String, String> res = blob.getMetadata();
						String latestVersion = res.get("version");
						String latestCheckSum = res.get("checkSum");
						if (originFileInfolder.contains(fileName)) {
							cmd.modifyInfo(fileName, latestCheckSum, latestVersion,
									SessionInfo.getInstance().getWorkFolder());
						} else {
							cmd.addToXML(fileName,latestVersion ,SessionInfo.getInstance()
									.getWorkFolder());
						}
						myTip.setToolTip(new ImageIcon(
								ConfigurationData.DOWN_IMG), "File " + fileName
								+ " is download secessfully!");
						return;
					}
				}
			}
			System.out.println("Cannot find the file "+fileName+" you selected on DNS " + fileRemoteDNS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * start download thread
	 */
	public void start() {
		System.out.println("Start downloading...");
		downloader.start();
	}

	/**
	 * stop download thread
	 */
	public static void stop() {
		System.out.println("download stopping ...");
		downloader = null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		downloadImpl();
	}
}
