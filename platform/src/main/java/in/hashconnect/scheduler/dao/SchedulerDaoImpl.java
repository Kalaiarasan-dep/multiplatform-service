package in.hashconnect.scheduler.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import in.hashconnect.scheduler.vo.ScheduleMaster;
import in.hashconnect.scheduler.vo.ScheduledJob;

@Repository
public class SchedulerDaoImpl implements SchedulerDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<ScheduleMaster> getMasters() {
		String query = "select id,cron,bean,context from scheduled_master where status='Active'";
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<ScheduleMaster>(ScheduleMaster.class));
	}

	@Override
	public void scheduleJob(ScheduleMaster sm) {
		String query = "insert into scheduled_job (master_id,status,context,created_date)values(?,'PENDING',?,now())";
		jdbcTemplate.update(query, sm.getId(), sm.getContext());

	}

	@Override
	public List<ScheduledJob> getJobsByStatus(String status) {
		String query = "select sj.id,sm.bean,sj.context from scheduled_job sj join scheduled_master sm on sj.master_id=sm.id where sj.status=?";
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<ScheduledJob>(ScheduledJob.class), status);
	}

	@Override
	public void updateJobStatus(ScheduledJob job) {
		String query = "update scheduled_job set status=?,error=?,modified_date=now() where id=?";
		jdbcTemplate.update(query, job.getStatus(), job.getError(), job.getId());

	}
}
