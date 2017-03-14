package com.example.notebooktest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public static final String CREATE_NOTEBOOK="create table NoteBook("
			+"id integer primary key autoincrement,"
			+"date text,"
			+"top_id integer,"
			+"time text,"
			+"notetext text,"
			+"belong_id integer)";
	/*public static final String CREATE_CATEGORY="create table Category("
			+"id integer primary key autoincrement,"
			+"category_name text,"
			+"categoory_code integer)";
	*/
	
	private Context mContext;

	public MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_NOTEBOOK);
		//db.execSQL(CREATE_CATEGORY);
		Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//更新数据库数据版本
		switch(oldVersion){
			case 1:
				db.execSQL("alter table NoteBook add column belong_id integer");
			default:
		}

	}

}
