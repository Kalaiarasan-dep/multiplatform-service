package in.hashconnect.multitenancy.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import in.hashconnect.multitenancy.vo.TenantContext;

public class MultitenantDataSource extends AbstractRoutingDataSource {

	@Override
	protected String determineCurrentLookupKey() {
		return TenantContext.getCurrentTenant();
	}

}
