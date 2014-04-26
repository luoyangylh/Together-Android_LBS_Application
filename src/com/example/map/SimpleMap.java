package com.example.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;

public class SimpleMap extends Activity {

	private GoogleMap map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_map);

		// Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        /*LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));*/
        
        /**
		 * Get position from MapMarker
		*/ 
		Intent intent = getIntent();
		double longitude_double = intent.getDoubleExtra("longitude", 0.0);
		double latitude_double = intent.getDoubleExtra("latitude", 0.0);
		String longitude = String.valueOf(longitude_double);
		String latitude = String.valueOf(latitude_double);
		
		LatLng pass = new LatLng(latitude_double, longitude_double);
		
		map.addMarker(new MarkerOptions().title("pass").snippet("passed by mapmarker" + longitude + latitude).position(pass));
		
        map.setMyLocationEnabled(true);
        
        map.setOnMapLongClickListener(new OnMapLongClickListener() {
		
        	@Override
        	public void onMapLongClick(LatLng point) {
        		map.addMarker(new MarkerOptions()
		        	.position(point)
		        	.title("You are here")           
		        	.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));  
        	}});
	}
}