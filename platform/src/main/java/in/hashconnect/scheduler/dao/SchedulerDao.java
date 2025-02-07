package in.hashconnect.scheduler.dao;

import java.util.List;

import in.hashconnect.scheduler.vo.ScheduleMaster;
import in.hashconnect.scheduler.vo.ScheduledJob;

public interface SchedulerDao {
	List<ScheduleMaster> getMasters();

	void scheduleJob(ScheduleMaster master);

	List<ScheduledJob> getJobsByStatus(String status);

	void updateJobStatus(ScheduledJob job);
}
