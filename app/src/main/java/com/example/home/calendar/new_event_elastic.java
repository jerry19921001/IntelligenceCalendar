package com.example.home.calendar;


import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jerry Lin on 2015/6/13.
 */
public class new_event_elastic extends commonOperation {

    private Toast toast;
    private EventsDao EventDb=null;
    private CheckBox isElastic;
    private EditText time;
    boolean checked=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_elastic);
        findViews();
        final Calendar c = Calendar.getInstance();
        sYear = c.get(Calendar.YEAR);
        sMonth = c.get(Calendar.MONTH);
        sDay = c.get(Calendar.DAY_OF_MONTH);
        sHour = c.get(Calendar.HOUR);
        sMinute = 0;
        eYear = c.get(Calendar.YEAR);
        eMonth = c.get(Calendar.MONTH);
        eDay = c.get(Calendar.DAY_OF_MONTH);
        eHour = c.get(Calendar.HOUR);
        eMinute = 0;
        spendtime = 0;
        EventDb=new EventsDao(this);
        updateDisplay();
        selectTime();
    }
    public void onCheckboxClicked(View view){
        checked=((CheckBox)view).isChecked();
        TextView hideText=(TextView)findViewById(R.id.SpendTime);
        time=(EditText)findViewById(R.id.spend_time);
        switch (view.getId()){
            case R.id.is_elastic:
                if(checked){
                    hideText.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                }
                else{
                    hideText.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                }
                break;
        }
    }
    public void OnClickButton(View Event)
    {
        if(Event.getId()== R.id.back) {//   cancel
            EventDb.Close();
            new_event_elastic.this.finish();
        }
        else if (Event.getId() == R.id.finish) {//  OK
            String event_name;
            Event event;
            EditText name=( EditText )findViewById( R.id.name );// find the id of the edittext
            Editable string_event_name=name.getText();// get the text of the edittext
            event_name=string_event_name.toString();// change the text to string
            if(checked) {
                time = (EditText) findViewById(R.id.spend_time);
                Editable string_spend_time = time.getText();
                spendtime = Integer.parseInt(string_spend_time.toString());
            }

            int start[]={ sYear,sMonth+1,sDay,sHour,sMinute };
            int end[]={ eYear,eMonth+1,eDay,eHour,eMinute };
            event=new Event( event_name,start,end );
            if(checked) {
                event.setDoHours(spendtime);
                event.setIsStatic(0);
            }
            else {
                System.out.println("new_event:" + sYear + " " + sMonth + " " + sDay + " " + sHour);
                int fixDoHours=calculateFixDoHours(start, end);
                event.setDoHours(fixDoHours);//待修改
                event.setIsStatic(1);
            }
            if (EventDb.Insert(event) == -1) {
                toast = Toast.makeText(getApplicationContext(), "Save has been fail", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(getApplication(), event.getName() + " has been saved", Toast.LENGTH_SHORT);
                toast.show();
            }
            EventDb.Close();
            new_event_elastic.this.finish();
        }

    }
    public int calculateFixDoHours(int startDate[],int endDate[]){
        int answer=0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String sD = new String(startDate[0] + "-" + startDate[1] + "-" + startDate[2]),
                    eD = new String(endDate[0] + "-" + endDate[1] + "-" + endDate[2]);
            Date beginDate = format.parse(sD);
            Date finishDate = format.parse(eD);
            int day = (int) (beginDate.getTime() - finishDate.getTime()) / (60 * 60 * 1000);//force "long" to "int"
            answer = day - startDate[3] + endDate[3];
        }
        catch (Exception e){
            System.out.print("Calculate Date Error");
        }
        //System.out.println("DoHours:"+answer);
        return answer;
    }
    public void findViews() {
        showStartDate = (TextView) findViewById(R.id.startdate);
        showEndDate = (TextView) findViewById(R.id.enddate);
        showStartTime = (TextView) findViewById(R.id.starttime);
        showEndTime = (TextView) findViewById(R.id.endtime);
        Bstartdate = (Button) findViewById(R.id.start_date);
        Benddate = (Button) findViewById(R.id.end_date);
        //SelectTime();


        Bstartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment1 datePickerFragment = new DatePickerFragment1();
                FragmentManager fm = getFragmentManager();
                datePickerFragment.show(fm, "datePicker");
            }
        });
        Benddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment2 datePickerFragment = new DatePickerFragment2();
                FragmentManager fm = getFragmentManager();
                datePickerFragment.show(fm, "datePicker");
            }
        });
        startSpinner=(Spinner)findViewById(R.id.start_time);
        endSpinner=(Spinner)findViewById(R.id.end_time);
    }
}
