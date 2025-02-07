package in.hashconnect.geocode.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Geometry {
    private Location location;
    @JsonProperty("location_type")
    private String locationType;
    private ExactLocation bounds;
    @JsonProperty("viewport")
    private ExactLocation viewPort;

    public Geometry() {
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationType() {
        return this.locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public ExactLocation getBounds() {
        return this.bounds;
    }

    public void setBounds(ExactLocation bounds) {
        this.bounds = bounds;
    }

    public ExactLocation getViewPort() {
        return this.viewPort;
    }

    public void setViewPort(ExactLocation viewPort) {
        this.viewPort = viewPort;
    }
}
