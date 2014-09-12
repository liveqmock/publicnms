package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.CPUcollectdata;
import com.gatherdb.GathersqlListManager;



/**
 * 
 * 
 * 将采集主机的cpu采集结果转换成sql并放到内存类表中
 * @author konglq
 *
 */
public class HostcpuResultTosql {
	
	/**
	 * 
	 * 把cpu的采集数据成成sql放入的内存列表中
	 */
	public void CreateResultTosql(Hashtable ipdata,String ip)
	{
		
		CPUcollectdata cpudata = null;
		Vector cpuVector = null;
		//StringBuffer sBuffer = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		
		
	
	if(ipdata != null){
		//处理主机设备的数据
		if(ipdata.containsKey("cpu")){
			//CPU
			//cpuhash = (Hashtable)ipdata.get("cpu");
			cpuVector = (Vector) ipdata.get("cpu");
			
			if (cpuVector != null && cpuVector.size() > 0) {
				//得到CPU平均
				cpudata = (CPUcollectdata) cpuVector.elementAt(0);
				if (!cpudata.getThevalue().equals("-1")) {
					if (cpudata.getRestype().equals("dynamic")) {
						Calendar tempCal = (Calendar) cpudata.getCollecttime();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						String tablename = "cpu" + allipstr;
						String sql = "";
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
							+ cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','"
							+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
							+ cpudata.getThevalue() + "','" + time + "')";
						}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
							+ cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','"
							+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
							+ cpudata.getThevalue() + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
						}else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
							+ cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','"
							+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
							+ cpudata.getThevalue() + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
						}
						
						
						GathersqlListManager.Addsql(sql);
						sql=null;
						tablename=null;
						//time=null;
						cpudata=null;
					}
				}
				
			}
			cpuVector=null;
			
		}
		
	}
	}
	
	
	
	/**
	 * 
	 * 把cpu的采集数据成成sql放入的内存列表中
	 */
	public void CreateLinuxResultTosql(Hashtable ipdata,String ip)
	{
		
		CPUcollectdata cpudata = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		
		Vector cpuVector = (Vector) ipdata.get("cpu");
		if (cpuVector != null && cpuVector.size() > 0) {
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ip);
			if(ipAllData == null)ipAllData = new Hashtable();
			ipAllData.put("cpu",cpuVector);
		    ShareData.getSharedata().put(ip, ipAllData);
			//得到CPU平均
			cpudata = (CPUcollectdata) cpuVector.elementAt(0);
			if (cpudata.getRestype().equals("dynamic")) {
				//session.save(cpudata);
				Calendar tempCal = (Calendar) cpudata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "cpu" + allipstr;
				String sql = "";
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into " + tablename
					+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
					+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
					+ cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','"
					+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
					+ cpudata.getThevalue() + "','" + time + "')";
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into " + tablename
					+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
					+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
					+ cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','"
					+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
					+ cpudata.getThevalue() + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
				}else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into " + tablename
					+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
					+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
					+ cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','"
					+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
					+ cpudata.getThevalue() + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
				}
				//添加sql
				GathersqlListManager.Addsql(sql);
				sql=null;
				
				
			}
			
		}
		
		//CPU详细信息
		List cpuperflist = (List) ipdata.get("cpuperflist");
		if (cpuperflist != null && cpuperflist.size() > 0) {
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ip);
			if(ipAllData == null)ipAllData = new Hashtable();
			ipAllData.put("cpuperflist",cpuperflist);
		    ShareData.getSharedata().put(ip, ipAllData);
			//得到CPU详细情况
//		    for(int i=0;i<cpuperflist.size();i++){
//		    	
//		    }
		    Hashtable cpuperfhash = (Hashtable)cpuperflist.get(0);
			String[] items1={"usr","sys","wio","idle"};
			String[] items2={"user","nice","system","iowait","steal","idle"};
			String nice=(String)cpuperfhash.get("%nice");
			if (cpudata != null) {
				Calendar tempCal = (Calendar) cpudata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "cpudtl" + allipstr;
				String sql="";
				if (nice==null||nice.equalsIgnoreCase("null")) {
						String values1[]=new String[4];
						values1[0]=(String)cpuperfhash.get("%usr");
						values1[1]=(String)cpuperfhash.get("%sys");
						values1[2]=(String)cpuperfhash.get("%wio");
						values1[3]=(String)cpuperfhash.get("%idle");
					for (int i = 0; i < items1.length; i++) {
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
							+ cpudata.getEntity() + "','"+items1[i]+"','" + cpudata.getUnit() + "','"
							+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
							+ values1[i] + "','" + time + "')";
						}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
							+ cpudata.getEntity() + "','"+items1[i]+"','" + cpudata.getUnit() + "','"
							+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
							+ values1[i] + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
						}else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
							+ cpudata.getEntity() + "','"+items1[i]+"','" + cpudata.getUnit() + "','"
							+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
							+ values1[i] + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
						}

						GathersqlListManager.Addsql(sql);
						sql=null;
					}
					values1=null;
				}else {//Linux
					String values2[]=new String[6];
					values2[0]=(String)cpuperfhash.get("%user");
					values2[1]=(String)cpuperfhash.get("%nice");
					values2[2]=(String)cpuperfhash.get("%system");
					values2[3]=(String)cpuperfhash.get("%iowait");
					values2[4]=(String)cpuperfhash.get("%steal");
					values2[5]=(String)cpuperfhash.get("%idle");
				for (int i = 0; i < items2.length; i++) {
					if (values2[4]==null||values2[4].equalsIgnoreCase("null")) {
						continue;
					}
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into " + tablename
						+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
						+ cpudata.getEntity() + "','"+items2[i]+"','" + cpudata.getUnit() + "','"
						+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
						+ values2[i] + "','" + time + "')";
					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into " + tablename
						+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
						+ cpudata.getEntity() + "','"+items2[i]+"','" + cpudata.getUnit() + "','"
						+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
						+ values2[i] + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
					}else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into " + tablename
						+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+ "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','"
						+ cpudata.getEntity() + "','"+items2[i]+"','" + cpudata.getUnit() + "','"
						+ cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'"
						+ values2[i] + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
					}

					GathersqlListManager.Addsql(sql);
					sql=null;
				}
				values2=null;
			}
			}
			
		}
	

}
	
}
