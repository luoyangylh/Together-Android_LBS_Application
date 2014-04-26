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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class RecieveMessage extends ListActivity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
    private String user_id;
 
    // Creating JSON Parser object
    JSONParserShow jParser = new JSONParserShow();
 
    ArrayList<HashMap<String, String>> messagesList;
 
    // url to get all messages list
    private static String url_all_messages = "http://together1.oicp.net:408/together/get_message.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGES = "messages";
    private static final String TAG_UID = "uid";
    private static final String TAG_SENDER = "sender";
    private static final String TAG_MESSAGE = "message";
 
    // messages JSONArray
    JSONArray messages = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_messages);
 
        final Data data = (Data) getApplication();
        user_id = data.getID();
        Log.d("uid is ", user_id);
        
        // Hashmap for ListView
        messagesList = new ArrayList<HashMap<String, String>>();
 
        // Loading messages in Background Thread
        new LoadAllMessages().execute();
 
        // Get listview
        ListView lv = getListView();
 
        // on seleting single message
        // launching Edit message Screen
        /*lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String fid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();
                Log.d("fid is: ", fid);
                
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SendMessage.class);
                // sending pid to next activity
                in.putExtra(TAG_UID, fid);
                //in.putExtra(TAG_MESSAGE, name);
 
                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });
 */
    }
 
    // Response from Edit message Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted message
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
 
    /**
     * Background Async Task to Load all message by making HTTP Request
     * */
    class LoadAllMessages extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecieveMessage.this);
            pDialog.setMessage("Loading messages. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All messages from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", user_id));  
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_messages, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("All messages: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // messages found
                    // Getting Array of messages
                    messages = json.getJSONArray(TAG_MESSAGES);
 
                    // looping through All messages
                    for (int i = 0; i < messages.length(); i++) {
                        JSONObject c = messages.getJSONObject(i);
 
                        // Storing each json item in variable
                        String sender = c.getString(TAG_SENDER);
                        String message = c.getString(TAG_MESSAGE);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_SENDER, sender);
                        map.put(TAG_MESSAGE, message);
 
                        // adding HashList to ArrayList
                        messagesList.add(map);
                    }
                } else {
                    // no messages found
                    // Launch Add New message Activity
                    Intent i = new Intent(getApplicationContext(),
                            SimpleMap.class);
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
            // dismiss the dialog after getting all messages
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            RecieveMessage.this, messagesList,
                            R.layout.list_message, new String[] { TAG_SENDER,
                                    TAG_MESSAGE},
                            new int[] { R.id.sender, R.id.message });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
 
        }
 
    }
}
