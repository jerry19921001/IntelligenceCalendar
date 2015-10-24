package com.example.home.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by user on 2015/5/23.
 */
public class EventsDao {
    // the table name
    public static final String Table_Name="Events";
    // the column of the table
    public static final String Key_Id="_id";// 0 long
    public static final String Col_Event_Name="Event_Name";// 1 String
    public static final String Col_Start_Year="Start_Year";// 2 int
    public static final String Col_Start_Month="Start_Month";// 3 int
    public static final String Col_Start_Day="Start_Day";// 4 int
    public static final String Col_Start_Hour="Start_Hour";// 5 int
    public static final String Col_End_Year="End_Year";// 6 int
    public static final String Col_End_Month="End_Month";// 7 int
    public static final String Col_End_Day="End_Day";// 8 int
    public static final String Col_End_Hour="End_Hour";// 9 int
    public static final String Col_Do_Hour="Do_Hour";// 10 int
    public static final String Col_Is_Static="Is_Static";// 11 int (1:static : 0:dynamic)
    public static final String Col_Blocks="Blocks";// 12 int
    public static final String Col_Inside_MixEventDAO="Inside";// 13 int (1:inside : 0:not inside)
    // the string of create table
    public static final String Create_Table="Create Table "+Table_Name+" ( "+
            Key_Id+" Integer Primary Key AUTOINCREMENT, "+
            Col_Event_Name+" Text Not Null, "+
            Col_Start_Year+" Integer Not Null, "+
            Col_Start_Month+" Integer Not Null, "+
            Col_Start_Day+" Integer Not Null, "+
            Col_Start_Hour+" Integer Not Null, "+
            Col_End_Year+" Integer Not Null, "+
            Col_End_Month+" Integer Not Null, "+
            Col_End_Day+" Integer Not Null, "+
            Col_End_Hour+" Integer Not Null, "+
            Col_Do_Hour+" Integer Not Null, "+
            Col_Is_Static+" Integer Not Null, "+
            Col_Blocks+" Integer Not Null, "+
            Col_Inside_MixEventDAO+" Integer Not Null "+
            " ); ";
    // the database
    private SQLiteDatabase db=null;
    // the construction of the EventDao
    // the function to open the database and close the database
    public EventsDao( Context context ){
        db=MyDBhelper.getDatabase( context );
    }
    public void Close(){
        db.close();
    }
    // the other function
    public long Insert( Event event ){
        ContentValues cv=new ContentValues();

        cv.put( Col_Event_Name, event.getName() );
        cv.put( Col_Start_Year,event.getStartYear() );
        cv.put( Col_Start_Month,event.getStartMonth() );
        cv.put( Col_Start_Day,event.getStartDay() );
        cv.put( Col_Start_Hour,event.getStartHour() );
        cv.put( Col_End_Year,event.getEndYear() );
        cv.put( Col_End_Month,event.getEndMonth() );
        cv.put( Col_End_Day,event.getEndDay() );
        cv.put( Col_End_Hour,event.getEndHour() );
        cv.put( Col_Do_Hour,event.getDoHours() );
        if( event.isStatic() ) cv.put( Col_Is_Static,1 );
        else cv.put( Col_Is_Static,0 );
        cv.put( Col_Blocks,event.getBlocks() );
        cv.put(Col_Inside_MixEventDAO,0);

        long id=db.insert( Table_Name,null,cv );

        return id;
    }
    public boolean Update( Event event,int dif ){
        System.out.println("in EventsDao.Update:");
        System.out.println("print the event");
        event.print();
        String where=Key_Id+"="+event.getPreviousId();
        String sql="select * from "+Table_Name+" where "+where;
        Cursor result=db.rawQuery(sql, null);
        result.moveToFirst();
        int originalDoHour=result.getInt(10);
        int originalStart[]={result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5),0};
        int originalEnd[]={result.getInt(6),result.getInt(7),result.getInt(8),result.getInt(9),0};
        result.close();
        System.out.println("dif=" + dif);
        System.out.println("originalDoHour=" + originalDoHour);
        int newDoHour=originalDoHour+dif;
        System.out.println("newDoHour=" + newDoHour);
        ContentValues cv=new ContentValues();

