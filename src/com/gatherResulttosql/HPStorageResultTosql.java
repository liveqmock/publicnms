package com.gatherResulttosql;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.node.ArrayInfo;
import com.afunms.polling.node.Battery;
import com.afunms.polling.node.Controller;
import com.afunms.polling.node.CtrlPort;
import com.afunms.polling.node.DIMM;
import com.afunms.polling.node.Disk;
import com.afunms.polling.node.Enclosure;
import com.afunms.polling.node.EnclosureFru;
import com.afunms.polling.node.HP;
import com.afunms.polling.node.Lun;
import com.afunms.polling.node.Port;
import com.afunms.polling.node.Processor;
import com.afunms.polling.node.SubSystemInfo;
import com.afunms.polling.node.SystemInfo;
import com.afunms.polling.node.VFP;
import com.ibm.db2.jcc.a.b;

public class HPStorageResultTosql {
	
	/**
	 * 
	 * 把hp存储采集数据生成sql放入的内存列表中
	 */
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DBManager dao = null;
	ResultSet rs;
	
	public void CreateResultTosql(HP hp,String ipid)
	{
		Calendar tempCal=Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		StringBuffer sql = null;
		if(hp.getSystemInfo() != null)
		{
			SystemInfo systeminfo = hp.getSystemInfo();
			try {			
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstoragesysteminfo(nodeipid,verdorid,productid,arrayworldwidename,arrayserialnumber,alias,softwarerevision,commandexecutiotimestamp,collecttime)values('");
			    sql.append(ipid);
			    sql.append("','");
			    sql.append(systeminfo.getVerdorID());
			    sql.append("','");
			    sql.append(systeminfo.getProductID());
			    sql.append("','");
			    sql.append(systeminfo.getArrayWorldWideName());
			    sql.append("','");
			    sql.append(systeminfo.getArraySerialNumber());
			    sql.append("','");
			    sql.append(systeminfo.getAlias());
			    sql.append("','");
			    sql.append(systeminfo.getSoftwareRevision());
			    sql.append("','");
			    sql.append(systeminfo.getCommandexecutioTimestamp());
			    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("','");
				    sql.append(time);//collecttime
				    sql.append("')");
			    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("',");
				    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
				    sql.append(")");
			    }
			    dao = new DBManager();
			    dao.executeUpdate(sql.toString());			    
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally
			{
				dao.close();
				sql = null;
			}
		}
		if(hp.getArrayInfo() != null)
		{
			ArrayInfo arrayinfo = hp.getArrayInfo();
			try {
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstoragearrayinfo(nodeipid,arraystatus,firmwarerevision,productrevision,localcontrollerproductrevision,remotecontrollerproductrevision,collecttime)values('");
			    sql.append(ipid);
			    sql.append("','");
			    sql.append(arrayinfo.getArrayStatus());
			    sql.append("','");
			    sql.append(arrayinfo.getFirmwareRevision());
			    sql.append("','");
			    sql.append(arrayinfo.getProductRevision());
			    sql.append("','");
			    sql.append(arrayinfo.getLocalControllerProductRevision());
			    sql.append("','");
			    sql.append(arrayinfo.getRemoteControllerProductRevision());			    
			    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("','");
				    sql.append(time);//collecttime
				    sql.append("')");
			    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("',");
				    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
				    sql.append(")");
			    }
			    dao = new DBManager();
			    dao.executeUpdate(sql.toString());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally
			{
				dao.close();
				sql = null;
			}
			if(arrayinfo.getLastEventLogEntry() != null)
			{
			try {
				int arrayinfoid = 0;
				String querysql = "select id from nms_hpstoragearrayinfo where collecttime='" + time + "'";
				try {
					dao = new DBManager();
					rs = dao.executeQuery(querysql);
					while(rs.next())
					   arrayinfoid = rs.getInt("id");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					dao.close();
				}
				
				dao = new DBManager();
				Map<String,String> lastEventLogEntry = arrayinfo.getLastEventLogEntry();
				Set<String> key = lastEventLogEntry.keySet();        
				for (Iterator it = key.iterator(); it.hasNext();) 
				{            
					String s = (String) it.next();            
					//System.out.println(map.get(s)); 
					sql = new StringBuffer(200);
					sql.append("insert into nms_hpstoragelasteventlogentry(arrayinfoid,lasteventlogentryname,lasteventlogentrydata,collecttime)values(");
				    sql.append(arrayinfoid);
				    sql.append(",'");
				    sql.append(s);
				    sql.append("','");
				    sql.append(lastEventLogEntry.get(s));				    			    
				    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("','");
					    sql.append(time);//collecttime
					    sql.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("',");
					    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
					    sql.append(")");
				    }
				    dao.addBatch(sql.toString());
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally
			{
				try {
					dao.executeBatch();
					dao.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
				    dao.close();
				    sql = null;
				}
			}
			}
		}
		if(hp.getEnclosures() != null && hp.getEnclosures().size()>0)
		{
			List<Enclosure> enclosures = hp.getEnclosures();
			for(int i=0;i<enclosures.size();i++)
			{
				Enclosure enclosure = enclosures.get(i);
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstorageenclosure(nodeipid,name,enclosureid,enclosurestatus,enclosuretype,nodewwn,collecttime)values('");
			    sql.append(ipid);
			    sql.append("','");
			    sql.append(enclosure.getName());
			    sql.append("','");
			    sql.append(enclosure.getEnclosureID());
			    sql.append("','");
			    sql.append(enclosure.getEnclosureStatus());
			    sql.append("','");
			    sql.append(enclosure.getEnclosureType());
			    sql.append("','");
			    sql.append(enclosure.getNodeWWN());			    
			    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("','");
				    sql.append(time);//collecttime
				    sql.append("')");
			    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("',");
				    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
				    sql.append(")");
			    }
			    try {
			    	dao = new DBManager();
				    dao.executeUpdate(sql.toString());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally
				{
					dao.close();
					sql = null;
				}
			    int enclosureid = 0;
			    try {
					String querysql = "select id from nms_hpstorageenclosure where name='" + enclosure.getName() + "' and collecttime='" + time + "'";
					dao = new DBManager();
					rs = dao.executeQuery(querysql);
					while(rs.next())
				 	  enclosureid = rs.getInt("id");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally
				{
					dao.close();
				}
				if(enclosure.getFrus() != null && enclosure.getFrus().size()>0)
				{
				List<EnclosureFru> enclosurefrus = enclosure.getFrus();
				dao = new DBManager();
				for(int j=0;j<enclosurefrus.size();j++)
				{
					EnclosureFru enclosurefru = enclosurefrus.get(i);
					sql = new StringBuffer(200);
					sql.append("insert into nms_hpstorageenclosurefru(enclosureid,fru,hwcomponent,identification,idstatus,collecttime)values(");
				    sql.append(enclosureid);
				    sql.append(",'");
				    sql.append(enclosurefru.getFru());
				    sql.append("','");
				    sql.append(enclosurefru.getHwComponent());
				    sql.append("','");
				    sql.append(enclosurefru.getIdentification());
				    sql.append("','");
				    sql.append(enclosurefru.getIdStatus());				    			    
				    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("','");
					    sql.append(time);//collecttime
					    sql.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("',");
					    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
					    sql.append(")");
				    }
				    dao.addBatch(sql.toString());
				}
				try {
					dao.executeBatch();
					dao.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
				    dao.close();
				    sql = null;
				}
				}
			}
		}
		if(hp.getControllers() != null && hp.getControllers().size()>0)
		{
			List<Controller> controllers = hp.getControllers();
			for(int i=0;i<controllers.size();i++)
			{
				Controller controller = controllers.get(i);
				sql = new StringBuffer(500);
				sql.append("insert into nms_hpstoragecontroller(nodeipid,name,status,serialnumber,vendorid,productid,productrevision," +
						"firmwarerevision,manufacturingproductcode,controllertype,batterychargerfirmwarerevision," +
						"enclosureswitchsetting,driveaddressbasis,enclosureid,looppair,loopid,hardaddress,collecttime)values('");
			    sql.append(ipid);
			    sql.append("','");
			    sql.append(controller.getName());
			    sql.append("','");
			    sql.append(controller.getStatus());
			    sql.append("','");
			    sql.append(controller.getSerialNumber());
			    sql.append("','");
			    sql.append(controller.getVendorID());
			    sql.append("','");
			    sql.append(controller.getProductID());
			    sql.append("','");
			    sql.append(controller.getProductRevision());
			    sql.append("','");			    
			    sql.append(controller.getFirmwareRevision());
			    sql.append("','");			   
			    sql.append(controller.getManufacturingProductCode());
			    sql.append("','");			    
			    sql.append(controller.getControllerType());
			    sql.append("','");			   
			    sql.append(controller.getBatteryChargerFirmwareRevision());
			    sql.append("','");
			    sql.append(controller.getEnclosureSwitchSetting());
			    sql.append("','");
			    sql.append(controller.getDriveAddressBasis());
			    sql.append("','");
			    sql.append(controller.getEnclosureID());			    
			    sql.append("','");
			    sql.append(controller.getLoopPair());
			    sql.append("','");
			    sql.append(controller.getLoopID());
			    sql.append("','");
			    sql.append(controller.getHardAddress());			   			    
			    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("','");
				    sql.append(time);//collecttime
				    sql.append("')");
			    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("',");
				    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
				    sql.append(")");
			    }
			    try {
			    	dao = new DBManager();
				    dao.executeUpdate(sql.toString());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally
				{
					dao.close();
					sql = null;
				}
				int controllerid = 0;
				try {
					String querysql = "select id from nms_hpstoragecontroller where name='" + controller.getName() + "' and collecttime='" + time + "'";
					dao = new DBManager();
					rs = dao.executeQuery(querysql);
					while(rs.next())
					   controllerid = rs.getInt("id");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally
				{					
					dao.close();
				}
				if(controller.getFrontPortList() != null && controller.getFrontPortList().size()>0)
				{
				List<CtrlPort> frontPortList = controller.getFrontPortList();
				dao = new DBManager();
				for(int j=0;j<frontPortList.size();j++)
				{
					CtrlPort ctrlport = frontPortList.get(j);
					sql = new StringBuffer(300);
					sql.append("insert into nms_hpstoragectrlport(controllerid,name,status,portinstance," +
							"hardaddress,linkstate,nodewwn,portwwn,topology,datarate,portid,devicehostname,hardwarepath,devicepath,porttype,collecttime)values(");
				    sql.append(controllerid);
				    sql.append(",'");
				    sql.append(ctrlport.getName());
				    sql.append("','");
				    sql.append(ctrlport.getStatus());
				    sql.append("','");
				    sql.append(ctrlport.getPortInstance());
				    sql.append("','");
				    sql.append(ctrlport.getHardAddress());
				    sql.append("','");
				    sql.append(ctrlport.getLinkState());
				    sql.append("','");
				    sql.append(ctrlport.getNodeWWN());
				    sql.append("','");
				    sql.append(ctrlport.getPortWWN());
				    sql.append("','");
				    sql.append(ctrlport.getTopology());
				    sql.append("','");
				    sql.append(ctrlport.getDataRate());
				    sql.append("','");
				    sql.append(ctrlport.getPortID());
				    sql.append("','");
				    sql.append(ctrlport.getDeviceHostName());
				    sql.append("','");
				    sql.append(ctrlport.getHardwarePath());
				    sql.append("','");
				    sql.append(ctrlport.getDevicePath());
				    sql.append("','");
				    sql.append("front");
				    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("','");
					    sql.append(time);//collecttime
					    sql.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("',");
					    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
					    sql.append(")");
				    }
				    dao.addBatch(sql.toString());
				}
				try {
					dao.executeBatch();
					dao.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
				    dao.close();
				    sql = null;
				}
				}
				if(controller.getBackPortList() != null && controller.getBackPortList().size()>0)
				{
				List<CtrlPort> backPortList = controller.getBackPortList();
				dao = new DBManager();
				for(int j=0;j<backPortList.size();j++)
				{
					CtrlPort ctrlport = backPortList.get(j);
					sql = new StringBuffer(300);
					sql.append("insert into nms_hpstoragectrlport(controllerid,name,status,portinstance," +
							"hardaddress,linkstate,nodewwn,portwwn,topology,datarate,portid,devicehostname,hardwarepath,devicepath,porttype,collecttime)values(");
				    sql.append(controllerid);
				    sql.append(",'");
				    sql.append(ctrlport.getName());
				    sql.append("','");
				    sql.append(ctrlport.getStatus());
				    sql.append("','");
				    sql.append(ctrlport.getPortInstance());
				    sql.append("','");
				    sql.append(ctrlport.getHardAddress());
				    sql.append("','");
				    sql.append(ctrlport.getLinkState());
				    sql.append("','");
				    sql.append(ctrlport.getNodeWWN());
				    sql.append("','");
				    sql.append(ctrlport.getPortWWN());
				    sql.append("','");
				    sql.append(ctrlport.getTopology());
				    sql.append("','");
				    sql.append(ctrlport.getDataRate());
				    sql.append("','");
				    sql.append(ctrlport.getPortID());
				    sql.append("','");
				    sql.append(ctrlport.getDeviceHostName());
				    sql.append("','");
				    sql.append(ctrlport.getHardwarePath());
				    sql.append("','");
				    sql.append(ctrlport.getDevicePath());
				    sql.append("','");
				    sql.append("back");
				    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("','");
					    sql.append(time);//collecttime
					    sql.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("',");
					    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
					    sql.append(")");
				    }
				    dao.addBatch(sql.toString());
				}
				try {
					dao.executeBatch();
					dao.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
				    dao.close();
				    sql = null;
				}
				}
				if(controller.getBattery() != null)
				{
				Battery battery = controller.getBattery();
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstoragebattery(controllerid,name,status,identification,manufacturername,devicename,manufacturerdate," +
						"remainingcapacity,voltage,dischargecycles,collecttime)values(");
			    sql.append(controllerid);
			    sql.append(",'");
			    sql.append(battery.getName());
			    sql.append("','");
			    sql.append(battery.getStatus());
			    sql.append("','");
			    sql.append(battery.getIdentification());
			    sql.append("','");
			    sql.append(battery.getManufacturerName());
			    sql.append("','");
			    sql.append(battery.getDeviceName());
			    sql.append("','");
			    sql.append(battery.getManufacturerDate());
			    sql.append("','");
			    sql.append(battery.getRemainingCapacity());
			    sql.append("','");
			    sql.append(battery.getVoltage());
			    sql.append("','");
			    sql.append(battery.getDischargeCycles());
			    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("','");
				    sql.append(time);//collecttime
				    sql.append("')");
			    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("',");
				    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
				    sql.append(")");
			    }
			    try {
			    	dao = new DBManager();
				    dao.executeUpdate(sql.toString());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally
				{
					dao.close();
					sql = null;
				}
				}
				if(controller.getProcessor() != null)
				{
				Processor processor = controller.getProcessor();
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstorageprocessor(controllerid,name,status,identification,collecttime)values(");
			    sql.append(controllerid);
			    sql.append(",'");
			    sql.append(processor.getName());
			    sql.append("','");
			    sql.append(processor.getStatus());
			    sql.append("','");
			    sql.append(processor.getIdentification());			    
			    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("','");
				    sql.append(time);//collecttime
				    sql.append("')");
			    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			    	sql.append("',");
				    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
				    sql.append(")");
			    }
			    try {
			    	dao = new DBManager();
				    dao.executeUpdate(sql.toString());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally
				{
					dao.close();
					sql = null;
				}
				}
				if(controller.getDimmList() != null && controller.getDimmList().size()>0)
				{
				List<DIMM> dimmlist = controller.getDimmList();
				dao = new DBManager();
				for(int j=0;j<dimmlist.size();j++)
				{
					DIMM dimm = dimmlist.get(i);
					sql = new StringBuffer(200);
					sql.append("insert into nms_hpstoragedimm(controllerid,name,status,identification," +
							"capacity,collecttime)values(");
				    sql.append(controllerid);
				    sql.append(",'");
				    sql.append(dimm.getName());
				    sql.append("','");
				    sql.append(dimm.getStatus());
				    sql.append("','");
				    sql.append(dimm.getIdentification());
				    sql.append("','");
				    sql.append(dimm.getCapacity());				    				    
				    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("','");
					    sql.append(time);//collecttime
					    sql.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sql.append("',");
					    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
					    sql.append(")");
				    }
				    dao.addBatch(sql.toString());
				}
				try {
					dao.executeBatch();
					dao.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
				    dao.close();
				    sql = null;
				}
				}
			}
			
		}
		if(hp.getPorts() != null && hp.getPorts().size()>0)
		{
			List<Port> ports = hp.getPorts();
			dao = new DBManager();
			for(int i=0;i<ports.size();i++)
			{
				Port port = ports.get(i);
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstorageport(nodeipid,name,portid,behavior," +
				"topology,queuefullthreshold,datarate,collecttime)values('");
	            sql.append(ipid);
	            sql.append("','");
	            sql.append(port.getName());
	            sql.append("','");
	            sql.append(port.getPortID());
	            sql.append("','");
	            sql.append(port.getBehavior());
	            sql.append("','");
	            sql.append(port.getTopology());
	            sql.append("','");
	            sql.append(port.getQueueFullThreshold());
	            sql.append("','");
	            sql.append(port.getDataRate());
	            if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	    	         sql.append("','");
		             sql.append(time);//collecttime
		             sql.append("')");
	           }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	    	        sql.append("',");
		            sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
		            sql.append(")");
	           }
	           dao.addBatch(sql.toString());
			}
			try {
				dao.executeBatch();
				dao.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			    dao.close();
			    sql = null;
			}
		}
		if(hp.getDisks() != null && hp.getDisks().size()>0)
		{
			List<Disk> disks = hp.getDisks();
			dao = new DBManager();
			for(int i=0;i<disks.size();i++)
			{
				Disk disk = disks.get(i);
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstoragedisk(nodeipid,name,status,diskstate," +
				"vendorid,productid,productrevision,datacapacity,blocklength,address,nodewwn,initializestate," +
				"redundancygroup,volumesetserialnumber,serialnumber,firmwarerevision,collecttime)values('");
	            sql.append(ipid);
	            sql.append("','");
	            sql.append(disk.getName());
	            sql.append("','");
	            sql.append(disk.getStatus());
	            sql.append("','");
	            sql.append(disk.getDiskState());
	            sql.append("','");
	            sql.append(disk.getVendorID());
	            sql.append("','");
	            sql.append(disk.getProductID());
	            sql.append("','");
	            sql.append(disk.getProductRevision());
	            sql.append("','");
	            sql.append(disk.getDataCapacity());
	            sql.append("','");
	            sql.append(disk.getBlockLength());
	            sql.append("','");
	            sql.append(disk.getAddress());
	            sql.append("','");
	            sql.append(disk.getNodeWWN());
	            sql.append("','");
	            sql.append(disk.getInitializeState());
	            sql.append("','");
	            sql.append(disk.getRedundancyGroup());
	            sql.append("','");
	            sql.append(disk.getVolumeSetSerialNumber());
	            sql.append("','");
	            sql.append(disk.getSerialNumber());
	            sql.append("','");
	            sql.append(disk.getFirmwareRevision());
	            if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	    	         sql.append("','");
		             sql.append(time);//collecttime
		             sql.append("')");
	           }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	    	        sql.append("',");
		            sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
		            sql.append(")");
	           }
	           dao.addBatch(sql.toString());
			}
			try {
				dao.executeBatch();
				dao.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			    dao.close();
			    sql = null;
			}
		}
		if(hp.getLuns() != null && hp.getLuns().size()>0)
		{
			List<Lun> luns = hp.getLuns();
			dao = new DBManager();
			for(int i=0;i<luns.size();i++)
			{
				Lun lun = luns.get(i);
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstoragelun(nodeipid,name,redundancygroup,active," +
				"datacapacity,wwn,numberofbusinesscopies,collecttime)values('");
	            sql.append(ipid);
	            sql.append("','");
	            sql.append(lun.getName());
	            sql.append("','");
	            sql.append(lun.getRedundancyGroup());
	            sql.append("','");
	            sql.append(lun.getActive());
	            sql.append("','");
	            sql.append(lun.getDataCapacity());
	            sql.append("','");
	            sql.append(lun.getWwn());
	            sql.append("','");
	            sql.append(lun.getNumberOfBusinessCopies());	            	          
	            if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	    	         sql.append("','");
		             sql.append(time);//collecttime
		             sql.append("')");
	           }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	    	        sql.append("',");
		            sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
		            sql.append(")");
	           }
	           dao.addBatch(sql.toString());
			}
			try {
				dao.executeBatch();
				dao.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			    dao.close();
			    sql = null;
			}
		}
		if(hp.getVfps() != null && hp.getVfps().size()>0)
		{
			List<VFP> vfps = hp.getVfps();
			dao = new DBManager();
			for(int i=0;i<vfps.size();i++)
			{
				VFP vfp = vfps.get(i);
				sql = new StringBuffer(200);
				sql.append("insert into nms_hpstoragevfp(nodeipid,name,vfpbaudrate,vfppagingvalue,collecttime)values('");
	            sql.append(ipid);
	            sql.append("','");
	            sql.append(vfp.getName());
	            sql.append("','");
	            sql.append(vfp.getVFPBaudRate());
	            sql.append("','");
	            sql.append(vfp.getVFPPagingValue());	            	            	          
	            if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	    	         sql.append("','");
		             sql.append(time);//collecttime
		             sql.append("')");
	           }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	    	        sql.append("',");
		            sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
		            sql.append(")");
	           }
	           dao.addBatch(sql.toString());
			}
			try {
				dao.executeBatch();
				dao.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			    dao.close();
			    sql = null;
			}
		}
		if(hp.getSubSystemInfo() != null)
		{
			SubSystemInfo subSystemInfo = hp.getSubSystemInfo();			
			sql = new StringBuffer(200);
			sql.append("insert into nms_hpstoragesubsysteminfo(nodeipid,raidlevel,autoformatdrive,hangdetection,capacitydepletionthreshold," +
					"queuefullthresholdmaximum,enableoptimizepolicy,enablemanualoverride,manualoverridedestination,readcachedisable," +
					"rebuildpriority,securityenabled,shutdowncompletion,subsystemtypeid,unitattention,volumesetpartition,writecacheenable," +
					"writeworkingsetinterval,enableprefetch,disablesecondarypathpresentation,collecttime)values('");
	        sql.append(ipid);
	        sql.append("','");
	        sql.append(subSystemInfo.getRaidLevel());
	        sql.append("','");
	        sql.append(subSystemInfo.getAutoFormatDrive());
	        sql.append("','");
	        sql.append(subSystemInfo.getHangDetection());
	        sql.append("','");
	        sql.append(subSystemInfo.getCapacityDepletionThreshold());
	        sql.append("','");
	        sql.append(subSystemInfo.getQueueFullThresholdMaximum());
	        sql.append("','");
	        sql.append(subSystemInfo.getEnableOptimizePolicy());
	        sql.append("','");
	        sql.append(subSystemInfo.getEnableManualOverride());
	        sql.append("','");
	        sql.append(subSystemInfo.getManualOverrideDestination());
	        sql.append("','");
	        sql.append(subSystemInfo.getReadCacheDisable());
	        sql.append("','");
	        sql.append(subSystemInfo.getRebuildPriority());
	        sql.append("','");
	        sql.append(subSystemInfo.getSecurityEnabled());
	        sql.append("','");
	        sql.append(subSystemInfo.getShutdownCompletion());
	        sql.append("','");
	        sql.append(subSystemInfo.getSubsystemTypeID());
	        sql.append("','");
	        sql.append(subSystemInfo.getUnitAttention());
	        sql.append("','");
	        sql.append(subSystemInfo.getVolumeSetPartition());
	        sql.append("','");
	        sql.append(subSystemInfo.getWriteCacheEnable());
	        sql.append("','");
	        sql.append(subSystemInfo.getWriteWorkingSetInterval());
	        sql.append("','");
	        sql.append(subSystemInfo.getEnablePrefetch());
	        sql.append("','");
	        sql.append(subSystemInfo.getDisableSecondaryPathPresentation());
	        if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	    	      sql.append("','");
		          sql.append(time);//collecttime
		          sql.append("')");
	        }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	    	      sql.append("',");
		          sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
		          sql.append(")");
	        }           
	        try {
		    	dao = new DBManager();
			    dao.executeUpdate(sql.toString());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally
			{
				dao.close();
				sql = null;
			}
		}
	}
}