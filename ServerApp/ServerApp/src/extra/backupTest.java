package extra;

import java.util.ArrayList;

import serviceServer.RoutingTable;
import serviceServer.ServerBackupCommunication;

public class backupTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerBackupCommunication backuptest = new ServerBackupCommunication();
		ArrayList<RoutingTable> files = new ArrayList<RoutingTable>();
		RoutingTable rt = new RoutingTable();
		rt.setFileName("sample1.txt");
		rt.setUserName("jitin");
		rt.setVersion(1);
		files.add(rt);
		RoutingTable rt2 = new RoutingTable();
		rt2.setFileName("sample2.txt");
		rt2.setUserName("jitin");
		rt2.setVersion(1);
		files.add(rt2);
//		backuptest.downloadMissMatch(files);
//		System.out.println("done downloading!");
//		
//		backuptest.uploadBackup(files);
//		System.out.println("done uploading!");
		
		backuptest.cleanTemp(files);
		System.out.println("done cleaning!");
	}

}
