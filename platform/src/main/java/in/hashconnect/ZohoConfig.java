package in.hashconnect;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.hashconnect.zoho.ZohoApiProcessor;
import in.hashconnect.zoho.ZohoApiProcessorImpl;
import in.hashconnect.zoho.dao.ZohoDao;
import in.hashconnect.zoho.dao.ZohoDaoImpl;

@Configuration
@ConditionalOnProperty(value = "zoho.enabled", havingValue = "true", matchIfMissing = false)
public class ZohoConfig {

	@Bean
	public ZohoApiProcessor zohoApiProcessor() {
		ZohoApiProcessorImpl impl = new ZohoApiProcessorImpl();
		impl.init();
		return impl;
	}

	@Bean
	public ZohoDao zohoDao(DataSource dataSource) {
		ZohoDaoImpl dao = new ZohoDaoImpl();
		dao.setDataSource(dataSource);
		return dao;
	}
}
