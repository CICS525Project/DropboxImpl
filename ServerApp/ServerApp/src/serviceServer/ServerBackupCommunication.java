package serviceServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
 * @author ignacio
 *
 */
public class ServerBackupCommunication {
	/**
	 * method that download files from the service container to the local virtual machine in order to upload them later
	 * @param files receives an arraylist of files to be downloaded based on the mismatches found between container and routing table
	 */
	public void downloadMissMatch(ArrayList<RoutingTable> files) {


		// create temp folder
		new File("C:\\cloudboxTemp").mkdir();

		try {
			// Retrieve service storage account
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(Constants.STORAGECONNECTIONSTRING);

			// Create the blob client.
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			// Get a reference to a container.
			// The container name must be lower case
			CloudBlobContainer container = blobClient.getContainerReference(Constants.CONTAINER);

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

			
		}catch (FileNotFoundException fe) {
			System.out.println("File not found for downloading");
			
		}catch (Exception e) {
			// Output the stack trace.
			e.printStackTrace();
		}

	}

	/**
	 * MethodName: uploadBackup
	 * method that uploads the files described in the routingtable arraylist and puts them into the backup server
	 * @param files
	 */
	public void uploadBackup(ArrayList<RoutingTable> files) {
		try {
			// Retrieve service storage account
			CloudStorageAccount storageAccount1 = CloudStorageAccount
					.parse(Constants.backup1StorageConnectionString);
			CloudStorageAccount storageAccount2 = CloudStorageAccount
					.parse(Constants.backup2StorageConnectionString);

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
				// check if the current machine is not the backup server 1 !!!
				if (!Constants.CONTAINER.equals("backup1"))
				{
					blob1.upload(fis, source.length());
					System.out.println("file " + blob1.getName() + "uploaded to backup1");
				}

				// check if current machine is not backup 2 
				if (!Constants.CONTAINER.equals("backup2"))
				{
					blob2.upload(fis, source.length());
					System.out.println("file " + blob2.getName() + "uploaded to backup2");
				}
				fis.close();
			}

		} catch (FileNotFoundException fe) {
			System.out.println("File not found for uploading");
			
		}catch (Exception e) {
			System.out.println("Error uploading files to backup containers");
			// Output the stack trace.
			e.printStackTrace();
		}
	}
	/**
	 * MethodName:cleanTemp
	 * method that clean files in the temp local folder acording to provided arraylist
	 * @param files routing table array list with list of files to be deleted from local disk
	 */
	public void cleanTemp(ArrayList<RoutingTable> files) {
		String filePath = null;

		try {
			for (RoutingTable file : files) {
				filePath = "C:\\cloudboxTemp\\" + file.getFileName();

				Path tar=Paths.get(filePath);
				if (Files.deleteIfExists(tar)) {
					System.out.println(filePath + " deleted");
				}
				else {
					System.out.println(filePath + " deletion failed");
				}

			}

		} catch (FileNotFoundException fe) {
			System.out.println("File not found for deletion");
			
		}catch (Exception e) {
			System.out.println("Error removing files from temp folder");
			e.printStackTrace();
		}
	}
}	
