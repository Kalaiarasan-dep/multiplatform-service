package in.hashconnect.geocode.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExactLocation {
    @JsonProperty("northeast")
    private Location northEast;
    @JsonProperty("southwest")
    private Location southWest;

    public ExactLocation() {
    }

    public Location getNorthEast() {
        return this.northEast;
    }

    public void setNorthEast(Location northEast) {
        this.northEast = northEast;
    }

    public Location getSouthWest() {
        return this.southWest;
    }

    public void setSouthWest(Location southWest) {
        this.southWest = southWest;
    }
}
