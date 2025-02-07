package in.hashconnect.gmb.vo;

public class Sheet {
	private SheetProperties properties;
	private GridData data;
	
	public SheetProperties getProperties() {
		if(properties == null)
			properties = new SheetProperties();
		return properties;
	}
	public void setProperties(SheetProperties properties) {
		this.properties = properties;
	}
	public GridData getData() {
		return data;
	}
	public void setData(GridData data) {
		this.data = data;
	}
}
