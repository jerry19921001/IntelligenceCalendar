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
    public void InsertFromArrayList(ArrayList<Event> e){
        for (int i=0;i<e.size();i=i+1){
            this.Insert( e.get(i) );
        }
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
        cv.put(Col_No, e.getNo());

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
            database.delete(tableName,where,null);
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
                String startD1=temp.getStartYear()+"/"+temp.getStartMonth()+"/"+temp.getStartDay()+" "+"00";
                String endD2=temp.getEndYear()+"/"+temp.getEndMonth()+"/"+temp.getEndDay()+" "+"00";
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
                if (temp.getEndHour() != 0)
                {
                    differentDay ++;
                }
                //set the static event which can cross day
                Calendar c=Calendar.getInstance();
                Calendar c1=Calendar.getInstance();
                String startd=temp.getStartYear()+"/"+temp.getStartMonth()+"/"+temp.getStartDay()+" "+temp.getStartHour();
                String endd=temp.getEndYear()+"/"+temp.getEndMonth()+"/"+temp.getEndDay()+" "+temp.getEndHour();
                try {
                    d1 = sdf.parse(startd);
                    d2=sdf.parse(endd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
                        if (temp.getEndHour() == 0)
                        {
                            c.add(Calendar.DAY_OF_MONTH,-1);
                            tempEnd[0] = c.get(Calendar.YEAR);
                            tempEnd[1] = c.get(Calendar.MONTH)+1;
                            tempEnd[2] = c.get(Calendar.DAY_OF_MONTH);
                            tempEnd[3] = 24;
                        }
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
                calendar.add(Calendar.DAY_OF_MONTH,1);
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
        //System.out.println("year="+year+" month="+month+" day="+day);
        boolean hours[]=new boolean[24];
        String sql="select * from "+tableName+
                " where "+Col_Start_Year+"="+year+" and "+Col_Start_Month+"="+month+" and "+Col_Start_Day+"="+day;
        Cursor result=database.rawQuery(sql, null);

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
    // do by ja go
    private int GetBestJobID(ArrayList<Event> WaitForSchedule,Calendar BeginTime){
        Calendar Temp_EndTime=Calendar.getInstance();
        int ChosenPriorityId=-1;
        for(int index=0;index<WaitForSchedule.size();index+=1){//找優先度最高的事件 同時是可以做的 紀錄優先度最高事件的ID
            Calendar StartTime=Calendar.getInstance(),EndTime=Calendar.getInstance();
            StartTime.clear();EndTime.clear();
            StartTime.set(WaitForSchedule.get(index).getStartYear(), WaitForSchedule.get(index).getStartMonth(), WaitForSchedule.get(index).getStartDay(), WaitForSchedule.get(index).getStartHour(), WaitForSchedule.get(index).getStartMinute());
            EndTime.set(WaitForSchedule.get(index).getEndYear(), WaitForSchedule.get(index).getEndMonth(), WaitForSchedule.get(index).getEndDay(), WaitForSchedule.get(index).getEndHour(), WaitForSchedule.get(index).getEndMinute());
            if(!StartTime.after(BeginTime)){// 只判斷可做的時間到了沒     還沒判斷是不是超過結束時間 &&EndTime.after(Time)
                if(ChosenPriorityId==-1||Temp_EndTime.after(EndTime)){//這邊判斷優先度
                    if(WaitForSchedule.get(index).getDoHours()!=0){
                        ChosenPriorityId=index;
                        Temp_EndTime=(Calendar)EndTime.clone();
                    }
                }
                else if(!Temp_EndTime.before(EndTime)){//截止日相同
                    if(ChosenPriorityId!=index){
                        if(WaitForSchedule.get(ChosenPriorityId).getDoHours()>WaitForSchedule.get(index).getDoHours()){
                            if(WaitForSchedule.get(index).getDoHours()!=0){
                                if(WaitForSchedule.get(index).getBlocks()!=1){
                                    ChosenPriorityId=index;
                                    Temp_EndTime=(Calendar)EndTime.clone();
                                }

                            }
                        }
                    }

                }
            }
        }
        return ChosenPriorityId;
    }
    public ArrayList<Event> Schedule(ArrayList<Event> OriginData){
        ArrayList<Event> Schedule_Data=new ArrayList<Event>(),WaitForSchedule=new ArrayList<Event>();
        //先把固定的事件拿出來 OriginData會只剩下排程事件
        for(int i=0;i<OriginData.size();i+=1){
            Event t=OriginData.get(i);
            t.setStartMonth(t.getStartMonth()-1);
            t.setEndMonth(t.getEndMonth()-1);
            if(OriginData.get(i).isStatic()){
                Schedule_Data.add(OriginData.get(i));
            }
            else{
                WaitForSchedule.add(OriginData.get(i));
            }
        }
        Schedule_Data.clear();
        if(WaitForSchedule.size()==0){
            return Schedule_Data;
        }
        //找到排程事件中最早開始的時間點
        Calendar Time=Calendar.getInstance(),CompareTime=Calendar.getInstance();
        Event a=WaitForSchedule.get(0);
        Time.set(a.getStartYear(), a.getStartMonth(), a.getStartDay(), a.getStartHour(), a.getStartMinute());

        for(int i=1;i<WaitForSchedule.size();i+=1){
            a=WaitForSchedule.get(i);
            CompareTime.set(a.getStartYear(), a.getStartMonth(), a.getStartDay(), a.getStartHour(), a.getStartMinute());
            if(!Time.before(CompareTime)){
                Time.set(CompareTime.get(Calendar.YEAR),CompareTime.get(Calendar.MONTH),CompareTime.get(Calendar.DAY_OF_MONTH),0,0);
            }
        }
        //開始排程
        System.out.println("Start Schedule");
        Calendar BlockTime=Calendar.getInstance();BlockTime.clear();
        int BlockID=-1;
        while(!AllEventFinished(WaitForSchedule)){
            boolean Hour[]=this.getOneDayHourTime( Time.get(Calendar.YEAR),Time.get(Calendar.MONTH)+1,Time.get(Calendar.DAY_OF_MONTH) );//得到固定事件所佔的時間

            System.out.println("First loop");
            for(int i=6;i<24;i+=1){//最早從六點開始排
                if(Time.after(BlockTime)&&Time.before(BlockTime)){
                    WaitForSchedule.get(BlockID).setBlocks(0);
                    BlockID=-1;
                }
                if(Hour[i]==true) {

                    int ChosenPriorityId, TempDoHour = 1;
                    Time.set(Calendar.HOUR_OF_DAY, i);
                    ChosenPriorityId = GetBestJobID(WaitForSchedule, Time);
                    if (ChosenPriorityId == -1) {
                        if (i == 23) {
                            Time.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        continue;//如果沒有在這時間開始的事件或能做的事件時間都為0
                    }
                    System.out.println("over continue");
                    System.out.println("Event:"+WaitForSchedule.get(ChosenPriorityId).getName()+" do:"+WaitForSchedule.get(ChosenPriorityId).getDoHours());
                    //這邊開始做交換的事
                    Calendar Temp_EndTime=Calendar.getInstance();Temp_EndTime.clear();
                    Temp_EndTime.set((WaitForSchedule.get(ChosenPriorityId).getEndYear()),WaitForSchedule.get(ChosenPriorityId).getEndMonth(),WaitForSchedule.get(ChosenPriorityId).getEndDay(),WaitForSchedule.get(ChosenPriorityId).getEndHour(),WaitForSchedule.get(ChosenPriorityId).getEndMinute());
                    int ChangeID=-1;
                    Calendar ExchangeEventStartTime=Calendar.getInstance();ExchangeEventStartTime.clear();
                    ExchangeEventStartTime.set(WaitForSchedule.get(ChosenPriorityId).getStartYear(), WaitForSchedule.get(ChosenPriorityId).getStartMonth(), WaitForSchedule.get(ChosenPriorityId).getStartDay(), WaitForSchedule.get(ChosenPriorityId).getStartHour(), WaitForSchedule.get(ChosenPriorityId).getStartMinute());

                    Calendar ttemp=Calendar.getInstance();ttemp.clear();
                    ttemp=(Calendar)Time.clone();
                    ttemp.add(Calendar.HOUR_OF_DAY, WaitForSchedule.get(ChosenPriorityId).getDoHours());

                    if(Temp_EndTime.before(Time)||ttemp.after(Temp_EndTime)){//延遲
                        //進行交換事件
                        Calendar CompareCal=Calendar.getInstance();
                        System.out.println("Start Exchange");
                        int MaxDoHours=WaitForSchedule.get(ChosenPriorityId).getDoHours();
                        for(int k=Schedule_Data.size()-1;k>=0;k-=1){
                            //Schedule_Data k-1 的開始時間
                            Event b=Schedule_Data.get(k);
                            CompareCal.set(b.getStartYear(), b.getStartMonth(), b.getStartDay(), b.getStartHour(), b.getStartMinute());
                            if(!CompareCal.before(ExchangeEventStartTime)){//砍掉的事件的開始時間可以讓交換的事件加入
                                if (b.getDoHours()>MaxDoHours){//做的時間要大於現在延遲事件的時間
                                    ChangeID=k;
                                    MaxDoHours=b.getDoHours();
                                }
                            }
                        }
                    }
                    System.out.println("ID:"+ChangeID);
                    System.out.println("Ssize:" + Schedule_Data.size());
                    System.out.println("Wsize:" +WaitForSchedule.size());

                    //修改
                    if(ChangeID!=-1) {//事件延遲且有事件可以替換
                        int x=-1;
                        for(int w=0;w<WaitForSchedule.size();w+=1){//找原本在waitforschedule裡的index
                            if (WaitForSchedule.get(w).getId()==Schedule_Data.get(ChangeID).getId()){
                                x=w;
                                break;
                            }
                        }//下面的if要判斷x是不是跟之前的index一樣
                        if(x!=ChosenPriorityId){
                            Event ReplaceEvent = new Event();
                            Event b = WaitForSchedule.get(ChosenPriorityId);
                            Event c = Schedule_Data.get(ChangeID);
                            WaitForSchedule.get(ChosenPriorityId);
                            ReplaceEvent.setName(b.getName());
                            ReplaceEvent.setId(b.getId());
                            ReplaceEvent.setPreviousId(b.getId());
                            ReplaceEvent.setStartDate(Schedule_Data.get(ChangeID).getStartYear(), Schedule_Data.get(ChangeID).getStartMonth(), Schedule_Data.get(ChangeID).getStartDay(), Schedule_Data.get(ChangeID).getStartHour(), Schedule_Data.get(ChangeID).getStartMinute());
                            ReplaceEvent.setEndDate(Schedule_Data.get(ChangeID).getStartYear(), Schedule_Data.get(ChangeID).getStartMonth(), Schedule_Data.get(ChangeID).getStartDay(), Schedule_Data.get(ChangeID).getStartHour() + WaitForSchedule.get(ChosenPriorityId).getDoHours(), Schedule_Data.get(ChangeID).getStartMinute());
                            ReplaceEvent.setDoHours(WaitForSchedule.get(ChosenPriorityId).getDoHours());
                            //從以下開始會對排程進行改變


                            int index_in_WaitForSchedule = 0;
                            for (int w = 0; w < WaitForSchedule.size(); w += 1) {//找原本在waitforschedule裡的index
                                if (WaitForSchedule.get(w).getId() == Schedule_Data.get(ChangeID).getId()) {
                                    index_in_WaitForSchedule = w;
                                    break;
                                }
                            }

                            WaitForSchedule.get(index_in_WaitForSchedule).setDoHours(WaitForSchedule.get(index_in_WaitForSchedule).getDoHours() + Schedule_Data.get(ChangeID).getDoHours());//還原砍掉事件本來的doHour
                            System.out.println("recover doHour:" + WaitForSchedule.get(index_in_WaitForSchedule).getDoHours());
                            WaitForSchedule.get(index_in_WaitForSchedule).setBlocks(1);//借用Block當作不能使用
                            b.setDoHours(0);//對新增事件的doHours進行修改

                            for (int l = 0; l < WaitForSchedule.size(); l += 1) {
                                System.out.print(WaitForSchedule.get(l).getName() + " d" + WaitForSchedule.get(l).getDoHours() + " ");
                            }
                            System.out.println();
                            System.out.println("砍掉的事件");
                            System.out.println("Name" + c.getName());
                            System.out.println("Start:" + c.getStartYear() + " " + c.getStartMonth() + " " + c.getStartDay() + " " + c.getStartHour() + " " + c.getStartMinute());
                            System.out.println("End:" + c.getEndYear() + " " + c.getEndMonth() + " " + c.getEndDay() + " " + c.getEndHour() + " " + c.getEndMinute());
                            System.out.println("取代的事件");
                            System.out.println("Name" + ReplaceEvent.getName());
                            System.out.println("Start:" + ReplaceEvent.getStartYear() + " " + ReplaceEvent.getStartMonth() + " " + ReplaceEvent.getStartDay() + " " + ReplaceEvent.getStartHour() + " " + ReplaceEvent.getStartMinute());
                            System.out.println("End:" + ReplaceEvent.getEndYear() + " " + ReplaceEvent.getEndMonth() + " " + ReplaceEvent.getEndDay() + " " + ReplaceEvent.getEndHour() + " " + ReplaceEvent.getEndMinute());
                            Schedule_Data.remove(ChangeID);
                            Schedule_Data.add(ChangeID, ReplaceEvent);
                            System.out.println("ChangID:" + (ChangeID + 1) + " S_D:size:" + Schedule_Data.size());


                            for (int l = ChangeID + 1; l < Schedule_Data.size(); l += 1) {//對所有再砍掉事件後面的事件進行刪除
                                System.out.println(l);
                                for (int w = 0; w < WaitForSchedule.size(); w += 1) {//找原本在waitforschedule裡的index
                                    if (WaitForSchedule.get(w).getId() == Schedule_Data.get(l).getId()) {
                                        index_in_WaitForSchedule = w;
                                        break;
                                    }
                                }
                                WaitForSchedule.get(index_in_WaitForSchedule).setDoHours(WaitForSchedule.get(index_in_WaitForSchedule).getDoHours() + Schedule_Data.get(l).getDoHours());
                            }
                            for (int l = 0; l < WaitForSchedule.size(); l += 1) {
                                System.out.print(WaitForSchedule.get(l).getName() + " d" + WaitForSchedule.get(l).getDoHours() + " ");
                            }
                            System.out.println();
                            while (Schedule_Data.size() > ChangeID + 1) {
                                Schedule_Data.remove(Schedule_Data.size() - 1);
                            }
                            //紀錄Block存活時間
                            BlockTime = (Calendar) Time.clone();
                            BlockID = index_in_WaitForSchedule;
                            Time.set(ReplaceEvent.getEndYear(), ReplaceEvent.getEndMonth(), ReplaceEvent.getEndDay(), ReplaceEvent.getEndHour(), ReplaceEvent.getEndMinute());
                            i = ReplaceEvent.getEndHour() - 1;
                            System.out.println("Out from do DelayEvent, now Time:" + Time.get(Calendar.YEAR) + " " + Time.get(Calendar.MONTH) + " " + Time.get(Calendar.DAY_OF_MONTH) + " " + Time.get(Calendar.HOUR_OF_DAY));
                            continue;
                        }
                    }

                    //

                    Event Temp=new Event();
                    Temp.setDoHours(WaitForSchedule.get(ChosenPriorityId).getDoHours());
                    Temp.setPreviousId(WaitForSchedule.get(ChosenPriorityId).getId());
                    Temp.setName(WaitForSchedule.get(ChosenPriorityId).getName());
                    Temp.setId(WaitForSchedule.get(ChosenPriorityId).getId());

                    Temp.setDoHours(TempDoHour);//做一個新的事件 將做的時間先設成1
                    WaitForSchedule.get(ChosenPriorityId).setDoHours(WaitForSchedule.get(ChosenPriorityId).getDoHours()-1);//將doHour-1
                    int start[]={Time.get(Calendar.YEAR),Time.get(Calendar.MONTH),Time.get(Calendar.DAY_OF_MONTH),i,0};
                    int end[]={Time.get(Calendar.YEAR),Time.get(Calendar.MONTH),Time.get(Calendar.DAY_OF_MONTH),i+1,0};
                    Temp.setStartDate(start);

                    //下面再從接下來的時間繼續找 同時還要確認是同一事件 如果有其他優先度比較高的出現  就先跳出 先把事件加入ArrayList
                    System.out.println("Continuing making Event");
                    int j,BestID;
                    for(j=i+1;j<24;j+=1){
                        Time.add(Calendar.HOUR_OF_DAY,1);
                        BestID=GetBestJobID(WaitForSchedule,Time);
                        //要判斷優先度    or   doHour用完
                        if(BestID!=ChosenPriorityId||WaitForSchedule.get(ChosenPriorityId).getDoHours()<=0){
                            Temp.setDoHours(TempDoHour);
                            end[3]=j;//設定結束時間的小時數
                            Temp.setEndDate(end);
                            Schedule_Data.add(Temp);
                            break;
                        }
                        if (Hour[j] == true) {
                            TempDoHour+=1;
                            WaitForSchedule.get(ChosenPriorityId).setDoHours(WaitForSchedule.get(ChosenPriorityId).getDoHours()-1);//將doHour-1

                        }
                        else{//沒有連續的時間可以排  所以先加入ArrayList
                            Temp.setDoHours(TempDoHour);
                            end[3]=j;//設定結束時間的小時數
                            Temp.setEndDate(end);
                            Schedule_Data.add(Temp);
                            break;
                        }
                    }
                    if(j==24){//到晚上的排程都是OK的 要新增到ArrayList  ((23不會跳出 24才會跳出來
                        Time.add(Calendar.HOUR_OF_DAY,1);//等於24的時候會沒加到Time
                        Temp.setDoHours(TempDoHour);
                        end[3]=j;//設定結束時間的小時數
                        Temp.setEndDate(end);
                        Schedule_Data.add(Temp);
                        i=j;
                    }
                    else{//時間還沒做完(還沒到晚上23點) 繼續接下去做
                        i=j-1;
                    }
                }
                else {
                    Time.add(Calendar.HOUR_OF_DAY, 1);
                }
            }
            //Time.add(Calendar.DAY_OF_MONTH,1);//一天做完後加一天
        }
        for(int i=0;i<Schedule_Data.size();i+=1){
            Event t =Schedule_Data.get(i);
            t.setStartMonth(t.getStartMonth()+1);
            t.setEndMonth(t.getEndMonth()+1);
        }
        for(int g=0;g<Schedule_Data.size();g+=1){
            System.out.print(g + ":" + Schedule_Data.get(g).getName() + " ");
        }
        System.out.println("Go out");
        return Schedule_Data;
    }
    boolean AllEventFinished(ArrayList<Event> a){
        for(int i=0;i<a.size();i+=1){
            if(a.get(i).getDoHours()!=0){
                return false;
            }
        }
        return true;
    }
    // do by yun ja
    public ArrayList<Event> ScheduleByYunJa(ArrayList<Event> OriginData) {
        ArrayList<Event> Schedule_Data = new ArrayList<Event>(), WaitForSchedule = new ArrayList<Event>();
        //先把固定的事件拿出來 OriginData會只剩下排程事件
        for (int i = 0; i < OriginData.size(); i += 1) {
            Event t = OriginData.get(i);
            t.setStartMonth(t.getStartMonth() - 1);
            t.setEndMonth(t.getEndMonth() - 1);
            if (OriginData.get(i).isStatic()) {
                Schedule_Data.add(OriginData.get(i));
            } else {
                WaitForSchedule.add(OriginData.get(i));
            }
        }
        Schedule_Data.clear();
        if (WaitForSchedule.size() == 0) {
            return Schedule_Data;
        }
        for (int i = 0; i < WaitForSchedule.size(); i++) {
            Event now = WaitForSchedule.get(i);
            Calendar start = Calendar.getInstance(), end = Calendar.getInstance();
            start.set(now.getStartYear(), now.getStartMonth(), now.getStartDay(), now.getStartHour(), now.getStartMinute());
            end.set(now.getEndYear(), now.getEndMonth(), now.getEndDay(), now.getEndHour(), now.getEndMinute());
            int millisecondsofhour = 1000 * 60 * 60;
            long diff = (end.getTimeInMillis() - start.getTimeInMillis()) / millisecondsofhour;
            WaitForSchedule.get(i).setTotalTime(diff);
            long totaltime = WaitForSchedule.get(i).getTotalTime();
            int doeventtime = WaitForSchedule.get(i).getDoHours();
            WaitForSchedule.get(i).setPriority((double) totaltime / doeventtime);
        }
        //找到排程事件中最早開始的時間點
        Calendar Time = Calendar.getInstance(), CompareTime = Calendar.getInstance();
        Time.clear();
        CompareTime.clear();
        Event a = WaitForSchedule.get(0);
        Time.set(a.getStartYear(), a.getStartMonth(), a.getStartDay(), a.getStartHour(), a.getStartMinute());
        for (int i = 1; i < WaitForSchedule.size(); i += 1) {
            a = WaitForSchedule.get(i);
            CompareTime.set(a.getStartYear(), a.getStartMonth(), a.getStartDay(), a.getStartHour(), a.getStartMinute());
            if (!Time.before(CompareTime)) {
                Time.set(CompareTime.get(Calendar.YEAR), CompareTime.get(Calendar.MONTH), CompareTime.get(Calendar.DAY_OF_MONTH), 0, 0);
            }
        }
        while (!AllEventFinished(WaitForSchedule)) {
            boolean Hour[] = this.getOneDayHourTime(Time.get(Calendar.YEAR), Time.get(Calendar.MONTH) + 1, Time.get(Calendar.DAY_OF_MONTH));
            for (int i = 6; i < 24; i += 1) {//最早從六點開始排
                if (Hour[i] == true) {
                    Event Temp = new Event();
                    int ChosenPriorityId = -1, TempDoHour = 1;
                    for (int index = 0; index < WaitForSchedule.size(); index += 1) {//找優先度最高的事件 同時是可以做的 紀錄優先度最高事件的ID
                        Time.set(Calendar.HOUR_OF_DAY, i);
                        Calendar StartTime = Calendar.getInstance(), EndTime = Calendar.getInstance();
                        StartTime.clear();
                        EndTime.clear();
                        StartTime.set(WaitForSchedule.get(index).getStartYear(), WaitForSchedule.get(index).getStartMonth(), WaitForSchedule.get(index).getStartDay(), WaitForSchedule.get(index).getStartHour(), WaitForSchedule.get(index).getStartMinute());
                        EndTime.set(WaitForSchedule.get(index).getEndYear(), WaitForSchedule.get(index).getEndMonth(), WaitForSchedule.get(index).getEndDay(), WaitForSchedule.get(index).getEndHour(), WaitForSchedule.get(index).getEndMinute());
                        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd HH");
                        if (!StartTime.after(Time)) {
                            if (ChosenPriorityId == -1 || Temp.getPriority() > WaitForSchedule.get(index).getPriority()
                                    || (Temp.getPriority() == WaitForSchedule.get(index).getPriority() && Temp.getTotalTime() > WaitForSchedule.get(index).getTotalTime())) //判斷優先度
                            {
                                if (WaitForSchedule.get(index).getDoHours() != 0) {
                                    ChosenPriorityId = index;
                                    Temp.setDoHours(WaitForSchedule.get(index).getDoHours());
                                    Temp.setPreviousId(WaitForSchedule.get(index).getId());
                                    Temp.setName(WaitForSchedule.get(index).getName());
                                    Temp.setPriority(WaitForSchedule.get(index).getPriority());
                                    Temp.setTotalTime(WaitForSchedule.get(index).getTotalTime());
                                }
                            }
                            WaitForSchedule.get(index).setTotalTime(WaitForSchedule.get(index).getTotalTime() - 1);
                            WaitForSchedule.get(index).setPriority((double) WaitForSchedule.get(index).getTotalTime() / WaitForSchedule.get(index).getDoHours());
                        }
                    }
                    if (ChosenPriorityId == -1)
                        continue;//如果沒有在這時間開始的事件或能做的事件時間都為0
                    Temp.setDoHours(TempDoHour);//做一個新的事件 將做的時間先設成1
                    WaitForSchedule.get(ChosenPriorityId).setDoHours(WaitForSchedule.get(ChosenPriorityId).getDoHours() - 1);
                    WaitForSchedule.get(ChosenPriorityId).setPriority((double) WaitForSchedule.get(ChosenPriorityId).getTotalTime() / WaitForSchedule.get(ChosenPriorityId).getDoHours());
                    int start[] = {Time.get(Calendar.YEAR), Time.get(Calendar.MONTH), Time.get(Calendar.DAY_OF_MONTH), i, 0};
                    int end[] = {Time.get(Calendar.YEAR), Time.get(Calendar.MONTH), Time.get(Calendar.DAY_OF_MONTH), i + 1, 0};
                    Temp.setStartDate(start);
                    //下面再從接下來的時間繼續找 (同時還要確認是同一事件 如果有其他優先度比較高的出現 //目前不會出現) 就先跳出 先把事件加入ArrayList
                    int j;
                    for (j = i + 1; j < 24; j += 1) {
                        Time.add(Calendar.HOUR_OF_DAY, 1);
                        boolean flag = false;
                        for (int k = 0; k < WaitForSchedule.size(); k++) {
                            Event event = WaitForSchedule.get(k);
                            Calendar now = Calendar.getInstance();
                            now.clear();
                            now.set(event.getStartYear(), event.getStartMonth(), event.getStartDay(), event.getStartHour(), event.getStartMinute());
                            if ((!now.after(Time)) && WaitForSchedule.get(k).getDoHours() > 0) {
                                if (WaitForSchedule.get(ChosenPriorityId).getPriority() > event.getPriority()
                                        || (WaitForSchedule.get(ChosenPriorityId).getPriority() == event.getPriority() && WaitForSchedule.get(ChosenPriorityId).getTotalTime() > event.getTotalTime())) {
                                    flag = true;
                                }
                            }
                        }
                        if (WaitForSchedule.get(ChosenPriorityId).getDoHours() <= 0 || flag == true) //別的事件優先度較高或該事件已做完
                        {
                            Temp.setDoHours(TempDoHour);
                            end[3] = j;
                            Temp.setEndDate(end);
                            Schedule_Data.add(Temp);
                            break;
                        }
                        if (Hour[j] == true) {
                            for (int index = 0; index < WaitForSchedule.size(); index++) {
                                Event event = WaitForSchedule.get(index);
                                Calendar now = Calendar.getInstance();
                                now.set(event.getStartYear(), event.getStartMonth(), event.getStartDay(), event.getStartHour(), event.getStartMinute());
                                if (!now.after(Time)) {
                                    WaitForSchedule.get(index).setTotalTime(WaitForSchedule.get(index).getTotalTime() - 1);
                                    WaitForSchedule.get(index).setPriority((double) WaitForSchedule.get(index).getTotalTime() / WaitForSchedule.get(index).getDoHours());
                                }
                            }
                            TempDoHour += 1;
                            WaitForSchedule.get(ChosenPriorityId).setDoHours(WaitForSchedule.get(ChosenPriorityId).getDoHours() - 1);//將doHour-1
                            WaitForSchedule.get(ChosenPriorityId).setPriority((double) WaitForSchedule.get(ChosenPriorityId).getTotalTime() / WaitForSchedule.get(ChosenPriorityId).getDoHours());
                        } else {//沒有連續的時間可以排  所以先加入ArrayList
                            Temp.setDoHours(TempDoHour);
                            end[3] = j;
                            Temp.setEndDate(end);
                            Schedule_Data.add(Temp);
                            break;
                        }
                    }
                    if (j == 24) {//到晚上的排程都是OK的 要新增到ArrayList
                        Temp.setDoHours(TempDoHour);
                        end[3] = j;//設定結束時間的小時數
                        Temp.setEndDate(end);
                        Schedule_Data.add(Temp);
                        i = j;
                    } else {//時間還沒做完(還沒到晚上24點) 繼續接下去做
                        i = j - 1;
                    }
                }
            }
            Time.add(Calendar.HOUR_OF_DAY, 1);
            for (int k = 0; k < WaitForSchedule.size(); k++) {
                Event event = WaitForSchedule.get(k);
                Calendar now = Calendar.getInstance();
                now.set(event.getStartYear(), event.getStartMonth(), event.getStartDay(), event.getStartHour(), event.getStartMinute());
                for (int l = 0; l < 6; l++) {
                    if (!now.after(Time) && WaitForSchedule.get(k).getDoHours() > 0) {
                        WaitForSchedule.get(k).setTotalTime(WaitForSchedule.get(k).getTotalTime() - 1);
                    }
                    Time.add(Calendar.HOUR_OF_DAY, 1);
                }
                Time.add(Calendar.HOUR_OF_DAY, -6);
                WaitForSchedule.get(k).setPriority((double) WaitForSchedule.get(k).getTotalTime() / WaitForSchedule.get(k).getDoHours());
            }
        }
        for (int i = 0; i < Schedule_Data.size(); i++)
        {
            Schedule_Data.get(i).setStartMonth(Schedule_Data.get(i).getStartMonth() +1);
            Schedule_Data.get(i).setEndMonth(Schedule_Data.get(i).getEndMonth()+1);
        }
        return Schedule_Data;
    }
}
