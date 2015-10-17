package com.example.home.calendar;

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
    protected static void updateDisplay() {

        StringBuilder sDate = new StringBuilder().append(sYear).append("-").append(extendToTwoDigit(sMonth + 1)).append("-").append(extendToTwoDigit(sDay));
        showStartDate.setText(sDate);
        StringBuilder eDate = new StringBuilder().append(eYear).append("-").append(extendToTwoDigit(eMonth+1)).append("-").append(extendToTwoDigit(eDay));
        showEndDate.setText(eDate);
        StringBuilder sTime = new StringBuilder().append(extendToTwoDigit(sHour)).append(":00");
        showStartTime.setText(sTime);
        StringBuilder eTime = new StringBuilder().append(extendToTwoDigit(eHour)).append(":00");
        showEndTime.setText(eTime);
    }
    public void selectTime(){
        String data[] = new String[24];//set Spinner's data
        for(int i=0;i<24;i+=1){
            data[i]=i+"";
        }
        ArrayAdapter<String> item=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,data);//new String[]{"1","2","3"}
        item.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(item);
        endSpinner.setAdapter(item);
        startSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterview, View view, int position, long id) {
                startHour = startSpinner.getSelectedItem().toString();
                sHour = Integer.valueOf(startHour);
                if (eHour <= sHour) {
                    endSpinner.setSelection(startSpinner.getSelectedItemPosition() + 1);
                }
                updateDisplay();
            }

            public void onNothingSelected(AdapterView adapterview) {
                Toast.makeText(commonOperation.this, "XXXXXX", Toast.LENGTH_LONG).show();
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
                endHour = endSpinner.getSelectedItem().toString();
                eHour = Integer.valueOf(endHour);
                updateDisplay();
            }

            public void onNothingSelected(AdapterView adapterview) {
                Toast.makeText(commonOperation.this, "XXXX", Toast.LENGTH_LONG).show();

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
            int flag;
            if (eYear < sYear) flag=1;
            else if (eMonth < sMonth) flag=1;
            else if (eDay < sDay) flag=1;
            else flag=0;
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
            eYear = year;
            eMonth = month;
            eDay = day;
            updateDisplay();
        }
    }

}
