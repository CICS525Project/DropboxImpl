package serviceServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import utils.Constants;

import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
/**
 * Class for communicating the service server with backup server
 * @author cics525
 *
 */
public class ServerBackupCommunication {

	// ServiceContainer serviceContainer = new ServiceContainer();
	// ArrayList<RoutingTable> missMatch = new ArrayList<RoutingTable>();
	public static final String backup1StorageConnectionString = "DefaultEndpointsProtocol=http;"
			+ "AccountName=cics525group6;AccountKey=gAI6LQdhg/WnhMDPa46IYr66NLODOnMoP/LXJGsBtpYOCtO7ofKCL3YuOOsmLyUyHVf/63BNVI9H/ZI4OSgILg==";
	public static final String backup2StorageConnectionString = "DefaultEndpointsProtocol=http;"
			+ "AccountName=portalvhdsx4hlg0ss7c0mj;AccountKey=Jvwx3oWQ+vnJV+O88panubHMI72jgITFC2CqjSk1hCU32dvJeGvgEAEMTcicdgIbicqnn0aE7W9a5R7MWo0vgg==";

	/**
	 * method that download files from the service container to the local virtual machine in order to upload them later
	 * @param files receives an arraylist of files to be downloaded based on the mismatches found between container and routing table
	 */
	public void downloadMissMatch(ArrayList<RoutingTable> files) {
		
		// create temp folder
		new File("C:\\cloudboxTemp").mkdir();
		
		try {
			// Retrieve service storage account
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Constants.STORAGECONNECTIONSTRING);

			// Create the blob client.
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			// Get a reference to a container.
			// The container name must be lower case
			CloudBlobContainer container = blobClient
					.getContainerReference(Constants.CONTAINER);

			for (RoutingTable file : files) {
				CloudBlockBlob blob = container.getBlockBlobReference(file
						.getFileName());
				blob.downloadAttributes();
				// verify attributes matching
				
				if (blob.getMetadata().get("name").equals(file.getUserName())
						&& blob.getMetadata().get("version").equals(Integer.toString(file.getVersion()))) {
					FileOutputStream fos = new FileOutputStream("C:\\cloudboxTemp\\"	+ blob.getName());
					blob.download(fos);
					System.out.println("file " + blob.getName() + "downloaded to temp folder");
					fos.close();
				}
				else {
					System.out.println(blob.getName()+ " File fetch failed!");
				}
			}

		} catch (Exception e) {
			// Output the stack trace.
			e.printStackTrace();
		}
		
	}
	
	/**
	 * method that uploads the files described in the routingtable arraylist and puts them into the backup server
	 * @param files
	 */
	public void uploadBackup(ArrayList<RoutingTable> files) {
		try {
			// Retrieve service storage account
			CloudStorageAccount storageAccount1 = CloudStorageAccount
					.parse(backup1StorageConnectionString);
			CloudStorageAccount storageAccount2 = CloudStorageAccount
					.parse(backup2StorageConnectionString);

			// Create the blob client.
			CloudBlobClient blobClient1 = storageAccount1.createCloudBlobClient();
			CloudBlobClient blobClient2 = storageAccount2.createCloudBlobClient();

			// Get a reference to a container.
			// The container name must be lower case
			CloudBlobContainer container1 = blobClient1
					.getContainerReference("backup1");
			CloudBlobContainer container2 = blobClient2
					.getContainerReference("backup2");
			
			// Define the path to a local file.
		    String filePath = null;
		    HashMap<String,String> metadata = new HashMap<String,String>();
		    
		    for (RoutingTable file : files) {
		    	filePath = "C:\\cloudboxTemp\\" + file.getFileName();
		    	// Create or overwrite the "myimage.jpg" blob with contents from a local file.
			    CloudBlockBlob blob1 = container1.getBlockBlobReference(file.getFileName());
			    CloudBlockBlob blob2 = container2.getBlockBlobReference(file.getFileName());
			    // setting metadata
			    metadata.put("name", file.getUserName());
			    metadata.put("version", Integer.toString(file.getVersion()));
			    blob1.setMetadata(metadata);
			    blob2.setMetadata(metadata);
			    File source = new File(filePath);
			    FileInputStream fis = new FileInputStream(source);
			    blob1.upload(fis, source.length());
			    System.out.println("file " + blob1.getName() + "uploaded to backup1");
			    blob2.upload(fis, source.length());
			    System.out.println("file " + blob2.getName() + "uploaded to backup2");
			    fis.close();
		    }

		    
			
		} catch (Exception e) {
			System.out.println("Error uploading files to backup containers");
			// Output the stack trace.
			e.printStackTrace();
		}
	}
	/**
	 * method that clean files in the temp local folder acording to provided arraylist
	 * @param files routing table array list with list of files to be deleted from local disk
	 */
	public void cleanTemp(ArrayList<RoutingTable> files) {
		
		String filePath = null;
	    
		try {
			for (RoutingTable file : files) {
		    	filePath = "C:\\cloudboxTemp\\" + file.getFileName();

			    Path tar=Paths.get(filePath);
			    Files.delete(tar);
			    System.out.println(filePath + " deleted");
		    }
			
		} catch (Exception e) {
			System.out.println("Error removing files from temp folder");
			e.printStackTrace();
		}
	}
}	