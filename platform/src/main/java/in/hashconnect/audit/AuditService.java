package in.hashconnect.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import in.hashconnect.util.StringUtil;

/**
 * This is to add user id and comments into audit history. 
 * 
 */
@Aspect
@Component
public class AuditService {
	private final Logger logger = LoggerFactory.getLogger(AuditService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@After("@annotation(Auditable)")
    public void audit(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("Entering method: " + className + "." + methodName);
        Object ar[] = joinPoint.getArgs();
        for (Object obj : ar) {
        	if (obj instanceof AuditRequest) {
        		AuditRequest audit =  (AuditRequest)obj;
        		if (StringUtil.isValid(audit.getUserId())) {
        			String formatComments = !StringUtil.isValid(audit.getComments())?
        					StringUtil.concate(className, " - ", methodName): audit.getComments();
        			audit.setComments(formatComments); 
        			saveAudit(audit);
        		}
        	}
        }
        logger.info("Exiting method: " + className + "." + methodName);
    }
	
	
	private int saveAudit(AuditRequest req) {
		String query = "insert into audit(login_time, user_id, comments) values(now(), ? , ?)";
		try {
			return jdbcTemplate.update(query, req.getUserId(), req.getComments());
		} catch(Exception e) {
			logger.error("auditing failed ", e);
		}
		return 0;
	}
}
