package in.hashconnect.gmb.vo;

import java.util.List;

public class RelationshipData {

  private RelevantLocation parentLocation;
  private List<RelevantLocation> childrenLocations;
  
  public RelevantLocation getParentLocation() {
    return parentLocation;
  }
  public void setParentLocation(RelevantLocation parentLocation) {
    this.parentLocation = parentLocation;
  }
  public List<RelevantLocation> getChildrenLocations() {
    return childrenLocations;
  }
  public void setChildrenLocations(List<RelevantLocation> childrenLocations) {
    this.childrenLocations = childrenLocations;
  }
 
  
  
}
