/**
 * 
 */
package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Jitin
 *
 */
public class Constants {
	
	public static final String B1HOST = "cics525group6.cloudapp.net";
	public static final String B2HOST = "cics525group6b2.cloudapp.net";
	public static final String backup1StorageConnectionString = "DefaultEndpointsProtocol=http;"
			+ "AccountName=cics525group6;AccountKey=gAI6LQdhg/WnhMDPa46IYr66NLODOnMoP/LXJGsBtpYOCtO7ofKCL3YuOOsmLyUyHVf/63BNVI9H/ZI4OSgILg==";
	public static final String backup2StorageConnectionString = "DefaultEndpointsProtocol=http;"
			+ "AccountName=portalvhdsx4hlg0ss7c0mj;AccountKey=Jvwx3oWQ+vnJV+O88panubHMI72jgITFC2CqjSk1hCU32dvJeGvgEAEMTcicdgIbicqnn0aE7W9a5R7MWo0vgg==";

	
//	 public static final int CPORT = 12345; // port for RMI with client
//	 public static final int SPORT = 9999; // port for RMI with other service servers
//	 public static final String USER = "cics525";
//	 public static final String PASSWORD = "MSSgroup6";
//	
//	// variables to be set
//	 public String URL;
//	 public String DRIVER;
//	 public String SQLSERVER;
//	 public String DBNAME;
//	 public String STORAGECONNECTIONSTRING;
//	 public String HOST;
//	 public String CONTAINER;
//	 
//	 public Constants() {
//		URL = null;
//		DRIVER = null;
//		SQLSERVER = null;
//		DBNAME = null;
//		STORAGECONNECTIONSTRING = null;
//		HOST = null;
//		CONTAINER = null;
//	 }
//	
//	 private String localMachine;
//	
//	public void setConstants() {
//		try {
//		      InetAddress address = InetAddress.getLocalHost();
//		      localMachine = address.getHostName();
//		      System.out.println("My name is " + localMachine);
//		     
//		   
//			
//		    } catch (UnknownHostException e) {
//		      System.out.println("I'm sorry. I don't know my own name.");
//		    }
//		finally {
//			 // setting constants for service 1  
//			if (localMachine.equals("cics525group6S1.cloudapp.net")) {
//				URL = "jdbc:sqlserver://r8n9umoc58.database.windows.net;";
//				DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//				SQLSERVER = "r8n9umoc58";
//				DBNAME = "cics525group6DBS1";
//				STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"
//						+ "AccountName=portalvhdsnq9hdydm7mjhf;AccountKey=2v7HZEVkrWSbSZ599UKsmt/5iutYlpoE1m3DOM5yZ6hFdZfn4VZrGGuZRk1L/eHraWFBGT6s7MQ1FyzvvLJjLg==";
//				HOST = "cics525group6S1.cloudapp.net";
//				CONTAINER = "service1";
//			}
//			
//			// constants for service2
//			if (localMachine.equals("cics525group6S2.cloudapp.net")) {
//				URL = "jdbc:sqlserver://r8n9umoc58.database.windows.net;";
//				DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//				SQLSERVER = "r8n9umoc58";
//				DBNAME = "cics525group6DBS2";
//				STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"
//						+ "AccountName=portalvhdsnq9hdydm7mjhf;AccountKey=2v7HZEVkrWSbSZ599UKsmt/5iutYlpoE1m3DOM5yZ6hFdZfn4VZrGGuZRk1L/eHraWFBGT6s7MQ1FyzvvLJjLg==";
//				HOST = "cics525group6S2.cloudapp.net";
//				CONTAINER = "service2";
//			}
//
//			// constants for service3
//			if (localMachine.equals("cics525group6S3.cloudapp.net")) {
//				URL = "jdbc:sqlserver://x4dtun92cb.database.windows.net;";
//				DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//				SQLSERVER = "x4dtun92cb";
//				DBNAME = "cics525group6DBS3";
//				STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"
//						+ "AccountName=cics525group6;AccountKey=gAI6LQdhg/WnhMDPa46IYr66NLODOnMoP/LXJGsBtpYOCtO7ofKCL3YuOOsmLyUyHVf/63BNVI9H/ZI4OSgILg==";
//				HOST = "cics525group6S3.cloudapp.net";
//				CONTAINER = "service3";
//			}
//
//			// constants for service4
//			if (localMachine.equals("cics525group6s4.cloudapp.net")) {
//				URL = "jdbc:sqlserver://dmpf5vuf2x.database.windows.net;";
//				DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//				SQLSERVER = "dmpf5vuf2x";
//				DBNAME = "cics525group6DBS4";
//				STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"
//						+ "AccountName=portalvhdsx4hlg0ss7c0mj;AccountKey=Jvwx3oWQ+vnJV+O88panubHMI72jgITFC2CqjSk1hCU32dvJeGvgEAEMTcicdgIbicqnn0aE7W9a5R7MWo0vgg==";
//				HOST = "cics525group6s4.cloudapp.net";
//				CONTAINER = "service4";
//			}
//
//			// constants for backup 1
//			if (localMachine.equals("cics525group6.cloudapp.net")) {
//				 URL = "jdbc:sqlserver://x68gbjthkj.database.windows.net;";
//				 DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//				 SQLSERVER = "x68gbjthkj";
//				 DBNAME = "cics525group6DBB1";
//				 STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"	+ "AccountName=cics525group6;AccountKey=gAI6LQdhg/WnhMDPa46IYr66NLODOnMoP/LXJGsBtpYOCtO7ofKCL3YuOOsmLyUyHVf/63BNVI9H/ZI4OSgILg==";
//				 HOST = "cics525group6.cloudapp.net";
//				 CONTAINER ="backup1";
//			}
//
//			// constants for backup 2
//			if (localMachine.equals("cics525group6b2.cloudapp.net")) {
//				URL = "jdbc:sqlserver://dmpf5vuf2x.database.windows.net;";
//				DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//				SQLSERVER = "dmpf5vuf2x";
//				DBNAME = "cics525group6DBB2";
//				STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"
//						+ "AccountName=portalvhdsx4hlg0ss7c0mj;AccountKey=Jvwx3oWQ+vnJV+O88panubHMI72jgITFC2CqjSk1hCU32dvJeGvgEAEMTcicdgIbicqnn0aE7W9a5R7MWo0vgg==";
//				HOST = "cics525group6b2.cloudapp.net";
//				CONTAINER = "backup2";
//			}
//			
//		}
//
//	}
	
