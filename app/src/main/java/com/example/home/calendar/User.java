package com.example.home.calendar;

/**
 * Created by user on 2015/9/3.
 */
public class User {
    // i=0 : start , i=1 : end
    private int sleepTime[]=new int[2];
    private int lunchTime[]=new int[2];
    private int dinnerTime[]=new int[2];
    // i=0 : start , i=1 : end
    public void setSleepTime( int sleepTime[] ){
        for(int i=0;i<2;i=i+1){
            this.sleepTime[i]=sleepTime[i];
        }
    }
    public void setLunchTime( int time[] ){
        for(int i=0;i<2;i=i+1){
            this.lunchTime[i]=time[i];
        }
    }
    public void setDinnerTime( int time[] ){
        for(int i=0;i<2;i=i+1){
            this.dinnerTime[i]=time[i];
        }
    }
    // get an array , i=0 : start , i=1 : end
    public int[] getSleepTime(){
        return this.sleepTime;
    }
    public int[] getLunchTime(){
        return this.lunchTime;
    }
    public int[] getDinnerTime(){
        return this.dinnerTime;
    }
}
