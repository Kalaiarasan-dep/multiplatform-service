package in.hashconnect.geocode.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Address {
    @JsonProperty("long_name")
    private String longName;
    @JsonProperty("short_name")
    private String shortName;
    private List<String> types;

    public Address() {
    }

    public String getLongName() {
        return this.longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<String> getTypes() {
        return this.types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
