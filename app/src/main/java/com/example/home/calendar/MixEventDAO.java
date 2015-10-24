package com.example.home.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by user on 2015/9/26.
 */
public class MixEventDAO {
    //table name
    public static final String tableName="MixEvent";
    //the column of the table
    public static final String Key="_id";//0 int
    public static final String Col_Event_Name="Event_Name";// 1 String
    public static final String Col_Start_Year="Start_Year";// 2 int
    public static final String Col_Start_Month="Start_Month";// 3 int
    public static final String Col_Start_Day="Start_Day";// 4 int
    public static final String Col_Start_Hour="Start_Hour";// 5 int
    public static final String Col_End_Year="End_Year";// 6 int
    public static final String Col_End_Month="End_Month";// 7 int
    public static final String Col_End_Day="End_Day";// 8 int
    public static final String Col_End_Hour="End_Hour";// 9 int
    public static final String Col_Previous_Id_From_EventDAO="Previous_Id_From_EventDAO";//10 int
    public static final String Col_Do_Hour="Do_Hour";// 11 int
    public static final String Col_Is_Static="Is_Static";// 12 int
    public static final String Col_No="No";// 13 int
    //string : create table
    public static final String CreateTable="Create Table "+tableName+" ( "+
            Key+" Integer Primary Key AUTOINCREMENT, "+
            Col_Event_Name+" Text Not Null, "+
            Col_Start_Year+" Integer Not Null, "+
            Col_Start_Month+" Integer Not Null, "+
            Col_Start_Day+" Integer Not Null, "+
            Col_Start_Hour+" Integer Not Null, "+
            Col_End_Year+" Integer Not Null, "+
            Col_End_Month+" Integer Not Null, "+
            Col_End_Day+" Integer Not Null, "+
            Col_End_Hour+" Integer Not Null, "+
            Col_Previous_Id_From_EventDAO+" Integer Not Null, "+
            Col_Do_Hour+" Integer Not Null, "+
            Col_Is_Static+" Integer Not Null, "+
            Col_No+" Integer Not Null "+
            " ); ";
    //the database
    static SQLiteDatabase database=null;
    // the construction
    public MixEventDAO( Context context ){
        database=MyDBhelper.getDatabase(context);
    }
    public void Close(){
        database.close();
    }
    // the functions
    public long Insert( Event e ){
        ContentValues cv=new ContentValues();

        cv.put(Col_Event_Name,e.getName());
        cv.put(Col_Start_Year,e.getStartYear());
        cv.put(Col_Start_Month,e.getStartMonth());
        cv.put(Col_Start_Day,e.getStartDay());
        cv.put(Col_Start_Hour,e.getStartHour());
        cv.put(Col_End_Year,e.getEndYear());
        cv.put(Col_End_Month,e.getEndMonth());
        cv.put(Col_End_Day,e.getEndDay());
        cv.put(Col_End_Hour,e.getEndHour());
        cv.put(Col_Previous_Id_From_EventDAO,e.getId());
        cv.put(Col_Do_Hour,e.getDoHours());
        if( e.isStatic() ) cv.put(Col_Is_Static,1);
        else cv.put(Col_Is_Static,0);
        cv.put(Col_No,e.getNo());

        long id=database.insert(tableName, null, cv);

        return id;
    }
    public boolean Update( Event e ){
        String where=Key+"="+e.getId();
        ContentValues cv=new ContentValues();

        cv.put(Col_Event_Name,e.getName());
        cv.put(Col_Start_Year,e.getStartYear());
        cv.put(Col_Start_Month,e.getStartMonth());
        cv.put(Col_Start_Day,e.getStartDay());
        cv.put(Col_Start_Hour,e.getStartHour());
        cv.put(Col_End_Year,e.getEndYear());
        cv.put(Col_End_Month,e.getEndMonth());
        cv.put(Col_End_Day,e.getEndDay());
        cv.put(Col_End_Hour,e.getEndHour());
        cv.put(Col_Previous_Id_From_EventDAO,e.getId());
        cv.put(Col_Do_Hour,e.getDoHours());
        if( e.isStatic() ) cv.put(Col_Is_Static,1);
        else cv.put(Col_Is_Static,0);
        cv.put(Col_No,e.getNo());

        return database.update(tableName,cv,where,null)>0;
    }
    public boolean Delete( long id ){
        String where=Key+"="+id;
        return database.delete(tableName,where,null)>0;
    }
    // other function
    public void DeleteAll(){
        String sql="select * from "+tableName;
        Cursor result=database.rawQuery( sql,null );
        if( result.getCount()==0 ){
            result.close();
            return;
        }

        while (result.moveToNext()){
            String where=Key+"="+result.getInt(0);
            int count=database.delete(tableName,where,null);
        }
        result.close();
    }
    public void InsertStaticEvents(){
        String sql="select * "+
                " from "+EventsDao.Table_Name+
                " where "+EventsDao.Col_Is_Static+">0";//+" and "+EventsDao.Col_Inside_MixEventDAO+"=0";
        Cursor result=database.rawQuery(sql, null);
        if( result.getCount()==0 ){
            result.close();
            return;
        }

        while (result.moveToNext()){
            int start[]={ result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5),0 };
            int end[]={ result.getInt(6),result.getInt(7),result.getInt(8),result.getInt(9),0 };
            Event temp=new Event(result.getString(1),start,end);
            temp.setId(result.getLong(0));
            temp.setPreviousId(result.getLong(0));
            temp.setDoHours(result.getInt(11));
            temp.setIsStatic(1);

            if( Insert( temp )==-1 ) break;
        }

