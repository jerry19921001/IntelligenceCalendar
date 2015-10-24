package com.example.home.calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class UserPage extends ActionBarActivity {

    private Spinner lunchStartTime,lunchEndTime,dinnerStartTime,dinnerEndTime,sleepStartTime,sleepEndTime;
    private SharedPreferences pref=null;
    private String[] list={ "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        initializeView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick( View view ){
        switch( view.getId() ){
            case R.id.cancel: {
                Intent RestartMainPage=new Intent( UserPage.this, MainActivity.class );
                startActivity(RestartMainPage);
                UserPage.this.finish();
            }
            break;
            case R.id.OK: {
                int id1=lunchStartTime.getSelectedItemPosition();
                int id2 = lunchEndTime.getSelectedItemPosition();
                int id3=dinnerStartTime.getSelectedItemPosition();
                int id4=dinnerEndTime.getSelectedItemPosition();
                int id5=sleepStartTime.getSelectedItemPosition();
                int id6=sleepEndTime.getSelectedItemPosition();
                RadioGroup StartDayofWeek = (RadioGroup) findViewById(R.id.StartDayOfWeek);
                switch(StartDayofWeek.getCheckedRadioButtonId())
                {
                    case R.id.Sunday:
                        pref.edit().putInt("StartDayOfWeek", 7).commit();
                        break;
                    case R.id.Monday:
                        pref.edit().putInt("StartDayOfWeek", 1).commit();
                        break;
                }
                pref.edit().putInt("lunchStartTimeId",id1).commit();
                pref.edit().putInt("lunchEndTimeId",id2).commit();
                pref.edit().putInt("dinnerStartTimeId",id3).commit();
                pref.edit().putInt("dinnerEndTimeId",id4).commit();
                pref.edit().putInt("sleepStartTime",id5).commit();
                pref.edit().putInt("sleepEndTime",id6).commit();
                Toast t=Toast.makeText(getApplicationContext(),"Your user setting has been saved",Toast.LENGTH_SHORT);
                t.show();
                Intent RestartMainPage=new Intent( UserPage.this, MainActivity.class );
                startActivity(RestartMainPage);
                UserPage.this.finish();
            }
            break;
        }
    }
    private void initializeView(){
        ArrayAdapter<String> listAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list);
        RadioButton sun = (RadioButton) findViewById(R.id.Sunday);
        RadioButton mon = (RadioButton) findViewById(R.id.Monday);
        lunchStartTime=(Spinner)findViewById(R.id.startLunchTime);
        lunchEndTime=(Spinner)findViewById(R.id.endLunchTime);
        dinnerStartTime=(Spinner)findViewById(R.id.startDinnerTime);
        dinnerEndTime=(Spinner)findViewById(R.id.endDinnerTime);
        sleepStartTime=(Spinner)findViewById(R.id.startSleepTime);
        sleepEndTime=(Spinner)findViewById(R.id.wakeUpTime);

        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lunchStartTime.setAdapter(listAdapter);
        lunchEndTime.setAdapter(listAdapter);
        dinnerStartTime.setAdapter(listAdapter);
        dinnerEndTime.setAdapter(listAdapter);
        sleepStartTime.setAdapter(listAdapter);
        sleepEndTime.setAdapter(listAdapter);

        pref=getSharedPreferences("UserSetting",0);//get an object of preference

        lunchStartTime.setSelection(pref.getInt("lunchStartTimeId",0));
        lunchEndTime.setSelection(pref.getInt("lunchEndTimeId",0));
        dinnerStartTime.setSelection(pref.getInt("dinnerStartTimeId",0));
        dinnerEndTime.setSelection(pref.getInt("dinnerEndTimeId",0));
        sleepStartTime.setSelection(pref.getInt("sleepStartTime",0));
        sleepEndTime.setSelection(pref.getInt("sleepEndTime",5));
        if(pref.getInt("StartDayOfWeek", 0) == 7)
        {
            sun.setChecked(true);
        }
        else if(pref.getInt("StartDayOfWeek", 0) == 1)
        {
            mon.setChecked(true);
        }
    }

}
