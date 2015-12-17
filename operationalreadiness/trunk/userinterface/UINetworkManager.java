package userinterface;

import java.io.File;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import rulemodule.util.RuleFiredEvent;
import userinterface.util.DataIDReceivedListener;
import userinterface.util.DataListener;
import userinterface.util.DataRequester;
import userinterface.util.RuleHandler;
import userinterface.util.RuleListener;
import util.DataType;
import util.FrameworkHandler;
import util.connections.DataReceiver;



/**
 * UINetworkManager controls the Data Requesters, DataReceivers, and RuleHandlers 
 * @author Remo Cocco
 * @author Dan Urbano
 * @author Tom Renn
 * @author Kevin Desmond
 * @version 0.1
 */
public class UINetworkManager implements DataIDReceivedListener, RuleListener {
	
	HashMap<String, RuleHandler> ruleHandlers;
	HashMap<String, DataRequester> dataRequesters;
	HashMap<String, DataReceiver> dataReceivers;
	List<DataListener> dataListeners;
	List<RuleListener> ruleListeners;
	
	private final int FIRE_RULES  = 0; 
	private final int CREATE_RULE = 1;
	private final int REMOVE_RULE = 2;
	private final int GET_RULE    = 3;
	private final int GET_AVAILABLE_RULES  = 4;
	
	private final int FETCH_DATA_IDS = 0; 
	private final int FETCH_DATA   = 1;
	private final int FETCH_CLASS = 2;
		
	
	/**
	 * Constructs a UINetworkManager
	 */
	public UINetworkManager(){
		FrameworkHandler.loadStoredClasses();
		
		dataRequesters = new HashMap<String, DataRequester>();
		dataReceivers = new HashMap<String, DataReceiver>();
		ruleHandlers = new HashMap<String, RuleHandler> ();
		ruleListeners = new LinkedList<RuleListener>();
		dataListeners = new LinkedList<DataListener>();
	}
	
	/**
	 * add a RuleListener to the UINetworkManager
	 */
	public void registerRuleListener(RuleListener listener){
		ruleListeners.add(listener);
	}
	
	/**
	 * add a DataListener to the UINetworkManager
	 */
	public void registerDataListener(DataListener listener){
		dataListeners.add(listener);
	}
	
	/**
	 * remove a RuleListener
	 */
	public void unregisterRuleListener(RuleListener listener){
		ruleListeners.remove(listener);
	}
	
	/**
	 * remove a DataListener to the UINetworkManager
	 */
	public void unregisterDataListener(DataListener listener){
		dataListeners.remove(listener);
	}
	
