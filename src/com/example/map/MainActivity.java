package com.example.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Data data = (Data) getApplication();
		
		Intent intent = getIntent();
		final String user_id = intent.getStringExtra("user_id");
		
		data.setID(user_id);
		
		final Button button = (Button) findViewById(R.id.mapButton);
		final Button button_marker = (Button) findViewById(R.id.messageButton);
		final Button friendButton = (Button) findViewById(R.id.friendButton);
		
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent geoIntent = new Intent(MainActivity.this, ShowAllActivity.class);
				//geoIntent.putExtra("user_id", data.getID());
				startActivity(geoIntent);
			}
		});
		
		friendButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent geoIntent = new Intent(MainActivity.this, AllFriendsActivity.class);
				//geoIntent.putExtra("user_id", user_id);
				startActivity(geoIntent);
			}
		});
		
		button_marker.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent geoIntent = new Intent(MainActivity.this, MainMessage.class);
				//geoIntent.putExtra("user_id", user_id);
				startActivity(geoIntent);
			}
		});
		
		
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	

}