	// service 1 constants
	// Jitin's Azure Account
	 public static final String URL =
	 "jdbc:sqlserver://r8n9umoc58.database.windows.net;";
	 public static final String USER = "cics525";
	 public static final String PASSWORD = "MSSgroup6";
	 public static final String DRIVER =
	 "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	 public static final String SQLSERVER = "r8n9umoc58";
	 public static final String DBNAME = "cics525group6DBS1";
	 public static final String STORAGECONNECTIONSTRING =
	 "DefaultEndpointsProtocol=http;"
	 +
	 "AccountName=portalvhdsnq9hdydm7mjhf;AccountKey=2v7HZEVkrWSbSZ599UKsmt/5iutYlpoE1m3DOM5yZ6hFdZfn4VZrGGuZRk1L/eHraWFBGT6s7MQ1FyzvvLJjLg==";
	 public static final String HOST = "cics525group6S1.cloudapp.net";
	 public static final int CPORT = 12345; // port for RMI with client
	 public static final int SPORT = 9999; // port for RMI with other service servers
	 public static final String CONTAINER ="service1";

	// service 2 constants
	// Jitin's Azure Account
	// public static final String URL =
	// "jdbc:sqlserver://r8n9umoc58.database.windows.net;";
	// public static final String USER = "cics525";
	// public static final String PASSWORD = "MSSgroup6";
	// public static final String DRIVER =
	// "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	// public static final String SQLSERVER = "r8n9umoc58";
	// public static final String DBNAME = "cics525group6DBS2";
	// public static final String STORAGECONNECTIONSTRING =
	// "DefaultEndpointsProtocol=http;"
	// +
	// "AccountName=portalvhdsnq9hdydm7mjhf;AccountKey=2v7HZEVkrWSbSZ599UKsmt/5iutYlpoE1m3DOM5yZ6hFdZfn4VZrGGuZRk1L/eHraWFBGT6s7MQ1FyzvvLJjLg==";
	// public static final String HOST = "cics525group6S2.cloudapp.net";
	// public static final int CPORT = 12345; // port for RMI with client
	// public static final int SPORT = 9999; // port for RMI with other service
	// servers
	// public static final String CONTAINER ="service2";

//	 service 3 constants
//	 Ignacio's Azure Account
//	public static final String URL = "jdbc:sqlserver://x4dtun92cb.database.windows.net;";
//	public static final String USER = "cics525";
//	public static final String PASSWORD = "MSSgroup6";
//	public static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//	public static final String SQLSERVER = "x4dtun92cb";
//	public static final String DBNAME = "cics525group6DBS3";
//	public static final String STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"
//			+ "AccountName=cics525group6;AccountKey=gAI6LQdhg/WnhMDPa46IYr66NLODOnMoP/LXJGsBtpYOCtO7ofKCL3YuOOsmLyUyHVf/63BNVI9H/ZI4OSgILg==";
//	public static final String HOST = "cics525group6S3.cloudapp.net";
//	public static final int CPORT = 12345; // port for RMI with client
//	public static final int SPORT = 9999; // port for RMI with other service servers
//	public static final String CONTAINER = "service3";

