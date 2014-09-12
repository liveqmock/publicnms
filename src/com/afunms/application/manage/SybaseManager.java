/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.util.*;
import java.text.*;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.common.base.*;
import com.afunms.common.*;
import com.afunms.common.util.*;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.event.dao.EventListDao;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.loader.*;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.SybaseVO;
import com.afunms.application.model.TablesVO;
import com.afunms.system.model.User;
import com.afunms.topology.util.*;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.util.DBPool;
import com.afunms.application.util.DBRefreshHelper;
import com.afunms.application.util.IpTranslation;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.polling.impl.*;
import com.afunms.polling.api.*;

import com.afunms.report.jfree.ChartCreator;

import org.jfree.data.general.DefaultPieDataset;

public class SybaseManager extends BaseManager implements ManagerInterface
{
	public static Hashtable rsc_type_ht = new Hashtable();
	public static Hashtable req_mode_ht = new Hashtable();
	public static Hashtable mode_ht = new Hashtable();
	public static Hashtable req_status_ht = new Hashtable();
	public static Hashtable req_ownertype_ht = new Hashtable();  

	
	private String sybaseping()
    {    	   
		DBVo vo = new DBVo();
		
		
		Hashtable sysValue = new Hashtable();
		Hashtable sValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		double avgpingcon = 0;
		String id = getParaValue("id");
		request.setAttribute("id", id);
		try{
			DBDao dao = new DBDao();
			try{
				vo = (DBVo)dao.findByID(id);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if(vo.getManaged() == 0)managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "<font color=red>服务停止</font>";
			
			/*dao = new DBDao();
			try{
				if(dao.getSysbaseIsOk(vo.getIpAddress(), vo.getUser(), vo.getPassword(), Integer.parseInt(vo.getPort()))){
					runstr = "正在运行";
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}*/
//			sysValue = ShareData.getSysbasedata();
//			if(sysValue!=null&&sysValue.size()>0){
//				if(sysValue.containsKey(vo.getIpAddress())){
//					sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//					if(sValue.containsKey("status")){
//						String p_status = (String)sValue.get("status");
//						if(p_status != null && p_status.length()>0){
//							if("1".equalsIgnoreCase(p_status)){
//								runstr = "正在运行";
//							}
//						}
//					}
//				}
//			}
			//获取sybase信息
			SybaseVO sysbaseVO = new SybaseVO();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex+":"+vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip); 
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
				status = (String)tempStatusHashtable.get("status");
			}
			if(status.equals("1")){
				runstr = "正在运行";
			}
			if(dao != null){
				dao.close();
			}
			request.setAttribute("runstr", runstr);
			//sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			SybaseVO sysbaseVO = (SybaseVO)sValue.get("sysbaseVO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpAddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}
			//画图
			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
			p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);

			//ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon",pingconavg);

			//imgurlhash
			imgurlhash.put("ConnectUtilization","resource/image/jfreechart/"+newip+"ConnectUtilization"+".png");    
			
			  avgpingcon = new Double(pingconavg+"").doubleValue();
			  String chart1 = null,chart2 = null,chart3 = null,responseTime = null;
			  DefaultPieDataset dpd = new DefaultPieDataset();
			  dpd.setValue("可用率",avgpingcon);
			  dpd.setValue("不可用率",100 - avgpingcon);
			  chart1 = ChartCreator.createPieChart(dpd,"",130,130); 
			
