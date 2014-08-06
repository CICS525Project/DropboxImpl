package dataTransfer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.windowsazure.services.blob.client.CloudBlob;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.ListBlobItem;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

import userUtil.ClientMetaData;

public class FolderWatch implements Runnable {
	private Path dir;
	private ClientMetaData cmd;
	private UserOperate uopt;
	private FileOptHelper fopt;

	public FolderWatch(String path) {
		this.dir = Paths.get(path);
		cmd = new ClientMetaData();
		fopt = new FileOptHelper();
		start();
	}

	/**
	 * watch operations on files in the folder
	 * 
	 * @param dir
	 * @throws Exception
	 */
	public void watchFile(Path dir) throws Exception {
		uopt = new UserOperate(SessionInfo.getInstance().getRemoteDNS(),
				SessionInfo.getInstance().getPortNum());
		WatchService watcher = FileSystems.getDefault().newWatchService();
		dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		while (true) {
			try {
				WatchKey key = watcher.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind kind = event.kind();
					if (kind == OVERFLOW) {
						continue;
					}
					WatchEvent<Path> e = (WatchEvent<Path>) event;
					Path fileName = e.context();
					String fn = fileName.toString();
					if (fn.startsWith(".DS") || fn.startsWith("~")
							|| fn.endsWith("~")) {
						System.out.println(".ds and ~ checked");
						continue;
					} else if (fn.equals("file.xml")) {
						System.out.println("xml checked");
						continue;
					} else {
						// add new operations here
						System.out.println("Event " + kind.name()
								+ " happened, which filename is " + fn);
						if (kind.name().equals("ENTRY_CREATE")) {
							// if remote does not have this file
							HashMap<String, Integer> remoteFiles = uopt
									.getServerVersion(SessionInfo.getInstance()
											.getUsername());
							if (remoteFiles == null) {
								System.out.println("remote is null");
							}
							if (!remoteFiles.containsKey(fn)) {
								System.out
										.println("On create remote does not have file: "
												+ fn);
								cmd.addToXML(fn, "1", SessionInfo.getInstance()
										.getWorkFolder());
								fopt.uploadFileControl(fn);
							} else {
								// if file is deleted and user uploads it again,
								// need to set version as 1
								if (remoteFiles.get(fn) == -1) {
									cmd.addToXML(fn, "1", SessionInfo
											.getInstance().getWorkFolder());
									fopt.uploadFileControl(fn);
								} else if (cmd.compareLcalandRmtVersion(fn) == 1) {
									System.out
											.println("On create remote have smaller version");
									cmd.addToXML(fn, "1", SessionInfo
											.getInstance().getWorkFolder());
									fopt.uploadFileControl(fn);
								}
							}
						}
						if (kind.name().equals("ENTRY_MODIFY")) {
							// add into upload queue
							// modify local version number here
							String remoteCheckSum = null;
							String fileRemoteDNS = uopt.getOneFileAddress(fn);
							UserOperate fileDNSOPT = new UserOperate(
									fileRemoteDNS, SessionInfo.getInstance()
											.getPortNum());
							String fileContainerString = fileDNSOPT
									.fileContainer();
							String[] part = fileContainerString.split(",");
							CloudStorageAccount storageAccount;
							storageAccount = CloudStorageAccount.parse(part[0]);
							CloudBlobClient blobClient = storageAccount
									.createCloudBlobClient();
							String containerName = part[1];
							CloudBlobContainer container = blobClient
									.getContainerReference(containerName);
							for (ListBlobItem blobItem : container.listBlobs()) {
								if (blobItem instanceof CloudBlob) {
									CloudBlob blob = (CloudBlob) blobItem;
									blob.downloadAttributes();
									if (fn.equals(blob.getName())) {
										HashMap<String, String> res = blob
												.getMetadata();
										remoteCheckSum = res.get("checkSum");
										break;
									}
								}
							}

							String oldVersion = cmd.readVersionForOne(fn);
							String checkSum = fopt.getHashCode(fopt
									.hashFile(SessionInfo.getInstance()
											.getWorkFolder()
											+ File.separator
											+ fn));
							System.out.println("remote c " + remoteCheckSum);
							System.out.println("new c " + checkSum);
							// if file is just modified
							// not just download and overlapped
							if (remoteCheckSum != null) {
								if (!remoteCheckSum.equals(checkSum)) {
									int newVersion = Integer
											.parseInt(oldVersion) + 1;
									cmd.modifyInfo(fn, checkSum, String
											.valueOf(newVersion), SessionInfo
											.getInstance().getWorkFolder());
									fopt.uploadFileControl(fn);
								}
							} else {
								fopt.uploadFileControl(fn);
							}
						}
						if (kind.name().equals("ENTRY_DELETE")) {
							// check if local meta exists
							// if exists, means deletion is initialized by this
							// user
							// if not exists, means deletion is operated by
							// other shared user and sync her, discard
							SyncWithRemote.stop();
							HashMap<String, String> localFileAndVersion = cmd
									.readHashCode(SessionInfo.getInstance()
											.getWorkFolder());
							if (localFileAndVersion.containsKey(fn)) {
								cmd.removeOneRecord(SessionInfo.getInstance()
										.getWorkFolder(), fn);
								uopt.deleteRemoteFile(fn);
							} else {
								new SyncWithRemote();
							}
						}
					}
				}
				if (!key.reset()) {
					break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			watchFile(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		System.out.println("Starting folder watcher ...");
		Thread t = new Thread(this);
		t.start();
	}
}
