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
import java.util.HashMap;

import userMetaData.ClientMetaData;

public class folderWatcher implements Runnable {
	private Path dir;
	private ClientMetaData cmd;
	private UserOperate uopt;
	private FileOptHelper fopt;
	public folderWatcher(String path) {
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
		uopt = new UserOperate(sessionInfo.getInstance().getRemoteDNS(),
				sessionInfo.getInstance().getPortNum());
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
					if (fn.startsWith(".DS")) {
						System.out.println(".ds checked");
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
									.getServerVersion(sessionInfo.getInstance()
											.getUsername());
							if (!remoteFiles.containsKey(fn)) {
								cmd.addToXML(fn, sessionInfo.getInstance()
										.getWorkFolder());
								OperationQueue.getInstance().add(
										fn,
										OperationQueue.getInstance()
												.getUploadQueue());
							} else {
								// if file is deleted and user uploads it again,
								// need to set version as 1
								if (cmd.compareLcalandRmtVersion(fn) == 1) {
									cmd.addToXML(fn, sessionInfo.getInstance()
											.getWorkFolder());
									OperationQueue.getInstance().add(
											fn,
											OperationQueue.getInstance()
													.getUploadQueue());
								}
							}
						}
						if (kind.name().equals("ENTRY_MODIFY")) {
							// add into upload queue
							/** modify local version number here **/
							String oldVersion = cmd.readVersionForOne(fn);
							int newVersion = Integer.parseInt(oldVersion) + 1;
							String checkSum = fopt.getHashCode(fopt.hashFile(sessionInfo.getInstance().getWorkFolder()+File.separator+fn));
							cmd.modifyInfo(fn, checkSum, String.valueOf(newVersion), sessionInfo.getInstance().getWorkFolder());
							OperationQueue.getInstance().add(
									fn,
									OperationQueue.getInstance()
											.getUploadQueue());
						}
						if (kind.name().equals("ENTRY_DELETE")) {
							/**
							 * change version number in the remote container to
							 * -1, and delete file in the container
							 **/
							
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