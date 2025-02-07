package in.hashconnect.geocode;

import java.util.List;
import java.util.Map;

import in.hashconnect.geocode.vo.Location;

public interface GeocodeService {
	List<Map<String, String>> findAutoCompleteResults(String keyword);

	Location getLocationByPin(String pincode);

	Map<String, Object> findPlaceDetailsById(String placeId);

	Location getLocationByLatLng(String lat,String lng);
}
