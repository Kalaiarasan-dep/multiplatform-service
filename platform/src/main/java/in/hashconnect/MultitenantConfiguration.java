package in.hashconnect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import in.hashconnect.multitenancy.config.MultitenantDataSource;
import in.hashconnect.multitenancy.filter.TenantFilter;
import in.hashconnect.multitenancy.properties.DefaultDataSourceProperties;
import in.hashconnect.multitenancy.properties.TenantProperties;
import in.hashconnect.multitenancy.vo.Tenant;

@Configuration
@ConditionalOnProperty(value = "multitenancy.enabled", havingValue = "true", matchIfMissing = false)
public class MultitenantConfiguration {

	@Value("${multi.tenant.default:'default'}")
	private String defaultTenant;

	@Bean
	public DataSource dataSource(TenantProperties tenantProperties,
			DefaultDataSourceProperties defaultDataSourceProps) {
		Map<Object, Object> resolvedDataSources = new HashMap<>();

		List<Tenant> tenants = tenantProperties.getTenant();

		// All Tenants
		for (Tenant tenant : tenants) {
			DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
			try {
				String tenantId = tenant.getName();
				dataSourceBuilder.driverClassName(tenant.getDriverClassName());
				dataSourceBuilder.username(tenant.getUsername());
				dataSourceBuilder.password(tenant.getPassword());
				dataSourceBuilder.url(tenant.getUrl());
				resolvedDataSources.put(tenantId, dataSourceBuilder.build());
			} catch (Exception exp) {
				throw new RuntimeException("Problem in tenant datasource:" + exp);
			}
		}

		DataSourceBuilder<?> defaultDataSourceBuilder = DataSourceBuilder.create();
		defaultDataSourceBuilder.driverClassName(defaultDataSourceProps.getDriverClassName());
		defaultDataSourceBuilder.username(defaultDataSourceProps.getUsername());
		defaultDataSourceBuilder.password(defaultDataSourceProps.getPassword());
		defaultDataSourceBuilder.url(defaultDataSourceProps.getUrl());

		AbstractRoutingDataSource dataSource = new MultitenantDataSource();
		dataSource.setDefaultTargetDataSource(defaultDataSourceBuilder.build());
		dataSource.setTargetDataSources(resolvedDataSources);
		dataSource.afterPropertiesSet();
		return dataSource;
	}

	@Bean
	@Order(1)
	public Filter tenantFilter() {
		return new TenantFilter();
	}

	@Bean
	@ConfigurationProperties("multi")
	public TenantProperties tenantProperties() {
		return new TenantProperties();
	}

	@Bean
	@ConfigurationProperties("spring.datasource")
	public DefaultDataSourceProperties defaultDataSourceProps() {
		return new DefaultDataSourceProperties();
	}

}
