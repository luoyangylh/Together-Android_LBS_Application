package com.example.map;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.widget.TextView;

public class AttendEvent extends Activity {
	
	/**
	 * JSON Response node names
	 */
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	
	/**
	 * Defining layout items
	 */
	TextView uid;
	TextView eid;
	TextView attendError;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attend_event);
		
		/**
		 * Get parameters from activity
		 */
		Intent intent = getIntent();
		//String user_id = intent.getStringExtra("user_id");
		String event_id = intent.getStringExtra("event_id");
		
		final Data data = (Data) getApplication();
        String user_id = data.getID();
		
		uid = (TextView) findViewById(R.id.user_id);
		eid = (TextView) findViewById(R.id.event_id);
		attendError = (TextView) findViewById(R.id.attendError);
		
		uid.setText(user_id);
		eid.setText(event_id);
		
		/**
		 * Attend button click event
		 */
		final Button attendButton = (Button) findViewById(R.id.attendButton);		
		attendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetAsync(v);
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
            nDialog = new ProgressDialog(AttendEvent.this);
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
                attendError.setText("Error in Network Connection");
            }
        }
    }
	
	private class ProcessCreateEvent extends AsyncTask<String, String, JSONObject> {
		
		/**
		 * Defining Process dialog
		 */
		private ProgressDialog pDialog;
		
		String user_id, event_id;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			user_id = uid.getText().toString();
			event_id = eid.getText().toString();
	        pDialog = new ProgressDialog(AttendEvent.this);
	        pDialog.setTitle("Contacting Servers");
	        pDialog.setMessage("Attending ...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}

		@Override
        protected JSONObject doInBackground(String... args) {

		    UserFunctions userFunction = new UserFunctions();
		    JSONObject json = userFunction.attendEvent(user_id, event_id);

            return json;
		}
		@Override
        protected void onPostExecute(JSONObject json) {
			
			/**
		     * Checks for success message.
		     **/
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					attendError.setText("");
		            String res = json.getString(KEY_SUCCESS);

		            String red = json.getString(KEY_ERROR);
		            if(Integer.parseInt(res) == 1){
		            	pDialog.setTitle("Getting Data");
		                pDialog.setMessage("Loading Info");

		                attendError.setText("Successfully attended");

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
					attendError.setText("Error occured in attend");
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
