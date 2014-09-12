package montnets;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.SendSmsConfig;

public class SmsDao extends BaseDao implements DaoInterface{
  
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public SmsDao() {
	   super("sms_server");
		// TODO Auto-generated constructor stub
	}

	/**
	 * °´eventlist²éÕÒ
	 */
	public List findByEvent(String eventlist,String starttime,String endtime) {//yangjun
		List list = new ArrayList();
		try { 
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				rs = conn.executeQuery("select * from sms_server where eventlist like '%" + eventlist + "' and eventtime >= '" +starttime+"' and eventtime <= '"+endtime+"'");
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				rs = conn.executeQuery("select * from sms_server where eventlist like '%" + eventlist + "' and eventtime >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and eventtime <= to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')");
			}
			
			while(rs.next())
	        	list.add(loadFromRS(rs)); 	
		} catch (Exception ex) {
			SysLogger.error("SmsDao.findByEvent()", ex);
		} finally {
			conn.close();
		}
		return list;
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		SendSmsConfig vo = new SendSmsConfig();
	      try
	      {
	          vo.setId(rs.getInt("id"));
	          vo.setName(rs.getString("name"));
	          vo.setMobilenum(rs.getString("mobilenum"));
	          vo.setEventlist(rs.getString("eventlist"));
	          Calendar cal = Calendar.getInstance();
	          Date newdate = new Date();
	          newdate.setTime(rs.getTimestamp("eventtime").getTime());
	          cal.setTime(newdate);
	          vo.setEventtime(cal);

	      }
	      catch(Exception e)
	      {
	          SysLogger.error("SmsDao.loadFromRS()",e);
	          vo = null;
	      }
	      return vo;
	}

	public boolean save(BaseVo baseVo) {
		// TODO Auto-generated method stub
			SendSmsConfig vo = (SendSmsConfig)baseVo;	  
		   Date d = new Date();
		   String time=sdf.format(d);;
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into sms_server(name,mobilenum,eventlist,eventtime)values(");
		   sql.append("'");
		   sql.append(vo.getName());
		   sql.append("','");
		   sql.append(vo.getMobilenum());
		   sql.append("','");
		   sql.append(vo.getEventlist());
		   
		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			   sql.append("','");
			   sql.append(time);
			   sql.append("'");
			   sql.append(")");
		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			   sql.append("',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))");
		   }
		   
		   return saveOrUpdate(sql.toString());
	}
     
	public List loadAll()
	  {
	     List list = new ArrayList(5);
	     try
	     {
	         rs = conn.executeQuery("select * from sms_server order by id");
	         System.out.println(rs);
	         while(rs.next())
	        	list.add(loadFromRS(rs)); 
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("SmsDao:loadAll()",e);
	         list = null;
	     }
	     finally
	     {
	         conn.close();
	     }
	     return list;
	  }
	
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
