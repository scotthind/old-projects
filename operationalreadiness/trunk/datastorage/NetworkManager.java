package datastorage;

import java.io.IOException;

import datastorage.util.ConnectionScheduler;
import datastorage.util.DataStorageAdapter;
import datastorage.util.DataStorageManager;

import util.FrameworkHandler;

/**
 * Handles the DataStorageManager and all the schedulers that accept new connections.
 * 
 * @author Tom Renn
 */
public class NetworkManager {
	private DataStorageManager dataManager;
	private Thread newDataScheduler;
	private Thread dataRequestScheduler;
	private Thread dataFowardScheduler;
	
	/**
	 * Basic constructor, takes a DataStorageManager. Then proceeds to load class files this module
	 * may have previously received and saved in the /classes/ directory.
	 * @param dataManager
	 */
	public NetworkManager(DataStorageAdapter storageAdapter){
		this.dataManager = new DataStorageManager(storageAdapter);
		FrameworkHandler.loadStoredClasses();
	}
	
	/**
	 * The port that will handle new data from a DataPlugin module
	 * 
	 * @param port
	 * @throws IOException
	 */
	public void setDataReceiverPort(int port) throws IOException {
		newDataScheduler = new Thread(new ConnectionScheduler(ConnectionScheduler.DATA_PLUGIN, dataManager, port));
		newDataScheduler.start();
	}
	
	/**
	 * The port that will handle data requests from a UI module
	 * @param port
	 * @throws IOException
	 */
	public void setDataRequestsPort(int port) throws IOException {
		dataRequestScheduler = new Thread(new ConnectionScheduler(ConnectionScheduler.DATA_FETCH, dataManager, port));
		dataRequestScheduler.start();
	}
	
	/**
	 * The port that will be used accept data-forward observers
	 * @param port
	 * @throws IOException
	 */
	public void setDataFowardScheduler(int port) throws IOException {
		dataFowardScheduler = new Thread(new ConnectionScheduler(ConnectionScheduler.DATA_FORWARD, dataManager, port));
		dataFowardScheduler.start();
	}
	
	/**
	 * Change the dataStorageAdapter within DataStorageManager
	 * @param dsa
	 */
    public void setDataStorageAdapter(DataStorageAdapter dsa) {
        dataManager.setDataStorageAdapter(dsa);
    }

    /**
     * Get the DataStorageAdapter
     * @return
     */
    public DataStorageAdapter getDataStorageAdapter() {
        return dataManager.getDataStorageAdapter();
    }
}
