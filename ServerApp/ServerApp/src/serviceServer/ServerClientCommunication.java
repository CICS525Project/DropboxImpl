package serviceServer;

//Include the following imports to use service bus APIs
import com.microsoft.windowsazure.services.serviceBus.*;
import com.microsoft.windowsazure.services.serviceBus.models.*;
import com.microsoft.windowsazure.services.core.*;

public class ServerClientCommunication {
	
	/**
	 * 
	 * @param destination String that identifies the client machine (user) that must take the message from the queue 
	 * @param message Message to be passed to the client in the format <upload/download>,<file owner>,<filename>.
	 * @return returns true when notification is successfully inserted into the queue or false otherwise 
	 */
	boolean sendNotification (String destination, String message) {
		// Use AZURE service bus to implement a queue of notifications.
		// The queue is UNIQUE in the system. Messages from all service servers and to all users goes into the same queue.
		Configuration config = ServiceBusConfiguration
				.configureWithWrapAuthentication("SystemQueue", "owner",
						"MqzK0pxJFJZ+3nWuSx1I3Z8QEbJzLHxaMlJDz/VyfEY=",
						".servicebus.windows.net",
						"-sb.accesscontrol.windows.net/WRAPv0.9");

		ServiceBusContract service = ServiceBusService.create(config);

		try {
			// Create message, passing a string message for the body
			BrokeredMessage busMessage = new BrokeredMessage(message);
		    // Set some additional custom app-specific property
		    busMessage.setProperty("user", destination);
		    			    
		    // Send message to the topic
			service.sendTopicMessage("ServerMessages", busMessage);
			System.out.println("Done!");
			return true;

		    
		} catch (ServiceException e) {
			System.out.print("ServiceException encountered: ");
			System.out.println(e.getMessage());
			return false;
		}
	
	}

}
