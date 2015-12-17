package rulemodule;

import java.io.IOException;

import util.FrameworkHandler;


public class TestDriver {

	public static void main(String[] args){
		
		RNetworkManager ruleNetManager = new RNetworkManager();
		
		//ruleNetManager.registerWithDataStorage("localhost", 7167);
		
		try {
			ruleNetManager.setPort(2222);
			//ruleNetManager.registerWithDataStorage("150.250.190.192", 7367);
			FrameworkHandler.loadStoredClasses();
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
