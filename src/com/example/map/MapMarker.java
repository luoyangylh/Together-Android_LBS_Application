package com.example.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarker extends Activity {
	static final LatLng HRBB = new LatLng(30.618761, -96.338765);
	static final LatLng MSC = new LatLng(30.613553, -96.393868);
	private GoogleMap map;
	int a = 4;
	TextView tvLocInfo;
	String user_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_marker);
		
		Intent intent = getIntent();
		user_id = intent.getStringExtra("user_id");

		map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mapMarker)).getMap();
		Marker hrbb = map.addMarker(new MarkerOptions()
				.position(HRBB)
				.title("HRBB")
				.snippet("this is hrbb" + "this is tamu"));
		
		Marker msc = map.addMarker(new MarkerOptions()
				.position(MSC)
				.title("MSC")
				.snippet("MSC is cool " + a)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_launcher)));
		//click on info window to join the activity
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
            public void onInfoWindowClick(Marker marker) {
				//if (marker.getTitle() == null ) {
					Intent intent = new Intent(MapMarker.this, CreateEvent.class);
					LatLng current = marker.getPosition();
					double longitude = current.longitude;
					double latitude = current.latitude;
					intent.putExtra("longitude", longitude);
					intent.putExtra("latitude", latitude);
					intent.putExtra("user_id", user_id);
					startActivity(intent);
				//}
				//else {
					//Intent intent = new Intent(MapMarker.this, AttendEvent.class);
					//String event_id = marker.getTitle();
				//}
            }
        });
		
		map.setMyLocationEnabled(true);
		// Move the camera instantly to hrbb with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(HRBB, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		
	}
	
	/*@Override
	public void onMapLongClick(LatLng point) {
	  tvLocInfo.setText("New marker added@" + point.toString());
	  map.addMarker(new MarkerOptions().position(point).title(point.toString()));
	}*/

}