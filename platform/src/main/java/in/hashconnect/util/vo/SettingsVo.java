package in.hashconnect.util.vo;

public class SettingsVo {
    private int slNo;
    private Long id;
    private String name;
    private String value;
    private String whatsAppKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getWhatsAppKey() {
        return whatsAppKey;
    }

    public void setWhatsAppKey(String whatsAppKey) {
        this.whatsAppKey = whatsAppKey;
    }
}
