package com.example.home.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        cv.put(Col_Previous_Id_From_EventDAO,e.getPreviousId());
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH");
        long millionOfOneDay=86400000; // 60*60*24*1000=86400000
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

            if( temp.isCrossDay() ){
                // set the format of date
                String startD1=temp.getStartYear()+"/"+temp.getStartMonth()+"/"+temp.getStartDay()+" "+temp.getStartHour();
                String endD2=temp.getEndYear()+"/"+temp.getEndMonth()+"/"+temp.getEndDay()+" "+temp.getEndHour();
                // change the string to date format
                Date d1=null,d2= null;
                try {
                    d1 = sdf.parse(startD1);
                    d2=sdf.parse(endD2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // calculate the different day
                long differentDay=Math.abs((d2.getTime() - d1.getTime()) / millionOfOneDay);
                long mod_differentDay = Math.abs((d2.getTime() - d1.getTime()) % millionOfOneDay);
                if (mod_differentDay != 0) differentDay ++;
                if (temp.getEndHour() != 0)
                {
                    differentDay ++;
                }
                //set the static event which can cross day
                Calendar c=Calendar.getInstance();
                Calendar c1=Calendar.getInstance();
                c.setTime(d1);//start time
                c1.setTime(d2);//end time
                int days=0;
                //System.out.println("c.getday="+c.get(Calendar.DAY_OF_MONTH));

                while( days<differentDay ){
                    Event cutEvents=new Event();
                    cutEvents.setName(temp.getName());
                    if( days==0 ){ //the first day
                        int tempStart[]={ c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR_OF_DAY),0 };
                        //c.add(Calendar.DAY_OF_MONTH,1);
                        int tempEnd[]={ c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),24,0 };
                        cutEvents.setStartDate(tempStart);
                        cutEvents.setEndDate(tempEnd);
                    }
                    else if( differentDay-days==1 ){ // the last day
                        int tempStart[]={ c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),0,0 };
                        int tempEnd[]={ c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),temp.getEndHour(),0 };
                        cutEvents.setStartDate(tempStart);
                        cutEvents.setEndDate(tempEnd);
                    }
                    else{ // the other day
                        int tempStart[]={ c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),0,0 };
                        int tempEnd[]={ c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),24,0 };
                        cutEvents.setStartDate(tempStart);
                        cutEvents.setEndDate(tempEnd);
                    }
                    cutEvents.setIsStatic(1);
                    cutEvents.setPreviousId(temp.getId());
                    cutEvents.setDoHours(temp.getDoHours());

                    this.Insert(cutEvents);

                    c.add(Calendar.DAY_OF_MONTH,1);
                    days=days+1;
                }
            }
            else{
                Insert( temp );
            }
        }

        result.close();
    }
    public void Sort(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH");
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
            // get the information of one dynamic event
            int start[]={ dynamicResult.getInt(2),dynamicResult.getInt(3),dynamicResult.getInt(4),dynamicResult.getInt(5),0 };
            int end[]={ dynamicResult.getInt(6),dynamicResult.getInt(7),dynamicResult.getInt(8),dynamicResult.getInt(9),0 };
            Event dynamicEvent=new Event(dynamicResult.getString(1),start,end);
            dynamicEvent.setDoHours(dynamicResult.getInt(10));
            dynamicEvent.setId(dynamicResult.getLong(0));
            dynamicEvent.setPreviousId(dynamicResult.getLong(0));
            //set the date and the calendar for the dynamic event
            String startDate=dynamicEvent.getStartYear()+"/"+dynamicEvent.getStartMonth()+"/"+dynamicEvent.getStartDay()+" "+dynamicEvent.getStartHour();
            Date date=null;
            Calendar calendar=Calendar.getInstance();
            try {
                date=sdf.parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            //find which day can put something
            int remainDoHour=dynamicEvent.getDoHours();
            // find which hours and which days can be use
            while( remainDoHour>0 ){
                //get the start month
                int startMonth=calendar.get(Calendar.MONTH)+1;
                // the 24 hours of one day
                boolean hours[]=new boolean[24];
                for(int i=0;i<24;i++){
                    if( i<6 ) hours[i]=false;// 0~6 sleep time
                    else hours[i]=true;
                }
                // find the static events of the day
                String sqlOfStaticEvent="select * from "+tableName+
                        " where "+Col_Start_Year+"="+calendar.get(Calendar.YEAR)+" and "+Col_Start_Month+"="+startMonth+" and "+Col_Start_Day+"="+calendar.get(Calendar.DAY_OF_MONTH);
                Cursor staticResult=database.rawQuery(sqlOfStaticEvent,null);
                if( staticResult.getCount()!=0 ){
                    while( staticResult.moveToNext() ){
                        // set the section of hours to be false
                        for(int i=staticResult.getInt(5);i<staticResult.getInt(9);i++){
                            hours[i]=false;
                        }
                    }
                }
                staticResult.close();
                // check the start hour nad the start day
                int startHour=0;
                if( calendar.get(Calendar.DAY_OF_MONTH)==dynamicEvent.getStartDay() ) startHour=dynamicEvent.getStartHour();
                else startHour=6;
                // the information to insert new event
                int useHour=0;
                // find hours to insert the dynamic event
                for(int i=startHour;i<24;i=i+1){
                    if( hours[i] && remainDoHour>0 ){
                        boolean endOfTheDay=false;
                        // make sure the next hour
                        for(int j=i;j<24;j++){
                            if( !hours[j] || remainDoHour==0 ){
                                endOfTheDay=false;
                                int startTime[]={ calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),i,0 };
                                int endTime[]={ calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),j,0 };
                                Event eventInsertToMix=new Event(dynamicEvent.getName(),startTime,endTime);
                                eventInsertToMix.setPreviousId(dynamicEvent.getId());
                                eventInsertToMix.setDoHours(useHour);
                                Insert(eventInsertToMix);
                                i=j;
                                break;
                            }
                            else{
                                hours[j]=false;
                                useHour=useHour+1;
                                remainDoHour=remainDoHour-1;
                                endOfTheDay=true;
                            }
                            //check if j==23
                            /*if( j==23 ){ // the end of the end time
                                int startTime[]={ calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),i,0 };
                                int endTime[]={ calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),23,0 };
                                Event eventInsertToMix=new Event(dynamicEvent.getName(),startTime,endTime);
                                eventInsertToMix.setPreviousId(dynamicEvent.getId());
                                eventInsertToMix.setDoHours(useHour);
                                System.out.println("in for for if(j==23) : ");
                                eventInsertToMix.print();
                                Insert(eventInsertToMix);
                                i=j;
                            }*/
                        }
                        if( endOfTheDay ){
                            int startTime[]={ calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),i,0 };
                            int endTime[]={ calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),24,0 };
                            Event eventInsertToMix=new Event(dynamicEvent.getName(),startTime,endTime);
                            eventInsertToMix.setPreviousId(dynamicEvent.getId());
                            eventInsertToMix.setDoHours(useHour);
                            Insert(eventInsertToMix);
                        }
                    }
                    if( remainDoHour==0 ) break;
                }
                // find the next day
                calendar.add(Calendar.DAY_OF_YEAR,1);
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
            temp.setIsStatic(result.getInt(12));

            events.add(temp);
        }
        result.close();
        
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
    public boolean[] getOneDayHourTime(int year,int month,int day){
        boolean hours[]=new boolean[24];
        String sql="select * from "+tableName+
                " where "+Col_Start_Year+"="+year+" and "+Col_Start_Month+"="+month+" and "+Col_Start_Day+"="+day;
        Cursor result=database.rawQuery(sql,null);

        for(int i=0;i<24;i=i+1){
            if( i<6 ) hours[i] = false;
            else hours[i]=true;
        }
        while( result.moveToNext() ){
            int start=result.getInt(5),end=result.getInt(9);
            for(int i=start;i<end;i=i+1){
                hours[i]=false;
            }
        }
        result.close();

        return hours;
    }
}
