package dataTransfer;

import java.util.*;

import sun.security.jca.GetInstance;


public class OperationQueue {
	
	private static OperationQueue singleton = null;
	private static Queue optQueue;
	
	public OperationQueue(){
		optQueue = (Queue) new HashMap<String,String>();
	}
	public static OperationQueue getInstance(){
		if(singleton == null){
			singleton = new OperationQueue();
		}
		return singleton;
	}
}
