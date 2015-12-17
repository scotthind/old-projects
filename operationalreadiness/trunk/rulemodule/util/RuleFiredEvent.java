package rulemodule.util;

import java.io.Serializable;
import java.util.List;

import util.DataType;

public class RuleFiredEvent implements Serializable {
  
	private static final long serialVersionUID = 1L;
   
   String ruleName;
   List<DataType> modifiedData;
   List<DataType> usedData;
   
   public RuleFiredEvent(String ruleName, List<DataType> usedData, List<DataType> modifiedData){
	   this.ruleName = ruleName;
	   this.usedData = usedData;
	   this.modifiedData = modifiedData;
   }
   
   public String getRuleName(){
	   return ruleName;
   }
   
   public List<DataType> getUsedData(){
	   return usedData;
   }
   
   public List<DataType> getModifiedData(){
	return modifiedData;
	   
   }
     
}
