package com.example.home.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Calendar;
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
    Calendar cal = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_event);

        setTitle("10/25~11/1");

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
    private void prepareData(){
        database = new MixEventDAO(this);
        listparent=new ArrayList<String>();
        listChild=new HashMap<String,List<String>>();

        listparent.add("Monday");
        listparent.add("Tuesday");
        listparent.add("Wednesday");
        listparent.add("Thursday");
        listparent.add("Friday");
        listparent.add("Saturday");
        listparent.add("Sunday");
/*
        List<String> first=new ArrayList<String>();
        first.add("第一天事件");
        first.add("事件一");
        first.add("事件二");

        List<String> second= new ArrayList<String>();
        second.add("第二天事件");
        second.add("事件一");
        second.add("事件二");

        List<String> third = new ArrayList<String>();
        third.add("第三天事件");
        third.add("事件一");
        third.add("事件二");

        listChild.put(listparent.get(0), first);
        listChild.put(listparent.get(1), second);
        listChild.put(listparent.get(2),third);
*/
        int weekday=cal.get(Calendar.DAY_OF_WEEK);
        List<String> event=new ArrayList<String>();
        for(int i=0;i<7;i+=1){
            event.clear();
            final ArrayList<Event> data=database.getOneDayEvents(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH)-weekday+1+i);
            if(data.isEmpty()){
                event.add("No Event");
                event.add("");
                event.add("");
            }
            else{
                for(int j=0;j<data.size();j+=1){
                    event.add(data.get(j).getName());
                    event.add(data.get(j).getStartHour()+":00");
                    event.add(data.get(j).getEndHour()+":00");
                }
            }
            listChild.put(listparent.get(i),event);
        }
    }
}
