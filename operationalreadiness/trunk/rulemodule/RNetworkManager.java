package rulemodule;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import rulemodule.util.ConnectionScheduler;
import rulemodule.util.RuleManager;

import util.FrameworkHandler;
import util.connections.DataReceiver;


/**
 *
 * Class RNetworkManager
 * @author Remo Cocco
 * @author Dan Urbano
 * @author Tom Renn
 * @version 0.1
 */

public class RNetworkManager {
	private HashMap<String, Thread> dataStorageHosts;
	private ConnectionScheduler scheduler;
	private Thread schedulerThread;
	
	public RNetworkManager() {
		FrameworkHandler.loadStoredClasses();
		dataStorageHosts = new HashMap<String, Thread>();
	}
	
	/**
	 * Set which port this NetworkManager will listen on
	 * @param port
	 * @throws IOException 
	 */
	public void setPort(int port) throws IOException{
		if (schedulerThread != null) {
			scheduler.closeConnection();
		}
		
		scheduler = new ConnectionScheduler(ConnectionScheduler.RULE_CONNECTION, port);
		schedulerThread = new Thread(scheduler);
		schedulerThread.start();
	}
	
	
	/**
	 * Register a Data Storage Host
	 * @param host Host address of data storage
	 * @param port Port the DataStorage forwarder is listening on
	 */
    void registerWithDataStorage(String host, int port) {
    	String key = makeKey(host,port);
    	
    	if ( dataStorageHosts.containsKey(key) ){
    		
    		Thread thread = dataStorageHosts.get(key);
    		
    		if (thread.isAlive()){ // thread is still running
    			FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to add data storage with identical address to one that is already running");
    			return;
    		}
    		else { // thread has died, no longer need to keep it around
    			dataStorageHosts.remove(key);
    		}
    	}
        try {
        	Socket socket = new Socket(host, port);
			DataReceiver reciever = new DataReceiver(socket, RuleManager.getInstance(), true);
			Thread thread = new Thread(reciever);
			thread.start();
			
			dataStorageHosts.put(key, thread);
        } catch (Exception e) {
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_WARN, this, e);
		}
        
    }

    /**
     * Unregister Data Storage Host
     * @param host
     * @param port
     */
    void unregisterWithDataStorage(String host, int port) {
    	String key = makeKey(host,port);
    	if (dataStorageHosts.containsKey(key)){
    		Thread thread = dataStorageHosts.get(key);
    		thread.stop();
    	}
    	dataStorageHosts.remove(key);
    }
    
    /**
     * Returns a list of all the Data Storage Hosts
     * @return dataStorageHosts
     */
    Set<String> getDataStorageHosts() {
    	return dataStorageHosts.keySet();
    }
    
    // create host:port key
    private String makeKey(String host, int port){ return host+":"+port; }
}

