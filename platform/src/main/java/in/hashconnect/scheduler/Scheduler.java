package in.hashconnect.scheduler;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;

import in.hashconnect.scheduler.dao.SchedulerDao;
import in.hashconnect.scheduler.vo.ScheduleMaster;
import in.hashconnect.scheduler.vo.ScheduledJob;
import in.hashconnect.util.JsonUtil;

@SuppressWarnings("unchecked")
public class Scheduler {

	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

	@Autowired
	private SchedulerDao schedulerDao;

	@Autowired
	private ApplicationContext applicationContext;

	private static final String STS_PENDING = "PENDING";
	private static final String STS_INPROGRESS = "INPROGRESS";
	private static final String STS_FAILED = "FAILED";
	private static final String STS_SUCCESS = "SUCCESS";

	@Scheduled(cron = "${schedule.job.cron}")
	public void scheduleReport() throws IOException, SQLException {
		List<ScheduleMaster> reports = schedulerDao.getMasters();

		ZoneId zoneId = ZoneId.ofOffset("GMT", ZoneOffset.of("+05:30"));

		reports.stream().forEach(sm -> {
			try {
				LocalDateTime now = LocalDateTime.now(zoneId).withSecond(0).with(ChronoField.MILLI_OF_SECOND, 0);

				CronExpression expression = CronExpression.parse(sm.getCron());
				LocalDateTime cron = expression.next(now.minus(1, ChronoUnit.MINUTES)).withSecond(0)
						.with(ChronoField.MILLI_OF_SECOND, 0);

				if (now.isEqual(cron)) {
					logger.info("scheduling for id {}", sm.getId());
					schedulerDao.scheduleJob(sm);
				}
			} catch (Exception e) {
				logger.error("failed to schedule job: " + sm.getBean(), e);
			}
		});
	}

	@Scheduled(cron = "${run.scheduled.job.cron}")
	public void runScheduledReport() throws IOException, SQLException {
		List<ScheduledJob> reports = schedulerDao.getJobsByStatus(STS_PENDING);

		reports.parallelStream().forEach(job -> {
			logger.info("running a scheduledJob: {}", job.getId());
			Map<String, Object> context = JsonUtil.readValue(job.getContext(), Map.class);
			if (context == null)
				context = new HashMap<>();
			context.put("job", job);

			String status = STS_INPROGRESS, error = null;

			job.setStatus(status);
			schedulerDao.updateJobStatus(job);
			try {
				applicationContext.getBean(job.getBean(), Job.class).execute(context);
				status = STS_SUCCESS;
			} catch (Exception e) {
				logger.error("ScheduledJob[{}] has failed", job.getBean(), e);
				error = StringUtils.left(ExceptionUtils.getStackTrace(e), 500);
				status = STS_FAILED;
			} finally {
				job.setStatus(status);
				job.setError(error);
				schedulerDao.updateJobStatus(job);
				logger.info("scheduledJobId:{} completed with status:{}", job.getId(), job.getStatus());
			}
		});
	}
}

// end
