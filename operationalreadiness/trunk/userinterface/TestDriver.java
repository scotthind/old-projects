package userinterface;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import rulemodule.util.RuleFiredEvent;
import userinterface.util.DataListener;
import userinterface.util.DataRequester;
import userinterface.util.RuleHandler;
import userinterface.util.RuleListener;
import util.DataType;
import util.connections.DataReceiver;

public class TestDriver implements DataListener, RuleListener{
	   
	   private static final String TOM = "150.250.190.192";
	   private static final String NO_ONE = "150.250.190.249";
	   private static final String KEVIN = "150.250.190.118";
	   
	   private static final int REQUEST = 7369;
	   private static final int RECEIVE = 7367;
	   private static final int RULE = 2223;
	   
	   
	
	   public static UINetworkManager manager;
	   public static DataReceiver receiver;
	   public static RuleHandler handler;
	   public static DataRequester requester;
	   

	public static void main (String args[]) throws UnknownHostException, IOException, InterruptedException{
		TestDriver listener = new TestDriver();
		
		manager = new UINetworkManager();
		
		Socket tomReceive = new Socket(TOM, RECEIVE);

        receiver = new DataReceiver(tomReceive, manager, true);
        manager.registerDataReceiver(TOM, RECEIVE, receiver);
        manager.registerDataListener(listener);
		
//		requester = new DataRequester(KEVIN, REQUEST);
//		manager.registerDataRequester(KEVIN, REQUEST, requester);
		
    	handler = new RuleHandler(KEVIN, RULE, manager);
    	manager.registerRuleHandler(KEVIN, RULE, handler);
    	manager.registerRuleListener(listener);
    	
    	
    	//data Requester
//	   manager.fetchClassFile(KEVIN, REQUEST, listener);
//	   manager.fetchAvailableDataIDs(KEVIN, REQUEST, listener);  
//	   manager.fetchData(KEVIN, REQUEST, listener, null, null);
	   
       Thread.sleep(20000);
    	
	  
		//rule Handler
	//	manager.createRule(KEVIN, RULE, "C:\\Users\\Shahid\\Desktop\\tire.drl");
		manager.createRule(KEVIN, RULE, "C:\\Users\\Shahid\\Desktop\\headlight.drl");
//		manager.getAvailableRules(KEVIN, RULE, listener);
//		manager.getRule(KEVIN, RULE, listener, "testpackage", "Is too old");
//		manager.fireRules(KEVIN, RULE, listener);
//		manager.removeRule(KEVIN, RULE, "package", "ruleName");
//		manager.removeRule(KEVIN, RULE, "testpackage", "Is too young");
//		manager.getAvailableRules(KEVIN, RULE, listener);
		manager.fireRules(KEVIN, RULE, listener);
		

//		
//		
//    	manager.removeRule(TOM, RULE, "testpackage", "Is too young");
//    	manager.getAvailableRules(TOM, RULE, listener);		
}


	@Override
	public void didReceiveRule(String ruleID) {
		if (ruleID == null)
			System.out.println("The rule you are looking for does not exist");
		else
			System.out.print(ruleID + " is in the knowledge session");
	}

	@Override
	public void didReceiveAvailableRules(List<String> availableRules) {
		System.out.println(availableRules.size() + " Available Rule ID's:");
		for(String s: availableRules){
			System.out.println(s);
		}
		
	}

	@Override
	public void didReceiveRuleFiredEvents(List<RuleFiredEvent> ruleFiredEvents) {
		
		System.out.println(ruleFiredEvents.size() + " Rule Fired Events Occured:");
		int i = 1;
		
		for(RuleFiredEvent rfi: ruleFiredEvents){
			System.out.print("  " + i + ".");
			System.out.println(rfi.getRuleName() + ": ");
			
			if (!rfi.getUsedData().isEmpty()){
				System.out.print("    Used Data: ");
				for(DataType dt: rfi.getUsedData())
					System.out.print(dt.getDataID() + " ");
			}
			System.out.println();
			if (!rfi.getModifiedData().isEmpty()){
				System.out.print("    Modified Data: ");
				for(DataType dt: rfi.getModifiedData())
					System.out.print(dt.getDataID() + " ");
			}
			i++;
		}
		System.out.println();
		
	}

	@Override
	public void didReceiveAvailableDataIDs(List<String> availableDataIDs) {
		System.out.println(availableDataIDs.size() + "Available Data IDs received:");
		for(String s: availableDataIDs){
			System.out.print(s + " ");
		}
		System.out.println();
		
	}


	@Override
	public void didReceiveDataUpdate(DataType dataType) {
		System.out.println("Recieved data: " + dataType.getDataID());
		
	}


	@Override
	public void didReceiveClassFile(File file) {
		System.out.println("Received file: " + file.getAbsolutePath());
		
	}

	



	
}
