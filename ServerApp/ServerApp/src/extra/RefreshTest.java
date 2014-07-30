package extra;

import java.sql.SQLException;
import java.util.ArrayList;

import routingTable.ServiceContainer;
import serviceServer.RoutingTable;

// class for manually test the refresh method with a given container and the routing table of the current machine

public class RefreshTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServiceContainer serviceContainer = new ServiceContainer();
		ArrayList<RoutingTable> missMatch = new ArrayList<RoutingTable>();
		
		missMatch = serviceContainer.checkContainerWithRoutingTable("service3", "cics525group6S3.cloudapp.net");
		try {
			serviceContainer.updateRTComplete(missMatch);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
