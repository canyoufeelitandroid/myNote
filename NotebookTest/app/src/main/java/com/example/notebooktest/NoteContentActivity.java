package com.example.notebooktest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.thread.manyclass.AddPictoEdit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteContentActivity extends Activity implements View.OnClickListener{
	private ImageButton moreBtn;
	private ImageButton back_last;
	private ImageButton picBtn;
	private LinearLayout titleLine;
	private EditText onenote;
	private MyDatabaseHelper cdbHelper;
	SQLiteDatabase cdb;
	String notetext="";
	private int note_id=0;
	private List<String> pic_uri;
	private AddPictoEdit addPictoEdit;
	private int top_id=0;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notecontent);
		Intent contentIntent=getIntent();
		addPictoEdit=new AddPictoEdit(this);

		pic_uri=new ArrayList<String>();
		note_id=contentIntent.getIntExtra("note_id", 0);
		top_id=contentIntent.getIntExtra("top_id",0);
		//final int position=contentIntent.getIntExtra("position", 1);
		notetext=contentIntent.getStringExtra("notetext");
		Log.i("data","mynotetext1:"+notetext);
		init();
		//将必须的字段图片插入到输入框中
		addPictoEdit.readPic(notetext,onenote,pic_uri);
	}

	private void init(){
		titleLine=(LinearLayout)findViewById(R.id.titleLine);
		moreBtn=(ImageButton)findViewById(R.id.myButton_1);
		moreBtn.setImageResource(R.drawable.more);
		moreBtn.setOnClickListener(this);

		back_last=(ImageButton)findViewById(R.id.myButton);
		back_last.setOnClickListener(this);
		picBtn=(ImageButton)findViewById(R.id.picBtn);
		picBtn.setOnClickListener(this);

		onenote=(EditText)findViewById(R.id.onenote);
		cdbHelper=new PublicDB(NoteContentActivity.this).createDB();
		cdb=cdbHelper.getWritableDatabase();
		//onenote.setText(notetext);
		//输入框获得焦点后的操作
		onenote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.i("data","it is focus");
				if(hasFocus){
					picBtn.setVisibility(View.VISIBLE);
					back_last.setImageResource(R.drawable.checked);
				}
			}
		});
	}



     @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	//Intent backIntent=new Intent(NoteContentActivity.this,MainActivity.class);
    	//startActivity(backIntent);
		//setResult(RESULT_FIRST_USER);
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
				pic_uri.add(bitString);
				//转换URI字符串部分为显示图片
				SpannableString spannableString=addPictoEdit.insertPic(bitmap,bitString);
				onenote.append(spannableString);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.myButton:
				saveOrBack();
				break;
			case R.id.picBtn:
				Intent picIntent=new Intent();
				picIntent.setType("image/*");
				picIntent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(picIntent,1);
				break;
			case R.id.myButton_1:
				showPopupMenu(moreBtn);
				break;
			default:
				break;
		}
	}

	private void showPopupMenu(View view){
		//当前menu显示的相对view的位置
		PopupMenu popupMenu=new PopupMenu(this,view);
		//menu布局
		popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
		//menu点击事件
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				if("Delete".equals(menuItem.getTitle())){
					showDeleteDialog(note_id);
				}else if("Top".equals(menuItem.getTitle())&&top_id==0){
					//cdb.execSQL("alter table NoteBook add column top_id integer");
					cdb.execSQL("update NoteBook set top_id=? where id="+note_id,new String[]{"1"});
					//cdb.execSQL("delete from NoteBook where notetext is null");
//					Cursor cursor=cdb.rawQuery("select * from NoteBook",null);
//					if(cursor.moveToFirst()){
//						do{
//							String text=cursor.getString(cursor.getColumnIndex("notetext"));
//							int top_id=cursor.getInt(cursor.getColumnIndex("top_id"));
//							Log.i("data","text is "+text);
//							Log.i("data","top is "+top_id);
//						}while(cursor.moveToNext());
//					}
					
					Log.i("data","alter successed");
				}else if("Top".equals(menuItem.getTitle())&&top_id==1){
					cdb.execSQL("update NoteBook set top_id=? where id="+note_id,new String[]{"0"});
				}
				Toast.makeText(getApplicationContext(),menuItem.getTitle().toString(),Toast.LENGTH_LONG).show();
				return true;
			}
		});
		//menu关闭事件
//		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//			@Override
//			public void onDismiss(PopupMenu popupMenu) {
//				Toast.makeText(getApplicationContext(),"关闭popupmenu",Toast.LENGTH_LONG).show();
//			}
//		});
		popupMenu.show();

	}
	private void showDeleteDialog(final int note_id){
		AlertDialog.Builder dialog=new AlertDialog.Builder(NoteContentActivity.this);
		dialog.setTitle("删除便签");
		dialog.setMessage("你确定要删除这个便签吗？");
		dialog.setCancelable(false);
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				cdb.execSQL("delete from NoteBook where id="+note_id);
				Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		dialog.show();
	}

	private void saveOrBack(){
		if(onenote.hasFocus()){
			String notetext_new=onenote.getText().toString();
			Log.i("data","notext_new:"+notetext_new);
			for(int i=0;i<pic_uri.size();i++){
				String myUri=pic_uri.get(i).toString();
				if(notetext_new.contains(myUri)){
					notetext_new=notetext_new.replace(myUri,"//pic:"+myUri+"//pic:");
				}
			}
			if(!notetext_new.equals(notetext)){
				SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
				SimpleDateFormat formatters = new SimpleDateFormat ("HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间
				String notedate = formatter.format(curDate).toString();
				String notetime=formatters.format(curDate).toString();
				cdb.execSQL("update NoteBook set date=?,time=?,notetext=? where id="+note_id, new String[]{notedate,notetime,notetext_new});
			}
			//令输入框外的控件获得焦点
			back_last.setImageResource(R.drawable.icon);
			titleLine.setFocusable(true);
			titleLine.setFocusableInTouchMode(true);
			titleLine.requestFocus();
			titleLine.requestFocusFromTouch();
			Toast.makeText(NoteContentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
			return;
		}
			finish();

	}

}
