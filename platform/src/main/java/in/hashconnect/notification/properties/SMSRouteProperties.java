package in.hashconnect.notification.properties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import in.hashconnect.notification.service.vo.Route;

public class SMSRouteProperties {

	private Map<String, Route> routesMap = new ConcurrentHashMap<String, Route>();

	public Route route(String route) {
		return routesMap.get(route);
	}

	public Map<String, Route> getRoutesMap() {
		return routesMap;
	}

	public void setRoutesMap(Map<String, Route> routesMap) {
		this.routesMap = routesMap;
	}

}