        result.close();
    }
    public void Sort(){
        String sql="select * "+
                "from "+EventsDao.Table_Name+
                " where "+Col_Is_Static+"=0"+
                " order by "+Col_End_Year+","+Col_End_Month+","+Col_End_Day+","+Col_End_Hour+
                ","+Col_Start_Year+","+Col_Start_Month+","+Col_Start_Day+","+Col_Start_Hour;
        //find the dynamic events
        Cursor dynamicResult=database.rawQuery(sql,null);
        if( dynamicResult.getCount()==0 ){
            dynamicResult.close();
            return;
        }
        //start sort
        while ( dynamicResult.moveToNext() ){
            // get the information of the dynamic event
            int start[]={ dynamicResult.getInt(2),dynamicResult.getInt(3),dynamicResult.getInt(4),dynamicResult.getInt(5),0 };
            int end[]={ dynamicResult.getInt(6),dynamicResult.getInt(7),dynamicResult.getInt(8),dynamicResult.getInt(9),0 };
            Event dynamicEvent=new Event(dynamicResult.getString(1),start,end);
            dynamicEvent.setDoHours(dynamicResult.getInt(10));
            dynamicEvent.setId(dynamicResult.getLong(0));
            dynamicEvent.setPreviousId(dynamicResult.getLong(0));
            //find which day can put something
            int startDay=dynamicEvent.getStartDay();
            int endDay=dynamicEvent.getEndDay();

            while (startDay<=endDay){
                //setting hours of one day
                boolean hours[]=new boolean[24];
                boolean can=true;
                for (int i=0;i<24;i++){ // 0~6 sleeping time
                    if( i<6 ) hours[i]=false;
                    else hours[i]=true;
                }
                //find events which has been set in the start day of dynamic event
                String sqlOfStaticEvent="select * "+
                        "from "+tableName+
                        " where "+Col_Start_Year+"="+dynamicEvent.getStartYear()+" and "+Col_Start_Month+"="+dynamicEvent.getStartMonth()+" and "+Col_Start_Day+"="+startDay+
                        " order by "+Col_Start_Hour;
                Cursor oneDayEventResult=database.rawQuery(sqlOfStaticEvent,null);
                if( oneDayEventResult.getCount()!=0 ){
                    while ( oneDayEventResult.moveToNext() ){
                        for(int i=oneDayEventResult.getInt(5);i<oneDayEventResult.getInt(9);i++){
                            hours[i]=false;
                        }
                    }
                }
                oneDayEventResult.close();
                //set the start hour and end hour
                int startHour,endHour;
                if( startDay==dynamicEvent.getStartDay() ) startHour=dynamicEvent.getStartHour();
                else startHour=6;
                if( endDay==dynamicEvent.getEndDay() ) endHour=dynamicEvent.getEndHour();
                else endHour=24;
                //insert dynamic event into this table
                int count=0;
                int no=0;
                for (int i=startHour;i<endHour;i++){
                    if( hours[i] ){
                        no=no+1;
                        //record how many hours has been sorted
                        int temp=count;
                        if( i+dynamicEvent.getDoHours()-count+1>=24 ) temp=24;
                        else temp=i+dynamicEvent.getDoHours()-count+1;
                        //make sure the next hour can be use
                        for (int j=i;j<temp;j++){
                            //insert some hours of the dynamic event into the mix table
                            if( !hours[j] || count>=dynamicEvent.getDoHours() ){
                                //set the information of the dynamic event
                                int startTime[]={ dynamicEvent.getStartYear(),dynamicEvent.getStartMonth(),dynamicEvent.getStartDay(),i,0 };
                                int endTime[]={ dynamicEvent.getEndYear(),dynamicEvent.getEndMonth(),dynamicEvent.getEndDay(),j,0 };
                                Event anEventInsertInMix=new Event(dynamicEvent.getName(),startTime,endTime);
                                anEventInsertInMix.setId(dynamicEvent.getId());
                                anEventInsertInMix.setPreviousId(dynamicEvent.getId());
                                anEventInsertInMix.setDoHours(j-i);
                                anEventInsertInMix.setNo(no);
                                //insert the section of dynamic event into mix table
                                Insert(anEventInsertInMix);
                                break;
                            }
                            //check the next hour
                            else{
                                count++;
                                hours[j]=false;
                                can=false;
                            }
                        }
                    }
                    if( count==dynamicEvent.getDoHours() ){
                        can=true;
                        break;
                    }
                }
                if( can ) break;
                //find the next day
                startDay++;
            }
        }

        dynamicResult.close();
    }
    public Event getOneEvent( long id ){
        String sql="select * from "+tableName+
                " where "+Key+"="+id;
        Cursor result=database.rawQuery(sql,null);
        result.moveToFirst();

        int start[]={ result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5),0 };
        int end[]={ result.getInt(6),result.getInt(7),result.getInt(8),result.getInt(9),0 };
        Event e=new Event(result.getString(1),start,end);
        e.setId(result.getLong(0));
        e.setPreviousId(result.getLong(10));
        e.setDoHours(result.getInt(11));
        e.setIsStatic(result.getInt(12));
        e.setNo(result.getInt(13));

        result.close();
        //System.out.println("in MixEventDAO getOneEvent:");
        //e.print();
        //System.out.println("out getOneEvent");
        return e;
    }
    public ArrayList<Event> getOneDayEvents(int year,int month,int day){
        String sql="select * "+
                "from "+tableName+
                " where "+Col_Start_Year+"="+year+" and "+Col_Start_Month+"="+month+" and "+Col_Start_Day+"="+day+
                " order by "+Col_Start_Hour;
        Cursor result=database.rawQuery(sql,null);
        ArrayList<Event> events=new ArrayList<>();

        while (result.moveToNext()){
            int start[]={ result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5),0 };
            int end[]={ result.getInt(6),result.getInt(7),result.getInt(8),result.getInt(9),0 };
            Event temp=new Event(result.getString(1),start,end);
            temp.setId(result.getLong(0));
            temp.setPreviousId(result.getLong(10));
            temp.setDoHours(result.getInt(11));

            events.add(temp);
        }
        return events;
    }
    public Event getFatherEvent(long preid){ // get the father event of the subtask
        String sql="select * from "+EventsDao.Table_Name+" where "+EventsDao.Key_Id+"="+preid;
        Cursor result=database.rawQuery(sql,null);
        result.moveToFirst();

        int start[]={ result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5),0 };
        int end[]={ result.getInt(6),result.getInt(7),result.getInt(8),result.getInt(9),0 };
        Event fatherEvent=new Event(result.getString(1),start,end);
        fatherEvent.setId(result.getLong(0));
        fatherEvent.setDoHours(result.getInt(10));
        fatherEvent.setIsStatic(result.getInt(11));
        fatherEvent.setBlocks(result.getInt(12));

        result.close();

        return fatherEvent;
    }
}
