package com.example.home.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by User205 on 2015/6/6.
 */
public class day_list_test extends ActionBarActivity {
    private ListView lv;
    private EventsDao db=null;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.day_list_test);
        System.out.println("In day_list_test");

        int Y=0,M=0,D=0;
        Intent getdata=this.getIntent();
        Bundle bd=getdata.getExtras();
        Y=bd.getInt("Year");
        M=bd.getInt("Month");
        D=bd.getInt("Day");
        System.out.println(Y+" "+M+" "+D);


        db=new EventsDao(this);
        ArrayList<Event> data=db.GetOneDayEvents(Y, M, D);
        db.Close();

        System.out.println("close db");
        this.setTitle("Day Event");


        lv=(ListView)findViewById(R.id.show_day_event);
        System.out.println("before array");
        ArrayList<HashMap<String,Object>> day_event=new ArrayList<HashMap<String,Object>>();
        int num_event=25,initialT=25,endT=25;
        if(data.size()>0){
            num_event=0;
            initialT=data.get(num_event).getStartHour();
            endT=data.get(num_event).getEndHour();
        }

        System.out.println("After");
        for(int i=0;i<24;i+=1){
            String event_name="";
            if(num_event>data.size()){
                //do not thing
            }
            else if(endT<i){
                num_event+=1;
                if(num_event<data.size()) {
                    initialT = data.get(num_event).getStartHour();
                    endT = data.get(num_event).getEndHour();
                }
            }
            else if(initialT<=i&&endT>i){
                event_name=data.get(num_event).getName();
            }
            HashMap<String,Object>setData=new HashMap<String,Object>();

            setData.put("time_slot",i);
            setData.put("hour_event",event_name);
            day_event.add(setData);
        }
        SimpleAdapter msimpleAdapter=new SimpleAdapter(this,day_event,R.layout.hour_list,new String[] {"time_slot", "hour_event"},
                new int[] {R.id.time_slot,R.id.hour_event});
        lv.setAdapter(msimpleAdapter);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    public String getDateTime(){
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String strDate = sdFormat.format(date);
//System.out.println(strDate);
        return strDate;
    }
}
