package in.hashconnect.controller.vo;

import java.sql.Timestamp;

public class OtpEntry {

    private String otp;
    private String sentTo;
    private Timestamp ExpiryTime;
    private String sentTime;

    private String purpose;

    private Integer isValidated;

    private String userId;

    private String ip;

    private Long expiryInterval;

    public Long getExpiryInterval() {
        return expiryInterval;
    }

    public void setExpiryInterval(Long expiryInterval) {
        this.expiryInterval = expiryInterval;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }


    public Timestamp getExpiryTime() {
        return ExpiryTime;
    }

    public void setExpiryTime(Timestamp expiryTime) {
        ExpiryTime = expiryTime;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Integer isValidated) {
        this.isValidated = isValidated;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
