package com.afunms.security.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.security.model.Symantec;
import com.afunms.security.model.SymantecLog;
import com.afunms.security.model.SymantecStat;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;

public class SymantecDao extends BaseDao
{
   private int total;    //病毒或机器总数;

   public SymantecDao()
   {
       super("nms_symantec");
	   total = 0;
   }

  //按IP或病毒名选出日志
  public List selectByContent(Symantec vo)
  {
     List list = null;
     try
     {
     	String sql = null;
        if(vo.getMachineIp()!=null)
        {
           sql = "select * from nms_symantec where machine_ip='" + vo.getMachineIp()
               + "' and substring(begintime,1,10)='" + vo.getBeginDate() + "'";
        }
        else
        {
           sql = "select * from nms_symantec where virus='" + vo.getVirus()
               + "' and substring(begintime,1,10)='" + vo.getBeginDate() + "'";
        }        
        rs = conn.executeQuery(sql);
        list = new ArrayList();
        Symantec vo1 = null;
        int i = 0;
        while(rs.next())
        {
           vo1 = new Symantec();
           vo1.setMachineIp(rs.getString("machine_ip"));         
           vo1.setVirus(rs.getString("virus"));
           vo1.setVirusFile(rs.getString("virus_file"));
           vo1.setDealWay(rs.getString("deal_way"));
           list.add(vo1);
           i++;
           if(i>100) break;  //只列出一条记录
        }
     }
     catch (Exception ex)
     {
        SysLogger.error("Error in SymantecDAO.selectByContent()");
        list = null;
     }
     finally
     {
        conn.close();
     }
     return list;
  }
 
  /**
   * 按log文件名和城市号选出日志已读的行数
   */
  public SymantecLog findLogByID(int id)
  {
     SymantecLog vo = null;
     try
     {
        rs = conn.executeQuery("select * from nms_symantec_log where id=" + id);
        if(rs.next())
        {
           vo = new SymantecLog();
           vo.setId(rs.getInt("id"));
           vo.setIp(rs.getString("ip"));
           vo.setLogFile(rs.getString("log_file"));
           vo.setLogRow(rs.getInt("log_row"));
        }
        rs.close();
     }
     catch (Exception ex)
     {
        SysLogger.error("Error in SymantecDAO.findLogByID()");
        vo = null;
     }
     return vo;
  }

  public void finish(SymantecLog slvo)
  {
    try
    {
       String sql = null;	
       sql = "update nms_symantec_log set log_file='" + slvo.getLogFile() + "',log_row=" + slvo.getLogRow()
           + ",log_time='" + SysUtil.getCurrentTime() + "',info='" + slvo.getInfo() + "' where id="
           + slvo.getId();
       conn.executeUpdate(sql);
    }
    catch (Exception ex)
    {
       SysLogger.error("Error in SymantecDAO.finish()");
    }
    finally
    {
       conn.close();
    }
  }
  
  public List virusStat(String begindate,int topn)
  {
     List listMachine = null;
     try
     {
        //先计算总共有多少种病毒
     	String sql = "select virus content,count(virus) total from nms_symantec where " 
                   + " substring(begintime,1,10)='" + begindate + "' group by virus";		     	
        rs = conn.executeQuery("select count(*) from (" + sql + ") t1");
        
        if(rs.next())
           total = rs.getInt(1);
        
        rs = conn.executeQuery(sql + " order by total desc");
        SymantecStat vo = null;
        listMachine = new ArrayList();
        int i = 0;
        while(rs.next())
        {
           vo = new SymantecStat();
           vo.setContent(rs.getString("content"));
           vo.setTotal(rs.getInt("total"));

           listMachine.add(vo);
           i++;
           if(i==topn) break;
        }
     }
     catch (Exception ex)
     {
        SysLogger.error("Error in SymantecDAO.virusMachine()");
        listMachine = null;
     }
     finally
     {
       conn.close();
     }
     return listMachine;
  }

  public List machineStat(String begindate,int topn)
  {
     List listVirus = null;
     try
     {
        //先计算总共有多少种病毒
     	String sql = "select machine_ip content,count(machine_ip) total from nms_symantec where " 
                   + " substring(begintime,1,10)='" + begindate + "' group by machine_ip";		     	
        rs = conn.executeQuery("select count(*) from (" + sql + ") t1");
        
        if(rs.next())
           total = rs.getInt(1);
        
        rs = conn.executeQuery(sql + " order by total desc");
        SymantecStat vo = null;
        listVirus = new ArrayList();
        int i = 0;
        while(rs.next())
        {
           vo = new SymantecStat();
           vo.setContent(rs.getString("content"));
           vo.setTotal(rs.getInt("total"));

           listVirus.add(vo);
           i++;
           if(i==topn) break;
        }
     }
     catch (Exception ex)
     {
        SysLogger.error("Error in SymantecDAO.virusStat()");
        listVirus = null;
     }
     finally
     {
       conn.close();
     }
     return listVirus;
  }

  public void deleteLog(String beginDate)
  {
	  try
	  {
          conn.executeUpdate("delete from nms_symantec where SUBSTRING(begintime,1,10)='" + beginDate + "'");		  
	  }
	  catch(Exception e)
	  {
		  SysLogger.error("Error in SymantecDao.deleteLog()");
	  }
	  finally
	  {
		  conn.close();
	  }
  }
      
  public int getTotal()
  {
     return total;
  }
  
  public BaseVo loadFromRS(ResultSet rs)
  {
	  return null;
  }
}
