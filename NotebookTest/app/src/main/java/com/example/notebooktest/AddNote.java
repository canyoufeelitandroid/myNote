package com.example.notebooktest;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.notebooktest.R.id.myButton;

public class AddNote extends Activity implements OnClickListener{
	private ImageButton back_last;
	private ImageButton search;
	private ImageButton picBtn;
	private EditText noteText;
	private MyDatabaseHelper adbHelper;
	SQLiteDatabase adb;
	private int data=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addlayout);
		try{
			data=getIntent().getIntExtra("belong_id",0);
			Log.i("data",""+data);
		}catch (Exception e){
			e.printStackTrace();
		}
		init();
	}

	private  void init(){
		adbHelper=new PublicDB(AddNote.this).createDB();
		adb=adbHelper.getWritableDatabase();

		noteText=(EditText)findViewById(R.id.noteText);
		back_last=(ImageButton)findViewById(myButton);
		search=(ImageButton)findViewById(R.id.myButton_1);
		picBtn=(ImageButton)findViewById(R.id.picBtn);

		back_last.setImageResource(R.drawable.checked);
		picBtn.setVisibility(View.VISIBLE);
		picBtn.setOnClickListener(this);
		search.setVisibility(View.GONE);
		back_last.setOnClickListener(this);
		noteText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if(b){
					picBtn.setVisibility(View.VISIBLE);
				}else{
					picBtn.setVisibility(View.GONE);
				}
			}
		});

	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case myButton:
			addNote();
			break;
		case R.id.picBtn:
			Intent picIntent=new Intent();
			picIntent.setType("image/*");
			picIntent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(picIntent,1);
		default:
			break;
		}
	}

	private void addNote(){
		if(noteText.hasFocus()) {
			String noteadd = noteText.getText().toString();
			Log.i("data","noteadd is "+noteadd);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatters = new SimpleDateFormat("HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间
			String notedate = formatter.format(curDate).toString();
			String notetime = formatters.format(curDate).toString();
			adb.execSQL("insert into NoteBook(date,time,notetext,belong_id)values(?,?,?,?)", new String[]{notedate, notetime, noteadd, String.valueOf(data)});
			//adb.execSQL("insert NoteBook(date,time,notetext,belong_id)values("+notedate+","+notetime+","+noteadd+","+data+")");
			//使输入框外的控件获得焦点
			back_last.setImageResource(R.drawable.icon);
			back_last.setFocusable(true);
			back_last.setFocusableInTouchMode(true);
			back_last.requestFocus();
			back_last.requestFocusFromTouch();
			Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_LONG).show();
			return;
		}
		//摧毁当前活动
		Log.i("data","存储为"+noteText.getText().toString());
		finish();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK&&requestCode==1){
			Uri uri=data.getData();
			ContentResolver cr=this.getContentResolver();
			try{
				Bitmap bitmap= BitmapFactory.decodeStream(cr.openInputStream(uri));
				String bitString=uri.toString();
				SpannableString spannableString=new com.thread.manyclass.AddPictoEdit(this).insertPic(bitmap,bitString);
				noteText.append(spannableString);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
