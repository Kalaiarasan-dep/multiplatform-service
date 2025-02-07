package in.hashconnect.api;

import static in.hashconnect.api.vo.Response.STATUS.FAILED;
import static in.hashconnect.api.vo.Response.STATUS.SUCCESS;
import static in.hashconnect.util.StringUtil.concate;
import static in.hashconnect.util.StringUtil.*;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.MapUtils.getBooleanValue;
import static org.apache.commons.collections4.MapUtils.getMap;
import static org.apache.commons.collections4.MapUtils.getObject;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import in.hashconnect.api.dao.ApiDao;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.util.StringUtil;

@SuppressWarnings("unchecked")
public class GetService extends AbstractMasterService implements CommonService {

	@Autowired
	private ApiDao apiDao;

	@Autowired
	private ApplicationContext applicationContext;

	private static final String DEFAULT_VARIANT = "default";

	@Override
	public GetResponse process(String name, Map<String, Object> params) {
		GetResponse response = new GetResponse();

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

		Map<String, Object> dbVariants = (Map<String, Object>) getMap(config, "select");
		boolean defaultVariantFilters = getBooleanValue(dbVariants, "useDefaultAlways");
		boolean suportRoleBasedQueries = getBooleanValue(dbVariants, "suportRoleBasedQueries")
				|| getBooleanValue(dbVariants, "supportRoleBasedQueries");

		// universal pre processor
		String preProcessor = getString(dbVariants, "preProcessor");
		if (isValid(preProcessor)) {
			Response<?> preProcessResponse = applicationContext.getBean(preProcessor, CommonApiPreProcessor.class)
					.process(params);
			if (preProcessResponse.getStatus() == FAILED) {
				response.setStatus(FAILED);
				response.setDesc(defaultIfNotValid(preProcessResponse.getDesc(), "preprocess failed"));
				return response;
			}
		}

		String variant = findVariant(params);
		if (defaultVariantFilters || !isValid(variant)) {
			variant = DEFAULT_VARIANT;
			String defaultParam = getString(dbVariants, "useDefaultAlwaysRequestParam");
			if (isValid(defaultParam)) {
				variant = getString(params, defaultParam);
			}
		}

		if (suportRoleBasedQueries)
			variant = concate(variant, "_", getString(params, "role"));

		Map<String, Object> dbVariant = (Map<String, Object>) getMap(dbVariants, variant);
		if (MapUtils.isEmpty(dbVariant)) {
			response.setStatus(FAILED);
			response.setDesc("variant not found - " + variant);
			return response;
		}

		// variant preprocessor
		preProcessor = getString(dbVariant, "preProcessor");
		if (isValid(preProcessor)) {
			Response<?> preProcessResponse = applicationContext.getBean(preProcessor, CommonApiPreProcessor.class)
					.process(params);
			if (preProcessResponse.getStatus() == FAILED) {
				response.setStatus(FAILED);
				response.setDesc(defaultIfNotValid(preProcessResponse.getDesc(), "preprocess failed"));
				return response;
			}
		}

		List<String> mandatoryFields = (List<String>) getObject(dbVariant, "mandatoryFields");
		if (isNotEmpty(mandatoryFields)) {
			List<String> missingFieldsInRequest = mandatoryFields.stream().filter(k -> !params.containsKey(k))
					.collect(Collectors.toList());
			if (!missingFieldsInRequest.isEmpty()) {
				response.setStatus(FAILED);
				response.setDesc("invalid_request");
				return response;
			}
		}

		// validate data
		Map<String, String> regexOnFields = (Map<String, String>) getMap(dbVariant, "regexFields");
		if (MapUtils.isNotEmpty(regexOnFields)) {
			for (String key : params.keySet()) {
				String regex = getString(regexOnFields, key);
				if (!isValid(regex)) {
					continue;
				}

				String value = getString(params, key);
				if (!Pattern.matches(regex, value)) {
					response.setStatus(FAILED);
					response.setDesc("invalid_request: " + key + " does not match regex " + regex);
					return response;
				}
			}
		}

		if (defaultVariantFilters) {
			Map<String, Object> allowedParams = (Map<String, Object>) getMap(dbVariant, "allowedParams");
			if (defaultVariantFilters && MapUtils.isNotEmpty(allowedParams)) {
				String dynamicWhereClause = buildWhere(params, allowedParams);
				dbVariant.put("customClause", dynamicWhereClause);
			}
		}

		response = apiDao.get(dbVariant, params);

		// post processor
		String postProcessor = getString(dbVariant, "postProcessor");
		if (isValid(postProcessor)) {
			applicationContext.getBean(postProcessor, CommonApiPostProcessor.class).process(response, params);
			if (response.getStatus() == FAILED) {
				response.setStatus(FAILED);
				response.setDesc(defaultIfNotValid(response.getDesc(), "postProcessResponse failed"));
				return response;
			}
		}

		response.setStatus(SUCCESS);
		return response;
	}
}
