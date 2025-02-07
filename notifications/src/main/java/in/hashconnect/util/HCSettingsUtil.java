package in.hashconnect.util;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;



public class HCSettingsUtil extends in.hashconnect.common.util.SettingsUtil {
    @Resource
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(HCSettingsUtil.class);

    @Override
    public void init() {
        //initialization method
    }

}
