package in.hashconnect.config.properties;

import in.hashconnect.controller.vo.Route;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Configuration
@ConfigurationProperties("sms.gateway")
public class SMSRouteProperties {

	private List<Route> routes;

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	private Map<String, Route> routesMap = new HashMap<>();

	public Route route(String route) {
		if (routesMap.isEmpty()) {
			synchronized (routesMap) {
				for (Route r : routes) {
					routesMap.put(r.getName(), r);
				}
			}
		}
		return routesMap.get(route);
	}

}
