package com.example.home.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User205 on 2015/4/11.
 */
public class list_event extends ActionBarActivity {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listparent;
    private HashMap<String,List<String>> listChild;
    private MixEventDAO database;
    Calendar cal = Calendar.getInstance(),FirstDateOfWeek=Calendar.getInstance();
    private int sMonth=0,sDay=0,eMonth=0,eDay=0;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_event);

        DataInitial();

        setTitle(sMonth + "/" + sDay + "~" + eMonth + "/" + eDay);
        prepareData();

        expListView=(ExpandableListView)findViewById(R.id.ExpandListView);

        listAdapter = new CusExpandableListAdapter(this,listparent,listChild);

        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                /*int tt = (int) id;
                long temp = data.get(tt).getId();
                Intent modify_event = new Intent(list_event.this, modify_event.class);
                modify_event.putExtra("ID", temp);
                startActivity(modify_event);*/
                return false;
            }
        });

    }
    private void DataInitial(){
        int dayOfWeek=cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_MONTH, -dayOfWeek + 1 + 1);//加原本的1 加上因為起始暫定為星期一  所以多加1
        FirstDateOfWeek.setTime(cal.getTime());
        sMonth=cal.get(Calendar.MONTH)+1;
        sDay=cal.get(Calendar.DAY_OF_MONTH);
        cal.add(Calendar.DAY_OF_MONTH, 6);
        eMonth=cal.get(Calendar.MONTH)+1;
        eDay=cal.get(Calendar.DAY_OF_MONTH);
    }
    private void prepareData() {
        database = new MixEventDAO(this);
        listparent = new ArrayList<String>();
        listChild = new HashMap<String, List<String>>();

        listparent.add("Monday");
        listparent.add("Tuesday");
        listparent.add("Wednesday");
        listparent.add("Thursday");
        listparent.add("Friday");
        listparent.add("Saturday");
        listparent.add("Sunday");

        ArrayList<Event> data;
        for (int i = 0; i < 7; i += 1) {
            List<String> event = new ArrayList<String>();
            data = database.getOneDayEvents(FirstDateOfWeek.get(Calendar.YEAR), FirstDateOfWeek.get(Calendar.MONTH) + 1, FirstDateOfWeek.get(Calendar.DAY_OF_MONTH));
            if (data.isEmpty()) {
                event.add("No Event");
                event.add("0");
                event.add("0");
            } else {
                for (int j = 0; j < data.size(); j += 1) {
                    event.add(data.get(j).getName());
                    event.add(data.get(j).getStartHour() + ":00");
                    event.add(data.get(j).getEndHour() + ":00");
                }
            }
            listChild.put(listparent.get(i), event);
            FirstDateOfWeek.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}
