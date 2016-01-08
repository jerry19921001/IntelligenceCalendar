package com.example.home.calendar;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by User205 on 2015/12/27.
 */
public class Schedule_algorithm {
    public ArrayList<Event> Schedule(ArrayList<Event> OriginData){
        ArrayList<Event> Schedule_Data=new ArrayList<Event>(),WaitForSchedule=new ArrayList<Event>();
        //先把固定的事件拿出來 OriginData會只剩下排程事件
        for(int i=0;i<OriginData.size();i+=1){
            if(OriginData.get(i).isStatic()){
                Schedule_Data.add(OriginData.get(i));
            }
            else{
                WaitForSchedule.add(OriginData.get(i));
            }
        }
        if(WaitForSchedule.size()==0){
            return OriginData;
        }
        //找到排程事件中最早開始的時間點
        Calendar Time=Calendar.getInstance(),CompareTime=Calendar.getInstance();
        Event a=WaitForSchedule.get(0);
        Time.set(a.getStartYear(), a.getStartMonth(), a.getStartDay(), a.getStartHour(), a.getStartMinute());
        for(int i=1;i<WaitForSchedule.size();i+=1){
            CompareTime.set(a.getStartYear(),a.getStartMonth(),a.getStartDay(),a.getStartHour(),a.getStartMinute());
            if(!Time.before(CompareTime)){
                Time.set(CompareTime.get(Calendar.YEAR),CompareTime.get(Calendar.MONTH),CompareTime.get(Calendar.DAY_OF_YEAR));
            }
        }
        //開始排程
        while(!AllEventFinished(WaitForSchedule)){
            boolean Hour[]=new boolean[24];
            for(int i=0;i<24;i+=1){//找一天中有空的時間 0~6不排
                if(i<6){
                    Hour[i]=false;
                }
                else{
                    //這邊Call判斷的函式 填入固定事件
                    Hour[i]=true;
                }
            }
            for(int i=6;i<24;i+=1){//最早從六點開始排
                if(Hour[i]==true){
                    Event Temp=new Event();
                    int ChosenPriorityId=-1,TempDoHour=1;
                    for(int index=0;index<WaitForSchedule.size();index+=1){//找優先度最高的事件 同時是可以做的 紀錄優先度最高事件的ID
                        Time.set(Calendar.HOUR,i);
                        Calendar StartTime=Calendar.getInstance(),EndTime=Calendar.getInstance();
                        StartTime.clear();EndTime.clear();
                        StartTime.set(WaitForSchedule.get(index).getStartYear(), WaitForSchedule.get(index).getStartMonth(), WaitForSchedule.get(index).getStartDay(), WaitForSchedule.get(index).getStartHour(), WaitForSchedule.get(index).getStartMinute());
                        EndTime.set(WaitForSchedule.get(index).getEndYear(),WaitForSchedule.get(index).getEndMonth(),WaitForSchedule.get(index).getEndDay(),WaitForSchedule.get(index).getEndHour(),WaitForSchedule.get(index).getEndMinute());
                        if(StartTime.before(Time)){// 只判斷可做的時間到了沒     還沒判斷是不是超過結束時間 &&EndTime.after(Time)
                            if(ChosenPriorityId==-1||Temp.getDoHours()>WaitForSchedule.get(index).getDoHours()){//這邊判斷優先度
                                if(WaitForSchedule.get(index).getDoHours()!=0){
                                    ChosenPriorityId=index;
                                    Temp.setDoHours(WaitForSchedule.get(index).getDoHours());
                                    Temp.setPreviousId(WaitForSchedule.get(index).getId());
                                    Temp.setName(WaitForSchedule.get(index).getName());
                                }
                            }
                        }
                    }
                    if(ChosenPriorityId==-1)continue;//如果沒有在這時間開始的事件或能做的事件時間都為0
                    Temp.setDoHours(TempDoHour);//做一個新的事件 將做的時間先設成1
                    int start[]={Time.get(Calendar.YEAR),Time.get(Calendar.MONTH),Time.get(Calendar.DAY_OF_MONTH),i,0};
                    int end[]={Time.get(Calendar.YEAR),Time.get(Calendar.MONTH),Time.get(Calendar.DAY_OF_MONTH),i+1,0};
                    Temp.setStartDate(start);
                    //下面再從接下來的時間繼續找 (同時還要確認是同一事件 如果有其他優先度比較高的出現 //目前不會出現) 就先跳出 先把事件加入ArrayList
                    int j;
                    for(j=i+1;j<24;j+=1){
                        if(WaitForSchedule.get(ChosenPriorityId).getDoHours()<=0){//doHour用完
                            Temp.setDoHours(TempDoHour);
                            end[3]=j;
                            Temp.setEndDate(end);
                            Schedule_Data.add(Temp);
                            break;
                        }
                        if (Hour[j] == true) {
                            TempDoHour+=1;
                            WaitForSchedule.get(ChosenPriorityId).setDoHours(WaitForSchedule.get(ChosenPriorityId).getDoHours()-1);//將doHour-1

                        }
                        else{//沒有連續的時間可以排  所以先加入ArrayList
                            Temp.setDoHours(TempDoHour);
                            end[3]=j;
                            Temp.setEndDate(end);
                            Schedule_Data.add(Temp);
                            break;
                        }
                    }
                    if(j==23){//到晚上的排程都是OK的 要新增到ArrayList
                        Temp.setDoHours(TempDoHour);
                        end[3]=j;//設定結束時間的小時數
                        Temp.setEndDate(end);
                        Schedule_Data.add(Temp);

                    }
                    else{//時間還沒做完(還沒到晚上23點) 繼續接下去做
                        i=j;
                    }
                }
            }
            Time.add(Calendar.DAY_OF_YEAR,1);//一天做完後加一天
        }
        return Schedule_Data;
    }
    boolean AllEventFinished(ArrayList<Event> a){
        for(int i=0;i<a.size();i+=1){
            if(a.get(i).getDoHours()!=0){
                return false;
            }
        }
        return true;
    }
    public ArrayList<Event> RandomEvent(){
        ArrayList<Event> temp=new ArrayList<Event>();
        Event a=new Event(),b=new Event(),c=new Event();
        a.setStartDate(2016, 1, 1, 10, 0);
        a.setEndDate(2016, 1, 1, 15, 0);
        a.setIsStatic(1);temp.add(a);

        b.setStartDate(2016, 1, 1, 18, 0);
        b.setEndDate(2016, 1, 1, 20, 0);
        b.setIsStatic(1);temp.add(b);

        c.setStartDate(2016, 1, 1, 10, 0);
        c.setEndDate(2016, 1, 4, 20, 0);
        c.setDoHours(10);
        c.setIsStatic(0);
        temp.add(c);
        return temp;
    }
    public static void main(String args[]){
        Schedule_algorithm a=new Schedule_algorithm();
        ArrayList<Event> tep=a.RandomEvent(),ans;
        ans=a.Schedule(tep);
        for (int i=0;i<ans.size();i+=1){
            ans.get(i).print();
        }
    }
}
