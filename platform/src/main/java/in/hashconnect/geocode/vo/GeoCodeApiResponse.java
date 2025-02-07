package in.hashconnect.geocode.vo;

import java.util.List;

public class GeoCodeApiResponse {
    private List<AddressDetails> results;
    private String status;

    public GeoCodeApiResponse() {
    }

    public List<AddressDetails> getResults() {
        return this.results;
    }

    public void setResults(List<AddressDetails> results) {
        this.results = results;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
