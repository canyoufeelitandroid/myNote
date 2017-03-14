package com.example.notebooktest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class TitleLayout extends LinearLayout {
	
	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.title, this);
		
		ImageButton myButton=(ImageButton)findViewById(R.id.myButton);
		myButton.setOnClickListener(new myButtonListener());
		ImageButton myButton_1=(ImageButton)findViewById(R.id.myButton_1);
	
	}

	class myButtonListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			((Activity)getContext()).finish();
		}
}
	


}
