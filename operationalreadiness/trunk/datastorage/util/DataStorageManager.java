package datastorage.util;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import util.DataReceivedListener;
import util.DataType;
import util.FrameworkHandler;
import util.connections.DataForwarder;

/**
 * Handles the new data coming into the DataStorage Module by adding it to storage via the 
 * DataStorageAdapter and forwarding it to all observers. 
 * 
 * @author Tom Renn
 */
public class DataStorageManager implements DataReceivedListener{

	private DataStorageAdapter storageAdapter;
	private List<DataForwarder> forwardingObservers;
	
	/**
	 * Basic constructor
	 */
	public DataStorageManager(DataStorageAdapter storageAdapter){
		forwardingObservers = new LinkedList<DataForwarder>();
		this.storageAdapter = storageAdapter;
}
	
	/**
	 * When receiving a class, forward it to all observers
	 * @param file File of the class
	 */
	public synchronized void classReceived(File file) {
		if (FrameworkHandler.DEBUG_MODE)
			System.out.print("Class received: " + file.getName());
		
		for ( DataForwarder forwarder : forwardingObservers ){
			forwarder.addClass(file);
			if (FrameworkHandler.DEBUG_MODE)
				System.out.print(" [forwarded]");
			synchronized (forwarder) {
				forwarder.notify();
			}
			
		}
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println();
	}
	
	/**
	 * After receiving new data add it to the 
	 * TODO: if forwarded is dead, remove it from the list
	 */
	public synchronized Serializable dataReceived(Serializable data) {
		if (FrameworkHandler.DEBUG_MODE)
			System.out.print("Data received: " + data);
		
		Serializable error = storageAdapter.putData((DataType) data);
		if (error != null)
			return error;
		
		for ( DataForwarder forwarder : forwardingObservers ){
			if (forwarder.isAlive() == false){ // forwarded lost connection or died
				forwardingObservers.remove(forwarder);
				continue;
			}
			
			forwarder.addData(data);
			if (FrameworkHandler.DEBUG_MODE)
				System.out.print(" [forwarded]");
			synchronized (forwarder) {
				forwarder.notify();
			}
		}
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println();
		
		return null;
	}
	
	
	/**
	 * Get data IDs
	 * 
	 * @return A list of all data IDs or an empty list
	 */
	public List<String> getAvailableDataIDs() {
		System.out.println("DataStorageManager: Fetched data ID's");
		return storageAdapter.getAvailableDataIDs();
	}
	
	/**
	 * Get specified data. If constraint is null, the single most recent datum is retrieved.
	 * @param dataID ID of data
	 * @param constraint search constraint
	 * @return List of Data, or an empty list
	 */
	public List<DataType> getDataForID(String dataID, Object constraint){
		System.out.println("DataStorageManager: Fetched Data: " + dataID + " Constraint: " + constraint);
		return storageAdapter.getData(dataID, constraint);
	}
	
    public void setDataStorageAdapter(DataStorageAdapter dsa) {
        storageAdapter = dsa;
    }

    public DataStorageAdapter getDataStorageAdapter() {
        return storageAdapter;
    }
    
    public void addDataForwarder(DataForwarder forwarder){
    	System.out.println("Added data forwarder");
    	forwardingObservers.add(forwarder);
    }

    
}