			request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("max",maxhash);
			request.setAttribute("chart1",chart1);
			request.setAttribute("sqlsys",sysValue);
			request.setAttribute("sysbaseVO",sysbaseVO);
		        
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/sybaseping.jsp";
    }
	
	private String sybasecap()
    {    	   
		DBVo vo = new DBVo();
		
		
		Hashtable sysValue = new Hashtable();
		Hashtable sValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		double avgpingcon = 0;
		String id = getParaValue("id");
		request.setAttribute("id", id);
		try{
			DBDao dao = new DBDao();
			try{
				vo = (DBVo)dao.findByID(id);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if(vo.getManaged() == 0)managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "<font color=red>服务停止</font>";
			/*dao = new DBDao();
			try{
				if(dao.getSysbaseIsOk(vo.getIpAddress(), vo.getUser(), vo.getPassword(), Integer.parseInt(vo.getPort()))){
					runstr = "正在运行";
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}*/
//			sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			if(sysValue!=null&&sysValue.size()>0){
//				if(sysValue.containsKey(vo.getIpAddress())){
//					sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//					if(sValue.containsKey("status")){
//						String p_status = (String)sValue.get("status");
//						if(p_status != null && p_status.length()>0){
//							if("1".equalsIgnoreCase(p_status)){
//								runstr = "正在运行";
//							}
//						}
//					}
//				}
//			}
			//获取sybase信息
			SybaseVO sysbaseVO = new SybaseVO();
			List<TablesVO> tablesVO = new ArrayList<TablesVO>();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex+":"+vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip); 
			
			dao = new DBDao();
			tablesVO = dao.getSybase_nmsdbinfo(serverip);
			
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
				status = (String)tempStatusHashtable.get("status");
			}
			if(status.equals("1")){
				runstr = "正在运行";
			}
			if(dao != null){
				dao.close();
			}
			request.setAttribute("runstr", runstr);
//						SybaseVO sysbaseVO = (SybaseVO)sValue.get("sysbaseVO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpAddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}
			//画图
			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
			//p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);

			//ping平均值
			//maxhash = new Hashtable();
			//maxhash.put("avgpingcon",pingconavg);

			//imgurlhash
			//imgurlhash.put("ConnectUtilization","resource/image/jfreechart/"+newip+"ConnectUtilization"+".png");    
			
			  avgpingcon = new Double(pingconavg+"").doubleValue();
			  String chart1 = null,chart2 = null,chart3 = null,responseTime = null;
			  DefaultPieDataset dpd = new DefaultPieDataset();
			  dpd.setValue("可用率",avgpingcon);
			  dpd.setValue("不可用率",100 - avgpingcon);
			  chart1 = ChartCreator.createPieChart(dpd,"",130,130); 
			
			request.setAttribute("tablesVO", tablesVO);
			request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("max",maxhash);
			request.setAttribute("chart1",chart1);
			request.setAttribute("sqlsys",sysValue);
			request.setAttribute("sysbaseVO",sysbaseVO);
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/sybasecap.jsp";
    }
	
	private String sybasedb()
    {    	   
		DBVo vo = new DBVo();
		
		
		Hashtable sysValue = new Hashtable();
		Hashtable sValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		double avgpingcon = 0;
		String id = getParaValue("id");
		request.setAttribute("id", id);
		try{
			DBDao dao = new DBDao();
			try{
				vo = (DBVo)dao.findByID(id);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			
			
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if(vo.getManaged() == 0)managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "<font color=red>服务停止</font>";
		/*	dao = new DBDao();
			try{
				if(dao.getSysbaseIsOk(vo.getIpAddress(), vo.getUser(), vo.getPassword(), Integer.parseInt(vo.getPort()))){
					runstr = "正在运行";
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}*/
//			sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			if(sysValue!=null&&sysValue.size()>0){
//				if(sysValue.containsKey(vo.getIpAddress())){
//					sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//					if(sValue.containsKey("status")){
//						String p_status = (String)sValue.get("status");
//						if(p_status != null && p_status.length()>0){
//							if("1".equalsIgnoreCase(p_status)){
//								runstr = "正在运行";
//							}
//						}
//					}
//				}
//			}
			//获取sybase信息
			SybaseVO sysbaseVO = new SybaseVO();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex+":"+vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip); 
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
				status = (String)tempStatusHashtable.get("status");
			}
			if(status.equals("1")){
				runstr = "正在运行";
			}
			if(dao != null){
				dao.close();
			}
			request.setAttribute("runstr", runstr);
			//sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			SybaseVO sysbaseVO = (SybaseVO)sValue.get("sysbaseVO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpAddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}
			//画图
			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
			//p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);

			//ping平均值
			//maxhash = new Hashtable();
			//maxhash.put("avgpingcon",pingconavg);

			//imgurlhash
			//imgurlhash.put("ConnectUtilization","resource/image/jfreechart/"+newip+"ConnectUtilization"+".png");    
			
			  avgpingcon = new Double(pingconavg+"").doubleValue();
			  String chart1 = null,chart2 = null,chart3 = null,responseTime = null;
			  DefaultPieDataset dpd = new DefaultPieDataset();
			  dpd.setValue("可用率",avgpingcon);
			  dpd.setValue("不可用率",100 - avgpingcon);
			  chart1 = ChartCreator.createPieChart(dpd,"",130,130); 
			
			request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("max",maxhash);
			request.setAttribute("chart1",chart1);
			request.setAttribute("sqlsys",sysValue);
			request.setAttribute("sysbaseVO",sysbaseVO);
		        
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/sybasedb.jsp";
    }
	
	private String sybasedevice()
    {    	   
		DBVo vo = new DBVo();
		
		
		Hashtable sysValue = new Hashtable();
		Hashtable sValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		double avgpingcon = 0;
		String id = getParaValue("id");
		request.setAttribute("id", id);
		try{
			DBDao dao = new DBDao();
			try{
				vo = (DBVo)dao.findByID(id);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			
			
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if(vo.getManaged() == 0)managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "<font color=red>服务停止</font>";
				/*dao = new DBDao();
			try{
				if(dao.getSysbaseIsOk(vo.getIpAddress(), vo.getUser(), vo.getPassword(), Integer.parseInt(vo.getPort()))){
					runstr = "正在运行";
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}*/
//			sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			if(sysValue!=null&&sysValue.size()>0){
//				if(sysValue.containsKey(vo.getIpAddress())){
//					sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//					if(sValue.containsKey("status")){
//						String p_status = (String)sValue.get("status");
//						if(p_status != null && p_status.length()>0){
//							if("1".equalsIgnoreCase(p_status)){
//								runstr = "正在运行";
//							}
//						}
//					}
//				}
//			}
			//获取sybase信息
			SybaseVO sysbaseVO = new SybaseVO();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex+":"+vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip); 
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
				status = (String)tempStatusHashtable.get("status");
			}
			if(status.equals("1")){
				runstr = "正在运行";
			}
			if(dao != null){
				dao.close();
			}
			request.setAttribute("runstr", runstr);
			//sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			SybaseVO sysbaseVO = (SybaseVO)sValue.get("sysbaseVO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpAddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}   
			
			  avgpingcon = new Double(pingconavg+"").doubleValue();
			  String chart1 = null,chart2 = null,chart3 = null,responseTime = null;
			  DefaultPieDataset dpd = new DefaultPieDataset();
			  dpd.setValue("可用率",avgpingcon);
			  dpd.setValue("不可用率",100 - avgpingcon);
			  chart1 = ChartCreator.createPieChart(dpd,"",130,130); 
			
			request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("max",maxhash);
			request.setAttribute("chart1",chart1);
			request.setAttribute("sqlsys",sysValue);
			request.setAttribute("sysbaseVO",sysbaseVO);
		        
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/sybasedevice.jsp";
    }
	
	private String sybaseproc()
    {    	   
		DBVo vo = new DBVo();
		
		
		Hashtable sysValue = new Hashtable();
		Hashtable sValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		double avgpingcon = 0;
		String id = getParaValue("id");
		request.setAttribute("id", id);
		try{
			DBDao dao = new DBDao();
			try{
				vo = (DBVo)dao.findByID(id);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			
			
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if(vo.getManaged() == 0)managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "<font color=red>服务停止</font>";
				/*dao = new DBDao();
			try{
				if(dao.getSysbaseIsOk(vo.getIpAddress(), vo.getUser(), vo.getPassword(), Integer.parseInt(vo.getPort()))){
					runstr = "正在运行";
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}*/
//			sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			if(sysValue!=null&&sysValue.size()>0){
//				if(sysValue.containsKey(vo.getIpAddress())){
//					sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//					if(sValue.containsKey("status")){
//						String p_status = (String)sValue.get("status");
//						if(p_status != null && p_status.length()>0){
//							if("1".equalsIgnoreCase(p_status)){
//								runstr = "正在运行";
//							}
//						}
//					}
//				}
//			}
			//获取sybase信息
			SybaseVO sysbaseVO = new SybaseVO();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex+":"+vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip); 
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
				status = (String)tempStatusHashtable.get("status");
			}
			if(status.equals("1")){
				runstr = "正在运行";
			}
			if(dao != null){
				dao.close();
			}
			request.setAttribute("runstr", runstr);
			//sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			SybaseVO sysbaseVO = (SybaseVO)sValue.get("sysbaseVO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpAddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}   
			
			  avgpingcon = new Double(pingconavg+"").doubleValue();
			  String chart1 = null,chart2 = null,chart3 = null,responseTime = null;
			  DefaultPieDataset dpd = new DefaultPieDataset();
			  dpd.setValue("可用率",avgpingcon);
			  dpd.setValue("不可用率",100 - avgpingcon);
			  chart1 = ChartCreator.createPieChart(dpd,"",130,130); 
			
			request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("max",maxhash);
			request.setAttribute("chart1",chart1);
			request.setAttribute("sqlsys",sysValue);
			request.setAttribute("sysbaseVO",sysbaseVO);
		        
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/sybaseprocess.jsp";
    }
	
	private String sybaseengine()
    {    	   
		DBVo vo = new DBVo();
		
		
		Hashtable sysValue = new Hashtable();
		Hashtable sValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		double avgpingcon = 0;
		String id = getParaValue("id");
		request.setAttribute("id", id);
		try{
			DBDao dao = new DBDao();
			try{
				vo = (DBVo)dao.findByID(id);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			
			
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if(vo.getManaged() == 0)managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "<font color=red>服务停止</font>";
				/*dao = new DBDao();
			try{
				if(dao.getSysbaseIsOk(vo.getIpAddress(), vo.getUser(), vo.getPassword(), Integer.parseInt(vo.getPort()))){
					runstr = "正在运行";
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}*/
//			sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			if(sysValue!=null&&sysValue.size()>0){
//				if(sysValue.containsKey(vo.getIpAddress())){
//					sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//					if(sValue.containsKey("status")){
//						String p_status = (String)sValue.get("status");
//						if(p_status != null && p_status.length()>0){
//							if("1".equalsIgnoreCase(p_status)){
//								runstr = "正在运行";
//							}
//						}
//					}
//				}
//			}
			//获取sybase信息
			SybaseVO sysbaseVO = new SybaseVO();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex+":"+vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip); 
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
				status = (String)tempStatusHashtable.get("status");
			}
			if(status.equals("1")){
				runstr = "正在运行";
			}
			if(dao != null){
				dao.close();
			}
			request.setAttribute("runstr", runstr);
			//sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			SybaseVO sysbaseVO = (SybaseVO)sValue.get("sysbaseVO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpAddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}   
			
			  avgpingcon = new Double(pingconavg+"").doubleValue();
			  String chart1 = null,chart2 = null,chart3 = null,responseTime = null;
			  DefaultPieDataset dpd = new DefaultPieDataset();
			  dpd.setValue("可用率",avgpingcon);
			  dpd.setValue("不可用率",100 - avgpingcon);
			  chart1 = ChartCreator.createPieChart(dpd,"",130,130); 
			
			request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("max",maxhash);
			request.setAttribute("chart1",chart1);
			request.setAttribute("sqlsys",sysValue);
			request.setAttribute("sysbaseVO",sysbaseVO);
		        
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/sybaseengine.jsp";
    }
	
	private String sybaseevent()
    {    	   
		DBVo vo = new DBVo();
		
		Hashtable sysValue = new Hashtable();
		Hashtable sValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		double avgpingcon = 0;
		//String flag = "";
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		String flag = getParaValue("flag");
		request.setAttribute("flag",flag);
		String id = getParaValue("id");
		request.setAttribute("id", id);
		try{
			DBDao dao = new DBDao();
			try{
				vo = (DBVo)dao.findByID(id);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			
			
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if(vo.getManaged() == 0)managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "<font color=red>服务停止</font>";
				/*dao = new DBDao();
			try{
				if(dao.getSysbaseIsOk(vo.getIpAddress(), vo.getUser(), vo.getPassword(), Integer.parseInt(vo.getPort()))){
					runstr = "正在运行";
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}*/
//			sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			if(sysValue!=null&&sysValue.size()>0){
//				if(sysValue.containsKey(vo.getIpAddress())){
//					sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//					if(sValue.containsKey("status")){
//						String p_status = (String)sValue.get("status");
//						if(p_status != null && p_status.length()>0){
//							if("1".equalsIgnoreCase(p_status)){
//								runstr = "正在运行";
//							}
//						}
//					}
//				}
//			}
			//获取sybase信息
			SybaseVO sysbaseVO = new SybaseVO();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex+":"+vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip); 
			String statusStr = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
				statusStr = (String)tempStatusHashtable.get("status");
			}
			if(statusStr.equals("1")){
				runstr = "正在运行";
			}
			if(dao != null){
				dao.close();
			}
			request.setAttribute("runstr", runstr);
			//sysValue = ShareData.getSysbasedata();
			//sValue = (Hashtable)sysValue.get(vo.getIpAddress());
//			SybaseVO sysbaseVO = (SybaseVO)sValue.get("sysbaseVO");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpAddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			String strStartDay = getParaValue("startdate");
			String strToDay = getParaValue("todate");
			if(strStartDay!=null && !"".equals(strStartDay)){
			starttime1 = strStartDay+" 00:00:00";
			}
			if(strToDay!=null && !"".equals(strToDay)){
			totime1 = strToDay+" 23:59:59";
			}

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}   
			
			  avgpingcon = new Double(pingconavg+"").doubleValue();
			  String chart1 = null,chart2 = null,chart3 = null,responseTime = null;
			  DefaultPieDataset dpd = new DefaultPieDataset();
			  dpd.setValue("可用率",avgpingcon);
			  dpd.setValue("不可用率",100 - avgpingcon);
			  chart1 = ChartCreator.createPieChart(dpd,"",130,130); 
			
		    	status = getParaIntValue("status");
		    	level1 = getParaIntValue("level1");
		    	if(status == -1)status=99;
		    	if(level1 == -1)level1=99;
		    	request.setAttribute("status", status);
		    	request.setAttribute("level1", level1);
		    	
		    	b_time = getParaValue("startdate");
				t_time = getParaValue("todate");
		    	
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				if (b_time == null){
					b_time = sdf1.format(new Date());
				}
				if (t_time == null){
					t_time = sdf1.format(new Date());
				}
				try{
					User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
					EventListDao eventdao = new EventListDao();
					try{
						list = eventdao.getQuery(starttime1,totime1,"db",status+"",level1+"",user.getBusinessids(),vo.getId());
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						eventdao.close();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				request.setAttribute("list", list);
				request.setAttribute("startdate", b_time);
				request.setAttribute("todate", t_time);
			request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("max",maxhash);
			request.setAttribute("chart1",chart1);
			request.setAttribute("sqlsys",sysValue);
			request.setAttribute("sysbaseVO",sysbaseVO);
			
		        
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/sybaseevent.jsp";
    }


	
	public String execute(String action) 
	{	    
        if(action.equals("ready_add"))
        	return "/application/db/add.jsp";
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new DBDao();
    	    setTarget("/application/db/edit.jsp");
            return readyEdit(dao);
        }
        if(action.equals("sybaseping"))
        	return sybasecap();
        if(action.equals("sybasecap"))
            return sybasecap();
        if(action.equals("sybasedb"))
            return sybasedb();
        if(action.equals("sybasedevice"))
            return sybasedevice();
        if(action.equals("sybaseproc"))
            return sybaseproc();
        if(action.equals("sybaseengine"))
            return sybaseengine();
        if(action.equals("sybaseevent"))
            return sybaseevent();
        if(action.equals("sychronizeData"))
        {
        	return sychronizeData();
        }
        if(action.equals("isDatabaseOK"))
        	return isDatabaseOK();
        if(action.equals("sybaseManagerPingReport")){
        	return sybaseManagerPingReport();
        }
        if(action.equals("sybaseManagerPingReportQuery")){
        	return sybaseManagerPingReportQuery();
        }
        if(action.equals("sybaseManagerNatureReport")){
        	return sybaseManagerNatureReport();
        }
        if(action.equals("sybaseManagerNatureReportQuery")){
        	return sybaseManagerNatureReportQuery();
        }
        if(action.equals("sybaseManagerCldReport")){
        	return sybaseManagerCldReport();
        }
        if(action.equals("sybaseManagerCldReportQuery")){
        	return sybaseManagerCldReportQuery();
        }
        if(action.equals("sybaseManagerEventReport")){
        	return sybaseManagerEventReport();
        }
        if(action.equals("sybaseManagerEventReportQuery")){
        	return sybaseManagerEventReportQuery();
        }
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	   private void p_draw_line(Hashtable hash,String title1,String title2,int w,int h){
	    	List list = (List)hash.get("list");
	    	try{
	    	if(list==null || list.size()==0){
	    		draw_blank(title1,title2,w,h);
	    	}
	    	else{
	    	String unit = (String)hash.get("unit");
	    	if (unit == null)unit="%";
	    	ChartGraph cg = new ChartGraph();
	    	
	    	TimeSeries ss = new TimeSeries(title1,Minute.class);
	    	TimeSeries[] s = {ss};
	    	for(int j=0; j<list.size(); j++){
	    			Vector v = (Vector)list.get(j);
	    			Double	d=new Double((String)v.get(0));			
	    			String dt = (String)v.get(1);
	    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    			Date time1 = sdf.parse(dt);				
	    			Calendar temp = Calendar.getInstance();
	    			temp.setTime(time1);
	    			Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
	    			ss.addOrUpdate(minute,d);
	    	}
	    	cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
	    	
	    	
	    	
	    	
	    	}
	    	hash = null;
	    	}
	    	catch(Exception e){e.printStackTrace();}
	    	}
	    
	    private void draw_blank(String title1,String title2,int w,int h){
	    	ChartGraph cg = new ChartGraph();
	    	TimeSeries ss = new TimeSeries(title1,Minute.class);
	    	TimeSeries[] s = {ss};
	    	try{
	    		Calendar temp = Calendar.getInstance();
	    		Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
	    		ss.addOrUpdate(minute,null);
	    		cg.timewave(s,"x(时间)","y",title1,title2,w,h);
	    	}
	    	catch(Exception e){e.printStackTrace();}
	    }
		static{
			rsc_type_ht.put("1","NULL 资源（未使用）");
			rsc_type_ht.put("2","数据库");
			rsc_type_ht.put("3","文件");
			rsc_type_ht.put("4","索引");
			rsc_type_ht.put("5","表");
			rsc_type_ht.put("6","页");
			rsc_type_ht.put("7","键");
			rsc_type_ht.put("8","扩展盘区");
			rsc_type_ht.put("9","RID（行 ID)");
			rsc_type_ht.put("10","应用程序");

			req_mode_ht.put("0","NULL");
			req_mode_ht.put("1","Sch-S");
			req_mode_ht.put("2","Sch-M");
			req_mode_ht.put("3","S");
			req_mode_ht.put("4","U");
			req_mode_ht.put("5","X");
			req_mode_ht.put("6","IS");
			req_mode_ht.put("7","IU");
			req_mode_ht.put("8","IX");
			req_mode_ht.put("9","SIU");
			req_mode_ht.put("10","SIX");
			req_mode_ht.put("11","UIX");
			req_mode_ht.put("12","BU");
			req_mode_ht.put("13","RangeS_S");
			req_mode_ht.put("14","RangeS_U");
			req_mode_ht.put("15","RangeI_N");
			req_mode_ht.put("16","RangeI_S");
			req_mode_ht.put("17","RangeI_U");
			req_mode_ht.put("18","RangeI_X");
			req_mode_ht.put("19","RangeX_S");
			req_mode_ht.put("20","RangeX_U");
			req_mode_ht.put("21","RangeX_X");
			
			req_status_ht.put("1","已授予	");
			req_status_ht.put("2","正在转换");
			req_status_ht.put("3","正在等待");
			
			req_ownertype_ht.put("1","事务");
			req_ownertype_ht.put("2","游标");
			req_ownertype_ht.put("3","会话");
			req_ownertype_ht.put("4","ExSession");
			
			mode_ht.put("0","不授权访问资源");
			mode_ht.put("1","架构稳定性，确保不在任何会话控制架构元素上的架构稳定性锁时除去架构元素，如表或索引。");
			mode_ht.put("2","架构修改，必须由任何要更改指定资源架构的会话进行控制。确保没有其它的会话正在引用指定的对象。");
			mode_ht.put("3","共享，授权控制会话对资源进行共享访问。");
			mode_ht.put("4","更新，表示在最终可能更新的资源上获取更新锁。用于防止常见形式的死锁，这类死锁在多个会话锁定资源并且稍后可能更新资源时发生。");
			mode_ht.put("5","排它，授权控制会话对资源进行排它访问。");
			mode_ht.put("6","意向共享，表示有意将 S 锁放置在锁层次结构内的某个从属资源上。");
			mode_ht.put("7","意向更新，表示有意将 U 锁放置在锁层次结构内的某个从属资源上。");
			mode_ht.put("8","意向排它，表示有意将 X 锁放置在锁层次结构内的某个从属资源上。");
			mode_ht.put("9","共享意向更新，表示对有意在锁层次结构内的从属资源上获取更新锁的资源进行共享访问。");
			mode_ht.put("10","共享意向排它，表示对有意在锁层次结构内的从属资源上获取排它锁的资源进行共享访问。");
			mode_ht.put("11","更新意向排它，表示更新锁控制有意在锁层次结构内的从属资源上获取排它锁的资源。");
			mode_ht.put("12","大容量操作");
			mode_ht.put("13","共享键范围和共享资源锁，表示可串行范围扫描。");
			mode_ht.put("14","共享键范围和更新资源锁，表示可串行更新扫描。");
			mode_ht.put("15","插入键范围和空资源锁，用于在索引中插入新键之前测试范围。");
			mode_ht.put("16","通过 RangeI_N 和 S 锁的重叠创建的键范围转换锁");
			mode_ht.put("17","通过 RangeI_N 和 U 锁的重叠创建的键范围转换锁");
			mode_ht.put("18","通过 RangeI_N 和 X 锁的重叠创建的键范围转换锁");
			mode_ht.put("19","通过 RangeI_N 和 RangeS_S 锁的重叠创建的键范围转换锁");
			mode_ht.put("20","通过 RangeI_N 和 RangeS_U 锁的重叠创建的键范围转换锁");
			mode_ht.put("21","排它键范围和排它资源锁，该转换锁在更新范围中的键时使用。");
		}
		private String sychronizeData()
		{
			DBRefreshHelper dbRefreshHelper = new DBRefreshHelper();
			
			String dbvoId = request.getParameter("id");
			String dbPage = request.getParameter("dbPage");
			DBDao dbDao = new DBDao();
			DBVo dbVo = (DBVo)dbDao.findByID(dbvoId);
			dbRefreshHelper.execute(dbVo);
			if(dbPage.equals("sybasecap"))
			{
				return sybasecap();
			}
			if(dbPage.equals("sybasedb"))
			{
				return sybasedb();
			}
			if(dbPage.equals("sybasedevice"))
			{
				return sybasedevice();
			}
			if(dbPage.equals("sybaseevent"))
			{
				return sybaseevent();
			}
			
			return "/application/db/sybasecap.jsp";
		}
		private String isDatabaseOK()
		{
			DBDao dbdao = null;
			OraclePartsDao oraclePartsDao = null;
			String myip = request.getParameter("myip");
			String myport = request.getParameter("myport");
			int port = Integer.parseInt(myport);
			String myUser = request.getParameter("myUser");
			String myPassword = request.getParameter("myPassword");
			String id = request.getParameter("id");
			String dbType = request.getParameter("dbType");
			request.setAttribute("dbType", dbType);
			DBVo vo =  null;
			boolean sybaseIsOK = false;
			try {
				//ddddddd
				dbdao = new DBDao();
				vo = (DBVo)dbdao.findByID(id);
				String dbname = vo.getDbName();
				sybaseIsOK = dbdao.getSysbaseIsOk(myip, myUser, myPassword, port);
				request.setAttribute("dbname", dbname);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dbdao != null)
					dbdao.close();
			}
			request.setAttribute("oracleIsOK", sybaseIsOK);
			return "/tool/dbisok.jsp";
		}
		/**
		 * @author HONGLI
		 * date 2010-11-11
		 * Sybase可用性报表页
		 * @return
		 */
		public String sybaseManagerPingReport(){
			SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd"); 
			Date d = new Date();
			String startdate = getParaValue("startdate");
			if(startdate==null){
				startdate = sdf0.format(d);
			}else {
				try {
					startdate = sdf0.format(sdf0.parse(getParaValue("startdate")));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			String todate = getParaValue("todate");
			if(todate==null){
				todate = sdf0.format(d);
			}else {
				try {
					todate = sdf0.format(sdf0.parse(getParaValue("todate")));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			String starttime = startdate + " 00:00:00";
			String totime = todate + " 23:59:59";
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			DBVo vo = new DBVo();
			String id = (String)session.getAttribute("id");
			double avgpingcon = 0;
			String pingnow = "0.0";//当前连通率
			String pingmin = "0.0";//最小连通率
			String pingmax = "0.0";//最大连通率
			try{
				DBDao dao = new DBDao();
				try{
					vo = (DBVo)dao.findByID(id);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dao.close();
				}
				DBTypeDao typedao = new DBTypeDao();
				DBTypeVo typevo = null;
				try{
					typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					typedao.close();
				}
				request.setAttribute("db", vo);
				request.setAttribute("IpAddress", vo.getIpAddress());
				request.setAttribute("dbtye", typevo.getDbdesc());
//				Hashtable allsqlserverdata = ShareData.getSysbasedata();
//				Hashtable ipsqlserverdata = new Hashtable();
//				if(allsqlserverdata != null && allsqlserverdata.size()>0){
//					if(allsqlserverdata.containsKey(vo.getIpAddress())){
//						ipsqlserverdata = (Hashtable)allsqlserverdata.get(vo.getIpAddress());
//						if(ipsqlserverdata.containsKey("status")){
//							String p_status = (String)ipsqlserverdata.get("status");
//							if(p_status != null && p_status.length()>0){
//								if("1".equalsIgnoreCase(p_status)){
//									pingnow = "100.0";
//								}
//							}
//						}
//					}
//				}
				//获取sybase信息
				SybaseVO sysbaseVO = new SybaseVO();
				IpTranslation tranfer = new IpTranslation();
				String hex = tranfer.formIpToHex(vo.getIpAddress());
				dao = new DBDao();
				String serverip = hex+":"+vo.getId();
				sysbaseVO = dao.getSybaseDataByServerip(serverip); 
				String status = "0";
				Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
				if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
					status = (String)tempStatusHashtable.get("status");
				}
				if(status.equals("1")){
					pingnow = "100.0";
				}
				if(dao != null){
					dao.close();
				}
				String newip=SysUtil.doip(vo.getIpAddress());	
				request.setAttribute("newIp", newip);
				Hashtable ConnectUtilizationhash = new Hashtable();
				I_HostCollectData hostmanager=new HostCollectDataManager();
				try{
					ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime,totime);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				String pingconavg = "";
				if (ConnectUtilizationhash.get("avgpingcon")!=null){
					pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				}
				if(pingconavg != null){
					pingconavg = pingconavg.replace("%", "");//平均连通率
				}
				pingmax = (String)ConnectUtilizationhash.get("pingMax");//最大连通率
				pingmin = (String)ConnectUtilizationhash.get("pingmax");//最小连通率
				
				if(pingmin != null){
					pingmin = pingmin.replace("%", "");//最小连通率
				}
				avgpingcon = new Double(pingconavg+"").doubleValue();
				
				p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);//画图
			}catch(Exception e){
				e.printStackTrace();
			}
			request.setAttribute("avgpingcon", avgpingcon);
			request.setAttribute("pingmax", pingmax);
			request.setAttribute("pingmin", pingmin);
			request.setAttribute("pingnow",pingnow); 
			return "/capreport/db/showDbSybaseReport.jsp";
		}
		
		/**
		 * @author HONGLI 
		 * date 2010-11-11
		 * Sybase可用性报表 按时间查询
		 * @return
		 */
		public String sybaseManagerPingReportQuery(){
			return sybaseManagerPingReport();
		}
		
		/**
		 *@author HONGLI 
		 * date 2010-11-11
		 * 性能报表 
		 * @return
		 */
		public String sybaseManagerNatureReport(){
			SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd"); 
			Date d = new Date();
			String startdate = getParaValue("startdate");
			if(startdate==null){
				startdate = sdf0.format(d);
			}else {
				try {
					startdate = sdf0.format(sdf0.parse(getParaValue("startdate")));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			String todate = getParaValue("todate");
			if(todate==null){
				todate = sdf0.format(d);
			}else {
				try {
					todate = sdf0.format(sdf0.parse(getParaValue("todate")));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			String starttime = startdate + " 00:00:00";
			String totime = todate + " 23:59:59";
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			DBVo vo = new DBVo();
			String id = (String)session.getAttribute("id");
			double avgpingcon = 0;
			String pingnow = "0.0";//当前连通率
			String pingmin = "0.0";//最小连通率
			String pingmax = "0.0";//最大连通率
			Hashtable dbValue = new Hashtable();
			SybaseVO sysbaseVO = new SybaseVO();
			try{
				DBDao dao = new DBDao();
				try{
					vo = (DBVo)dao.findByID(id);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dao.close();
				}
				DBTypeDao typedao = new DBTypeDao();
				DBTypeVo typevo = null;
				try{
					typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					typedao.close();
				}
				request.setAttribute("db", vo);
				request.setAttribute("IpAddress", vo.getIpAddress());
				request.setAttribute("dbtye", typevo.getDbdesc());
//				Hashtable allsqlserverdata = ShareData.getSysbasedata();
//				Hashtable ipsqlserverdata = new Hashtable();
//				
//				if(allsqlserverdata != null && allsqlserverdata.size()>0){
//					if(allsqlserverdata.containsKey(vo.getIpAddress())){
//						ipsqlserverdata = (Hashtable)allsqlserverdata.get(vo.getIpAddress());
//						if(ipsqlserverdata.containsKey("status")){
//							String p_status = (String)ipsqlserverdata.get("status");
//							if(p_status != null && p_status.length()>0){
//								if("1".equalsIgnoreCase(p_status)){
//									pingnow = "100.0";
//								}
//							}
//						}
//						if(ipsqlserverdata.containsKey("dbValue")){
//							dbValue = (Hashtable)ipsqlserverdata.get("dbValue");
//						}
//						if(ipsqlserverdata.containsKey("sysbaseVO")){
//							sysbaseVO = (SybaseVO)ipsqlserverdata.get("sysbaseVO");
//						}
//					}
//				}
				//获取sybase信息
//				SybaseVO sysbaseVO = new SybaseVO();
				IpTranslation tranfer = new IpTranslation();
				String hex = tranfer.formIpToHex(vo.getIpAddress());
				dao = new DBDao();
				String serverip = hex+":"+vo.getId();
				sysbaseVO = dao.getSybaseDataByServerip(serverip); 
				String status = "0";
				Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
				if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
					status = (String)tempStatusHashtable.get("status");
				}
				if(status.equals("1")){
					pingnow = "100.0";
				}
				if(dao != null){
					dao.close();
				}
				
				String newip=SysUtil.doip(vo.getIpAddress());	
				request.setAttribute("newIp", newip);
				Hashtable ConnectUtilizationhash = new Hashtable();
				I_HostCollectData hostmanager=new HostCollectDataManager();
				try{
					ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime,totime);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				String pingconavg = "";
				if (ConnectUtilizationhash.get("avgpingcon")!=null){
					pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				}
				if(pingconavg != null){
					pingconavg = pingconavg.replace("%", "");//平均连通率
				}
				pingmax = (String)ConnectUtilizationhash.get("pingMax");//最大连通率
				pingmin = (String)ConnectUtilizationhash.get("pingmax");//最小连通率
				if(pingmax == null || !"".equals(pingmax)){
					pingmax = pingmax.replace("%", "");//平均连通率
				}
				if(pingmin != null || !"".equals(pingmin)){
					pingmin = pingmin.replace("%", "");//平均连通率
				}
				avgpingcon = new Double(pingconavg+"").doubleValue();
				
				p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);//画图
			}catch(Exception e){
				e.printStackTrace();
			}
			request.setAttribute("sysbaseVO", sysbaseVO);
			request.setAttribute("dbValue", dbValue);
			request.setAttribute("avgpingcon", avgpingcon+"");
			request.setAttribute("pingmax", pingmax);
			request.setAttribute("pingmin", pingmin);
			request.setAttribute("pingnow",pingnow); 
			return "/capreport/db/showDbSybaseNatureReport.jsp";
		}
		
		/**
		 *@author HONGLI 
		 * date 2010-11-11
		 * 性能报表  按日期查询
		 * @return
		 */
		public String sybaseManagerNatureReportQuery(){
			return sybaseManagerNatureReport();
		}
		
		/**
		 *@author HONGLI 
		 * date 2010-11-11
		 * 综合报表
		 * @return
		 */
		public String sybaseManagerCldReport(){
			SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd"); 
			Date d = new Date();
			String startdate = getParaValue("startdate");
			if(startdate==null){
				startdate = sdf0.format(d);
			}else {
				try {
					startdate = sdf0.format(sdf0.parse(getParaValue("startdate")));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			String todate = getParaValue("todate");
			if(todate==null){
				todate = sdf0.format(d);
			}else {
				try {
					todate = sdf0.format(sdf0.parse(getParaValue("todate")));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			String starttime = startdate + " 00:00:00";
			String totime = todate + " 23:59:59";
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			DBVo vo = new DBVo();
			DBTypeVo typevo = null;
			String id = (String)session.getAttribute("id");
			double avgpingcon = 0;
			String pingnow = "0.0";//当前连通率
			String pingmin = "0.0";//最小连通率
			String pingmax = "0.0";//最大连通率
			String runstr = "<font color=red>服务停止</font>";
			Hashtable dbValue = new Hashtable();
			String downnum = "";
//			数据库运行等级=====================
			String grade = "优";
			Hashtable mems = new Hashtable();//内存信息
			Hashtable sysValue = new Hashtable();
			SybaseVO sysbaseVO = new SybaseVO();
			List eventList = new ArrayList();//事件列表
			try{
				DBDao dao = new DBDao();
				try{
					vo = (DBVo)dao.findByID(id);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dao.close();
				}
				DBTypeDao typedao = new DBTypeDao();
				try{
					typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					typedao.close();
				}
				request.setAttribute("db", vo);
				request.setAttribute("IpAddress", vo.getIpAddress());
				request.setAttribute("dbtye", typevo.getDbdesc());
//				Hashtable allsqlserverdata = ShareData.getSysbasedata();
//				Hashtable ipsqlserverdata = new Hashtable();
//				
//				if(allsqlserverdata != null && allsqlserverdata.size()>0){
//					if(allsqlserverdata.containsKey(vo.getIpAddress())){
//						ipsqlserverdata = (Hashtable)allsqlserverdata.get(vo.getIpAddress());
//						if(ipsqlserverdata.containsKey("status")){
//							String p_status = (String)ipsqlserverdata.get("status");
//							if(p_status != null && p_status.length()>0){
//								if("1".equalsIgnoreCase(p_status)){
//									runstr = "正在运行";
//									pingnow = "100.0";
//								}
//							}
//						}
//						if(ipsqlserverdata.containsKey("dbValue")){
//							dbValue = (Hashtable)ipsqlserverdata.get("dbValue");
//						}
//						if(ipsqlserverdata.containsKey("retValue")){
//							mems = (Hashtable)((Hashtable)ipsqlserverdata.get("retValue")).get("mems");
//						}
//						if(ipsqlserverdata.containsKey("sysValue")){
//							sysValue = (Hashtable)ipsqlserverdata.get("sysValue");
//						}
//						if(ipsqlserverdata.containsKey("sysbaseVO")){
//							sysbaseVO = (SybaseVO)ipsqlserverdata.get("sysbaseVO");
//						}
//					}
//				}
				//获取sybase信息
//				SybaseVO sysbaseVO = new SybaseVO();
				IpTranslation tranfer = new IpTranslation();
				String hex = tranfer.formIpToHex(vo.getIpAddress());
				dao = new DBDao();
				String serverip = hex+":"+vo.getId();
				sysbaseVO = dao.getSybaseDataByServerip(serverip); 
				String statusStr = "0";
				Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
				if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
					statusStr = (String)tempStatusHashtable.get("status");
				}
				if(statusStr.equals("1")){
					runstr = "正在运行";
					pingnow = "100";
				}
				if(dao != null){
					dao.close();
				}
				String newip=SysUtil.doip(vo.getIpAddress());	
				request.setAttribute("newip", newip);
				Hashtable ConnectUtilizationhash = new Hashtable();
				I_HostCollectData hostmanager=new HostCollectDataManager();
				try{
					ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime,totime);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				String pingconavg = "";
				if (ConnectUtilizationhash.get("avgpingcon")!=null){
					pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				}
				if(pingconavg != null){
					pingconavg = pingconavg.replace("%", "");//平均连通率
				}
				if (ConnectUtilizationhash.get("downnum") != null){
					downnum = (String) ConnectUtilizationhash.get("downnum");
				}
				pingmax = (String)ConnectUtilizationhash.get("pingMax");//最大连通率
				pingmin = (String)ConnectUtilizationhash.get("pingmax");//最小连通率
				avgpingcon = new Double(pingconavg+"").doubleValue();
				
				p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);//画图
				
				//得到运行等级
				DBTypeDao dbTypeDao = new DBTypeDao();
				int count = 0;
				try {
					count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
					request.setAttribute("count", count);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbTypeDao.close();
				}
				
				if (count>0) {
					grade = "良";
				}
				if (!"0".equals(downnum)) {
					grade = "差";
				}
				
//				事件列表
				int status = getParaIntValue("status");
		    	int level1 = getParaIntValue("level1");
		    	if(status == -1)status=99;
		    	if(level1 == -1)level1=99;
		    	request.setAttribute("status", status);
		    	request.setAttribute("level1", level1);
				try{
					User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
					//SysLogger.info("user businessid===="+vo.getBusinessids());
					EventListDao eventdao = new EventListDao();
					try{
						eventList = eventdao.getQuery(starttime,totime,"db",status+"",level1+"",user.getBusinessids(),vo.getId());
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						eventdao.close();
					}
					
					//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			request.setAttribute("list", eventList);
			request.setAttribute("sysbaseVO", sysbaseVO);
			request.setAttribute("sysValue", sysValue);
			request.setAttribute("downnum", downnum);
			request.setAttribute("mems", mems);
			request.setAttribute("grade", grade);
			request.setAttribute("vo", vo);
			request.setAttribute("runstr", runstr);
			request.setAttribute("typevo", typevo);
			request.setAttribute("dbValue", dbValue);
			request.setAttribute("avgpingcon", avgpingcon+"");
			request.setAttribute("pingmax", pingmax);
			request.setAttribute("pingmin", pingmin);
			request.setAttribute("pingnow",pingnow); 
			return "/capreport/db/showDbSybaseCldReport.jsp";
		}
		
		/**
		 *@author HONGLI 
		 * date 2010-11-11
		 * 综合报表  按日期查询
		 * @return
		 */
		public String sybaseManagerCldReportQuery(){
			return sybaseManagerCldReport();
		}
		
		
		/**
		 *@author HONGLI 
		 * date 2010-11-11
		 * 事件报表   
		 * @return
		 */
		public String sybaseManagerEventReport(){
			SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd"); 
			Date d = new Date();
			String startdate = getParaValue("startdate");
			if(startdate==null){
				startdate = sdf0.format(d);
			}else {
				try {
					startdate = sdf0.format(sdf0.parse(getParaValue("startdate")));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			String todate = getParaValue("todate");
			if(todate==null){
				todate = sdf0.format(d);
			}else {
				try {
					todate = sdf0.format(sdf0.parse(getParaValue("todate")));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			String starttime = startdate + " 00:00:00";
			String totime = todate + " 23:59:59";
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			DBVo vo = new DBVo();
			DBTypeVo typevo = null;
			String id = (String)session.getAttribute("id");
			String downnum = "";
			List eventList = new ArrayList();//事件列表
			try{
				DBDao dao = new DBDao();
				try{
					vo = (DBVo)dao.findByID(id);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dao.close();
				}
				DBTypeDao typedao = new DBTypeDao();
				try{
					typevo = (DBTypeVo)typedao.findByID(vo.getDbtype()+"");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					typedao.close();
				}
				String newip=SysUtil.doip(vo.getIpAddress());	
				request.setAttribute("newip", newip);
				request.setAttribute("ipaddress",vo.getIpAddress());
				Hashtable ConnectUtilizationhash = new Hashtable();
				I_HostCollectData hostmanager=new HostCollectDataManager();
				try{
					ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","SYSPing","ConnectUtilization",starttime,totime);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				if (ConnectUtilizationhash.get("downnum") != null){
					downnum = (String) ConnectUtilizationhash.get("downnum");
				}
				
				//得到运行等级
				DBTypeDao dbTypeDao = new DBTypeDao();
				int count = 0;
				try {
					count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
					request.setAttribute("count", count);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbTypeDao.close();
				}
				
//				事件列表
				int status = getParaIntValue("status");
		    	int level1 = getParaIntValue("level1");
		    	if(status == -1)status=99;
		    	if(level1 == -1)level1=99;
		    	request.setAttribute("status", status);
		    	request.setAttribute("level1", level1);
				try{
					User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
					//SysLogger.info("user businessid===="+vo.getBusinessids());
					EventListDao eventdao = new EventListDao();
					try{
						eventList = eventdao.getQuery(starttime,totime,"db",status+"",level1+"",user.getBusinessids(),vo.getId());
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						eventdao.close();
					}
					//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			request.setAttribute("list", eventList);
			request.setAttribute("downnum", downnum);
			request.setAttribute("vo", vo);
			request.setAttribute("typevo", typevo);
			return "/capreport/db/showDbSybaseEventReport.jsp";
		}
		
		/**
		 *@author HONGLI 
		 * date 2010-11-11
		 * 事件报表   按日期查询
		 * @return
		 */
		public String sybaseManagerEventReportQuery(){
			return sybaseManagerEventReport();
		}
		
}