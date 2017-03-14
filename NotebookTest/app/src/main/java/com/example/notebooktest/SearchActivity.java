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
				 //���Ӧ�����ڸı��ʱ������Ķ����ɣ����廹û�õ�����
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				//�����ı���ı�֮ǰ��ִ�еĶ���
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				/**�����ı���ı�֮�� ��ִ�еĶ���
                 * ��Ϊ����Ҫ���ľ��ǣ����ı���ı��ͬʱ�����ǵ�listview������Ҳ������Ӧ�ı䶯��������һ����ʾ�ڽ����ϡ�
                 * �����������Ǿ���Ҫ�������ݵ��޸ĵĶ����ˡ�
                 * ����Handler��post��������ɸѡ����
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
			//����ǰ���id���ݹ�ȥ
			int note_id=mynote.getId();
			Intent contentIntent=new Intent(SearchActivity.this,NoteContentActivity.class);
			contentIntent.putExtra("notetext", notetext);
			contentIntent.putExtra("note_id", note_id);
			//contentIntent.putExtra("position", position);
			//�����
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
			//�������list
			snoteList.clear();
			String data=searchText.getText().toString();
			String sql_new="select * from NoteBook where notetext like '%"+data+"%'";
			//���ݵ�ǰEditText���ֵ������sql���������¹���list
			initMyNote(sql_new);
		   //����notifyDataSetChanged()��̬����ListView
		   sadapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onRestart() {
		/**
		 * ÿ�����������������¼���snotelist,�����´����ݿ�����ݣ�ˢ��listview
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
