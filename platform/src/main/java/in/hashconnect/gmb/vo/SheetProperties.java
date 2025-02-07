package in.hashconnect.gmb.vo;

public class SheetProperties {
	private Integer sheetId;
	private String title;
	private Integer index;
	private SheetType sheetType;
	private GridProperties gridProperties;

	public Integer getSheetId() {
		return sheetId;
	}

	public void setSheetId(Integer sheetId) {
		this.sheetId = sheetId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public SheetType getSheetType() {
		return sheetType;
	}

	public void setSheetType(SheetType sheetType) {
		this.sheetType = sheetType;
	}

	public GridProperties getGridProperties() {
		return gridProperties;
	}

	public void setGridProperties(GridProperties gridProperties) {
		this.gridProperties = gridProperties;
	}
}
