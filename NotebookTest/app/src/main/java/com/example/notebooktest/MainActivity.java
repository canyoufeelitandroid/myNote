package com.example.notebooktest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
	//��¼��һ�ΰ��·��ص�ʱ�䣨���룩
	long firstTime=0;
	//���˵�
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;

	public List<MyNote> noteList=new ArrayList<MyNote>(); 
	private ImageButton back_last;
	private ImageButton addnote;
	private ImageButton search;
	private TextView myText;
	private String belong_id="";
	private int data=0;
	MyNoteAdapter adapter;
	private MyDatabaseHelper dbHelper;
	SQLiteDatabase db;
	String sql="select * from NoteBook order by top_id desc";
	private ListView lv1;



	//�첽���أ�ѡ��˵����ϵ���Ŀ���޸ĳɶ�Ӧ�ı��������
	private android.os.Handler handler=new android.os.Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what!=0){
				String newTitle=msg.obj.toString();
				myText.setText(newTitle);
				adapter.notifyDataSetChanged();
			}else{
				myText.setText(R.string.app_title);
				adapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		dbHelper=new PublicDB(MainActivity.this).createDB();
		db=dbHelper.getReadableDatabase();

		initMyNote(sql);
		inttUI();
		//����Ч����ʵ��
		/*AlphaAnimation alphaAnimation=new AlphaAnimation(0.3f, 1f);
		alphaAnimation.setDuration(2000);
		LayoutAnimationController lac=new LayoutAnimationController(alphaAnimation);
		lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
		lac.setDelay(0.5f);
		lv1.setLayoutAnimation(lac);
	*/
	}

	private void inttUI(){

		//���˵�
		mDrawerLayout = (DrawerLayout) findViewById(R.id.menu_layout_main);
		mNavigationView = (NavigationView) findViewById(R.id.main_leftmenu);
		//���ò˵��������¼�
		setupDrawerContent(mNavigationView);
		lv1=(ListView)findViewById(R.id.lv1);
//		adapter=new NoteAdapter(MainActivity.this, R.layout.noteface, noteList);
		adapter=new MyNoteAdapter(MainActivity.this, noteList);
		addnote=(ImageButton)findViewById(R.id.addnote);
		addnote.setOnClickListener(this);
		back_last=(ImageButton)findViewById(R.id.myButton);
		back_last.setImageResource(R.drawable.settings_small);
		back_last.setOnClickListener(this);
		search=(ImageButton)findViewById(R.id.myButton_1);
		search.setOnClickListener(this);
		myText=(TextView)findViewById(R.id.myText);
		myText.setVisibility(View.VISIBLE);
		lv1.setAdapter(adapter);
		//���ListViewĳ����ת����Ӧ����
		lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				MyNote mynote=noteList.get(position);
				String notetext=mynote.getmyNote().toString();
				Log.i("data","mynotetext:"+notetext);
				//����ǰ���id���ݹ�ȥ
				int note_id=mynote.getId();
				int top_id=mynote.getTop_id();
				Intent contentIntent=new Intent(MainActivity.this,NoteContentActivity.class);
				contentIntent.putExtra("notetext", notetext);
				contentIntent.putExtra("note_id", note_id);
				contentIntent.putExtra("top_id",top_id);
				startActivity(contentIntent);
			}
		});
		lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
				AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle("Delete Note?");
				dialog.setMessage("Are you sure to delete this note?");
				dialog.setCancelable(false);
				dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
                        return;
					}
				});
				dialog.setPositiveButton("Sure", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int note_id=noteList.get(i).getId();
                        Log.i("data","note_id is "+note_id);
						db.execSQL("delete from NoteBook where id="+note_id);
						noteList.remove(i);
						Toast.makeText(getApplicationContext(), "Delete Succeed", Toast.LENGTH_SHORT).show();
						adapter.notifyDataSetChanged();
					}
				});
				dialog.show();
				return false;
			}
		});
	}

	//�˵���������¼�
	private void setupDrawerContent(NavigationView navigationView)
	{
		navigationView.setNavigationItemSelectedListener(

				new NavigationView.OnNavigationItemSelectedListener()
				{

					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem)
					{
						if(!menuItem.isChecked()){
							menuItem.setChecked(true);
							belong_id=menuItem.getTitle().toString();
							new Thread(new Runnable() {
								@Override
								public void run() {
									data=BelongId(belong_id);
									if(data!=0){
									noteList.clear();
										String sql_new="select * from NoteBook where belong_id = "+data;
									    initMyNote(sql_new);

									}else{
									    noteList.clear();
									    initMyNote(sql);
									}
									Message message=new Message();
									message.what=data;
									message.obj=belong_id;
									handler.sendMessage(message);

								}
							}).start();
						}else{
							menuItem.setChecked(false);
						}

						//�˵�������ر�
						mDrawerLayout.closeDrawers();
						return true;
					}
				});
	}

	private int BelongId(String string){
		if("Live".equals(string)){
			return 1;
		}else if("Work".equals(string)){
			return 2;
		}else if("Friends".equals(string)){
			return 3;
		}else if("All".equals(string)){
			return 0;
		}
		return 0;
	}

	private void initMyNote(String sql) {
			Cursor cursor=db.rawQuery(sql,null);
			if(cursor.moveToFirst()){
				do{
					String date=cursor.getString(cursor.getColumnIndex("date"));
					String time=cursor.getString(cursor.getColumnIndex("time"));
					String note=cursor.getString(cursor.getColumnIndex("notetext"));
					int top_id=cursor.getInt(cursor.getColumnIndex("top_id"));
					int id=cursor.getInt(cursor.getColumnIndex("id"));
					MyNote mynote=new MyNote(date,time,note,id,top_id);
					noteList.add(mynote);
				}while(cursor.moveToNext());
			}
		   cursor.close();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.addnote:
		    Intent intent=new Intent(MainActivity.this,AddNote.class);
			intent.putExtra("belong_id",data);
		    startActivity(intent);
		   // startActivityForResult(intent, 1);
		    break;
		case R.id.myButton_1:	
			Intent seintent=new Intent(MainActivity.this,SearchActivity.class);
			startActivity(seintent);
			break;
		case R.id.myButton:
			//�����ť��ʾ�໬�˵�
			mDrawerLayout.openDrawer(Gravity.LEFT);
			break;
		default:
			break;
		}
	}

	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		myText.setText(R.string.app_title);
		noteList.clear();
		initMyNote(sql);
		adapter.notifyDataSetChanged();
		super.onRestart();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			long secondTime=System.currentTimeMillis();
			if(secondTime-firstTime>800){//������ΰ���ʱ��������800���룬���˳�
				Toast toast=Toast.makeText(MainActivity.this,"�ٰ�һ���˳�����",Toast.LENGTH_SHORT);
				toast.getView().setBackgroundColor(Color.parseColor("#FFCC00"));

				toast.show();
				firstTime=secondTime;
				return true;
			}else{
				System.exit(0);
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
