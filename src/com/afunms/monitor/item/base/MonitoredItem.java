/**
 * <p>Description:被监视指标</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.item.base;

import java.util.List;

import com.afunms.topology.model.NodeMonitor;
import com.afunms.monitor.executor.base.MonitorFactory;

public abstract class MonitoredItem 
{
	protected int resultType; 
	protected double singleResult = -1;
	protected List multiResults;    		
    protected String moid;
    protected int threshold;
    protected String unit;
    protected int compare;
    protected int compareType;    
    protected int upperTimes;
    protected int violateTimes;    
    protected String alarmInfo;        
    protected int alarmLevel;
    protected boolean enabled;
    protected int interval;    
    protected long lastTime;
    protected long nextTime;          
    protected boolean alarm;
	private String nodetype;
	private String subentity;
	private int limenvalue0;
	private int limenvalue1;
	private int limenvalue2;
	private int time0;
	private int time1;
	private int time2;
	private int sms0;
	private int sms1;
	private int sms2;
	
	public String getNodetype() {
		return nodetype;
	}
	
	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}
	
	public String getSubentity() {
		return subentity;
	}
	
	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}
	
	public int getLimenvalue0() {
		return limenvalue0;
	}
	
	public void setLimenvalue0(int limenvalue0) {
		this.limenvalue0 = limenvalue0;
	}
	
	public int getLimenvalue1() {
		return limenvalue1;
	}
	
	public void setLimenvalue1(int limenvalue1) {
		this.limenvalue1 = limenvalue1;
	}
	
	public int getLimenvalue2() {
		return limenvalue2;
	}
	
	public void setLimenvalue2(int limenvalue2) {
		this.limenvalue2 = limenvalue2;
	}	
	
	public int getTime0() {
		return time0;
	}
	
	public void setTime0(int time0) {
		this.time0 = time0;
	}	
	
	public int getTime1() {
		return time1;
	}
	
	public void setTime1(int time1) {
		this.time1 = time1;
	}
	
	public int getTime2() {
		return time2;
	}
	
	public void setTime2(int time2) {
		this.time2 = time2;
	}
	
	public int getSms0() {
		return sms0;
	}
	
	public void setSms0(int sms0) {
		this.sms0 = sms0;
	}
	
	public int getSms1() {
		return sms1;
	}
	
	public void setSms1(int sms1) {
		this.sms1 = sms1;
	}
	public int getSms2() {
		return sms2;
	}
	
	public void setSms2(int sms2) {
		this.sms2 = sms2;
	}
    
	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

	public MonitoredItem()
    {  
    	lastTime = 0;
    	nextTime = 0;
    }
    
	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) 
	{
		this.lastTime = lastTime;
		this.nextTime = lastTime + interval;
	}

	public long getNextTime() {
		return nextTime;
	}

	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}

	public String getAlarmInfo() {
		return alarmInfo;
	}

	public void setAlarmInfo(String alarmInfo) {
		this.alarmInfo = alarmInfo;
	}

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public int getCompare() {
		return compare;
	}

	public void setCompare(int compare) {
		this.compare = compare;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getMoid() {
		return moid;
	}

	public void setMoid(String moid) {
		this.moid = moid;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public int getUpperTimes() {
		return upperTimes;
	}

	public void setUpperTimes(int upperTimes) {
		this.upperTimes = upperTimes;
	}

	public List getMultiResults() {
		return multiResults;
	}

	public void setMultiResults(List multiResults) {
		this.multiResults = multiResults;
	}

	public int getResultType() {
		return resultType;
	}

	public void setResultType(int resultType) {
		this.resultType = resultType;
	}

	public double getSingleResult() {
		return singleResult;
	}

	public void setSingleResult(double singleResult) {
		this.singleResult = singleResult;
	}

	public int getViolateTimes() {
		return violateTimes;
	}

	public void setViolateTimes(int violateTimes) {
		this.violateTimes = violateTimes;
	}
	
	public int getCompareType() {
		return compareType;
	}

	public void setCompareType(int compareType) {
		this.compareType = compareType;
	}
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}  
	
	protected void loadCommon(NodeMonitor nm)
	{
		MonitorObject mo = MonitorFactory.getMonitorObject(nm.getMoid());
		setMoid(nm.getMoid());  		
		setResultType(mo.getResultType());
		setAlarmInfo(mo.getAlarmInfo());
		setAlarmLevel(nm.getAlarmLevel());
		setCompare(nm.getCompare());
		setCompareType(nm.getCompareType());
		setThreshold(nm.getThreshold());
		setUpperTimes(nm.getUpperTimes());  			       
        setUnit(mo.getUnit());    		
		setViolateTimes(0);
		setEnabled(nm.isEnabled());
        if(nm.getIntervalUnit().equals("m"))  //分钟
           setInterval(nm.getPollInterval() * 60);
        else
           setInterval(nm.getPollInterval() * 60 * 60); //小时
        setNodetype(mo.getNodetype());
        setSubentity(mo.getSubentity());
        setLimenvalue0(mo.getLimenvalue0());
        setLimenvalue1(mo.getLimenvalue1());
        setLimenvalue2(mo.getLimenvalue2());
        setTime0(mo.getTime0());
        setTime1(mo.getTime1());
        setTime2(mo.getTime2());
        setSms0(mo.getSms0());
        setSms1(mo.getSms1());
        setSms2(mo.getSms2());
        
	}
	
	public abstract void loadSelf(NodeMonitor nm);
}