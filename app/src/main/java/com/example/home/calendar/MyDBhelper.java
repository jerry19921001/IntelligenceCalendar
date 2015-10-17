package com.example.home.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2015/5/23.
 */
public class MyDBhelper extends SQLiteOpenHelper {
    private static final String Database_Name="AllEvents.db";
    private static final int Database_Version=1;
    private static SQLiteDatabase database=null;

    MyDBhelper( Context context,String name,SQLiteDatabase.CursorFactory factory,int version ){
        super( context,name,factory,version );
    }
    @Override
    public void onCreate( SQLiteDatabase db ){
        db.execSQL(EventsDao.Create_Table);
        db.execSQL(MixEventDAO.CreateTable);
    }
    @Override
    public void onUpgrade( SQLiteDatabase db,int oldVersion,int newVersion ){
        String sql="Drop Table If Exists ";
        db.execSQL( sql+EventsDao.Table_Name );
        db.execSQL( sql+MixEventDAO.tableName );
        onCreate(db);
    }
    public static SQLiteDatabase getDatabase( Context context ){
        if( database==null || !database.isOpen() ){
            database=new MyDBhelper( context,Database_Name,null,Database_Version ).getReadableDatabase();
        }
        return database;
    }
}
