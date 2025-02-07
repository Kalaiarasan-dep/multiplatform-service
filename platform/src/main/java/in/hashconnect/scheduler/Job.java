package in.hashconnect.scheduler;

import java.util.Map;

public interface Job {

	void execute(Map<String, Object> context);
}
