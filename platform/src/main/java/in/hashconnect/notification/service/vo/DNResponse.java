package in.hashconnect.notification.service.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DNResponse {

    @JacksonXmlProperty(localName = "a2wackid")
    private String a2wackid;

    @JacksonXmlProperty(localName = "custref")
    private String custref;

    @JacksonXmlProperty(localName = "submitdt")
    private String submitdt;

    @JacksonXmlProperty(localName = "lastutime")
    private String lastutime;

    @JacksonXmlProperty(localName = "a2wstatus")
    private String a2wstatus;

    @JacksonXmlProperty(localName = "carrierstatus")
    private String carrierstatus;

    @JacksonXmlProperty(localName = "mnumber")
    private String mnumber;

    @JacksonXmlProperty(localName = "sts")
    private String sts;

    @JacksonXmlProperty(localName = "a2werrcode")
    private String a2werrcode;

    @JacksonXmlProperty(localName = "aid")
    private String aid;

    @JacksonXmlProperty(localName = "senderid")
    private String senderid;

    @JacksonXmlProperty(localName = "totalparts")
    private String totalparts;

    // Getter and Setter methods

    public String getA2wackid() {
        return a2wackid;
    }

    public void setA2wackid(String a2wackid) {
        this.a2wackid = a2wackid;
    }

    public String getCustref() {
        return custref;
    }

    public void setCustref(String custref) {
        this.custref = custref;
    }

    public String getSubmitdt() {
        return submitdt;
    }

    public void setSubmitdt(String submitdt) {
        this.submitdt = submitdt;
    }

    public String getLastutime() {
        return lastutime;
    }

    public void setLastutime(String lastutime) {
        this.lastutime = lastutime;
    }

    public String getA2wstatus() {
        return a2wstatus;
    }

    public void setA2wstatus(String a2wstatus) {
        this.a2wstatus = a2wstatus;
    }

    public String getCarrierstatus() {
        return carrierstatus;
    }

    public void setCarrierstatus(String carrierstatus) {
        this.carrierstatus = carrierstatus;
    }

    public String getMnumber() {
        return mnumber;
    }

    public void setMnumber(String mnumber) {
        this.mnumber = mnumber;
    }

    public String getSts() {
        return sts;
    }

    public void setSts(String sts) {
        this.sts = sts;
    }

    public String getA2werrcode() {
        return a2werrcode;
    }

    public void setA2werrcode(String a2werrcode) {
        this.a2werrcode = a2werrcode;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getTotalparts() {
        return totalparts;
    }

    public void setTotalparts(String totalparts) {
        this.totalparts = totalparts;
    }
}
