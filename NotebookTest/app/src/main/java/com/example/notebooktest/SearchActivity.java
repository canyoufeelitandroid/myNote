package com.example.notebooktest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity implements OnClickListener{
	private ImageButton backBtn;
	private EditText searchText;
	private ListView lv2;
	public List<MyNote> snoteList=new ArrayList<MyNote>(); 
	//NoteAdapter sadapter;
	private MyNoteAdapter sadapter;
	String sql="select * from NoteBook order by top_id desc";
    private MyDatabaseHelper sdbHelper;
	SQLiteDatabase sdb;
	//private PublicDB publicDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_item);
		//sdbHelper=new MyDatabaseHelper(this, "Note.db", null, 2);
		//publicDB=new PublicDB(SearchActivity.this);
		sdbHelper=new PublicDB(SearchActivity.this).createDB();
		sdb=sdbHelper.getReadableDatabase();
		initMyNote(sql);
		findView();
		lv2.setOnItemClickListener(new MyItemClickListener());
		
		
		searchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				 //这个应该是在改变的时候会做的动作吧，具体还没用到过。
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				//这是文本框改变之前会执行的动作
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				/**这是文本框改变之后 会执行的动作
                 * 因为我们要做的就是，在文本框改变的同时，我们的listview的数据也进行相应的变动，并且如一的显示在界面上。
                 * 所以这里我们就需要加上数据的修改的动作了。
                 * 根据Handler的post方法进行筛选操作
                 */
				myhandler.post(eChanged);
			}
		});
		
		
	}

	private void findView() {
		// TODO Auto-generated method stub
		backBtn=(ImageButton)findViewById(R.id.sh_back);
		backBtn.setOnClickListener(this);
		lv2=(ListView)findViewById(R.id.lv2);
		//sadapter=new NoteAdapter(this, R.layout.noteface, snoteList);
		sadapter=new MyNoteAdapter(SearchActivity.this,snoteList);
		searchText=(EditText)findViewById(R.id.searchText);

		lv2.setAdapter(sadapter);
	}
	
	private class MyItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MyNote mynote=snoteList.get(position);
			String notetext=mynote.getmyNote().toString();
			//将当前项的id传递过去
			int note_id=mynote.getId();
			Intent contentIntent=new Intent(SearchActivity.this,NoteContentActivity.class);
			contentIntent.putExtra("notetext", notetext);
			contentIntent.putExtra("note_id", note_id);
			//contentIntent.putExtra("position", position);
			//活动启动
			startActivity(contentIntent);
		}
		
	}
	

	private void initMyNote(String sql) {
		
		Cursor cursor=sdb.rawQuery(sql,null);
		if(cursor.moveToFirst()){
			do{
				String date=cursor.getString(cursor.getColumnIndex("date"));
				String time=cursor.getString(cursor.getColumnIndex("time"));
				String note=cursor.getString(cursor.getColumnIndex("notetext"));
				int top_id=cursor.getInt(cursor.getColumnIndex("top_id"));
				int id=cursor.getInt(cursor.getColumnIndex("id"));
				MyNote mynote=new MyNote(date,time,note,id,top_id);
				snoteList.add(mynote);
			}while(cursor.moveToNext());
			
		}
	   cursor.close();
      }
	
	
	Handler myhandler=new Handler();
	Runnable eChanged=new Runnable() {
		
		@Override
		public void run() {
			//首先清空list
			snoteList.clear();
			String data=searchText.getText().toString();
			String sql_new="select * from NoteBook where notetext like '%"+data+"%'";
			//根据当前EditText里的值，进行sql搜索，重新构造list
			initMyNote(sql_new);
		   //利用notifyDataSetChanged()动态更新ListView
		   sadapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onRestart() {
		/**
		 * 每次重新启动都会重新加载snotelist,并重新从数据库读数据，刷新listview
		 */
		snoteList.clear();
		initMyNote(sql);
		sadapter.notifyDataSetChanged();
		super.onRestart();
	}



	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.sh_back:
			finish();
			break;
		default:
			break;
		}
		
	}

	}
