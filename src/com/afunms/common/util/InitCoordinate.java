/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.util.Calendar;

import org.jfree.data.time.Minute;
public class InitCoordinate {
	
	private Calendar currenttime;
	private Calendar lasttime;
	private int pollminute;
	private int scale;//坐标刻度大小
	private int count;//坐标刻度个数
	private Minute[] minutes;
	private DateE datemanager=new DateE();
	private int alltime;//显示的总时间（以分钟为单位）
	private int allhour;
	private int allday;
    public InitCoordinate(Calendar currenttime,int allhour,int polltime){
		
    	this.allhour=allhour;
    	this.alltime=allhour*60;
    	this.currenttime=currenttime;
    	this.pollminute=polltime;
    	computeScale();
    	Inittime();
		count=alltime/scale;
    	//if(alltime%scale!=0)count++;//防止溢出
    	InitCoor();
    	//System.out.println("currenttime="+currenttime.get(Calendar.HOUR_OF_DAY)+"   "+currenttime.get(Calendar.MINUTE));
		//System.out.println("lasttime="+lasttime.get(Calendar.HOUR_OF_DAY)+"   "+lasttime.get(Calendar.MINUTE));
    	
    }

    public void InitCoordinateDay(Calendar currenttime,int allday,int polltime){
		
    	this.allday=allday;
    	this.alltime=allhour*24*60;
    	this.currenttime=currenttime;
    	this.pollminute=polltime;
    	computeScale();
    	InittimeDay();
		count=alltime/scale;
    	//if(alltime%scale!=0)count++;//防止溢出
    	InitCoor();
    	//System.out.println("currenttime="+currenttime.get(Calendar.HOUR_OF_DAY)+"   "+currenttime.get(Calendar.MINUTE));
		//System.out.println("lasttime="+lasttime.get(Calendar.HOUR_OF_DAY)+"   "+lasttime.get(Calendar.MINUTE));
    	
    }
    
    public int ComputeLocation(Calendar cal){
    	int interval=0;
    	long currenttime=cal.getTimeInMillis()/1000;
		lasttime.set(Calendar.MINUTE,lasttime.get(Calendar.MINUTE)/scale*scale);
		long intlasttime=lasttime.getTimeInMillis()/1000;
		
		long betweenMinute=(currenttime-intlasttime)/60;
		interval=Integer.parseInt(Long.toString(betweenMinute));
    	
        interval=interval/scale;
		interval=interval-1;
		//System.out.println("--------interval ="+interval);
        return interval;//从零开始算
    }
    
    public void Inittime(){
    	lasttime=(Calendar)currenttime.clone();
    	lasttime.add(Calendar.HOUR,-allhour);
    	lasttime.set(Calendar.SECOND,0);
		currenttime.set(Calendar.SECOND,0);
		//int i=lasttime.get(Calendar.MINUTE)%scale;
    	//if(i!=0){lasttime.set(Calendar.MINUTE,(lasttime.get(Calendar.MINUTE)/scale)*scale);}
       // currenttime=((Calendar)lasttime.clone());
        //currenttime.add(Calendar.HOUR_OF_DAY,allhour);
        //currenttime.add(Calendar.MINUTE,-1);
        
    }

    public void InittimeDay(){
    	lasttime=(Calendar)currenttime.clone();
    	lasttime.add(Calendar.DAY_OF_MONTH,-allday);
    	lasttime.set(Calendar.SECOND,0);
		currenttime.set(Calendar.SECOND,0);
		//int i=lasttime.get(Calendar.MINUTE)%scale;
    	//if(i!=0){lasttime.set(Calendar.MINUTE,(lasttime.get(Calendar.MINUTE)/scale)*scale);}
       // currenttime=((Calendar)lasttime.clone());
        //currenttime.add(Calendar.HOUR_OF_DAY,allhour);
        //currenttime.add(Calendar.MINUTE,-1);
        
    }

    public String getNow(){
    	return datemanager.getDateDetail(currenttime);
    }
    
    public String getBefore(){
    	return datemanager.getDateDetail(lasttime);
    }
    
    public void InitCoor(){
    minutes=new Minute[count];	
    Calendar temp=(Calendar)lasttime.clone();
    for(int i=0;i<count;i++){
		
		temp.add(Calendar.MINUTE,scale);
    	minutes[i]=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));	
		//System.out..out.println("--------in initcoordinate--------i"+i+" ----- "+datemanager.getDateDetail(temp));
    }
    }
    
    public void computeScale(){
    	if(0<pollminute&&pollminute<=10)scale=10;
    	else if(10<pollminute&&pollminute<=20)scale=20;
    	else if(20<pollminute&&pollminute<=30)scale=30;
    	else if(30<pollminute&&pollminute<=60)scale=60;
    	else {
    	if(pollminute%60!=0)scale=(pollminute/60+1)*60;
    	else scale=pollminute;
    	}
    }
    
	public int getScale() {
		return scale;
	}

	public int getCount() {
		return count;
	}

	public Minute[] getMinutes() {
		return minutes;
	}

}
