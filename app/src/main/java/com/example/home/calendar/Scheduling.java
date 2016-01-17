package com.example.home.calendar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

/**
 * Created by User205 on 2016/1/12.
 */
public class Scheduling extends ActionBarActivity {
    MixEventDAO db=null;
    EventsDao edb=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduling);

        Bundle bd=getIntent().getExtras();
        db=new MixEventDAO(this);
        edb=new EventsDao(this);
        int algorithm_ID=bd.getInt("AlgorithmID");
        if(algorithm_ID==1){//edd
            db.Sort();
        }
        else if(algorithm_ID==2){//The least delay work
            ArrayList<Event> temp=db.Schedule( edb.AllEvents() );
            db.InsertFromArrayList( temp );
        }
        else if(algorithm_ID==3){// others
            ArrayList<Event> temp=db.ScheduleByYunJa( edb.AllEvents() );
            db.InsertFromArrayList( temp );
        }
    }
}
