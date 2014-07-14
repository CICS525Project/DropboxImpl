package extra;
// Include the following imports to use blob APIs
import java.io.File;
import java.io.FileInputStream;

import com.microsoft.windowsazure.services.core.storage.*;
import com.microsoft.windowsazure.services.blob.client.*;

public class AzureConnect {
	
	// Define the connection-string with your values
	public static final String storageConnectionString = 
	    "DefaultEndpointsProtocol=http;" + 
	    "AccountName=cics525group6;" + 
	    "AccountKey=gAI6LQdhg/WnhMDPa46IYr66NLODOnMoP/LXJGsBtpYOCtO7ofKCL3YuOOsmLyUyHVf/63BNVI9H/ZI4OSgILg==";

	public static void main (String args[]) {
		try
		{
		   // Retrieve storage account from connection-string.
		   CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		   // Create the blob client.
		   CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		   // Get a reference to a container.
		   // The container name must be lower case
		   CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

		   // Create the container if it does not exist.
		   container.createIfNotExist();
		   
		// Create or overwrite the "clouds.jpg" blob with contents from a local file
		   CloudBlockBlob blob = container.getBlockBlobReference("text.txt");
		   File source = new File("text.txt");
		   blob.upload(new FileInputStream(source), source.length());
		   System.out.println("upload finished");
		   
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
		
	}
}