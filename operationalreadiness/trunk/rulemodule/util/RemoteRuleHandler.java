package rulemodule.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


import util.FrameworkHandler;


/**
 * Handles the rule manager from a remote connection
 * 
 */
public class RemoteRuleHandler implements Runnable{
	
	public final static int FIRE_RULES 	 = 0;
	public final static int CREATE_RULE	 = 1;
	public final static int REMOVE_RULE	 = 2;
	public final static int GET_RULE 	 = 3;
	public final static int GET_AVAL_RULES = 4;

	private RuleManager ruleManager;
	private Socket connection;
	
	
	private ObjectOutputStream objectOutput;
	private DataInputStream dataInput;
	private ObjectInputStream objectInput;
	
	
	// constructor
	public RemoteRuleHandler(Socket newConnection){
		ruleManager = RuleManager.getInstance();
		connection = newConnection;
		
		try {
			objectInput = new ObjectInputStream(connection.getInputStream());
			objectOutput = new ObjectOutputStream(connection.getOutputStream());
			dataInput = new DataInputStream(connection.getInputStream());

			
		} catch (Exception e){
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
		
		if (FrameworkHandler.DEBUG_MODE) {
			System.out.println("~ RemoteRuleHandler connection started");
		}
	}
	
	/**
	 * Execution of thread
	 */
	public void run() {
		Object response = null;
		
		while (true) {
			 try {
				 
				 int action = dataInput.readInt();
				 
				 switch (action) {
				 
				 case FIRE_RULES:
					 response = fireAllRules();
					 break;
					 
				 case CREATE_RULE:
					 createRule();
					 break;
					 
				 case REMOVE_RULE:
					 removeRule();
					 break;
					 
				 case GET_RULE:
					 response = getRule();
					 break;
					 
				 case GET_AVAL_RULES:
					 response = getAvailableRules();
					 break;
					 
				 }
				 
				objectOutput.writeObject(response); 
				 
			 } catch (Exception e){
				 FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
				 break;
			 }
		}
		
	}
	
	/**
	 * fire all the rules in the knowledge session
	 * @return ruleFiredEvents, a list containing events created when a rule is fired
	 */
	public List<RuleFiredEvent> fireAllRules() {
		return ruleManager.fireAllRules();
	}

	/**
	 * Give the ruleManager a rule to add to the rule engine
	 */
	public void createRule(){
		
		try{

		String filename = (String) objectInput.readObject();
		long numOfBytesInFile = dataInput.readLong();
		
		if (FrameworkHandler.DEBUG_MODE){
			System.out.printf("DataReceiver: Receiving file : %s - %d bytes\n", filename, numOfBytesInFile);
			System.out.println();
		}
		
		
		// create new file and output stream
		File newFile = new File(FrameworkHandler.getRuleDirectory(),filename);
		FileOutputStream wr = new FileOutputStream(newFile);
		
		
		byte[] buffer = new byte[connection.getReceiveBufferSize()];
		int bytesReceived = 0;
		int totalBytesReceived = 0;
		// write to file until we've received all bytes
		while(totalBytesReceived < numOfBytesInFile && (bytesReceived = connection.getInputStream().read(buffer))>0)
		{
			wr.write(buffer,0,bytesReceived);
			totalBytesReceived += bytesReceived;
			System.out.println("bytes received: " + bytesReceived + "bytes total: " + totalBytesReceived);
		}
		wr.close();
		
		ruleManager.addRule(newFile.getAbsolutePath());
		
		}catch (Exception e){
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
		
		
	}
	
	/**
	 * remove a rule from the ruleEngine
	 * Note: Objects do not get re-evaluated after the removal of a rule
	 */
	public void removeRule() { 
		
		try { 
		String packageName = (String) objectInput.readObject();
	    String ruleName = (String) objectInput.readObject();
		
	    ruleManager.removeRule(packageName, ruleName);
		
		} catch (Exception e){
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
		
		
	}
	
	/**
	 * retrieve a rule from the knowledge base
	 * @return
	 */
	public Object getRule() { 
		String packageName = null;
		String ruleName = null;
		
		try{
			packageName = (String) objectInput.readObject();
		    ruleName = (String) objectInput.readObject();
		
	   
		} catch (Exception e){
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
		
		return ruleManager.getRule(packageName, ruleName);
	    
	} 
	
	/**
	 * @return all the rules in the knowledge base
	 */
	public Object getAvailableRules() {
		return  ruleManager.getAvailableRules();
		
	}
	
}	
