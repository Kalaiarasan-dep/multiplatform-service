package in.hashconnect.api;

import static in.hashconnect.util.StringUtil.concate;
import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.commons.text.CaseUtils;

public abstract class AbstractMasterService {
	private static final List<String> ESCAPE_PARAMS_FOR_VARIATNS = Arrays.asList("size", "start", "userid", "role");

	protected String findVariant(Map<String, Object> params) {
		Map<String, Object> sortedParams = new TreeMap<>();
		sortedParams.putAll(params);

		StringBuilder by = new StringBuilder("by ");
		sortedParams.forEach((k, v) -> {
			if (!ESCAPE_PARAMS_FOR_VARIATNS.contains(k))
				by.append(k).append(" ");
		});

		by.trimToSize();

		if ("by".equals(by.toString().trim())) {
			return null;
		}

		return CaseUtils.toCamelCase(by.toString(), false, ' ');
	}

	protected String buildWhere(Map<String, Object> params, Map<String, Object> allowedParams) {
		if(params.containsKey("budgetLsEq")){
			Long budgetLsEq = Long.valueOf((String) params.get("budgetLsEq"));
			if(budgetLsEq != null){
				params.put("budgetLsEq",budgetLsEq);
			}
		}
		if(params.containsKey("budgetGrEq")){
			Long budgetGrEq = Long.valueOf((String) params.get("budgetGrEq"));
			if(budgetGrEq != null){
				params.put("budgetLsEq",budgetGrEq);
			}
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : params.keySet()) {
			String formattedField = format(key, params, allowedParams);
			if (!isValid(formattedField))
				continue;

			if (!first)
				sb.append(" and ");

			sb.append(formattedField);
			first = false;
		}

		// processing static values
		for (String key : allowedParams.keySet()) {
			if (!key.startsWith("static_")) {
				continue;
			}
			String formattedKey = key.replace("static_", "");
			Object value = allowedParams.get(key);
			String formattedField = formatStaticParam(formattedKey, value, params);
			if (!isValid(formattedField))
				continue;

			if (!first)
				sb.append(" and ");

			sb.append(formattedField);
			first = false;
		}

		return sb.toString();
	}

	protected String formatStaticParam(String key, Object value, Map<String, Object> params) {
		boolean in = key.endsWith("In");
		boolean notIn = key.endsWith("NotIn");
		boolean like = key.endsWith("Like");
		boolean grEq = key.endsWith("GrEq");
		boolean lsEq = key.endsWith("LsEq");
		boolean isNull = key.endsWith("IsNull");
		boolean isNotEq = key.endsWith("IsNotEq");

		String col = key;
		if (!isValid(col))
			return "";

		if (isNull)
			return concate(key.replace("IsNull", ""), " is null");

		params.put(key, value);
		
		if (notIn) {
			String val = String.valueOf(value);
			params.put(key, Arrays.asList(val.split(",")));
			return concate(key.replace("NotIn", ""), " not in (:", key, ")");
		}

		if (in) {
			String val = String.valueOf(value);
			params.put(key, Arrays.asList(val.split(",")));
			return concate(key.replace("In", ""), " in (:", key, ")");
		}
		if (like) {
			params.put(key, concate("%", value, "%"));
			return concate(key.replace("Like", ""), " like (:", key, ")");
		}
		if (grEq)
			return concate(key.replace("GrEq", ""), " >= :", key);
		if (lsEq)
			return concate(key.replace("LsEq", ""), " <= :", key);
		if (isNotEq)
			return concate(key.replace("IsNotEq", ""), " <> :", key);

		// defaulting to Eq
		return concate(key.replace("Eq", ""), " = :", key);
	}

	private String format(String key, Map<String, Object> params, Map<String, Object> allowedParams) {
		boolean in = key.endsWith("In");
		boolean notIn = key.endsWith("NotIn");
		boolean like = key.endsWith("Like");
		boolean grEq = key.endsWith("GrEq");
		boolean lsEq = key.endsWith("LsEq");
		boolean isNull = key.endsWith("IsNull");

		String col = getString(allowedParams, key);
		if (!isValid(col))
			return "";
		
		if (notIn) {
			String val = getString(params, key);
			params.put(key, Arrays.asList(val.split(",")));
			return concate(col, " not in (:", key, ")");
		}
		if (in) {
			String val = getString(params, key);
			params.put(key, Arrays.asList(val.split(",")));
			return concate(col, " in (:", key, ")");
		}
		if (like) {
			String val = getString(params, key);
			params.put(key, concate("%", val, "%"));
			if (!col.contains(",")) {
				return concate(col, " like (:", key, ")");
			}

			StringBuilder multipleColumnCriteria = new StringBuilder();
			Stream.of(col.split(",")).forEach(c -> {
				if (multipleColumnCriteria.length() > 0)
					multipleColumnCriteria.append(" or ");
				multipleColumnCriteria.append(concate(c, " like (:", key, ")"));
			});

			if (isValid(multipleColumnCriteria.toString())) {
				return concate("(", multipleColumnCriteria, ")");
			}
			return null;
		}
		if (grEq)
			return concate(col, " >= :", key);
		if (lsEq)
			return concate(col, " <= :", key);
		if (isNull)
			return concate(col, " is null");
		// defaulting to Eq
		return concate(col, " = :", key);
	}

	public static void main(String[] args) {
		AbstractMasterService service = new GetService();

		Map<String, Object> params = new HashMap<>();
		params.put("sourceIn", Arrays.asList("OPEN"));
		params.put("regionIn", Arrays.asList("REGION"));
		params.put("dateGrEq", "2026-10-02");
		params.put("dateLsEq", "2026-10-02");
		params.put("searchLike", "1234");

		Map<String, Object> allowedParams = new HashMap<>();
		allowedParams.put("sourceIn", "source");
		allowedParams.put("regionIn", "region");
		allowedParams.put("dateGrEq", "date");
		allowedParams.put("dateLsEq", "date");
		allowedParams.put("static_la.id", "1");
		allowedParams.put("static_name", "abc");
		allowedParams.put("static_addIsNull", "abc");
		allowedParams.put("static_addIsNotEq", "12");
		allowedParams.put("searchLike", "l.name,a.name");

		String where = service.buildWhere(params, allowedParams);
		System.out.println(where);

		System.out.println(params);
	}
}
