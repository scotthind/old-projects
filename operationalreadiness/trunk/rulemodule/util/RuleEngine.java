package rulemodule.util;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import util.FrameworkHandler;

/**
 * The RuleEngine utilizes a Drools rule engine to evaluate data
 * @author Kevin Desmond
 */
public class RuleEngine {
	

	KnowledgeBuilderConfiguration kbuilderConfig;
	KnowledgeBaseConfiguration kbaseConfig;
	KnowledgeBuilder kbuilder;
	KnowledgeBase kbase;
	KnowledgeAgentConfiguration kagentConfig;
	StatefulKnowledgeSession ksession;
	
	public RuleEngine(){

	    kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    
		kbase = KnowledgeBaseFactory.newKnowledgeBase();
		
		ksession = kbase.newStatefulKnowledgeSession();

	}
	
	/**
	 * Add a rule file to the knowledge builder
	 * @param filename
	 */
	public void addRule(String filename){	
		//add rule file to the knowledge builder
		try{
		FileInputStream fis = new FileInputStream(filename);
		kbuilder.add(ResourceFactory.newInputStreamResource(fis), ResourceType.DRL);
		
		//get any errors that may have occurred when trying to compile the rule file
		if( kbuilder.hasErrors()){
			if (FrameworkHandler.DEBUG_MODE) {
				System.out.println("Drools Rule error --------------");
				System.err.println(kbuilder.getErrors().toString());
			}
			//FrameworkHandler.log(FrameworkHandler.LOG_ERROR, this, kbuilder.getErrors().toString());
		}
		//TODO: get rid of this else
		else {
			if (FrameworkHandler.DEBUG_MODE) {
				System.out.println("Rule Packages added successfully");
			}
		}
		
		//add knowledge packages from the knowledge builder into the knowledge base
		kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );	
		}catch(Exception e){
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
		
	}
	
	/**
	 * remove a rule from the knowledge base
	 * @param packageName
	 * @param ruleName
	 */
	public void removeRule(String packageName, String ruleName) {
		kbase.removeRule(packageName, ruleName); 
	}
	
	
	/**
	 * set global variables used in a rule
	 * @param varName
	 * @param varType
	 */
	public void setGlobal(String varName, Object var) {
		ksession.setGlobal(varName, var);	
	}
	
	/**
	 * Add an object to the knowledge session
	 * @param o
	 */
	public void insertObject(Object o){
		if(!(ksession.getFactHandle(o) == null)){
			retractObject(o);
		}
		
		ksession.insert(o);
		
	}
	
	/**
	 * Remove an object from the knowledge session
	 * @param o
	 */
	void retractObject(Object o){
		ksession.retract(ksession.getFactHandle(o));	
	}
	
	/**
	 * Fire all rules in the knowledge session 
	 */
	public void fireAllRules(){
		ksession.fireAllRules();
	}

	/**
	 * Get a rule from the knowledge session
	 * @param packageName
	 * @param rule
	 * @return the rule
	 */
	public Object getRule(String packageName, String rule) {
		String ruleToGet = null;
		if(!(kbase.getRule(packageName,rule) == null))
			ruleToGet = kbase.getRule(packageName, rule).getName();
		
		return ruleToGet;
	}

	/**
	 * Get a list of rules in the knowledge base
	 * @return a list of rules
	 */
	public List<String> getAvailableRules() {
		List<String> availableRules = new LinkedList<String>();
		
		for(KnowledgePackage kpack: kbase.getKnowledgePackages())
			for(Rule rule: kpack.getRules())
				availableRules.add(rule.getName());
		
		return availableRules;
	}
	
	
	
}
