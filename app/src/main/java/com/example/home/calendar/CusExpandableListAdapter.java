package com.example.home.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User205 on 2015/11/1.
 */
public class CusExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> parent_title;
    private HashMap<String,List<String>> child_content;
    public Calendar startDay;

    public CusExpandableListAdapter(Context context, List<String> title, HashMap<String, List<String>> content) {
        this.context = context;
        this.parent_title = title;
        this.child_content = content;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.child_content.get(this.parent_title.get(groupPosition)).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.child_content.get(this.parent_title.get(groupPosition)).size()/3;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText =(String) getChild(groupPosition,childPosition);
        //設置內容layout
        if(convertView == null){
            LayoutInflater infalInflater = (LayoutInflater)this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_layout,null);
        }

        //設置內容
        TextView txtListChildName=(TextView)convertView.findViewById(R.id.ItemName),
                txtListChildTime=(TextView)convertView.findViewById(R.id.ItemTime);
        txtListChildName.setText(child_content.get(parent_title.get(groupPosition)).get(3*childPosition));
        txtListChildTime.setText(child_content.get(parent_title.get(groupPosition)).get(3*childPosition+1)+"~"+
        child_content.get(parent_title.get(groupPosition)).get(3*childPosition+2));
        return convertView;
    }

    @Override
    public int getGroupCount() {
        return this.parent_title.size();
    }

    @Override
    public Object getGroup(int parentPosition) {
        return this.parent_title.get(parentPosition);
    }

    @Override
    public long getGroupId(int parentPosition) {
        return parentPosition;
    }

    private boolean childIsEmpty(int size){
        if(size==3)return true;
        else return false;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater infalInflater = (LayoutInflater)this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent_layout,null);
        }
        TextView Day=(TextView)convertView.findViewById(R.id.Day),
                Event1=(TextView)convertView.findViewById(R.id.Event1),
                Event2=(TextView)convertView.findViewById(R.id.Event2),
                Event3=(TextView)convertView.findViewById(R.id.Event3),
                Event4=(TextView)convertView.findViewById(R.id.Event4);
        Day.setText(parent_title.get(groupPosition));

        List<String> temp=new ArrayList<String>();
        temp=child_content.get(parent_title.get(groupPosition));
        int childSize=child_content.get(parent_title.get(groupPosition)).size();
        Event1.setText(temp.get(0));
        if(childIsEmpty(childSize)) {
            Event2.setText("");
            Event3.setText("");
            Event4.setText("");
        }
        else if(childSize>=6){
            Event2.setText(temp.get(3));
            if(childSize>=9){
                Event3.setText(temp.get(6));
                if(childSize>=12){
                    Event4.setText(temp.get(9));
                }
            }
        }


        return convertView;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
