package com.example.home.calendar;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by User205 on 2015/7/23.
 */
public class modify_event extends commonOperation {
    private EventsDao task;
    private MixEventDAO subTask;
    private long eventID;
    private Event modifyEvent;
    private boolean isStatic=false;
    private int ori_sYear, ori_sMonth, ori_sDay, ori_sHour, ori_sMinute, ori_eYear, ori_eMonth, ori_eDay, ori_eHour, ori_eMinute;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.modify_event);
        findViews();

        eventID = getIntent().getLongExtra("ID", 100);
        task= new EventsDao(this);
        subTask =new MixEventDAO(this);
        modifyEvent = new Event();
        modifyEvent = subTask.getOneEvent(eventID);

        isStatic=modifyEvent.isStatic();
        //The following two View just in modify_event.layout.
        EditText e_spendTime=(EditText)findViewById(R.id.spend_time);
        TextView t_spendTime=(TextView)findViewById(R.id.SpendTime);
        if(!isStatic) {
            spendtime = modifyEvent.getDoHours();
            e_spendTime.setText(Integer.toString(spendtime));
        }
        else{
            e_spendTime.setVisibility(View.GONE);
            t_spendTime.setVisibility(View.GONE);
        }

///////////////////////////////////
        EditText eventName = (EditText) findViewById(R.id.InputName);
        eventName.setText(modifyEvent.getName());
        ori_sYear=sYear = modifyEvent.getStartYear();
        ori_sMonth=sMonth = modifyEvent.getStartMonth() - 1;
        ori_sDay=sDay = modifyEvent.getStartDay();
        ori_sHour=sHour = modifyEvent.getStartHour();
        ori_sMinute=sMinute = modifyEvent.getStartMinute();
        ori_eYear=eYear = modifyEvent.getEndYear();
        ori_eMonth=eMonth = modifyEvent.getEndMonth() - 1;
        ori_eDay=eDay = modifyEvent.getEndDay();
        ori_eHour=eHour = modifyEvent.getEndHour();
        ori_eMinute=eMinute = modifyEvent.getEndMinute();

        updateDisplay();
        selectTime();
        startSpinner.setSelection(sHour);
        endSpinner.setSelection(eHour);
    }

    @Override
    public void findViews() {
        showStartDate = (TextView) findViewById(R.id.ShowStartDate);
        showEndDate = (TextView) findViewById(R.id.ShowEndDate);
        showStartTime = (TextView) findViewById(R.id.ShowStartTime);
        showEndTime = (TextView) findViewById(R.id.ShowEndTime);
        Bstartdate = (Button) findViewById(R.id.GetStartDate);
        Benddate = (Button) findViewById(R.id.GetEndDate);
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

        startSpinner = (Spinner) findViewById(R.id.GetStartTime);
        endSpinner = (Spinner) findViewById(R.id.GetEndTime);
    }

    public void modifyEvent(View decision) {
        EditText name = (EditText) findViewById(R.id.InputName);
        int start[] = {sYear, sMonth + 1, sDay, sHour, sMinute};
        int end[] = {eYear, eMonth + 1, eDay, eHour, eMinute};
        modifyEvent.setAll(eventID, name.getText().toString(), start, end);
        if(!isStatic){
            EditText setDoHour=(EditText)findViewById(R.id.spend_time);
            spendtime=Integer.parseInt(setDoHour.getText().toString());
            modifyEvent.setDoHours(spendtime);
        }

        switch (decision.getId()) {
            case R.id.Modify_Button:
                task.Update(modifyEvent,different());
                subTask.Update(modifyEvent);
                break;
            case R.id.Delete_Button:
                task.Delete(modifyEvent.getPreviousId());
                subTask.Delete(modifyEvent.getId());
                break;
            default://Cancel_Button
                break;
        }
        task.Close();
        subTask.Close();
        modify_event.this.finish();
    }
    private int different(){
        int result=0;
        Calendar cal =Calendar.getInstance();
        cal.set(ori_sYear,ori_sMonth,ori_sDay,ori_sHour,ori_sMinute);
        Date t1=cal.getTime();
        cal.set(ori_eYear, ori_eMonth, ori_eDay, ori_eHour, ori_eMinute);
        Date t2=cal.getTime();
        long result1=t1.getTime()-t2.getTime();
        cal.set(sYear,sMonth,sDay,sHour,sMinute);
        t1=cal.getTime();
        cal.set(eYear,eMonth,eDay,eHour,eMinute);
        t2=cal.getTime();
        long result2=t1.getTime()-t2.getTime();
        result=(int)((result1-result2)/(1000*3600));
        return result;

    }
}

