package com.example.map;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateEvent extends Activity {
	
	/**
	 * JSON Response node names
	 */
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	/**
	 * Defining layout items
	 */
	EditText inputName;
	EditText inputDate;
	EditText inputLocation;
	Button createButton;
	TextView createError;
	TextView lon;
	TextView lat;
	TextView uid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_event);
		
		/**
		 * Get position from MapMarker
		 */
		Intent intent = getIntent();
		double longitude_double = intent.getDoubleExtra("longitude", 0.0);
		double latitude_double = intent.getDoubleExtra("latitude", 0.0);
		String longitude = String.valueOf(longitude_double);
		String latitude = String.valueOf(latitude_double);
		//String user_id = intent.getStringExtra("user_name");
		final Data data = (Data) getApplication();
        String user_id = data.getID();
		
		/**
		 * Defining all layout items
		 */
		inputName = (EditText) findViewById(R.id.eventname);
		inputDate = (EditText) findViewById(R.id.eventdate);
		inputLocation = (EditText) findViewById(R.id.eventlocation);
		createError = (TextView) findViewById(R.id.createError);
		
		lon = (TextView) findViewById(R.id.longitude);
		lat = (TextView) findViewById(R.id.latitude);
		uid = (TextView) findViewById(R.id.user_id);
		
		lon.setText(longitude);
		lat.setText(latitude);
		uid.setText(user_id);
		
		/**
		 * Create button click event
		 */
		createButton = (Button) findViewById(R.id.createButton);
		createButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((!inputName.getText().toString().equals("")) && (!inputDate.getText().toString().equals("")) && (!inputLocation.getText().toString().equals("")))
				{
					if (inputName.getText().toString().length() > 2) {
						NetAsync(v);
					}
					else {
						Toast.makeText(getApplicationContext(), "Event name should be minimum 3 characters", Toast.LENGTH_SHORT).show();
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
	 * Check Internet connection
	 */
	private class NetCheck extends AsyncTask<String, String, Boolean>
	{
		private ProgressDialog nDialog;
		
		@Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(CreateEvent.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
		/**
         * Gets current device state and checks for working Internet connection by trying Google.
        **/
        @Override
        protected Boolean doInBackground(String... args) {
        	
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessCreateEvent().execute();
            }
            else{
                nDialog.dismiss();
                createError.setText("Error in Network Connection");
            }
        }
    }
	
	private class ProcessCreateEvent extends AsyncTask<String, String, JSONObject> {
		
		/**
		 * Defining Process dialog
		 */
		private ProgressDialog pDialog;
		
		String eventname, eventlocation;
		String eventdate;
		String longitude, latitude;
		String user_id;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			inputName = (EditText) findViewById(R.id.eventname);
			inputDate = (EditText) findViewById(R.id.eventdate);
			inputLocation = (EditText) findViewById(R.id.eventlocation);
			longitude = lon.getText().toString();
			latitude = lat.getText().toString();
			user_id = uid.getText().toString();
			eventname = inputName.getText().toString();
			eventlocation = inputLocation.getText().toString();
			eventdate = inputDate.getText().toString();
	        pDialog = new ProgressDialog(CreateEvent.this);
	        pDialog.setTitle("Contacting Servers");
	        pDialog.setMessage("Creating ...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}

		@Override
        protected JSONObject doInBackground(String... args) {

		    UserFunctions userFunction = new UserFunctions();
		    JSONObject json = userFunction.createEvent(eventname, eventdate, eventlocation, longitude, latitude, user_id);

            return json;
		}
		@Override
        protected void onPostExecute(JSONObject json) {
			
			/**
		     * Checks for success message.
		     **/
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					createError.setText("");
		            String res = json.getString(KEY_SUCCESS);

		            String red = json.getString(KEY_ERROR);
		            if(Integer.parseInt(res) == 1){
		            	pDialog.setTitle("Getting Data");
		                pDialog.setMessage("Loading Info");

		                createError.setText("Successfully created");

                        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		                //JSONObject json_user = json.getJSONObject("user");

		                /**
		                 * Removes all the previous data in the SQlite database
		                 

		                UserFunctions logout = new UserFunctions();
		                logout.logoutUser(getApplicationContext());
		                db.addUser(json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID));
						
						 * Stores registered data in SQlite Database Launch
						 * Registered screen
						 **/

						Intent created = new Intent(getApplicationContext(),
								ShowAllActivity.class);

						/**
						 * Close all views before launching Created screen
						 **/
						created.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(created);

						finish();
					}
				}

				else {
					pDialog.dismiss();
					createError.setText("Error occured in creation");
				}

			} catch (JSONException e) {
				e.printStackTrace();

			}
		}
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}

}