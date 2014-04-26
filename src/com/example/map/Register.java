package com.example.map;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import library.DatabaseHandler;
import library.UserFunctionsLogin;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.map.R;

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

public class Register extends Activity {
	/**
     *  JSON Response node names.
     **/
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "user_id";
    private static String KEY_USERNAME = "uname";
    private static String KEY_ERROR = "error";

    /**
     * Defining layout items.
     **/
    EditText inputUsername;
    EditText inputPassword;
    Button registerButton;
    TextView registerError;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		/**
		 * Defining all layout items
		 */
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.registerButton);
        registerError = (TextView) findViewById(R.id.registerError);
        
        /**
         * Button back to login
         */
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(v.getContext(), Login.class);
                startActivityForResult(myIntent, 0);
                finish();
			}
		});
        
        /**
         * Register button click event
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((!inputUsername.getText().toString().equals("")) && (!inputPassword.getText().toString().equals("")))
                {
                    if (inputUsername.getText().toString().length() > 2){
                    	NetAsync(v);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "Username should be minimum 3 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
			}
		});
	}
	
	/**
	 * Check Internet connection
	 */
	private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Register.this);
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
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                registerError.setText("Error in Network Connection");
            }
        }
    }

	private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

		/**
		 * Defining Process dialog
		 **/
		private ProgressDialog pDialog;

		String password, username;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			inputUsername = (EditText) findViewById(R.id.username);
			inputPassword = (EditText) findViewById(R.id.password);
			username = inputUsername.getText().toString();
			password = inputPassword.getText().toString();
	        pDialog = new ProgressDialog(Register.this);
	        pDialog.setTitle("Contacting Servers");
	        pDialog.setMessage("Registering ...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}

		@Override
        protected JSONObject doInBackground(String... args) {

		    UserFunctionsLogin userFunction = new UserFunctionsLogin();
		    JSONObject json = userFunction.registerUser(username, password);

            return json;
		}
		@Override
        protected void onPostExecute(JSONObject json) {
			
			/**
		     * Checks for success message.
		     **/
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					registerError.setText("");
		            String res = json.getString(KEY_SUCCESS);

		            String red = json.getString(KEY_ERROR);
		            if(Integer.parseInt(res) == 1){
		            	pDialog.setTitle("Getting Data");
		                pDialog.setMessage("Loading Info");

		                registerError.setText("Successfully Registered");

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		                JSONObject json_user = json.getJSONObject("user");

		                /**
		                 * Removes all the previous data in the SQlite database
		                 

		                UserFunctionsLogin logout = new UserFunctionsLogin();
		                logout.logoutUser(getApplicationContext());
		                db.addUser(json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID));
						/**
						 * Stores registered data in SQlite Database Launch
						 * Registered screen
						 **/

						Intent registered = new Intent(Register.this,
								Registered.class);

						/**
						 * Close all views before launching Registered screen
						 **/
						registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(registered);

						finish();
					}

					else if (Integer.parseInt(red) == 2) {
						pDialog.dismiss();
						registerError.setText("User already exists");
					} else if (Integer.parseInt(red) == 3) {
						pDialog.dismiss();
						registerError.setText("Invalid Email id");
					}
				}

				else {
					pDialog.dismiss();
					registerError.setText("Error occured in registration");
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
