package in.hashconnect.geocode.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class AddressDetails {
    @JsonProperty("address_components")
    private List<Address> addressComponent;
    @JsonProperty("formatted_address")
    private String formattedAddress;
    private Geometry geometry;
    @JsonProperty("place_id")
    private String placeId;
    private List<String> types;

    public AddressDetails() {
    }

    public List<Address> getAddressComponent() {
        return this.addressComponent;
    }

    public void setAddressComponent(List<Address> addressComponent) {
        this.addressComponent = addressComponent;
    }

    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return this.geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<String> getTypes() {
        return this.types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
