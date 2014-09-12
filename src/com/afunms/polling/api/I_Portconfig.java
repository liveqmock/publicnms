/*
 * Created on 2005-4-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.api;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import com.afunms.polling.om.Portconfig;
public interface I_Portconfig{
			//增加
			public boolean createPortconfig(Portconfig portconfig) throws Exception;
			public boolean createPortconfig(Vector configV) throws Exception;
			//修改
			public boolean updatePortconfig(Portconfig portconfig) throws Exception;
			//删除
			public boolean deletePortconfig(String[] id) throws Exception;
			//查询byid
			public List getByip(String ip) throws Exception;
			public List getIps() throws Exception;
			public Hashtable getIpsHash(String ipaddress) throws Exception;
			public Portconfig loadPortconfig(Integer id) throws Exception;
			public Portconfig getByipandindex(String ip,String portindex) ;
			//查询bysearchkey
			public List getBySearch(String searchfield,String searchkeyword) throws Exception;
			//查询
			public List getPortconfig() throws Exception;
			public List Search(String searchfield,String searchkeyword) throws Exception;
			public void fromLastToPortconfig() throws Exception;
			public List getByIpAndReportflag(String ip,Integer reportflag) throws Exception;
}