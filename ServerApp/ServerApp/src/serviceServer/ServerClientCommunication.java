package serviceServer;

public class ServerClientCommunication {
	
	/**
	 * 
	 * @param destination String that identifies the client machine (user) that must take the message from the queue 
	 * @param message Message to be passed to the client (upload/download file, location, etc...)
	 * @return returns true when notification is successfully inserted into the queue or false otherwise 
	 */
	boolean sendNotification (String destination, String message) {
		// Use AZURE service bus to implement a queue of notifications.
		// The queue is UNIQUE in the system. Messages from all service servers and to all users goes into the same queue.
		
		return false;
		
		
	}

}
