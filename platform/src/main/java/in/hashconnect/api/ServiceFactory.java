package in.hashconnect.api;

import org.springframework.beans.factory.annotation.Autowired;

public class ServiceFactory {

	@Autowired
	private CommonService createService;
	@Autowired
	private CommonService getService;
	@Autowired
	private CommonService updateService;

	public CommonService get(String type) {
		switch (type) {
		case "create":
			return createService;
		case "update":
			return updateService;
		case "get":
			return getService;
		}
		return null;
	}
}
