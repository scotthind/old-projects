package userinterface.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import rulemodule.util.RuleFiredEvent;
import util.FrameworkHandler;

/**
 * The RuleHandler is the part of the User Interface module that handles the networking connections with
 * the Rule Module.  It contains a single socket to listen on.  
 * @author Kevin Desmond
 */
public class RuleHandler extends Thread{
	
	//number codes for output streams to Rules
	private final int FIRE_RULES  = 0; 
	private final int CREATE_RULE = 1;
	private final int REMOVE_RULE = 2;
	private final int GET_RULE    = 3;
	private final int GET_AVAILABLE_RULES  = 4;
	
    public Queue<List<Object>> queue;	
	Socket socket;
	
	DataOutputStream dataOutput;
	ObjectOutputStream objectOutput;
	ObjectInputStream objectInput;
	RuleListener rListener;
	private boolean running;
	
	/**
	 * Constructor
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public RuleHandler(String host, int port, RuleListener rListener) throws UnknownHostException, IOException{
		socket = new Socket(host, port);
		
		queue = new LinkedList<List<Object>>();
		
		OutputStream os = socket.getOutputStream();
	    dataOutput = new DataOutputStream(os);
	    objectOutput = new ObjectOutputStream(os);
	    
	    InputStream is = socket.getInputStream();
	    objectInput = new ObjectInputStream(is);
	    
	    this.rListener = rListener;
		
	    running = true;
	    
	    start();
	}
	
	/**
	 * Add list to queue
	 * @param items
	 */
	public void addToQueue(LinkedList<Object> items){
		queue.add(items);
	}
	
	/**
	 * Waits for a command to enter the queue from the UINetworkManager
	 * and then calls the appropriate function
	 */
	public void run(){
		while (running){
			if(queue.isEmpty()){
				synchronized(this){
					System.out.println("Rule handler waiting.....");
					try {
						this.wait();
					} catch (InterruptedException e) {
						FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
					}	
				}
			}	
			
			else {
				//retrieve the next list from the queue
				LinkedList<Object> nextItem = (LinkedList<Object>) queue.poll();
				
				
				//first Object in the list will be an integer corresponding to the function being called
				int i = (Integer) nextItem.poll();
			
				if(i == FIRE_RULES){
					fireRules();
				}
				else if(i== CREATE_RULE){
					String filename = (String) nextItem.poll(); //get the parameter for the function
					createRule(filename);
				}
				else if(i == REMOVE_RULE){
					//get the parameters and then call the function
					String packageName = (String) nextItem.poll();
					String ruleName = (String) nextItem.poll();
					removeRule(packageName, ruleName);
				}	
				else if(i == GET_RULE){ 
					//get the parameters and then call the function
					String packageName = (String) nextItem.poll();
					String ruleName = (String) nextItem.poll();
					getRule(packageName, ruleName);
				}
				else if(i == GET_AVAILABLE_RULES){ 
					getAvailableRules(); 
				}

			}
		}
	}

	/**
	 * Retrieve a list of rules currently in the rule engine 
	 */
	@SuppressWarnings("unchecked")
	private void getAvailableRules() {
		
    	try{
    		
		    dataOutput.writeInt(GET_AVAILABLE_RULES);
		    
		    List<String> availableRules = (List<String>) objectInput.readObject();
		    
		    rListener.didReceiveAvailableRules(availableRules);
    		
    	} catch(Exception e){
    		FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
    	}
    
		
	}

	/**
	 * Get a specific rule from the rule module
	 * @param filename
	 * @param ruleName 
	 * @return rule
	 */
	private void getRule(String packageName, String ruleName) {
		
    	try{
		    
		    dataOutput.writeInt(GET_RULE);
		    objectOutput.writeObject(packageName);
		    objectOutput.writeObject(ruleName);
		    
		    String rule = (String) objectInput.readObject();
		    
		    rListener.didReceiveRule(rule);
    		
    	} catch(Exception e){
    		FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
    	}
		
	}

	/**
	 * Remove a rule from the RuleModule
	 * @param filename
	 * @param ruleName 
	 */
	private void removeRule(String packageName, String ruleName) {
		
		try {
    		
    		dataOutput.writeInt(REMOVE_RULE);
    		objectOutput.writeObject(packageName);
    		objectOutput.writeObject(ruleName);
    		objectInput.readObject();//wait until something is read before continuing
    		
    	}catch(Exception e){
    		FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
    	}
		
	}

	/**
	 * Add a rule to the rule module
	 * @param dependingClasses
	 * @param rule
	 * @param filename
	 */
	private void createRule(String filename) {
		
		try{ 
    		
			File file = new File(filename);
			long length = file.length();
			
    		dataOutput.writeInt(CREATE_RULE);
    		
    		objectOutput.writeObject(file.getName());
    		dataOutput.writeLong(length);
    		
    		System.out.println("Sending .drl file");
		
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[socket.getSendBufferSize()];
			int bytesRead = 0;
			int numberOfBytesRead = 0;
			
			// write file
			while ((bytesRead = fis.read(buffer)) > 0 )
			{
				numberOfBytesRead += bytesRead;
				socket.getOutputStream().write(buffer, 0, bytesRead);
			}	
			objectInput.readObject(); //wait until something is read before continuing
	
    	}catch(Exception e){
    		FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
    	}
		
	}

	/**
	 * Fires all Rules and sends RuleFiredEvents to the RuleListener
	 */
	@SuppressWarnings("unchecked")
	private void fireRules() {
		List<RuleFiredEvent> ruleFiredEvents = null;
    	
    	try{
		    dataOutput.writeInt(FIRE_RULES);
		    
		    ruleFiredEvents = (List<RuleFiredEvent>) objectInput.readObject();
		       		
    	} catch(Exception e){
    		FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
    	}
		
    	rListener.didReceiveRuleFiredEvents(ruleFiredEvents);
		
	}
	
	public void endRun(){
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			if (FrameworkHandler.DEBUG_MODE){
				System.out.println("ERROR: Failed to close Rule Module Connection");
				e.printStackTrace();
			}
		}
	}
	
	
}
	

