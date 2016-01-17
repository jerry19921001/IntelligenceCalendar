package com.example.home.calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


/**
 * Created by User205 on 2016/1/12.
 */
public class SchedulePage extends ActionBarActivity {
    private RadioGroup AlgorithmSelection;
    private RadioButton EDD, DelayLeastWork, LeastDelayWorkTime;
    private Button OKButton, CancelButton;
    private int AlgorithmID = 1;
    private SharedPreferences pref = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_page);
        AlgorithmSelection = (RadioGroup) findViewById(R.id.Selection);
        EDD = (RadioButton) findViewById(R.id.radioButton);
        DelayLeastWork = (RadioButton) findViewById(R.id.radioButton2);
        LeastDelayWorkTime = (RadioButton) findViewById(R.id.radioButton3);

        AlgorithmSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedID) {
                String tep = "Algorithm";
                if (checkedID == EDD.getId()) {
                    Toast.makeText(getApplicationContext(), tep, Toast.LENGTH_SHORT).show();
                    AlgorithmID = 1;
                } else if (checkedID == DelayLeastWork.getId()) {
                    Toast.makeText(getApplicationContext(), "DLW", Toast.LENGTH_SHORT).show();
                    AlgorithmID = 2;
                } else if (checkedID == LeastDelayWorkTime.getId()) {
                    Toast.makeText(getApplicationContext(), "LDWT", Toast.LENGTH_SHORT).show();
                    AlgorithmID = 3;
                }
            }
        });

        OKButton = (Button) findViewById(R.id.S_Page_OK);
        CancelButton = (Button) findViewById(R.id.S_Page_Cancel);
    }

    public void GotoNewActivity(View v) {
        if (v.getId() == R.id.S_Page_OK) {
            //Intent intent = new Intent(this, Scheduling.class);
            //intent.putExtra("AlgorithmID", AlgorithmID);
            //startActivity(intent);

            pref = getSharedPreferences("UserSetting", 0);
            pref.edit().putInt("sortAlgorithm_ID", AlgorithmID).commit();

        } else if (v.getId() == R.id.S_Page_Cancel) {
        }
        this.finish();
    }
}
