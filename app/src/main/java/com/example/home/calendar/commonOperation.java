package com.example.home.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by User205 on 2015/7/27.
 */
public abstract class commonOperation extends ActionBarActivity {
    //The following three line need to be connected in function "findView"
    //protected static Calendar startDayInformation,endDayInformation;
    protected static TextView showStartDate,showStartTime,showEndDate,showEndTime;
    protected Button Bstartdate, Benddate, Bcancel;
    protected Spinner startSpinner,endSpinner;
    //The following parameters are for using
    protected static int sYear, sMonth, sDay, sHour, sMinute, eYear, eMonth, eDay, eHour, eMinute;// to store the start date and the end date
    public static int spendtime;// to store the start date and the end date
    String startHour, endHour;

    public abstract void findViews();
    protected static String extendToTwoDigit(int number) {
        if (number>=10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }
    public boolean errorTimeDetection(){
        Calendar startCalendar=Calendar.getInstance(),endCalendar=Calendar.getInstance();
        startCalendar.set(sYear,sMonth,sDay,sHour,sMinute);
        endCalendar.set(eYear,eMonth,eDay,eHour,eMinute);
        if(!startCalendar.before(endCalendar)){
            return false;
        }
        return true;
    }
    protected static void updateDisplay() {

        StringBuilder sDate = new StringBuilder().append(sYear).append("-").append(extendToTwoDigit(sMonth + 1)).append("-").append(extendToTwoDigit(sDay));
        showStartDate.setText(sDate);
        StringBuilder eDate = new StringBuilder().append(eYear).append("-").append(extendToTwoDigit(eMonth+1)).append("-").append(extendToTwoDigit(eDay));
        showEndDate.setText(eDate);
        //StringBuilder sTime = new StringBuilder().append(extendToTwoDigit(sHour)).append(":00");
        //showStartTime.setText("Start Hour");
        //StringBuilder eTime = new StringBuilder().append(extendToTwoDigit(eHour)).append(":00");
        //showEndTime.setText("End Hour");
    }
    public void selectTime(){
        String data[] = new String[24];//set Spinner's data
        for(int i=0;i<24;i+=1){
            data[i]=i+":00";
        }
        ArrayAdapter<String> item=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,data);//new String[]{"1","2","3"}
        item.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(item);
        endSpinner.setAdapter(item);
        startSpinner.setSelection(sHour);
        endSpinner.setSelection(eHour);
        startSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterview, View view, int position, long id) {
                startHour = startSpinner.getSelectedItem().toString().replace(":00", "");
                int original_sHour=sHour;
                sHour = Integer.valueOf(startHour);
                /*if(!errorTimeDetection()){
                    sHour=original_sHour;
                    Toast.makeText(getApplicationContext(),"Start Hour can not be exceed by End Hour",Toast.LENGTH_SHORT).show();
                    startSpinner.setSelection(sHour);
                }*/
                if (eHour <= sHour&&!errorTimeDetection()) {
                    if (sHour == 23)
                    {
                        Calendar temp=Calendar.getInstance();
                        temp.set(eYear,eMonth,eDay);
                        temp.add(Calendar.DAY_OF_YEAR, 1);
                        eYear=temp.get(Calendar.YEAR);
                        eMonth=temp.get(Calendar.MONTH);
                        eDay=temp.get(Calendar.DAY_OF_MONTH);
                        eHour = 0;
                        endSpinner.setSelection(eHour);
                    }
                    else
                    {
                        endSpinner.setSelection(startSpinner.getSelectedItemPosition() + 1);
                    }
                }
                updateDisplay();
            }

            public void onNothingSelected(AdapterView adapterview) {
            }
        });
        endSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterview, View view, int position, long id) {
                // sp.getSelectedItem().toString();make value convert yo String
                /*  what to do after setting
                TextView val;
                val=(TextView)findViewById(R.id.getSelected);
                String tex=sp.getSelectedItem().toString();
                val.setText(tex);*/
                System.out.println(sHour + " " + eHour);
                endHour = endSpinner.getSelectedItem().toString().replace(":00", "");
                int original_eHour=eHour;
                eHour = Integer.valueOf(endHour);
                if(!errorTimeDetection()){
                    eHour=original_eHour;
                    Toast.makeText(getApplicationContext(),"End Hour can not exceed Start Hour",Toast.LENGTH_SHORT).show();
                    endSpinner.setSelection(eHour);
                }

                updateDisplay();
            }

            public void onNothingSelected(AdapterView adapterview) {
            }
        });
    }
    public static class DatePickerFragment1 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, sYear, sMonth, sDay);
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            sYear = year;
            sMonth = month;
            sDay = day;
            int flag = 0;
            if (eYear <= sYear)
            {
                if (eMonth <= sMonth)
                {
                    if (eDay <= sDay)
                    {
                        flag = 1;
                    }
                }
            }
            if (flag==1)
            {
                eYear = year;
                eMonth = month;
                eDay = day;
            }
            updateDisplay();
        }
    }

    public static class DatePickerFragment2 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, eYear, eMonth, eDay);
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            int original_eYear = eYear, original_eMonth = eMonth, original_eDay = eDay;
            eYear = year;
            eMonth = month;
            eDay = day;
            int flag = 0;
            if (eYear <= sYear)
            {
                System.out.println("Year");
                if (eMonth <= sMonth)
                {
                    System.out.println("Month");
                    if (eDay < sDay)
                    {
                        System.out.println("Day");
                        flag = 1;
                    }
                    else if(eDay==sDay){
                        if(eHour<sHour){
                            flag=1;
                        }
                    }
                }
            }
            if (flag==1)
            {
                eYear = original_eYear;
                eMonth = original_eMonth;
                eDay = original_eDay;
            }
            updateDisplay();
        }
    }

}