	// Harry's Azure Account
    // public static final String URL =
    // "jdbc:sqlserver://dmpf5vuf2x.database.windows.net;";
     // public static final String USER = "cics525";
    // public static final String PASSWORD = "MSSgroup6";
    // public static final String DRIVER =
    // "com.microsoft.sqlserver.jdbc.SQLServerDriver";
     // public static final String SQLSERVER = "dmpf5vuf2x";
    // public static final String DBNAME = "cics525group6DBS4";
    // public static final String STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;" + "AccountName=portalvhdsx4hlg0ss7c0mj;AccountKey=Jvwx3oWQ+vnJV+O88panubHMI72jgITFC2CqjSk1hCU32dvJeGvgEAEMTcicdgIbicqnn0aE7W9a5R7MWo0vgg==";
     // public static final String HOST = "cics525group6s4.cloudapp.net";
    // public static final int CPORT = 12345; // port for RMI with client
    // public static final int SPORT = 9999; // port for RMI with other service servers
    // public static final String CONTAINER ="service4";

	
	// backup 1 constants
	// Ignacio's Azure Account
//	 public static final String URL =
//	 "jdbc:sqlserver://x68gbjthkj.database.windows.net;";
//	 public static final String USER = "cics525";
//	 public static final String PASSWORD = "MSSgroup6";
//	 public static final String DRIVER =
//	 "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//	 public static final String SQLSERVER = "x68gbjthkj";
//	 public static final String DBNAME = "cics525group6DBB1";
//	 public static final String STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"	+ "AccountName=cics525group6;AccountKey=gAI6LQdhg/WnhMDPa46IYr66NLODOnMoP/LXJGsBtpYOCtO7ofKCL3YuOOsmLyUyHVf/63BNVI9H/ZI4OSgILg==";
//	 public static final String HOST = "cics525group6.cloudapp.net";
//	 public static final int CPORT = 12345; // port for RMI with client
//	 public static final int SPORT = 9999; // port for RMI with other service	 servers
//	 public static final String CONTAINER ="backup1";

	// backup 2 constants
    // Harry's Azure Account
     // public static final String URL =
    // "jdbc:sqlserver://dmpf5vuf2x.database.windows.net;";
     // public static final String USER = "cics525";
    // public static final String PASSWORD = "MSSgroup6";
    // public static final String DRIVER =
    // "com.microsoft.sqlserver.jdbc.SQLServerDriver";
     // public static final String SQLSERVER = "dmpf5vuf2x";
    // public static final String DBNAME = "cics525group6DBB2";
    // public static final String STORAGECONNECTIONSTRING = "DefaultEndpointsProtocol=http;"    + "AccountName=portalvhdsx4hlg0ss7c0mj;AccountKey=Jvwx3oWQ+vnJV+O88panubHMI72jgITFC2CqjSk1hCU32dvJeGvgEAEMTcicdgIbicqnn0aE7W9a5R7MWo0vgg==";
     // public static final String HOST = "cics525group6b2.cloudapp.net";
    // public static final int CPORT = 12345; // port for RMI with client
    // public static final int SPORT = 9999; // port for RMI with other service servers
    // public static final String CONTAINER ="backup2";

}
