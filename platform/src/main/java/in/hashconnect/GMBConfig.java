package in.hashconnect;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.hashconnect.gmb.dao.GMBDao;
import in.hashconnect.gmb.dao.GMBDaoImpl;
import in.hashconnect.gmb.service.GMBService;
import in.hashconnect.gmb.service.GMBServiceImpl;

@Configuration
@ConditionalOnProperty(value = "gmb.enabled", havingValue = "true", matchIfMissing = false)
public class GMBConfig {
	@Bean
	public GMBDao gmbDao(DataSource dataSource) {
		GMBDaoImpl impl = new GMBDaoImpl();
		impl.setDataSource(dataSource);
		return impl;
	}

	@Bean
	public GMBService gmbService(GMBDao gmbDao) {
		return new GMBServiceImpl(gmbDao);
	}
}
