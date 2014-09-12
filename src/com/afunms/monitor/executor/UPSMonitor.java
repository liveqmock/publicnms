/**
 * <p>Description:collect ups status</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 齐鲁石化
 * @date 2007-01-24
 */

package com.afunms.monitor.executor;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.inform.model.Alarm;
import com.afunms.monitor.item.*;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.monitor.executor.base.*;
import com.afunms.polling.node.UPSNode;
import com.afunms.polling.base.*;
import com.afunms.topology.model.HostNode;
import com.afunms.inform.dao.MachineRoomExceptionDao;

public class UPSMonitor extends SnmpMonitor implements MonitorInterface
{      
    public UPSMonitor()
    {       	
    }
    public void collectData(HostNode node){
 	   
    }
    public Hashtable collect_Data(HostNode node){
 	   return null;
    }
    public void collectData(Node node,MonitoredItem monitoredItem)
    {  
    	UPSNode upsNode = (UPSNode)node;
    	UPSItem item = (UPSItem)monitoredItem;
    	item.setSingleResult(-1); //因为没有数据需要存入数据库
    	
    	String[] inputOids = new String[]{"1.3.6.1.4.1.705.1.6.2"};
    	String[] outputOids = new String[]{"1.3.6.1.4.1.705.1.7.2"};
    	
    	String[][] inputPhase = null;   
    	String[][] outputPhase = null;
    	try
    	{
    		/**
    		 * 注意：mge ups的table与一般的节点有区别,取出来的input table有18行，1列
    		 * output table有15行，1列
    		 */
    		inputPhase = snmp.getTableData(upsNode.getIpAddress(),upsNode.getCommunity(),inputOids);
    		outputPhase = snmp.getTableData(upsNode.getIpAddress(),upsNode.getCommunity(),outputOids);
    	}
    	catch(Exception e)
    	{
    		SysLogger.error(upsNode.getIpAddress() + "_UPSMonitor");
    	}   
    	if(inputPhase==null||outputPhase==null||outputPhase.length==0||inputPhase.length==0)
    		return;
	    	
    	List list = new ArrayList(6);
    	for(int i=0;i<3;i++) //只有三个相位
    	{
    		UPSPhase phase = new UPSPhase();
    		phase.setIo(1);
    		phase.setIndex(Integer.parseInt(inputPhase[i][0]));
    		phase.setVoltage(Integer.parseInt(inputPhase[i + 3][0]) / 10);
    		phase.setFrequency(Integer.parseInt(inputPhase[i + 6][0]) / 10);
    		phase.setCurrent(Integer.parseInt(inputPhase[i + 15][0]) / 10);
    		list.add(phase);
    	}
    	int upsLoad = 0;
    	for(int i=0;i<3;i++) 
    	{
    		UPSPhase phase = new UPSPhase();
    		phase.setIo(0);
    		phase.setIndex(Integer.parseInt(outputPhase[i][0]));
    		phase.setVoltage(Integer.parseInt(outputPhase[i + 3][0]) / 10 );
    		phase.setFrequency(Integer.parseInt(outputPhase[i + 6][0]) / 10);
    		phase.setCurrent(Integer.parseInt(outputPhase[i + 12][0]) / 10);
    		phase.setLoad(phase.getVoltage() * phase.getCurrent()); //单位是W
    		upsLoad += phase.getLoad();
    		
    		list.add(phase);
    	}
    	for(int i=3;i<6;i++) 
    	{
    		UPSPhase phase = (UPSPhase)list.get(i);
    		phase.setLoadPercent( phase.getLoad()*100 / upsLoad);
    	}    	
    	
    	item.setPhasesList(list);
    	item.setUpsLoad(upsLoad); //ups输出负载
    	
    	String temp = null;
        //------受UPS保持设备的数量(%)-----------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.4.21.0");
    	item.setDevicesNumber(Integer.parseInt(temp));
        //------电池蓄电(%)-----------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.5.2.0");
    	item.setBatteryLevel(Integer.parseInt(temp));
        //------电池能提供的电压(dV)------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.5.5.0");
    	item.setBatteryVoltage(Integer.parseInt(temp) / 10); 
        //------(市电断电后)电池能支持的时间(s)------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.5.1.0"); 
    	item.setBatteryTime(Integer.parseInt(temp) / 60); //单位分钟
        //------电池蓄电下降到多少时，UPS准备关机------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.4.8.0"); 
    	item.setLowBatteryLevel(Integer.parseInt(temp)); 
        //------电池有故障------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.5.9.0");
    	item.setBatteryFault(str2Boolean(temp)); 
        //------电池充电器有故障------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.5.15.0");
    	item.setBatteryChargerFault(str2Boolean(temp)); 
        //------电池低电量------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.5.16.0");
    	item.setBatteryLow(str2Boolean(temp));     	    	    	
        //------电池启用------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.7.3.0");
    	item.setOutputOnBattery(str2Boolean(temp));
        //------旁路启用------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.7.4.0");
    	item.setOutputOnByPass(str2Boolean(temp));
        //------额定负载------------
    	temp = snmp.getMibValue(upsNode.getIpAddress(),upsNode.getCommunity(),"1.3.6.1.4.1.705.1.4.12.0");
    	item.setUpsRatedLoad(Integer.parseInt(temp));  
    	//-------UPS超载---------------
    	if(item.getUpsLoad() > item.getUpsRatedLoad())  
    	   item.setOverLoad(true);
    }
    
    public void analyseData(Node node,MonitoredItem monitoredItem)
    {    
    	UPSNode upsNode = (UPSNode)node;
    	UPSItem item = (UPSItem)monitoredItem;
    	
    	if(item.isOverLoad())    		
    		addAlarmMsg(upsNode,"UPS输出超载");
		if(item.isBatteryLow())
			addAlarmMsg(upsNode,"UPS电池低电量");
		if(item.isBatteryChargerFault())
			addAlarmMsg(upsNode,"UPS电池充电器有故障");
		if(item.isBatteryFault())
			addAlarmMsg(upsNode,"UPS电池有故障");
        if(item.isOutputOnBattery())
        	addAlarmMsg(upsNode,"市电断电,UPS电池启用");        
        if(item.isOutputOnByPass())	
        	addAlarmMsg(upsNode,"UPS有故障,UPS旁路启用");
        
        if(upsNode.getAlarmMessage().size()!=0)
        {
            MachineRoomExceptionDao dao = new MachineRoomExceptionDao();
            dao.insert(upsNode.getAlarmMessage());
        }
    } 
    
    private boolean str2Boolean(String value)
    {
    	if("2".equals(value))
    	   return false;
    	else
    	   return true;	
    }
    
    private void addAlarmMsg(UPSNode upsNode,String message)
    {
    	SysLogger.info("UPS终于异常了=" + message);
    	
    	upsNode.setAlarm(true);
    	Alarm vo = new Alarm();
		vo.setIpAddress(upsNode.getIpAddress());
		vo.setLevel(3);  //所有UPS的异常都设最高报警级别
		vo.setMessage(message);
		vo.setLogTime(SysUtil.getCurrentTime());
		vo.setCategory(upsNode.getCategory());		
		upsNode.getAlarmMessage().add(vo);
		
		if("市电断电,UPS电池启用".equals(message))
		   upsNode.setStatus(5);
		else
		   upsNode.setStatus(4);	
    }
}