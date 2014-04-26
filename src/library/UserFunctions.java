package library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class UserFunctions {
	
	private JSONParser jsonParser;
	
	//URL of the PHP API
	private static String createURL = "http://together.from-tx.com:408/together_create_api/";
	private static String attendURL = "http://together.from-tx.com:408/together_attend_api/";
	private static String sendURL = "http://together.from-tx.com:408/together_send_api/";
	private static String create_tag = "create";
	private static String attend_tag = "attend";
	private static String send_tag = "send";
	
	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}
	
	/**
	 * Function to create
	 */
	public JSONObject createEvent(String eventname, String eventdate, String eventlocation, String longitude, String latitude, String user_id) {
		//Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", create_tag));
		params.add(new BasicNameValuePair("event_name", eventname));
		params.add(new BasicNameValuePair("event_date", eventdate));
		params.add(new BasicNameValuePair("event_location", eventlocation));
		params.add(new BasicNameValuePair("event_longitude", longitude));
		params.add(new BasicNameValuePair("event_latitude", latitude));
		params.add(new BasicNameValuePair("user_id", user_id));
		JSONObject json = jsonParser.getJSONFromUrl(createURL, params);
		return json;
	}
	
	/**
	 * Function to attend
	 */
	public JSONObject attendEvent(String user_id, String event_id) {
		//Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", attend_tag));
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("event_id", event_id));
		JSONObject json = jsonParser.getJSONFromUrl(attendURL, params);
		return json;
	}
	
	/**
	 * Function to send message
	 */
	public JSONObject sendEvent(String uid, String fid, String message) {
		//Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", send_tag));
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("fid", fid));
		params.add(new BasicNameValuePair("message", message));
		JSONObject json = jsonParser.getJSONFromUrl(sendURL, params);
		return json;
	}


	
}
