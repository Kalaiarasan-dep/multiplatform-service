package in.hashconnect.gmb.vo;

public class ServiceItem {
  
private Money price;
private StructuredServiceItem structuredServiceItem;
private FreeFormServiceItem freeFormServiceItem;
public Money getPrice() {
  return price;
}
public void setPrice(Money price) {
  this.price = price;
}
public StructuredServiceItem getStructuredServiceItem() {
  return structuredServiceItem;
}
public void setStructuredServiceItem(StructuredServiceItem structuredServiceItem) {
  this.structuredServiceItem = structuredServiceItem;
}
public FreeFormServiceItem getFreeFormServiceItem() {
  return freeFormServiceItem;
}
public void setFreeFormServiceItem(FreeFormServiceItem freeFormServiceItem) {
  this.freeFormServiceItem = freeFormServiceItem;
}


}