	/**
	 * add a RuleHandler to the UINetworkManager
	 * @param host
	 * @param port
	 */
	public void registerRuleHandler(String host, int port){
		
		try {
			RuleHandler handler = new RuleHandler(host, port, this);
			String key = makeKey(host,port);
			if(!ruleHandlers.containsKey(key)){
				ruleHandlers.put(key, handler);
			}
			else{
				FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to add Rule Handler with identical address to one that is already running");
			}

		} catch (Exception e) {
				FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
		
		
	}

	/**
	 * remove a RuleHandler
	 * @param host
	 * @param port
	 */
	public void unregisterRuleHandler(String host, int port){
		String key = makeKey(host,port);
		
		if (ruleHandlers.containsKey(key)){
			RuleHandler handler = ruleHandlers.get(key);
			handler.endRun(); //stops the rule handler 
			ruleHandlers.remove(key);
		}
	}
	
	/**
	 * Add a DataRequester
	 * @param host
	 * @param port
	 */
	public void registerDataRequester(String host, int port){
		
		try {
			DataRequester requester = new DataRequester(host, port, this);
			
			String key = makeKey(host,port);
			if(!dataRequesters.containsKey(key)){
				dataRequesters.put(key, requester);
			}
			else{
				FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to add Data Requester with identical address to one that is already running");
			}
		
		} catch (Exception e) {
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
		
		
	}
	
	/**
	 * Remove a DataRequester
	 * @param host
	 * @param port
	 */
	public void unregisterDataRequester(String host, int port){
		String key = makeKey(host,port);
		if (dataRequesters.containsKey(key)){
			DataRequester requester = dataRequesters.get(key);
		    requester.endRun(); //stops the data requester    
		    ruleHandlers.remove(key);
		}
	}
	
	/**
	 * Add a DataReceiver
	 * @param host
	 * @param port
	 */
	public void registerDataReceiver(String host, int port){
		
		try {
			Socket socket = new Socket(host, port);
			DataReceiver receiver = new DataReceiver(socket, this, true);
			
			String key = makeKey(host,port);
			if(!dataReceivers.containsKey(key)){
				dataReceivers.put(key, receiver);
				
				Thread thread = new Thread(receiver);
				thread.start();
				
				
			}
			else{
				FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to add Data Requester with identical address to one that is already running");
			}
		
		} catch (Exception e) {
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}

	}
	
	/**
	 * Remove a DataReceiver
	 * @param host
	 * @param port
	 */
	public void unregisterDataReceiver(String host, int port){
		String key = makeKey(host,port);
		if (dataReceivers.containsKey(key)){
			DataReceiver receiver = dataReceivers.get(key);
		    receiver.endRun(); //stops the data requester    
		    ruleHandlers.remove(key);
		}
	}
	
	
	/**
	 * add fireRules to the RuleHandler queue
	 * @param host
	 * @param port
	 */
    public void fireRules(String host, int port){
    	String key = makeKey(host, port);
    	if(ruleHandlers.containsKey(key)){
    		LinkedList<Object> items = new LinkedList<Object>();
        	items.add(FIRE_RULES);
        	RuleHandler handler = ruleHandlers.get(key);
        	handler.addToQueue(items);
        	synchronized(handler){
        		handler.notify();
        	}
    	}
    	else 
    		FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to fire rules with non-existing connection");
    }

	/**
	 * add create to the RuleHandler queue
	 * @param host
	 * @param port
	 * @param filename
	 */
    public void createRule(String host, int port, String filename) {
    	String key = makeKey(host,port);
    	if(ruleHandlers.containsKey(key)){
    		LinkedList<Object> items = new LinkedList<Object>();
    		items.add(CREATE_RULE);
    		items.add(filename);
    		RuleHandler handler = ruleHandlers.get(key);
    		handler.addToQueue(items);
    		synchronized(handler){
    			handler.notify();
    		}
    	}
    	else 
    		FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to create rule with non-existing connection");
    	
    }
    
    
    /**
     * add createRule to the RuleHandler queue
     * @param host
     * @param port
     * @param filename
     */
    public void editRule(String host, int port, String filename){
    	createRule(host, port, filename);
    }
    
    
    /**
     * add removeRule to the RuleHandler queue
     * @param host
     * @param port
     * @param packageName
     * @param ruleName
     */
    public void removeRule(String host, int port, String packageName, String ruleName) {
    	String key = makeKey(host, port);
    	if(ruleHandlers.containsKey(key)){
    		LinkedList<Object> items = new LinkedList<Object>();
    		items.add(REMOVE_RULE);
    		items.add(packageName);
    		items.add(ruleName);
    		RuleHandler handler = ruleHandlers.get(key);
    		handler.addToQueue(items);
    		synchronized(handler){
    			handler.notify();
    		}
    	}
    	else 
    		FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to remove rule from non-existing connection");
    }
    
    /**
     * add getRule to the RuleHandler queue
     * @param host
     * @param port
     * @param packageName
     * @param ruleName
     */
    public void getRule(String host, int port, String packageName, String ruleName) {
    	String key = makeKey(host, port);
    	if(ruleHandlers.containsKey(key)){
    		LinkedList<Object> items = new LinkedList<Object>();
    		items.add(GET_RULE);
    		items.add(packageName);
    		items.add(ruleName);
    		RuleHandler handler = ruleHandlers.get(key);
    		handler.addToQueue(items);
    		synchronized(handler){
    			handler.notify();
    		}	
    	}
    	else 
    		FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to get a rule from non-existing connection");
    	
    } 
    
    /**
     * add getAvailableRules to the RuleHandler queue
     * @param host
     * @param port
     */
    public void getAvailableRules(String host, int port) {
    	String key = makeKey(host, port);
    	if(ruleHandlers.containsKey(key)){
    		LinkedList<Object> items = new LinkedList<Object>();
    		items.add(GET_AVAILABLE_RULES);
			RuleHandler handler = ruleHandlers.get(key);
			handler.addToQueue(items);
			synchronized(handler){
	    		handler.notify();
	    	}
		}
		else{
			FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to get available rules from non-existing connection");
		}
		
    	
    	
    }
    
    
	/**
	 * add fetchAvailableDataIDs to the DataRequester queue
	 * @param host
	 * @param port
	 */
    public void fetchAvailableDataIDs(String host, int port) {
    	String key = makeKey(host, port);
    	if(dataRequesters.containsKey(key)){
    		LinkedList<Object> items = new LinkedList<Object>();
    		items.add(FETCH_DATA_IDS);
    		DataRequester requester = dataRequesters.get(key);
    		requester.addToQueue(items);
    		synchronized(requester){
    			requester.notify();
    		}
    	}
    	else
    		FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to fetch available data ids from non-existing connection");

    }    

   /**
    * add fetchData to the DataRequester queue
    * @param host
    * @param port
    * @param dataID
    * @param constraint
    */
   public void fetchData(String host, int port, String dataID, Serializable constraint) {
	   String key = makeKey(host, port);
	   if(dataRequesters.containsKey(key)){
		   LinkedList<Object> items = new LinkedList<Object>();
		   items.add(FETCH_DATA);
		   items.add(dataID);
		   items.add(constraint);
		   DataRequester requester = dataRequesters.get(key);
		   requester.addToQueue(items);
		   synchronized(requester){
			   requester.notify();
		   }
	   }
	   else
   		FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to fetch data from non-existing connection");

    }
   
    /**
     * add fetchClassFile to the DataRequester queue
     * @param host
     * @param port
     */
    public void fetchClassFiles(String host, int port){
    	String key = makeKey(host, port);
    	if(dataRequesters.containsKey(key)){
    		LinkedList<Object> items =  new LinkedList<Object>();
    		items.add(FETCH_CLASS);
    		DataRequester requester = dataRequesters.get(key);
    		requester.addToQueue(items);
    		synchronized(requester){
    			requester.notify();
    		}
    	}
    	else
    		FrameworkHandler.log(FrameworkHandler.LOG_WARN, this, "Attempted to fetch class files from non-existing connection");

    }

    /**
     * Sends the received data to all DataListeners
     */
	@Override
	public Serializable dataReceived(Serializable data) {
		for(DataListener dl: dataListeners){
			dl.didReceiveDataUpdate((DataType) data);
		}
		return null;
	}

	/**
	 * Send the received file to the DataListeners
	 */
	@Override
	public void classReceived(File file) {
		for(DataListener dl: dataListeners){
			dl.didReceiveClassFile(file);
		}
		
	}

	/**
	 * Sends the received dataIDs to the DataListeners
	 */
	@Override
	public void receiveDataIDs(List<String> dataIDs) {
		for(DataListener dl: dataListeners){
			dl.didReceiveAvailableDataIDs(dataIDs);
		}
		
	}

	/** 
	 * Sends the received rule to RuleListeners
	 */
	@Override
	public void didReceiveRule(String ruleID) {
		for (RuleListener rl: ruleListeners){
			rl.didReceiveRule(ruleID);
		}
		
	}

	/**
	 * Sends the received list of rules to the RuleListeners
	 */
	@Override
	public void didReceiveAvailableRules(List<String> availableRules) {
		for (RuleListener rl: ruleListeners){
			rl.didReceiveAvailableRules(availableRules);
		}
		
	}

	/**
	 * Sends the received RuleFiredEvents to the RuleListeners
	 */
	@Override
	public void didReceiveRuleFiredEvents(List<RuleFiredEvent> ruleFiredEvents) {
		for (RuleListener rl: ruleListeners){
			rl.didReceiveRuleFiredEvents(ruleFiredEvents);
		}
		
	}
	
	// create host:port key
    private String makeKey(String host, int port){ return host+":"+port; }
    
			
}

    
    

