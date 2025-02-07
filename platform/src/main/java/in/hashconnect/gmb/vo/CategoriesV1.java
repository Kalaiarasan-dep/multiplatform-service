package in.hashconnect.gmb.vo;

import java.util.List;

public class CategoriesV1 {

  private CategoryV1 primaryCategory;
  private List<CategoryV1> additionalCategories;
  
  public CategoryV1 getPrimaryCategory() {
    return primaryCategory;
  }
  public void setPrimaryCategory(CategoryV1 primaryCategory) {
    this.primaryCategory = primaryCategory;
  }
  public List<CategoryV1> getAdditionalCategories() {
    return additionalCategories;
  }
  public void setAdditionalCategories(List<CategoryV1> additionalCategories) {
    this.additionalCategories = additionalCategories;
  }
  
}
