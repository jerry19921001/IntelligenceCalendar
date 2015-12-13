package com.example.home.calendar;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by User205 on 2015/7/23.
 */
public class modify_event extends commonOperation {
    private EventsDao task;
    private MixEventDAO subTask;
    private long eventID;
    private Event modifyEvent,temp;
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
        temp=new Event();
        modifyEvent = new Event();
        temp = subTask.getOneEvent(eventID);
        modifyEvent=task.getOneEvent(temp.getPreviousId());

        //System.out.println("in modify_event");
        modifyEvent.print();

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
        //showStartTime = (TextView) findViewById(R.id.ShowStartTime);
        //showEndTime = (TextView) findViewById(R.id.ShowEndTime);
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

    //@Override
    /*public void onBackPressed() {
        super.onBackPressed();
        Intent RestartMainPage=new Intent( modify_event.this, MainActivity.class );
        startActivity(RestartMainPage);
        modify_event.this.finish();
    }*/
    public boolean doHourErrorDetection(){
        if(spendtime==-1||spendtime==0)return true;
        Calendar sDate=Calendar.getInstance(),eDate=Calendar.getInstance();
        sDate.set(sYear,sMonth,sDay,sHour,sMinute);
        eDate.set(eYear,eMonth,eDay,eHour,eMinute);
        long sDateTime=sDate.getTimeInMillis();
        long eDateTime=eDate.getTimeInMillis();
        long differ=(eDateTime-sDateTime)/(1000*60*60);//相差小時數
        //System.out.println("total Time:"+differ);
        if(differ>spendtime){
            return false;
        }
        else{
            return true;
        }
    }
    public void modifyEvent(View decision) {
        switch (decision.getId()) {
            case R.id.Delete_Button:
                task.Delete(modifyEvent.getId());
                subTask.Delete(modifyEvent.getId());
                modify_event.this.finish();
                break;
            case R.id.Cancel_Button:
                modify_event.this.finish();
                break;
            case R.id.Modify_Button:
                EditText name = (EditText) findViewById(R.id.InputName);
                String new_name=name.getText().toString();
                boolean AllSpaceName=true;
                for(int i=0;i<new_name.length();i+=1){
                    if(new_name.charAt(i)==' '){
                        //continue checking
                    }
                    else{
                        AllSpaceName=false;
                        break;
                    }
                }
                if(!AllSpaceName){
                    int start[] = {sYear, sMonth + 1, sDay, sHour, sMinute};
                    int end[] = {eYear, eMonth + 1, eDay, eHour, eMinute};
                    modifyEvent.setAll(temp.getPreviousId(), name.getText().toString(), start, end);
                    EditText setDoHour=(EditText)findViewById(R.id.spend_time);
                    if(!isStatic){
                        if(setDoHour.getText().toString().isEmpty()){
                            spendtime=-1;
                        }
                        else{
                            spendtime=Integer.parseInt(setDoHour.getText().toString());
                        }
                        modifyEvent.setDoHours(spendtime);
                    }
                    if(!doHourErrorDetection() || isStatic ){
                        //Intent RestartMainPage=new Intent( modify_event.this, MainActivity.class );
                        //startActivity(RestartMainPage);
                        task.Update(modifyEvent,different());
                        subTask.Update(modifyEvent);
                        modify_event.this.finish();
                    }
                    else{
                        Toast.makeText(this,"Spend time Error!!!",Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(this,"Name space cann't be empty!!!",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }


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
    public void changeDate(View event){
        if(event.getId()==R.id.ShowStartDate){
            Bstartdate.callOnClick();
        }
        else if(event.getId()==R.id.ShowEndDate){
            Benddate.callOnClick();
        }
    }
}

