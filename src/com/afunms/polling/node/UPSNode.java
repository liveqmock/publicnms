/**
 * <p>Description:ups node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2007-01-24
 */

package com.afunms.polling.node;

import java.util.List;

import com.afunms.inform.model.Alarm;
import com.afunms.monitor.item.*;
import com.afunms.polling.base.Node;

public class UPSNode extends Node
{
    public UPSNode()
    {    
    	category = 101;
    }
    
	private String community;  //其它属性父类都有
    private String Location; 
    private String subtype; 

    public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
    public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}  
	
	/**
	 * 覆盖父类同名方法
	 */
	public String getShowMessage()
	{
		StringBuffer msg = new StringBuffer(200);
		msg.append("<font color='green'>类型:UPS");
		msg.append("</font><br>名称:");
		msg.append(alias);
		msg.append("<br>");
		msg.append("IP地址:");
		msg.append(ipAddress);
		msg.append("<br>");
		
		if(status==0||status==2||status==3)
		{			
			msg.append("<font color='red'>");
			if(status==0)
			   msg.append("不被管理</font>");
			else if(status==2)
			   msg.append("设备忙</font>");
			else
			   msg.append("Ping不通</font>");	
			return msg.toString();
		}	
		UPSItem item = (UPSItem)moidList.get(1);  
		List phasesList = item.getPhasesList();
		
		//--------取输入值---------
		StringBuffer vSb = new StringBuffer(50);
		StringBuffer aSb = new StringBuffer(50);		
		vSb.append("输入电压:<br>");
		aSb.append("输入电流:<br>");		
		for(int i=0;i<3;i++)
		{
			UPSPhase phase = (UPSPhase)phasesList.get(i);
			String phaseIndex = "(" + phase.getIndex() + "):";									
			vSb.append(phaseIndex);
			vSb.append(phase.getVoltage() + "V");
			aSb.append(phaseIndex);
			aSb.append(phase.getCurrent() + "A");
            if(i!=2)
            {
            	vSb.append(",");
            	aSb.append(",");
            }
		}
		msg.append(vSb.toString());
		msg.append("<br>");
		msg.append(aSb.toString());
		msg.append("<br>");
		
		//---------输出值----------
		vSb = new StringBuffer(50);
		aSb = new StringBuffer(50);
		StringBuffer loadSb = new StringBuffer(50);
		vSb.append("输出电压:<br>");
		aSb.append("输出电流:<br>");
		loadSb.append("输出负载:<br>");
		for(int i=3;i<6;i++)
		{
			UPSPhase phase = (UPSPhase)phasesList.get(i);			
			String phaseIndex = "(" + phase.getIndex() + "):";	
			vSb.append(phaseIndex);
			vSb.append(phase.getVoltage() + "V");
			aSb.append(phaseIndex);
			aSb.append(phase.getCurrent() + "A");
			loadSb.append(phaseIndex);
			loadSb.append(phase.getLoadPercent() + "%");
            if(i!=5)
            {
            	vSb.append(",");
            	aSb.append(",");
            	loadSb.append(",");
            }						
		}
		msg.append(vSb.toString());
		msg.append("<br>");
		msg.append(aSb.toString());
		msg.append("<br>");
		msg.append(loadSb.toString());
		msg.append("<br>");	
		msg.append("电池蓄电:");
		msg.append(item.getBatteryLevel());
		msg.append("%<br>");
		msg.append("电池电压:");
		msg.append(item.getBatteryVoltage());
		msg.append("V<br>");
				
		if(alarm) //status=4,5
		{	
		    msg.append("<font color='red'>--报警信息:--</font><br>");
		    for(int i=0;i<alarmMessage.size();i++)
		    {
		        msg.append(((Alarm)alarmMessage.get(i)).getMessage());
		        msg.append("<br>");
		    }
		}
		msg.append("更新时间:" + lastTime);
		return msg.toString();
	}	
}