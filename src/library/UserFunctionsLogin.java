package library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

import library.DatabaseHandler;


public class UserFunctionsLogin {
	private JSONParser jsonParser;

    //URL of the PHP API
    private static String loginURL = "http://together1.oicp.net:408/together_login_api/";
    private static String registerURL = "http://together1.oicp.net:408/together_login_api/";

    private static String login_tag = "login";
    private static String register_tag = "register";

    // constructor
    public UserFunctionsLogin(){
        jsonParser = new JSONParser();
    }
    
    /**
     * Function to login
     */
    public JSONObject loginUser(String username, String password) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }
    
    /**
     * Function to register
     */
    public JSONObject registerUser(String username, String password) {
    	// Building Parameters
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("tag", register_tag));
    	params.add(new BasicNameValuePair("username", username));
    	params.add(new BasicNameValuePair("password", password));
    	JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
    	return json;
    }
    
    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }

}
