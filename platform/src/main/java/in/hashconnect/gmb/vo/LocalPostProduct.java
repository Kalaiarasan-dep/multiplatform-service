package in.hashconnect.gmb.vo;

public class LocalPostProduct {

	private String productName;
	private Money lowerPrice;
	private Money upperPrice;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Money getLowerPrice() {
		return lowerPrice;
	}
	public void setLowerPrice(Money lowerPrice) {
		this.lowerPrice = lowerPrice;
	}
	public Money getUpperPrice() {
		return upperPrice;
	}
	public void setUpperPrice(Money upperPrice) {
		this.upperPrice = upperPrice;
	}
}
