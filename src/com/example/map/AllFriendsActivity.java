package com.example.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.JSONParserShow;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class AllFriendsActivity extends ListActivity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
    private String user_id;
 
    // Creating JSON Parser object
    JSONParserShow jParser = new JSONParserShow();
 
    ArrayList<HashMap<String, String>> friendsList;
 
    // url to get all friends list
    private static String url_all_friends = "http://together1.oicp.net:408/together/get_friend.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FRIENDS = "friends";
    private static final String TAG_UID = "uid";
    private static final String TAG_NAME = "name";
 
    // friends JSONArray
    JSONArray friends = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_friends);
 
        final Data data = (Data) getApplication();
        user_id = data.getID();
        Log.d("uid is ", user_id);
        
        // Hashmap for ListView
        friendsList = new ArrayList<HashMap<String, String>>();
 
        // Loading friends in Background Thread
        new LoadAllFriends().execute();
 
        // Get listview
        ListView lv = getListView();
		final Button friendButton = (Button) findViewById(R.id.addButton);

        // on seleting single friend
        // launching Edit friend Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String fid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();
                Log.d("fid is: ", fid);
                
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        ShowFriendActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_UID, fid);
                //in.putExtra(TAG_NAME, name);
 
                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });
        
        friendButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent geoIntent = new Intent(AllFriendsActivity.this, AddFriend.class);
				//geoIntent.putExtra("user_id", data.getID());
				startActivity(geoIntent);
			}
		});
 
    }
 
    // Response from Edit friend Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted friend
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
 
    /**
     * Background Async Task to Load all friend by making HTTP Request
     * */
    class LoadAllFriends extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllFriendsActivity.this);
            pDialog.setMessage("Loading friends. Please wait...");
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
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_friends, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("All friends: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // friends found
                    // Getting Array of friends
                    friends = json.getJSONArray(TAG_FRIENDS);
 
                    // looping through All friends
                    for (int i = 0; i < friends.length(); i++) {
                        JSONObject c = friends.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_UID);
                        String name = c.getString(TAG_NAME);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_UID, id);
                        map.put(TAG_NAME, name);
 
                        // adding HashList to ArrayList
                        friendsList.add(map);
                    }
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
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            AllFriendsActivity.this, friendsList,
                            R.layout.list_item, new String[] { TAG_UID,
                                    TAG_NAME},
                            new int[] { R.id.pid, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
 
        }
 
    }
}
