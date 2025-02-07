package in.hashconnect.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public class Common {
	public static boolean isValidURL(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (MalformedURLException | URISyntaxException e) {
			return false;
		}
	}
	
	public static boolean emailPatternMatches(String emailAddress) {
		String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
		        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		return patternMatches(emailAddress, regexPattern);
	}
	
	public static boolean mobileNumberPatternMatches(String number) {
		String regexPattern = "^\\d{10}$";
		return patternMatches(number, regexPattern);
	}
	
	private static boolean patternMatches(String string, String regexPattern) {
	    return Pattern.compile(regexPattern)
	      .matcher(string)
	      .matches();
	}

}
