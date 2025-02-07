package in.hashconnect.multitenancy.properties;

import java.util.List;

import in.hashconnect.multitenancy.vo.Tenant;

public class TenantProperties {

	private List<Tenant> tenant;

	public List<Tenant> getTenant() {
		return tenant;
	}

	public void setTenant(List<Tenant> tenant) {
		this.tenant = tenant;
	}
}
