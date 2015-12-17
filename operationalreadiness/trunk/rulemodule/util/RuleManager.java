package rulemodule.util;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


import util.DataReceivedListener;
import util.DataType;



/**
 *  The Rule Manager is in charge of managing the Rule Engine.
 *  It notifies the Rule Engine of the rules and data that need to be added or removed 
 *
 */
public class RuleManager implements DataReceivedListener, RuleListener{
	
	private List<RuleFiredEvent> ruleFiredEvents;
	private static RuleManager instance;
	private RuleEngine ruleEngine;
	private boolean globalWasSet;
	
	/**
	 * Get the RuleManager singleton
	 * 
	 * @return the singleton
	 */
	public static synchronized RuleManager getInstance(){
		
		if (instance == null) {
			instance = new RuleManager();
		}
		return instance;
	}

	/**
	 * Construct a RuleManager
	 */
	private RuleManager(){
		ruleFiredEvents = new LinkedList<RuleFiredEvent>();
		ruleEngine = new RuleEngine();
		globalWasSet = false;
		
	}

	@Override
	public Serializable dataReceived(Serializable data) {
		System.out.println("Received data : " + data);
		ruleEngine.insertObject(data);
		return null;
	}


	@Override
	public void classReceived(File file) {
		//nothing needs to be done here
	}


	/**
	 * Fire all rules in the RuleEngine's knowledge session
	 * @return ruleFiredEvents, a list of RuleFiredEvents created by the fired rules
	 */
	public List<RuleFiredEvent> fireAllRules() {
		ruleFiredEvents.clear(); //empty the list
		
		ruleEngine.fireAllRules();  //fire rules
		
		return ruleFiredEvents;
	}
	
	/**
	 * Add a rule file to the RuleEngine's knowledge base
	 * @param filename, the name of the file containing the rule(s) to add
	 */
	public void addRule(String filename){
		ruleEngine.addRule(filename);
		
		//the rule engine needs to be given the global variable used in the rule
		if (!globalWasSet){
			ruleEngine.setGlobal("listener", instance);
			globalWasSet = true;
		}
	}

	/**
	 * Remove a rule from the RuleEngine's knowledge base
	 * @param packageName, the name of the package containing the rule
	 * @param ruleName, the name of the rule
	 */
	public void removeRule(String packageName, String ruleName) {
		ruleEngine.removeRule(packageName, ruleName);
	}
	
	/**
	 * Retrieve a rule from the RuleEngine's knowledge base
	 * @param packageName, the name of the package containing the rule
	 * @param ruleName, the name of the rule
	 * @return The name of the rule, if the rule in the knowledge base, otherwise return null
	 */
	public Object getRule(String packageName, String ruleName) {
		return ruleEngine.getRule(packageName, ruleName);
	}
	
	/**
	 * @return the names of all the rules in the knowledge base
	 */
	public Object getAvailableRules() {
		return ruleEngine.getAvailableRules();
	}
	

	/**
	 * Called by the rules, this function creates RuleFiredEvents and adds them to the list
	 * to be returned at the end of fireAllRules
	 */
	@Override
	public void ruleDidFire(String ruleName, List<DataType> used,
			List<DataType> modified) {
		
		if (modified == null){
			modified = new LinkedList<DataType>();
		}
		
		if (used == null){
			used = new LinkedList<DataType>();
		}
		
		RuleFiredEvent rfe = new RuleFiredEvent(ruleName, used, modified);
		ruleFiredEvents.add(rfe);
		
	}
	
}
