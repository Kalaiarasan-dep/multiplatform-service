package in.hashconnect.api;

import static in.hashconnect.api.vo.Response.STATUS.FAILED;
import static in.hashconnect.api.vo.Response.STATUS.SUCCESS;
import static in.hashconnect.util.StringUtil.defaultIfNotValid;
import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getMap;
import static org.apache.commons.collections4.MapUtils.getObject;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import in.hashconnect.api.dao.ApiDao;
import in.hashconnect.api.vo.CreateResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.util.StringUtil;

@SuppressWarnings("unchecked")
public class CreateService implements CommonService {

	@Autowired
	private ApiDao apiDao;

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public CreateResponse process(String name, Map<String, Object> params) {
		CreateResponse response = new CreateResponse();

		if (!StringUtil.isValid(name)) {
			response.setStatus(FAILED);
			response.setDesc("invalid_request");
			return response;
		}

		Map<String, Object> config = apiDao.getConfig(name);
		if (config == null) {
			response.setStatus(FAILED);
			response.setDesc("invalid_request");
			return response;
		}

		Map<String, Object> dbInsert = (Map<String, Object>) getMap(config, "insert");
		List<String> mandatoryFields = (List<String>) getObject(dbInsert, "mandatoryFields");

		if (CollectionUtils.isNotEmpty(mandatoryFields)) {
			List<String> missingFieldsInRequest = mandatoryFields.stream().filter(k -> !params.containsKey(k))
					.collect(Collectors.toList());
			if (!missingFieldsInRequest.isEmpty()) {
				response.setStatus(FAILED);
				response.setDesc("invalid_request");
				return response;
			}
		}

		// validate data
		Map<String, String> regexOnFields = (Map<String, String>) getMap(dbInsert, "regexFields");
		if (MapUtils.isNotEmpty(regexOnFields)) {
			for (String key : params.keySet()) {
				String value = getString(params, key);
				String regex = getString(regexOnFields, key);
				if (isValid(regex)) {
					if (!Pattern.matches(regex, value)) {
						response.setStatus(FAILED);
						response.setDesc("invalid_request: " + key + " does not match regex " + regex);
						return response;
					}
				}
			}
		}

		// pre processor
		String preProcessor = getString(dbInsert, "preProcessor");
		if (isValid(preProcessor)) {
			Response<?> preProcessResponse = applicationContext.getBean(preProcessor, CommonApiPreProcessor.class)
					.process(params);
			if (preProcessResponse.getStatus() == FAILED) {
				response.setStatus(FAILED);
				response.setDesc(defaultIfNotValid(preProcessResponse.getDesc(), "preprocess failed"));
				return response;
			}
		}

		// return generated id
		long id = apiDao.create(dbInsert, params);

		Map<String, Object> data = new HashMap<>(1);
		data.put("id", id);
		response.setData(data);
		response.setStatus(SUCCESS);

		// post processor
		String postProcessor = getString(dbInsert, "postProcessor");
		if (isValid(postProcessor)) {
			applicationContext.getBean(postProcessor, CommonApiPostProcessor.class).process(response, params);
			if (response.getStatus() == FAILED) {
				response.setStatus(FAILED);
				response.setDesc(defaultIfNotValid(response.getDesc(), "postProcessResponse failed"));
				return response;
			}
		}

		return response;
	}
}
