package com.example.home.calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    private MixEventDAO database=null;
    CaldroidSampleCustomFragment caldroidFragment = new CaldroidSampleCustomFragment();
    Bundle args = new Bundle();
    Date lastday=new Date();
    Calendar cal = Calendar.getInstance();
    Date date_today = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd");
    SharedPreferences settings = null;
    DisplayMetrics metrics = new DisplayMetrics();
    public void shownowevent(View view, Date date)
    {
        Calendar now=Calendar.getInstance();
        now.setTime(date);
        final ArrayList<Event> data=database.getOneDayEvents(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
        String[] allevent;
        if (data.isEmpty())
        {
            allevent = new String[1];
            allevent[0] = "No Event";
        }
        else {
            allevent = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                String fixed_or_elastic;
                if (data.get(i).getDoHours() == 0){
                    fixed_or_elastic = "! ";
                }
                else{
                    fixed_or_elastic = "  ";
                }
                int initialT = data.get(i).getStartHour(), endT = data.get(i).getEndHour();
                String eventname = data.get(i).getName();
                allevent[i] = (initialT < 10 ? "0" : "") + initialT + " : 00 ~ " + (endT < 10 ? "0" : "") + endT + " : 00     " + fixed_or_elastic + eventname;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, allevent);
        ListPopupWindow popup = new ListPopupWindow(this);
        popup.setWidth(metrics.widthPixels-200);
        popup.setAdapter(adapter);
        popup.setAnchorView(view);
        if (data.isEmpty())
        {
            popup.setOnItemClickListener(null);
        }
        else
        {
            popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                    int tt = (int) id;
                    long temp = data.get(tt).getId();
                    Intent modify_event = new Intent(MainActivity.this, modify_event.class);
                    modify_event.putExtra("ID", temp);
                    startActivity(modify_event);
                    MainActivity.this.finish();
                }
            });
        }
        popup.show();
    }
    final CaldroidListener listener = new CaldroidListener() {

        @Override
        public void onSelectDate(Date date, View view) {
            if (lastday != null)
            {
                caldroidFragment.setBackgroundResourceForDate(R.color.white, lastday);
            }
            String s1 = sdf.format(date);
            String s2 = sdf.format(date_today);
            if (s1.compareTo(s2) == 0)
            {
                caldroidFragment.setBackgroundResourceForDate(R.drawable.red_border_new, date_today);
                //caldroidFragment.setBackgroundResourceForDate(R.color.green, date);

            }
            else
            {
                caldroidFragment.setBackgroundResourceForDate(R.drawable.red_border, date_today);
                caldroidFragment.setBackgroundResourceForDate(R.color.green, date);

            }
            lastday=date;
            caldroidFragment.refreshView();
            //shownowevent(date);
            shownowevent(view, date);
        }
        @Override
        public void onLongClickDate(Date date, View view) {
            Intent day_list=new Intent(MainActivity.this,day_list_test.class);
            Calendar now=Calendar.getInstance();
            now.setTime(date);
            Bundle data=new Bundle();
            data.putInt("Year",now.get(Calendar.YEAR));
            data.putInt("Month",now.get(Calendar.MONTH) + 1);
            data.putInt("Day", now.get(Calendar.DAY_OF_MONTH));
            day_list.putExtras(data);
            startActivity(day_list);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //database = new MixEventDAO(this);
        setContentView(R.layout.activity_main);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        settings = getSharedPreferences("UserSetting", 0);
        if (settings.getInt("StartDayOfWeek", 7) == 1)
        {
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        }
        else
        {
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.SUNDAY);
        }
        //args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
        //args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        caldroidFragment.setArguments(args);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        caldroidFragment.setCaldroidListener(listener);
        t.commit();
        //SetNotify();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        database=new MixEventDAO(this);
        database.DeleteAll();
        database.InsertStaticEvents();
        database.Sort();
        //database.Close();
        try {
            SetNotify();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        caldroidFragment.refreshView();
        //shownowevent(lastday);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {//action_settings
//            return true;
//        }
        switch(id) {
            case R.id.List:
                Intent change_to_list=new Intent(MainActivity.this,list_event.class);
                startActivity(change_to_list);
                Toast.makeText(getApplicationContext(),"change mode successfully",Toast.LENGTH_SHORT).show();
                break;
            case R.id.New_Event:
                Intent get_start_elastic = new Intent(MainActivity.this, new_event_elastic.class);
                startActivity(get_start_elastic);
                break;
            case R.id.Sort_Event:
                //database=new MixEventDAO(this);
                database.DeleteAll();
                database.InsertStaticEvents();
                database.Sort();
                //database.Close();
                break;
            case R.id.Today:
                caldroidFragment.moveToDate(cal.getTime());
                if (lastday != null)
                {
                    caldroidFragment.setBackgroundResourceForDate(R.color.white, lastday);
                    caldroidFragment.setBackgroundResourceForDate(R.drawable.red_border, date_today);
                    lastday = null;
                    //shownowevent(date_today);
                    caldroidFragment.refreshView();
                }
                break;
            case R.id.Day_List:
                Intent day_list=new Intent(MainActivity.this, day_list_test.class);
                Calendar now=Calendar.getInstance();
                Bundle data=new Bundle();
                data.putInt("Year",now.get(Calendar.YEAR));
                data.putInt("Month",now.get(Calendar.MONTH) + 1);
                data.putInt("Day", now.get(Calendar.DAY_OF_MONTH));
                day_list.putExtras(data);
                startActivity(day_list);
                break;
            case R.id.TestOption:
                Intent goToTestPage=new Intent(MainActivity.this,modify_event.class);
                startActivity(goToTestPage);
                break;
            case R.id.User:
                Intent goToUserPage=new Intent( MainActivity.this,UserPage.class );
                startActivity(goToUserPage);
                MainActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    void SetNotify() throws ParseException {
        Calendar c=Calendar.getInstance();// get the year ,the month ,and the day
        int year=c.get(Calendar.YEAR);
        int month=c.get( Calendar.MONTH )+1;
        int day=c.get(Calendar.DAY_OF_MONTH);// end

        //database=new MixEventDAO(this);// get events of one day
        ArrayList<Event> data=database.getOneDayEvents(year, month, day);
        //database.Close();// end

        for(int i=0;i<data.size();i=i+1){
            int id=i;
            Event e=data.get(i);
            String time=e.getStartYear()+" "+e.getStartMonth()+" "+e.getStartDay()+" "+e.getStartHour()+"~"
                    +e.getEndYear()+" "+e.getEndMonth()+" "+e.getEndDay()+" "+e.getEndHour();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH");
            int temp=e.getStartHour()-1;
            String start=e.getStartYear()+"/"+e.getStartMonth()+"/"+e.getStartDay()+" "+temp;
            //System.out.println(start);
            Date dt=sdf.parse(start);
            long dttime=dt.getTime();

            NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.icon).setContentTitle(e.getName()).setContentText(time).setWhen( dttime );
            Notification n=builder.build();
            NotificationManager manage=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manage.notify(i,n);
        }
    }
}
