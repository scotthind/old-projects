package datatypes;
import util.DataType;

public class Tire implements DataType{
	private static final long serialVersionUID = 4054684624454674690L;
	private String id;
	private int pressure, recommendedPressure, radius;
	
	public Tire(String id, int pressure, int recommendedPressure, int radius){
		this.id = id;
		this.setPressure(pressure);
		this.setRecommendedPressure(recommendedPressure);
		this.setRadius(radius);
	}
	
	@Override
	public String getDataID() {
		return id;
	}

	public int getPressure() {
		return pressure;
	}

	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	public int getRecommendedPressure() {
		return recommendedPressure;
	}

	public void setRecommendedPressure(int recommendedPressure) {
		this.recommendedPressure = recommendedPressure;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
	
}
