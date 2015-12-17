package datatypes;

import util.DataType;

public class Headlight implements DataType{
	private static final long serialVersionUID = -3733217335819466981L;
	private String id;
	private boolean lit, highbeamOns;
	
	public Headlight(String id, boolean lit, boolean highbeamsOn){
		this.id = id;
		this.setLit(lit);
		this.setHighbeamOns(highbeamsOn);
	}
	
	@Override
	public String getDataID() {
		return id;
	}

	public boolean isLit() {
		return lit;
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	public boolean isHighbeamOns() {
		return highbeamOns;
	}

	public void setHighbeamOns(boolean highbeamOns) {
		this.highbeamOns = highbeamOns;
	}


	
}
