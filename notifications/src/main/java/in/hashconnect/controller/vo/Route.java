package in.hashconnect.controller.vo;

public class Route {
	private String name;
	private String ip;
	private String aid;
	private String pin;
	private String version;
	private String key;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Route [name=" + name + ", ip=" + ip + ", aid=" + aid + ", pin=" + pin + ", version=" + version
				+ ", key=" + key + "]";
	}
}
