package extra;

import java.io.File;
import java.util.ArrayList;

import serviceServer.RoutingTable;
import serviceServer.ServerBackupCommunication;

public class backupTest {

	public static void main(String[] args) throws InterruptedException {
		
		
//		// cleaning temp dir before running the server again
//				File file = new File("C:\\cloudboxTemp");        
//		        String[] myFiles;      
//		            if(file.isDirectory()){  
//		                myFiles = file.list();  
//		                for (int i=0; i<myFiles.length; i++) {  
//		                    File myFile = new File(file, myFiles[i]);   
//		                    myFile.delete();  
//		                }  
//		             }
		
		// TODO Auto-generated method stub
		ServerBackupCommunication backuptest = new ServerBackupCommunication();
		ArrayList<RoutingTable> files = new ArrayList<RoutingTable>();
		RoutingTable rt = new RoutingTable();
		rt.setFileName("box.png");
		rt.setUserName("jitin");
		rt.setVersion(-1);
		files.add(rt);
		RoutingTable rt2 = new RoutingTable();
		rt2.setFileName("warn.png");
		rt2.setUserName("harry");
		rt2.setVersion(1);
		files.add(rt2);
		backuptest.downloadMissMatch(files);
		System.out.println("done downloading!");
		
		backuptest.uploadBackup(files);
		System.out.println("done uploading!");

		System.out.println("start deleting");
		backuptest.cleanTemp(files);
		System.out.println("done cleaning!");
	}

}
