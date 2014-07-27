package dataTransfer;

import java.util.*;

public class OperationQueue {

	private static OperationQueue singleton = null;
	private LinkedList<String> optQueueDown;
	private LinkedList<String> optQueueUp;

	public OperationQueue() {
		optQueueDown = new LinkedList<String>();
		optQueueUp = new LinkedList<String>();
	}

	public static OperationQueue getInstance() {
		if (singleton == null) {
			singleton = new OperationQueue();
		}
		return singleton;
	}

	public synchronized LinkedList<String> getDownloadQueue() {
		return optQueueDown;
	}

	public synchronized void setDownloadQueue(String filename) {
		optQueueDown.add(filename);
	}

	public synchronized LinkedList<String> getUploadQueue() {
		return optQueueUp;
	}

	public synchronized boolean add(String obj, LinkedList<String> optList) {
		return optList.add(obj);
	}

	public synchronized String peekDown() {
		return optQueueDown.peek();
	}

	public synchronized String pollDown() {
		return optQueueDown.poll();
	}

	public synchronized String peekUp(){
		return optQueueUp.peek();
	}
	
	public synchronized String pollUp(){
		return optQueueUp.poll();
	}
	
	public synchronized void remove(String fileObj, LinkedList<String> optList) {
		optList.remove(fileObj);
	}

	public int containsObj(String fn) {

		boolean inDown = false;
		boolean inUp = false;
		if (optQueueDown.isEmpty() && optQueueUp.isEmpty()) {
			return 0;
		}

		if (optQueueDown.contains(fn)) {
			inDown = true;
		}

		if (optQueueUp.contains(fn)) {
			inUp = true;
		}
		if (inDown && inUp) {
			return 3;
		}
		if (inDown) {
			return 1;
		}
		if (inUp) {
			return 2;
		}
		return 0;
	}

	public synchronized void removeUpAddDown(String fn) {
		remove(fn, optQueueUp);
		add(fn, optQueueDown);
	}

	public synchronized void removeDownAddDown(String fn) {
		remove(fn, optQueueDown);
		add(fn, optQueueUp);
	}

	public static void main(String[] args) {
//		OperationQueue opt = OperationQueue.getInstance();
//		String file = opt.peekDown();
//		if(file == null){
//			System.out.println("returns null");
//		}
	}
}
