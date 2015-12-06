package com.example.home.calendar;

/**
 * Created by user on 2015/6/13.
 */
public class Event {
    private static int dateNumber=5;
    // the information of the event
    private long id=0;
    private String eventName;
    private int startDate[]=new int [ dateNumber ];//year month day hour minute
    private int endDate[]=new int [ dateNumber ];//year month day hour minute
    private int doHours=0;
    private long previousId=0;
    private boolean isStatic=false;
    private int blocks=0;
    private int no=0;
    // the construction of the Event class
    public Event(){}
    public Event( String name,int start[],int end[] ){
        this.eventName=name;
        for(int i=0;i<dateNumber;i=i+1){
            startDate[i]=start[i];
        }
        for(int i=0;i<dateNumber;i=i+1){
            endDate[i]=end[i];
        }
    }
    // the function of setting the event
    public void setId( long id ){
        this.id=id;
    }
    public void setName( String name ){
        this.eventName=name;
    }
    public void setAll( long id,String name,int start[],int end[] ){
        this.id=id;
        this.eventName=name;
        for(int i=0;i<dateNumber;i=i+1){
            startDate[i]=start[i];
        }
        for(int i=0;i<dateNumber;i=i+1){
            endDate[i]=end[i];
        }
    }
    public void setStartDate( int start[] ){
        for(int i=0;i<dateNumber;i=i+1){
            startDate[i]=start[i];
        }
    }
    public void setEndDate( int end[] ){
        for(int i=0;i<dateNumber;i=i+1){
            endDate[i]=end[i];
        }
    }
    public void setDoHours( int doHours ){
        this.doHours=doHours;
    }
    public void setPreviousId( long previousId ){
        this.previousId=previousId;
    }
    public void setIsStatic( int i ){
        if( i==1 ) this.isStatic=true;
        else this.isStatic=false;
    }
    public void setBlocks( int blocks ){
        this.blocks=blocks;
    }
    public void setNo( int no ){
        this.no=no;
    }
    // the function of getting the information of the event
    public long getId(){
        return this.id;
    }
    public String getName(){
        return this.eventName;
    }
    public int getStartYear(){
        return startDate[0];
    }
    public int getStartMonth(){
        return startDate[1];
    }
    public int getStartDay(){
        return startDate[2];
    }
    public int getStartHour(){
        return startDate[3];
    }
    public int getStartMinute(){
        return startDate[4];
    }
    public int getEndYear(){
        return endDate[0];
    }
    public int getEndMonth(){
        return endDate[1];
    }
    public int getEndDay(){
        return endDate[2];
    }
    public int getEndHour(){
        return endDate[3];
    }
    public int getEndMinute(){
        return endDate[4];
    }
    public int getDoHours(){
        return this.doHours;
    }
    public long getPreviousId(){
        return this.previousId;
    }
    public int getBlocks(){
        return this.blocks;
    }
    public int getNo(){
        return this.no;
    }
    // get a boolean value to make sure is static or not
    public boolean isStatic(){
        if( this.isStatic ) return true;
        else return false;
    }
    // get a boolean value to make sure that the event crosses day
    public boolean isCrossDay(){
        if( this.startDate[0]!=this.endDate[0] || this.startDate[1]!=this.endDate[1] || this.startDate[2]!=this.endDate[2] ){
            return true;
        }
        return false;
    }
    // the function to print the all information of the event
    public void print(){
        System.out.println("id=" + id);
        System.out.println(eventName);
        System.out.print("start day : ");
        for(int i=0;i<dateNumber;i=i+1){
            System.out.print(startDate[i]+" ");
        }
        System.out.println();
        System.out.print("end day : ");
        for(int i=0;i<dateNumber;i=i+1){
            System.out.print(endDate[i]+" ");
        }
        System.out.println();
        System.out.println("dohour=" + doHours);
        System.out.println("preid=" + previousId);
        System.out.print("isStatic=");
        if( isStatic ) System.out.println("true");
        else System.out.println("false");
        System.out.println();
    }
}
