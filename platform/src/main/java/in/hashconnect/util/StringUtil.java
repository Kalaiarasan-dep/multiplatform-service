package in.hashconnect.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {
	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
	public static final String EMPTY = "";
	public static final String UNDERSCORE = "_";
	public static final String SPACE = " ";
	// below mainly used for zoho push.. dont modify this.
	public static final String NA = "NA/-";
	public static final String NULL = null;

	public static final String concate(Object... values) {
		StringBuilder builder = new StringBuilder();
		for (Object value : values) {
			builder.append(value);
		}
		return builder.toString();
	}

	public static boolean isNull(String value) {
		return value == null || "".equals(value.trim()) || "null".equalsIgnoreCase(value);
	}

	public static String replaceNewlineChars(String str) {
		return null == str ? "" : str.replaceAll("(\\r\\n|\\r|\\n|\\t)", " ");
	}

	public static boolean isValid(String value) {
		return null != value && !value.trim().equals("") && !"null".equals(value);
	}

	public static String defaultIfNotValid(String value, String defaultVal) {
		return !isValid(value) ? defaultVal : value;
	}

	public static String nullIfNotValid(String value) {
		return isValid(value) ? value : null;
	}

	public static <T> String arrayToCSV(T[] array, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0)
				sb.append(separator);
			sb.append(array[i]);
		}
		return sb.toString();
	}

	public static final String toString(String separator, Object... values) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;

		for (Object value : values) {
			if (!first)
				builder.append(separator);
			builder.append(value);
			first = false;
		}
		return builder.toString();
	}

	public static final boolean isValidContactNo(String value) {
		return isValid(value) && ((value.startsWith("91") && value.length() == 12)
				|| (!value.startsWith("91") && value.length() == 10));
	}

	public static float convertToFloat(String value) {
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
		}
		return 0f;
	}

	public static String emptyIfNull(String value) {
		return !isValid(value) ? EMPTY : value;
	}

	public static String spaceIfNull(String value) {
		return !isValid(value) ? SPACE : value;
	}

	public static String emptyIfNull(Integer value) {
		return value == null ? EMPTY : value.toString();
	}

	public static String naIfNull(String value) {
		return isValid(value) ? value : NA;
	}

	public static Date getDate(String pattern, String value) {
		try {
			return new SimpleDateFormat(pattern).parse(value);
		} catch (Exception e) {
			return null;
		}
	}

	public static int getDateField(String pattern) {
		switch (pattern.replaceAll("\\d", "")) {
		case "d":
			return Calendar.DATE;
		case "m":
			return Calendar.MONTH;
		case "y":
			return Calendar.YEAR;
		default:
			return Calendar.DATE;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, Class<T> clas) {
		if (!isValid(value))
			return null;

		T t = null;
		try {
			if (clas == Integer.class)
				t = (T) Integer.valueOf(value);
			if (clas == Long.class)
				t = (T) Long.valueOf(value);
			if (clas == Float.class)
				t = (T) Float.valueOf(value);
			if (clas == Double.class)
				t = (T) Double.valueOf(value);
		} catch (Exception e) {
		}

		return t;
	}

	public static Integer convertToInt(String value) {
		return (Integer) convert(value, Integer.class);
	}

	public static Long convertToLong(String value) {
		return (Long) convert(value, Long.class);
	}

	public static Long convertToLong(Object value) {
		return (Long) convert(String.valueOf(value), Long.class);
	}

	public static String convertToString(Object value) {
		return null != value ? String.valueOf(value) : null;
	}

	public static <T> String collectionToString(Collection<T> collection) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (T t : collection) {
			if (!first)
				builder.append(",");
			builder.append(t);
			first = false;
		}
		return builder.toString();
	}

	public static boolean isValidMobileNo(String mobileNo) {
		if (!isValid(mobileNo)) {
			return false;
		}
		return mobileNo.matches("\\d{10}");
	}

	public static String removeSpecialChars(String value) {
		if (!isValid(value)) {
			return null;
		}
		return value.replaceAll("[^a-zA-Z0-9-\\s]", " ");
	}

	public static boolean isEmailValid(String value) {
		if (!isValid(value)) {
			return false;
		}
		return Pattern.matches("^\\w+[\\w-\\.]*\\@[\\w\\.-]+\\.[\\w]{2,3}$", value);
	}

	public static boolean isPincodeValid(String pincode) {
		if (!isValid(pincode)) {
			return false;
		}

		return Pattern.matches("\\d+", pincode);
	}

	public static boolean isValidNumber(String value) {
		if (!isValid(value)) {
			return false;
		}

		return Pattern.matches("\\d+", value);
	}

	public static String removeWhiteSpaces(String value) {
		if (!isValid(value)) {
			return null;
		}
		return value.replaceAll("\\s", "");
	}

	public static String removeExtraSpace(String value) {
		if (!isValid(value))
			return null;
		return value.replaceAll("[\n\r\t]", "").replaceAll("\\s+", " ");
	}

	public static String formatMobileNumber(String mobileNo) {
		if (!isValid(mobileNo))
			return null;

		if (isValidMobileNo(mobileNo))
			return mobileNo;

		if (mobileNo.startsWith("91"))
			return mobileNo.substring("91".length());

		if (mobileNo.startsWith("+91"))
			return mobileNo.substring("+91".length());

		return mobileNo;
	}

	public static String concateAddress2(String address2, String landmark) {
		StringBuilder builder = new StringBuilder(emptyIfNull(address2));
		if (builder.length() > 0)
			builder.append(" ");
		builder.append(emptyIfNull(landmark));
		return builder.toString().trim();
	}

	public static String cut(String value, int length) {
		if (isValid(value) && value.length() > length) {
			return value.substring(0, length);
		}
		return value;
	}

	public static String escapeHtml(String value) {
		if (value == null)
			return value;
		return StringEscapeUtils.unescapeHtml(value).replaceAll("\\<.*?\\>", "");
	}
}
