package com.example.home.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User205 on 2015/4/11.
 */
public class list_event extends Activity {
    private ListView lv;
    EventsDao db=null;
    ArrayList<Event> Events=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_event);
        System.out.println("build db");
        //OneDayEvents=db.GetOneDayEvents()
        db=new EventsDao(this);
        System.out.println("after build db");
        Events=db.AllEvents();
        System.out.println("array size="+Events.size());
        for (int i=0;i<Events.size();i=i+1){
            System.out.println(Events.get(i).getId()+" "+Events.get(i).getName());
        }

        lv=(ListView)findViewById(R.id.ListTest);
        ArrayList<HashMap<String,Object>>list=new ArrayList<HashMap<String,Object>>();
        System.out.println("in for");
        for(int i=0;i<Events.size();i+=1){
            HashMap<String,Object>map=new HashMap<String,Object>();
            map.put("ItemTitle", Events.get(i).getName());
            map.put("ItemText",Events.get(i).getStartYear());
            map.put("ItemText2",Events.get(i).getStartMonth());
            map.put("ItemText3",Events.get(i).getStartDay());
            list.add(map);
        }
        System.out.println("out for");
        SimpleAdapter msimpleAdapter=new SimpleAdapter(this,list,R.layout.item,new String[] {"ItemTitle", "ItemText", "ItemText2", "ItemText3"},
                new int[] {R.id.ItemTitle,R.id.ItemText,R.id.ItemText2,R.id.ItemText3});
        lv.setAdapter(msimpleAdapter);
    }

}
