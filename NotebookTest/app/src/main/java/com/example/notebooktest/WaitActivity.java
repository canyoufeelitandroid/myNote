package com.example.notebooktest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class WaitActivity extends Activity {
	private TextView waittv1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wait);
		
		waittv1=(TextView)findViewById(R.id.waittv1);
		
		final Intent intent=new Intent(WaitActivity.this,MainActivity.class);
		Timer timer=new Timer();
		TimerTask task=new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startActivity(intent);
			}
			
		};
		
		timer.schedule(task, 500);
		
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}

}
