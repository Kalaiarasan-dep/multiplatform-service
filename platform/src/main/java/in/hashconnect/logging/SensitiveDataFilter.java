package in.hashconnect.logging;

import java.util.List;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;

@Plugin(name = "SensitiveDataFilter", category = "Core", elementType = "filter", printObject = true)
public class SensitiveDataFilter extends AbstractFilter {
	protected static List<String> sensitivePatterns = null;

	@Override
	public Result filter(LogEvent event) {
		String message = event.getMessage().getFormattedMessage();

		if (containsSensitiveData(message)) {
			return Result.DENY;
		}

		return Result.NEUTRAL;
	}

	private boolean containsSensitiveData(String message) {
		if (sensitivePatterns == null)
			return false;

		return sensitivePatterns.stream().anyMatch(message::matches);
	}

	public static void setPatterns(List<String> patterns) {
		sensitivePatterns = patterns;
	}

	@PluginFactory
	public static SensitiveDataFilter createFilter() {
		return new SensitiveDataFilter();
	}
}
