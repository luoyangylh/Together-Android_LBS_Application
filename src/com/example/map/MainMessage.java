package com.example.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMessage extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_main);
		
		//final Data data = (Data) getApplication();
		//final String user_id = data.getID();
		
		final Button sendButton = (Button) findViewById(R.id.sendButton);
		final Button receiveButton = (Button) findViewById(R.id.receiveButton);
		
		/**
		 * Send Button on click event
		 */
		sendButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMessage.this, SelectContact.class);
				//geoIntent.putExtra("user_id", data.getID());
				startActivity(intent);
				
			}
		});
		
		/**
		 * Receive Button on click event
		 */
		receiveButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMessage.this, RecieveMessage.class);
				//geoIntent.putExtra("user_id", data.getID());
				startActivity(intent);
				
			}
		});
		
	}

}
