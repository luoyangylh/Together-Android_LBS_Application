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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessage extends Activity {
	
	/**
	 * JSON Response node names
	 */
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	/**
	 * Defining layout items
	 */
	EditText inputMessage;
	Button button;
	TextView rid;
	TextView rname;
	TextView sendError;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message);
		
		
		Intent intent = getIntent();
		final String receiver = intent.getStringExtra("receiver");
		final String name = intent.getStringExtra("name");
		
		inputMessage = (EditText) findViewById(R.id.message);
		rid = (TextView) findViewById(R.id.rid);
		rname = (TextView) findViewById(R.id.rname);
		sendError = (TextView) findViewById(R.id.sendError);
		
		rname.setText(name);
		rid.setText(receiver);
		
		button = (Button) findViewById(R.id.sendButton);
		
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!inputMessage.getText().toString().equals("")) {
					NetAsync(v);
				}
				else {
					Toast.makeText(getApplicationContext(), "The message are empty", Toast.LENGTH_SHORT).show();
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
            nDialog = new ProgressDialog(SendMessage.this);
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
                sendError.setText("Error in Network Connection");
            }
        }
    }
	
	private class ProcessCreateEvent extends AsyncTask<String, String, JSONObject> {
		
		/**
		 * Defining Process dialog
		 */
		private ProgressDialog pDialog;
		
		String fid;
		String uid;
		String message;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			final Data data = (Data) getApplication();
			final String sender = data.getID();
			uid = sender;
			rid = (TextView) findViewById(R.id.rid);
			fid = rid.getText().toString();
			Log.d("fid is: ", fid);
			inputMessage = (EditText) findViewById(R.id.message);
			message = inputMessage.getText().toString();
	        pDialog = new ProgressDialog(SendMessage.this);
	        pDialog.setTitle("Contacting Servers");
	        pDialog.setMessage("Creating ...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}

		@Override
        protected JSONObject doInBackground(String... args) {

		    UserFunctions userFunction = new UserFunctions();
		    JSONObject json = userFunction.sendEvent(uid, fid, message);

            return json;
		}
		@Override
        protected void onPostExecute(JSONObject json) {
			
			/**
		     * Checks for success message.
		     **/
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					sendError.setText("");
		            String res = json.getString(KEY_SUCCESS);

		            String red = json.getString(KEY_ERROR);
		            if(Integer.parseInt(res) == 1){
		            	pDialog.setTitle("Sending Data");
		                pDialog.setMessage("Loading Info");

		                sendError.setText("Successfully sent");                   

						Intent intent = new Intent(SendMessage.this,
								SelectContact.class);

						/**
						 * Close all views before launching Created screen
						 **/
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(intent);

						finish();
					}
				}

				else {
					pDialog.dismiss();
					sendError.setText("Error occured in creation");
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