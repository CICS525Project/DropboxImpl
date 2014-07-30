package dataTransfer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class folderWatcher implements Runnable {
	private Path dir;
	private OperationQueue optQueue;

	public folderWatcher(String path) {
		this.dir = Paths.get(path);
		optQueue = OperationQueue.getInstance();
		start();
	}

	/**
	 * watch operations on files in the folder
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public void watchFile(Path dir) throws IOException {
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
					}
					else {
						// add new operations here
						System.out.println("Event " + kind.name()
								+ " happened, which filename is " + fn);
						if (kind.name().equals("ENTRY_CREATE")) {
							
						}
						if (kind.name().equals("ENTRY_MODIFY")) {
							
						}
						if (kind.name().equals("ENTRY_DELETE")) {
							
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
		}
	}

	public void start() {
		System.out.println("Starting folder watcher ...");
		Thread t = new Thread(this);
		t.start();
	}
	// public static void main(String[] args) {
	// new folderWatcher("/Users/haonanxu/Desktop/download");
	// }
}
