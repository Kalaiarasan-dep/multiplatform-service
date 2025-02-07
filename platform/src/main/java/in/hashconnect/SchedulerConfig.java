package in.hashconnect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import in.hashconnect.scheduler.Job;
import in.hashconnect.scheduler.Scheduler;
import in.hashconnect.scheduler.dao.SchedulerDao;
import in.hashconnect.scheduler.dao.SchedulerDaoImpl;
import in.hashconnect.scheduler.report.ReportJob;

@Configuration
@ConditionalOnProperty(value = "scheduler.enabled", havingValue = "true", matchIfMissing = false)
@EnableScheduling
public class SchedulerConfig {

	@Bean
	public Scheduler scheduler() {
		return new Scheduler();
	}

	@Bean
	public SchedulerDao schedulerDao() {
		return new SchedulerDaoImpl();
	}

	@Bean
	public Job reportJob() {
		return new ReportJob();
	}
}
