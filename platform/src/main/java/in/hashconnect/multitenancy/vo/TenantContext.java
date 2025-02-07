package in.hashconnect.multitenancy.vo;

public class TenantContext {

	private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<String>();

	public static String getCurrentTenant() {
		return CURRENT_TENANT.get();
	}

	public static void setCurrentTenant(String tenant) {
		CURRENT_TENANT.set(tenant);
	}
}