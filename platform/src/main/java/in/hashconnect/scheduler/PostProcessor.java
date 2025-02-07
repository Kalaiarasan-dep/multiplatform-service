package in.hashconnect.scheduler;

import java.util.Map;

public interface PostProcessor {
	void execute(Map<String, Object> context, byte[] out);
}
