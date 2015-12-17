package datastorage;



import java.io.IOException;

import datastorage.util.DataStorageStub;

import util.FrameworkHandler;


public class TestDriver {

	/*
	 * Begin the DataStorageModule server connections
	 * 
	 * 
	 */
	
	public static void main(String[] args){
		System.out.println("Starting Data Storage Server");
		System.out.println("------------------------------------------");
		
		NetworkManager networkManager = new NetworkManager(new DataStorageStub());
		try {
			networkManager.setDataFowardScheduler(7367);
			
			networkManager.setDataReceiverPort(7368);
			
			networkManager.setDataRequestsPort(7369);
	        
//			FrameworkHandler a = FrameworkHandler.getInstance();
			FrameworkHandler.log(FrameworkHandler.LOG_INFO, new DataStorageStub(), "DataStorage Module starting up ");
			
			Thread.sleep(4000);
//			
			//dataManager.dataReceived(new Headlight("", true, true));//("tire1", 30, 20, 10));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Thread.currentThread().sleep(5000);
//		networkManager.closeAllConnections();
	}
}
