package dataplugin.util;

import util.DataType;

public class DataTypeStub implements DataType{
	private static final long serialVersionUID = -3743101869203706966L;
	String example;
	
	public DataTypeStub(String str){
		example = str;
	}

	@Override
	public String getDataID() {
		return example;
	}

	public String getExample() { return example; }
	
	public void setExample(String example) { this.example = example; }
	
	public String toString(){
		return example;
	}
}
