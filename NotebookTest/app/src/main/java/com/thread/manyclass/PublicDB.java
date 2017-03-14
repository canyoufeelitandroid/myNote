package com.thread.manyclass;

import android.content.Context;

import com.example.notebooktest.MyDatabaseHelper;

/**
 * Created by 64088 on 2017/2/25.
 */

public class PublicDB {
    private MyDatabaseHelper dbHelper;
    private Context mContext;
    public PublicDB(Context context){
        mContext=context;
    }

    public MyDatabaseHelper createDB(){
        dbHelper=new MyDatabaseHelper(mContext, "Note.db", null, 2);
        return  dbHelper;
    }
}