        cv.put(Col_Event_Name,event.getName());
        cv.put(Col_Start_Year,event.getStartYear());
        cv.put(Col_Start_Month,event.getStartMonth());
        cv.put(Col_Start_Day,event.getStartDay());
        if( event.getStartHour()<originalStart[4] ) cv.put(Col_Start_Hour,event.getStartHour());
        else cv.put(Col_Start_Hour,originalStart[4]);
        cv.put(Col_End_Year,event.getEndYear());
        cv.put(Col_End_Month,event.getEndMonth());
        cv.put(Col_End_Day,event.getEndDay());
        if( event.getEndHour()>originalEnd[4] ) cv.put(Col_End_Hour,event.getEndHour());
        else cv.put(Col_End_Hour,originalEnd[4]);
        cv.put(Col_Do_Hour,newDoHour);
        if( event.isStatic() ) cv.put(Col_Is_Static,1);
        else cv.put(Col_Is_Static,0);
        cv.put(Col_Blocks, event.getBlocks());

        return db.update(Table_Name,cv,where,null)>0;
    }
    public ArrayList<Event> GetOneDayEvents( int sYear,int sMonth,int sDay ){
       // System.out.println("in getonedayevent");
        String sql="select * "+
                " from "+Table_Name+
                " where "+Col_Start_Year+" = "+sYear+" and "+Col_Start_Month+" = "+sMonth+" and "+ Col_Start_Day+" = "+sDay+
                " order by "+Col_Start_Hour;
        ArrayList<Event> oneDayEvents =new ArrayList<>();
        Cursor c=db.rawQuery( sql,null );

        while ( c.moveToNext() ){
            Event temp=new Event();
            int start[]={ c.getInt(2),c.getInt(3),c.getInt(4),c.getInt(5),0 };
            int end[]={ c.getInt(6),c.getInt(7),c.getInt(8),c.getInt(9),0 };

            temp.setAll( c.getLong(0),c.getString(1),start,end );
            temp.setDoHours(c.getInt(10));
            temp.setIsStatic(c.getInt(11));
            temp.setBlocks( c.getInt(12) );

            oneDayEvents.add(temp);
        }
        c.close();

        return oneDayEvents;
    }
    public boolean Delete( long id ){
        String where=Key_Id+"="+id;
        return db.delete( Table_Name,where,null )>0;
    }
    public ArrayList<Event>AllEvents(){
        String sql=" select * from "+Table_Name+
                " order by "+Col_Start_Year+" and "+Col_Start_Month+" and "+Col_Start_Day+" and "+Col_Start_Hour;
        ArrayList<Event> Events =new ArrayList<>();
        Cursor c=db.rawQuery( sql,null );

        while ( c.moveToNext() ){
            Event temp=new Event();
            int start[]={ c.getInt(2),c.getInt(3),c.getInt(4),c.getInt(5),0 };
            int end[]={ c.getInt(6),c.getInt(7),c.getInt(8),c.getInt(9),0 };

            temp.setAll( c.getLong(0),c.getString(1),start,end );
            temp.setDoHours(c.getInt(10));
            temp.setIsStatic(c.getInt(11));
            temp.setBlocks( c.getInt(12) );

            Events.add(temp);
        }
        c.close();

        return Events;
    }
    public Event getOneEvent( long id ){
        String sql="select * from "+Table_Name+" where "+Key_Id+" = "+id;
        Cursor result=db.rawQuery(sql,null);
        result.moveToFirst();

        int start[]={ result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5),0 };
        int end[]={ result.getInt(6),result.getInt(7),result.getInt(8),result.getInt(9),0 };
        Event e=new Event( result.getString(0),start,end );
        e.setDoHours(result.getInt(10));
        e.setIsStatic(result.getInt(11));
        e.setBlocks(result.getInt(12));

        return e;
    }
    public boolean setCol_Inside_MixEventDAO(long id){
        String where=Key_Id+"="+id;
        ContentValues cv=new ContentValues();
        cv.put(Col_Inside_MixEventDAO,1);
        return db.update(Table_Name,cv,where,null)>0;
    }
}
