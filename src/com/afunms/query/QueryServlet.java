package com.afunms.query;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QueryServlet extends HttpServlet {

	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=gb2312");
		PrintWriter out = response.getWriter();
		String dbType = request.getParameter("dbType");
		String ip = request.getParameter("ip");
		String dbName = request.getParameter("dbName");
		String port = request.getParameter("port");
		String user = request.getParameter("user");
		String pwd = request.getParameter("pwd");
		String driver="";
		String url="";
		if (dbType.equals("MySQL")) {

			driver="com.mysql.jdbc.Driver";
            url="jdbc:mysql://"+ip+":"+port+"/"+dbName+"?"+"useUnicode=true&characterEncoding=utf-8";
		} else if (dbType.equals("Oracle")) {
			driver="oracle.jdbc.driver.OracleDriver";
			url="jdbc:oracle:thin:@"+ip+":"+port+":"+dbName;
		}else if (dbType.equals("SQL Server")) {

//			driver="com.microsoft.jdbc.sqlserver.SQLServerDriver";
//			url="jdbc:microsoft:sqlserver://"+ip+":"+port+";DatabaseName="+dbName;
			driver="net.sourceforge.jtds.jdbc.Driver";
			url="jdbc:jtds:sqlserver://"+ip+":"+port+";DatabaseName="+dbName;
		}else if (dbType.equals("Sybase")) {

			driver="com.sybase.jdbc2.jdbc.SybDriver";
			url ="jdbc:sybase:Tds:" + ip + ":" + port + "/"+dbName;
		}else if (dbType.equals("Informix")) {
			String serverName = request.getParameter("serverName");
			driver="com.informix.jdbc.IfxDriver";
			System.out.println("---:"+serverName);
			url ="jdbc:informix-sqli://"+ip+":"+port+"/"+dbName+":INFORMIXSERVER="+serverName;
//			url="jdbc:informix-sqli:10.50.13.241:9999/fbisdb:informixserver=fbis_server";
		}else if (dbType.equals("DB2")) {

			driver="com.ibm.db2.jcc.DB2Driver";
			url="jdbc:db2://" + ip + ":" + port + "/" + dbName;

		}
		QueryService service=new QueryService();
		boolean isSuccess=service.testConnection(driver, url, user, pwd);
		
		if (isSuccess) {
			ServletContext context = this.getServletContext();
			out.println("<span style='color:green;'>测试连接成功<span>");
			context.getRequestDispatcher("/tool/home.jsp?status=测试连接成功").forward(request, response);
		//	out.println("<span style='color:green;'>测试连接成功<span>");
		}else {
			out.println("<span style='color:red;'>连接失败！</span>");
		}

	}

	public QueryServlet() {
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
