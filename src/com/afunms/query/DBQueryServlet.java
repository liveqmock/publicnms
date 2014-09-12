package com.afunms.query;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.oracleEntitynew;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.SysLogger;

public class DBQueryServlet extends HttpServlet {

	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=gb2312");
		PrintWriter out = response.getWriter();

		String driver="";
		String url="";
		
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		String strid=request.getParameter("id");
		String sid=request.getParameter("sid");
		//SysLogger.info("id: "+strid+"==========sid:"+sid);
		int id=0;
		try{
			id=Integer.parseInt(strid);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(sid != null && sid.length()>0 && !"null".equals(sid)){
			OraclePartsDao partdao=new OraclePartsDao();
			DBDao dbdao=null;
			try{
				partdao=new OraclePartsDao();
				//OracleEntity oracle=(OracleEntity)partdao.findByID(String.valueOf(0-id));
				oracleEntitynew oracle1=(oracleEntitynew)partdao.findByID1(sid);
				//OracleEntity oracle=(OracleEntity)partdao.findByID(sid);
				dbdao=new DBDao();
				vo=(DBVo)dbdao.findByID(oracle1.getId()+"");
				//sid=String.valueOf(0-id);
				strid=vo.getId()+"";
				//oracle.getd
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(partdao!=null)
				  partdao.close();
				if(dbdao!=null)
					dbdao.close();
			}
			
		}else{
			try {
				vo = (DBVo) dao.findByID(strid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo typevo = null;
		try {
			typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		String ip = vo.getIpAddress();
		String port = vo.getPort();
		String dbName = vo.getDbName();
		String user = vo.getUser();
		String server = vo.getAlias();
		String pwd = "";
		try{
			pwd = EncryptUtil.decode(vo.getPassword());
		}catch(Exception e){
			
		}
		if (typevo.getDbtype().equalsIgnoreCase("mysql")) {

			driver="com.mysql.jdbc.Driver";
            url="jdbc:mysql://"+ip+":"+port+"/"+dbName+"?"+"useUnicode=true&characterEncoding=utf-8";
		} else if (typevo.getDbtype().equalsIgnoreCase("oracle")) {
			driver="oracle.jdbc.driver.OracleDriver";
			url="jdbc:oracle:thin:@"+ip+":"+port+":"+dbName;
		}else if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {

			driver="com.microsoft.jdbc.sqlserver.SQLServerDriver";
			url="jdbc:microsoft:sqlserver://"+ip+":"+port+";DatabaseName=model";
		}else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {

			driver="com.sybase.jdbc2.jdbc.SybDriver";
			url =" jdbc:sybase:Tds:"+ip+":"+port+"/master?charset=iso_1&jconnect_version=0";
		}else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
			driver="com.informix.jdbc.IfxDriver";
			url = "jdbc:informix-sqli://" + ip + ":" + port + "/" + dbName + ":INFORMIXSERVER=" + server + ";user=" + user
			+ ";password=" + pwd; 
		}else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
			driver="com.ibm.db2.jcc.DB2Driver";
			url="jdbc:db2://"+ip+":"+port+"/"+dbName;
		}
		QueryService service=new QueryService();
		boolean isSuccess=service.testConnection(driver, url, user, pwd);
		
		if (isSuccess) {
			ServletContext context = this.getServletContext();
			out.println("<span style='color:green;'>测试连接成功<span>");
			context.getRequestDispatcher("/tool/sqlhome.jsp?status=OK").forward(request, response);
		//	out.println("<span style='color:green;'>测试连接成功<span>");
		}else {
			out.println("<span style='color:red;'>连接失败！</span>");
		}

	}

	public DBQueryServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		execute(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		execute(request, response);
	}

	public void init() throws ServletException {
		// Put your code here
	}

}
