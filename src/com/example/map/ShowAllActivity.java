package com.example.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import library.JSONParserShow;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
//import com.example.library.JSONParser;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowAllActivity extends Activity {
	 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParserShow jParser = new JSONParserShow();
 
    GoogleMap map;
    HashMap<String, ArrayList<String>> activityList;
    
    // url to get all activities list
    private static String url_all_activities = "http://together1.oicp.net:408/together/get_activity.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ACTIVITIES = "activities";
    private static final String TAG_PID = "event_id";
    private static final String TAG_NAME = "event_name";
    private static final String TAG_DATE = "event_date";
    private static final String TAG_POPULATION = "event_population";
    private static final String TAG_LOCATION = "event_location";
    private static final String TAG_LONGITUDE = "event_longitude";
    private static final String TAG_LATITUDE = "event_latitude";
 
    // activities JSONArray
    JSONArray activities = null;
    
    private String user_id;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all_activity);
        
        //Intent intent = getIntent();
        //user_id = intent.getStringExtra("user_id");
        
        final Data data = (Data) getApplication();
        user_id = data.getID();
        // Hashmap for ListView
        activityList = new HashMap<String, ArrayList<String>>();
        
        // Loading activities in Background Thread
        new LoadAllActivities().execute();
        
    }
 
    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllActivities extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowAllActivity.this);
            pDialog.setMessage("Loading activities. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All activities from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_activities, "GET", params);
            
            // Check your log cat for JSON reponse
            Log.d("All Activities: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // activities found
                    // Getting Array of activities
                    activities = json.getJSONArray(TAG_ACTIVITIES);
 
                    // looping through All activities
                    for (int i = 0; i < activities.length(); i++) {
                        JSONObject c = activities.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String date = c.getString(TAG_DATE);
                        String population = c.getString(TAG_POPULATION);
                        String location = c.getString(TAG_LOCATION);
                        String longitude = c.getString(TAG_LONGITUDE);
                        String latitude = c.getString(TAG_LATITUDE);
 
                        // creating new HashMap
                        ArrayList<String> activity_detail = new ArrayList<String>();
                        
                        // adding each child node to HashMap key => value
                        activity_detail.add(name);
                        activity_detail.add(date);
                        activity_detail.add(location);
                        activity_detail.add(population);
                        activity_detail.add(longitude);
                        activity_detail.add(latitude);
                        
                        
                        //add activity to list
                        activityList.put(id, activity_detail);
 
                    }
                } else {
                    // no activities found
                    // Launch Add New product Activity
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
            // dismiss the dialog after getting all activities
            pDialog.dismiss();
            //google map
            map = ((MapFragment) getFragmentManager()
                    .findFragmentById(R.id.showallactivity)).getMap();
            map.setMyLocationEnabled(true);
            
            /**
             * Auto move to current position
             */
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null)
            {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to North
                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

            
            //generate map markers
            for(Map.Entry<String, ArrayList<String>> entry : activityList.entrySet()){
                String key = entry.getKey();
                ArrayList<String> b = entry.getValue();
                String title = b.get(0);
                Float longitude = Float.parseFloat(b.get(4));
                Float latitude = Float.parseFloat(b.get(5));
                LatLng loc = new LatLng(latitude, longitude);
                
                Marker m = map.addMarker(new MarkerOptions()
    				.position(loc)
    				.title(key));
            }  
            
            //generate map info windows
            map.setInfoWindowAdapter(new InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.marker, null);
                    String id = marker.getTitle();
                    ArrayList<String> s = activityList.get(id);
                    String event = s.get(0);
                    String event_date = s.get(1);
                    String event_location = s.get(2);
                    String event_population = s.get(3);
                    
                    TextView title = (TextView) v.findViewById(R.id.title);
                    TextView date = (TextView) v.findViewById(R.id.date);
                    TextView addr = (TextView) v.findViewById(R.id.addr);
                    TextView population = (TextView) v.findViewById(R.id.population);
                    
                    
                    title.setText(id + ' ' + event);
                    date.setText("Date:" + event_date);
                    addr.setText("Location:" + event_location);
                    population.setText("Population:" + event_population);
                    return v;
                }
            });
            
            map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
    			@Override
                public void onInfoWindowClick(Marker marker) {
    				
    				String title = marker.getTitle();
    				String[] res = title.split(" ");
    				String event_id = res[0];
    				if (title.equals("no event")) {
    					Intent intent = new Intent(ShowAllActivity.this, CreateEvent.class);
    					LatLng current = marker.getPosition();
    					double longitude = current.longitude;
    					double latitude = current.latitude;
    					intent.putExtra("longitude", longitude);
    					intent.putExtra("latitude", latitude);
    					startActivity(intent);
    				}
    				else {
    					Intent intent = new Intent(ShowAllActivity.this, AttendEvent.class);
    					intent.putExtra("user_id", user_id);
    					intent.putExtra("event_id", event_id);
    	                startActivity(intent);
    				}
                }
            });
            
            map.setOnMapLongClickListener(new OnMapLongClickListener() {
        		
            	@Override
            	public void onMapLongClick(LatLng point) {
            		map.addMarker(new MarkerOptions()
    		        	.position(point)
    		        	.title("no event"));  
            		
            		map.setInfoWindowAdapter(new InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            View v = getLayoutInflater().inflate(R.layout.marker, null);
                            
                            TextView title = (TextView) v.findViewById(R.id.title);
                            TextView date = (TextView) v.findViewById(R.id.date);
                            TextView addr = (TextView) v.findViewById(R.id.addr);
                            TextView population = (TextView) v.findViewById(R.id.population);
                            
                            
                            title.setText("no event");
                            date.setText("Date:" + "NULL");
                            addr.setText("Location:" + "NULL");
                            population.setText("Population:" + "NULL");
                            return v;
                        }
                    });

            	}
            });
            }
    }
}