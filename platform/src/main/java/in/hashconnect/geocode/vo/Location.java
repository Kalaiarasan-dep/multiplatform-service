package in.hashconnect.geocode.vo;

import java.math.BigDecimal;

public class Location {
    private BigDecimal lat;
    private BigDecimal lng;
    private String city;
    private String state;

    public Location() {
    }

    public BigDecimal getLat() {
        return this.lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return this.lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
