package in.hashconnect.util;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date convertISOTextToDate(String text) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		OffsetDateTime offsetDateTime = OffsetDateTime.parse(text, formatter);
		return Date.from(offsetDateTime.toInstant());
	}

	public static String format(String format, Date date) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			return df.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date parse(String format, String date) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date add(Date d, int field, int value) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(field, value);
		return c.getTime();
	}
}
