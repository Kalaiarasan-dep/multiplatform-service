package in.hashconnect.geocode;

import static in.hashconnect.util.StringUtil.concate;
import static in.hashconnect.util.StringUtil.convertToString;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.geocode.vo.Address;
import in.hashconnect.geocode.vo.AddressDetails;
import in.hashconnect.geocode.vo.GeoCodeApiResponse;
import in.hashconnect.geocode.vo.Location;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.util.StringUtil;

@SuppressWarnings("unchecked")
public class GoogleGeocodeService implements GeocodeService {
	private static Logger logger = LoggerFactory.getLogger(GoogleGeocodeService.class);

	@Autowired
	private SettingsUtil settingsUtil;

	@Override
	public List<Map<String, String>> findAutoCompleteResults(String keyword) {
		logger.debug("searching on {}", keyword);

		List<Map<String, String>> results = new ArrayList<Map<String, String>>();

		try {
			keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
		}

		String url = concate(settingsUtil.getValue("google_auto_complete_url"), keyword);

		Map<String, Object> result = null;
		try {
			result = JsonUtil.readValue(new URL(url), Map.class);
			if (result == null) {
				logger.debug("searching on {}, result not found", keyword);
				return results;
			}

			List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("predictions");

			if (CollectionUtils.isNotEmpty(list)) {
				list.stream().forEach(val -> {
					String desc = convertToString(val.get("description"));
					String placeId = convertToString(val.get("place_id"));

					Map<String, String> map = new HashMap<String, String>();
					map.put("description", desc);
					map.put("place_id", placeId);
					results.add(map);
				});
			}
		} catch (Exception e) {
			logger.error("autoComplete search failed", e);
		}

		return results;
	}

	public Location getLocationByPin(String pincode) {
		String url = StringUtil.concate(this.settingsUtil.getValue("google_geocode_url"), pincode);

		logger.debug("get location by pincode - {}", pincode);

		GeoCodeApiResponse response;
		try {
			response = JsonUtil.readValue(new URL(url), GeoCodeApiResponse.class);
		} catch (MalformedURLException var13) {
			logger.error("failed to construct url", var13);
			return null;
		}

		if (isEmpty(response) || isEmpty(response.getResults())) {
			return null;
		}

		AddressDetails addDetails = response.getResults().stream().findFirst().get();
		Location location = !isEmpty(addDetails.getGeometry()) ? addDetails.getGeometry().getLocation() : null;
		Iterator<Address> adderesses = addDetails.getAddressComponent().iterator();

		String city = null, state = null;
		while (adderesses.hasNext()) {
			Address add = adderesses.next();
			if (add.getTypes().contains("administrative_area_level_1")) {
				state = add.getLongName();
			} else if (add.getTypes().contains("locality")) {
				city = add.getLongName();
			} else if (add.getTypes().contains("administrative_area_level_3")) {
				city = add.getLongName();
			}

			if (!isEmpty(city) && !isEmpty(state)) {
				break;
			}
		}

		location.setCity(city);
		location.setState(state);
		return location;
	}

	public Map<String, Object> findPlaceDetailsById(String placeId) {
		String url = settingsUtil.getValue("google_place_detail_url") + placeId;

		logger.debug("place details by id {}", placeId);

		Map<String, Object> result = null;
		try {
			result = JsonUtil.readValue(new URL(url), HashMap.class);

			logger.debug("placesId: {} got results {}", placeId, result);

			result = (Map<String, Object>) result.get("result");
			result = (Map<String, Object>) result.get("geometry");
			result = (Map<String, Object>) result.get("location");

			return result;
		} catch (Exception e) {
			logger.error("findPlaceDetailsById has failed", e);
			return Collections.emptyMap();
		}
	}

	@Override
	public Location getLocationByLatLng(String lat, String lng) {
		String url = StringUtil.concate(this.settingsUtil.getValue("geocode_api_lat_lng"),lat,",",lng);

		logger.debug("get location by pincode - {}", lat,lng);

		GeoCodeApiResponse response;
		try {
			response = JsonUtil.readValue(new URL(url), GeoCodeApiResponse.class);
		} catch (MalformedURLException var13) {
			logger.error("failed to construct url", var13);
			return null;
		}

		if (isEmpty(response) || isEmpty(response.getResults())) {
			return null;
		}

		AddressDetails addDetails = response.getResults().stream().findFirst().get();
		Location location = !isEmpty(addDetails.getGeometry()) ? addDetails.getGeometry().getLocation() : null;
		Iterator<Address> adderesses = addDetails.getAddressComponent().iterator();

		String city = null, state = null;
		while (adderesses.hasNext()) {
			Address add = adderesses.next();
			if (add.getTypes().contains("administrative_area_level_1")) {
				state = add.getLongName();
			} else if (add.getTypes().contains("locality")) {
				city = add.getLongName();
			} else if (add.getTypes().contains("administrative_area_level_3")) {
				city = add.getLongName();
			}

			if (!isEmpty(city) && !isEmpty(state)) {
				break;
			}
		}

		location.setCity(city);
		location.setState(state);
		return location;
	}
}
