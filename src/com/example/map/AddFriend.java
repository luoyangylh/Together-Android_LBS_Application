package com.example.map;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.JSONParserShow;
import library.UserFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.map.AllFriendsActivity.LoadAllFriends;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AddFriend extends Activity {
	
	// Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParserShow jParser = new JSONParserShow();
 
    ArrayList<HashMap<String, String>> friendsList;
 
    // url to get all friends list
    private static String url_all_friends = "http://together1.oicp.net:408/together/add_friend.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FRIENDS = "friends";
    private static final String TAG_UID = "uid";
    private static final String TAG_NAME = "name";
 
    // friends JSONArray
    JSONArray friends = null;

	EditText inputName;
	private String user_id;
	private String friend_name;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		
		//get user id
		final Data data = (Data) getApplication();
        user_id = data.getID();
		
		inputName = (EditText) findViewById(R.id.friend_name);
		friend_name = inputName.getText().toString();
		
		final Button addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!inputName.getText().toString().equals(""))
				{
					if (inputName.getText().toString().length() > 2) {
						new AddNewFriends().execute();
					}
					else {
						Toast.makeText(getApplicationContext(), "Friend name should be minimum 3 characters", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "One or more fields are empty", Toast.LENGTH_SHORT).show();
				} 
			}
		});		
	}
	
	/**
     * Background Async Task to Load all friend by making HTTP Request
     * */
    class AddNewFriends extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputName = (EditText) findViewById(R.id.friend_name);
    		friend_name = inputName.getText().toString();
    		Log.d("friend name is: ", friend_name);
            pDialog = new ProgressDialog(AddFriend.this);
            pDialog.setMessage("Adding friends. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All friends from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", user_id)); 
            params.add(new BasicNameValuePair("friend_name", friend_name)); 
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_friends, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("All friends: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // friends found
                    // Show new friend list
                	Intent geoIntent = new Intent(AddFriend.this, AllFriendsActivity.class);
    				//geoIntent.putExtra("user_id", user_id);
    				startActivity(geoIntent);
                    
                } else {
                    // no friends found
                    // Launch Add New friend Activity
                    Intent i = new Intent(getApplicationContext(),
                            AddFriend.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all friends
            pDialog.dismiss();
            Intent geoIntent = new Intent(AddFriend.this, AllFriendsActivity.class);
			//geoIntent.putExtra("user_id", user_id);
			startActivity(geoIntent);
 
        }
 
    }

}