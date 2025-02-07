package in.hashconnect.gmb.vo;

public class Account {

    private String type;
    private String name;
    private String accountName;
    private String role;
    private String permissionLevel;
    private String verificationState;
    private String vettedState;
    private String primaryOwner;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String getVerificationState() {
      return verificationState;
    }

    public void setVerificationState(String verificationState) {
      this.verificationState = verificationState;
    }

    public String getVettedState() {
      return vettedState;
    }

    public void setVettedState(String vettedState) {
      this.vettedState = vettedState;
    }

    public String getPrimaryOwner() {
      return primaryOwner;
    }

    public void setPrimaryOwner(String primaryOwner) {
      this.primaryOwner = primaryOwner;
    }
}
