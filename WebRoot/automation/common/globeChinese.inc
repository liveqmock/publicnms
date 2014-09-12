<!--全局变量函数-->
<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.topology.dao.HostNodeDao"%>
<%@page import="com.afunms.polling.node.Host"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%
String contextPath = request.getContextPath();
String tmp1 = request.getParameter("id"); 
String flag = request.getParameter("flag"); 
if(flag==null){flag="0";}
int ostype=0;
Host hostDetail = null;
if(tmp1 !=null){
	hostDetail = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp1));
	if(hostDetail != null)ostype=hostDetail.getOstype();
} 



String[][] hostDetailTitleFlag= {
                            {"系统信息" ,"/monitor.do?action=hostwindows&flag="+flag , "10"} ,
							{"性能信息" ,"/monitor.do?action=hostcpu&flag="+flag , "0"} ,
							{"流速信息" ,"/monitor.do?action=hostping&flag="+flag , "1" }, 
							{"进程信息","/monitor.do?action=hostproc&flag="+flag , "2" } ,
							{"ARP信息","/monitor.do?action=hostarp&flag="+flag , "3" } ,
							{"软件信息","/monitor.do?action=hostsw&flag="+flag , "4" } , 
                            {"服务信息","/monitor.do?action=hostwinservice&flag="+flag , "5" } ,
							{"syslog信息" ,"/monitor.do?action=hostsyslog&flag="+flag ,"6"},
							{"设备信息" ,"/monitor.do?action=hostdevice&flag="+flag ,"7"},
							{"存储信息" ,"/monitor.do?action=hoststorage&flag="+flag ,"8"},
							{"告警信息" ,"/monitor.do?action=hostevent&flag="+flag,"9" }
							};
String[][] hostDetailTitleFlag1 = {
							//{"流速信息" ,"/monitor.do?action=hostping" , "0" }, 
							{"性能信息" ,"/monitor.do?action=hostcpu&flag="+flag , "1"} , 
							//{"进程信息","/monitor.do?action=hostproc" , "2" } , 
							//{"SYSLOG信息" ,"/monitor.do?action=hostsyslog" ,"3"},
							{"告警信息" ,"/monitor.do?action=hostevent&flag="+flag,"9" }
							};
String[][] hostDetailTitle = null;
if(hostDetail != null && hostDetail.getCollecttype()!=3 && hostDetail.getCollecttype()!=4 && hostDetail.getCollecttype()!=8 && hostDetail.getCollecttype()!=9){
	hostDetailTitle = hostDetailTitleFlag;
}else{
	hostDetailTitle = hostDetailTitleFlag1;
}
StringBuffer hostDetailTitleSB = new StringBuffer();

hostDetailTitleSB.append("<table>");
hostDetailTitleSB.append("<tr>");
hostDetailTitleSB.append("<td width=\""+hostDetailTitle.length*72+"\">");
hostDetailTitleSB.append("<table>");
hostDetailTitleSB.append("<tr>");

for(int i = 0 ; i < hostDetailTitle.length ; i++){
	hostDetailTitleSB.append("<td id=\"hostDetailTitle-"+ hostDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + hostDetailTitle[i][1] + "\")'>" + hostDetailTitle[i][0]+ "</a></td>" );
}
hostDetailTitleSB.append("</tr>");
hostDetailTitleSB.append("</table>");
hostDetailTitleSB.append("</td>");
hostDetailTitleSB.append("</tr>");
hostDetailTitleSB.append("</table>");

String hostDetailTitleTable = hostDetailTitleSB.toString();



String[][] webDetailTitleFlag= {
							{"连通性" ,"/web.do?action=liantong" , "0" }, 
							{"页面信息" ,"/web.do?action=page" , "1"} , 
							{"路由跟踪","/web.do?action=tracert" , "3"},
							{"告警信息","/web.do?action=alarm" , "2" }
							};
String[][] webDetailTitle = webDetailTitleFlag;
StringBuffer webDetailTitleSB = new StringBuffer();
webDetailTitleSB.append("<table>");
webDetailTitleSB.append("<tr>");
webDetailTitleSB.append("<td width=\""+webDetailTitle.length*72+"\">");
webDetailTitleSB.append("<table>");
webDetailTitleSB.append("<tr>");
for(int i = 0 ; i < webDetailTitle.length ; i++){
	webDetailTitleSB.append("<td id=\"webDetailTitle-"+ webDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + webDetailTitle[i][1] + "\")'>" + webDetailTitle[i][0]+ "</a></td>" );
}
webDetailTitleSB.append("</tr>");
webDetailTitleSB.append("</table>");
webDetailTitleSB.append("</td>");
webDetailTitleSB.append("</tr>");
webDetailTitleSB.append("</table>");
String webDetailTitleTable = webDetailTitleSB.toString();

//portService
String[][] psDetailTitleFlag= {
							{"连通性" ,"/pstype.do?action=detail" , "0" }, 
							{"告警" ,"/pstype.do?action=alarm" , "1"},
							};
String[][] psDetailTitle = psDetailTitleFlag;
StringBuffer psDetailTitleSB = new StringBuffer();
psDetailTitleSB.append("<table>");
psDetailTitleSB.append("<tr>");
psDetailTitleSB.append("<td width=\""+psDetailTitle.length*72+"\">");
psDetailTitleSB.append("<table>");
psDetailTitleSB.append("<tr>");
for(int i = 0 ; i < psDetailTitle.length ; i++){
	psDetailTitleSB.append("<td id=\"psDetailTitle-"+ psDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + psDetailTitle[i][1] + "\")'>" + psDetailTitle[i][0]+ "</a></td>" );
}
psDetailTitleSB.append("</tr>");
psDetailTitleSB.append("</table>");
psDetailTitleSB.append("</td>");
psDetailTitleSB.append("</tr>");
psDetailTitleSB.append("</table>");
String psDetailTitleTable = psDetailTitleSB.toString();

//CISCO SLA
String[][] slaDetailTitleFlag= {
							{"连通性" ,"/ciscosla.do?action=detail" , "0" }, 
							{"告警" ,"/ciscosla.do?action=event" , "1"},
							};
String[][] slaDetailTitle = slaDetailTitleFlag;
StringBuffer slaDetailTitleSB = new StringBuffer();
slaDetailTitleSB.append("<table>");
slaDetailTitleSB.append("<tr>");
slaDetailTitleSB.append("<td width=\""+slaDetailTitle.length*72+"\">");
slaDetailTitleSB.append("<table>");
slaDetailTitleSB.append("<tr>");
for(int i = 0 ; i < slaDetailTitle.length ; i++){
	slaDetailTitleSB.append("<td id=\"slaDetailTitle-"+ slaDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + slaDetailTitle[i][1] + "\")'>" + slaDetailTitle[i][0]+ "</a></td>" );
}
slaDetailTitleSB.append("</tr>");
slaDetailTitleSB.append("</table>");
slaDetailTitleSB.append("</td>");
slaDetailTitleSB.append("</tr>");
slaDetailTitleSB.append("</table>");
String slaDetailTitleTable = slaDetailTitleSB.toString();

//tomcat
String[][] tomDetailTitleFlag= {
                            {"摘要信息" ,"sys()", "0" },
							{"性能信息" ,"detail()", "1" },
							{"应用","app()","2"}, 
							{"告警" ,"alarm()", "3"}
							};
String[][] tomDetailTitle = tomDetailTitleFlag;
StringBuffer tomDetailTitleSB = new StringBuffer();
tomDetailTitleSB.append("<table>");
tomDetailTitleSB.append("<tr>");
tomDetailTitleSB.append("<td width=\""+tomDetailTitle.length*72+"\">");
tomDetailTitleSB.append("<table>");
tomDetailTitleSB.append("<tr>");
for(int i = 0 ; i < tomDetailTitle.length ; i++){
	tomDetailTitleSB.append("<td id=\"tomDetailTitle-"+ tomDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick=\"" + tomDetailTitle[i][1] + "\">" + tomDetailTitle[i][0]+ "</a></td>" );
}
tomDetailTitleSB.append("</tr>");
tomDetailTitleSB.append("</table>");
tomDetailTitleSB.append("</td>");
tomDetailTitleSB.append("</tr>");
tomDetailTitleSB.append("</table>");
String tomDetailTitleTable = tomDetailTitleSB.toString();

//iis
String[][] iisDetailTitleFlag= {
							{"性能" ,"/iis.do?action=detail&flag="+flag, "0" },
							{"告警" ,"/iis.do?action=alarm&flag="+flag, "1"}
							};
String[][] iisDetailTitle = iisDetailTitleFlag;
StringBuffer iisDetailTitleSB = new StringBuffer();
iisDetailTitleSB.append("<table>");
iisDetailTitleSB.append("<tr>");
iisDetailTitleSB.append("<td width=\""+iisDetailTitle.length*72+"\">");
iisDetailTitleSB.append("<table>");
iisDetailTitleSB.append("<tr>");
for(int i = 0 ; i < iisDetailTitle.length ; i++){
	iisDetailTitleSB.append("<td id=\"iisDetailTitle-"+ iisDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + iisDetailTitle[i][1] + "\")'>" + iisDetailTitle[i][0]+ "</a></td>" );
}
iisDetailTitleSB.append("</tr>");
iisDetailTitleSB.append("</table>");
iisDetailTitleSB.append("</td>");
iisDetailTitleSB.append("</tr>");
iisDetailTitleSB.append("</table>");
String iisDetailTitleTable = iisDetailTitleSB.toString();


String[][] emailDetailTitleFlag= {
							{"连通性" ,"/mail.do?action=detail" , "0" }, 
							{"告警" ,"/mail.do?action=alarm" , "1"}
							};
String[][] emailDetailTitle = emailDetailTitleFlag;
StringBuffer emailDetailTitleSB = new StringBuffer();
emailDetailTitleSB.append("<table>");
emailDetailTitleSB.append("<tr>");
emailDetailTitleSB.append("<td width=\""+emailDetailTitle.length*72+"\">");
emailDetailTitleSB.append("<table>");
emailDetailTitleSB.append("<tr>");
for(int i = 0 ; i < emailDetailTitle.length ; i++){
	emailDetailTitleSB.append("<td id=\"emailDetailTitle-"+ emailDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + emailDetailTitle[i][1] + "\")'>" + emailDetailTitle[i][0]+ "</a></td>" );
}
emailDetailTitleSB.append("</tr>");
emailDetailTitleSB.append("</table>");
emailDetailTitleSB.append("</td>");
emailDetailTitleSB.append("</tr>");
emailDetailTitleSB.append("</table>");
String emailDetailTitleTable = emailDetailTitleSB.toString();


String[][] ftpDetailTitleFlag= {
							{"连通性" ,"/FTP.do?action=detail" , "0" }, 
							{"告警" ,"/FTP.do?action=alarm" , "1"}
							};
String[][] ftpDetailTitle = ftpDetailTitleFlag;
StringBuffer ftpDetailTitleSB = new StringBuffer();
ftpDetailTitleSB.append("<table>");
ftpDetailTitleSB.append("<tr>");
ftpDetailTitleSB.append("<td width=\""+ftpDetailTitle.length*72+"\">");
ftpDetailTitleSB.append("<table>");
ftpDetailTitleSB.append("<tr>");
for(int i = 0 ; i < ftpDetailTitle.length ; i++){
	ftpDetailTitleSB.append("<td id=\"ftpDetailTitle-"+ ftpDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + ftpDetailTitle[i][1] + "\")'>" + ftpDetailTitle[i][0]+ "</a></td>" );
}
ftpDetailTitleSB.append("</tr>");
ftpDetailTitleSB.append("</table>");
ftpDetailTitleSB.append("</td>");
ftpDetailTitleSB.append("</tr>");
ftpDetailTitleSB.append("</table>");
String ftpDetailTitleTable = ftpDetailTitleSB.toString();


String[][] tftpDetailTitleFlag= {
							{"连通性" ,"/tftp.do?action=detail" , "0" }, 
							{"告警" ,"/tftp.do?action=alarm" , "1"}
							};
String[][] tftpDetailTitle = tftpDetailTitleFlag;
StringBuffer tftpDetailTitleSB = new StringBuffer();
tftpDetailTitleSB.append("<table>");
tftpDetailTitleSB.append("<tr>");
tftpDetailTitleSB.append("<td width=\""+tftpDetailTitle.length*72+"\">");
tftpDetailTitleSB.append("<table>");
tftpDetailTitleSB.append("<tr>");
for(int i = 0 ; i < tftpDetailTitle.length ; i++){
	tftpDetailTitleSB.append("<td id=\"tftpDetailTitle-"+ tftpDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + tftpDetailTitle[i][1] + "\")'>" + tftpDetailTitle[i][0]+ "</a></td>" );
}
tftpDetailTitleSB.append("</tr>");
tftpDetailTitleSB.append("</table>");
tftpDetailTitleSB.append("</td>");
tftpDetailTitleSB.append("</tr>");
tftpDetailTitleSB.append("</table>");
String tftpDetailTitleTable = tftpDetailTitleSB.toString();

String[][] dhcpDetailTitleFlag= {
							{"连通性" ,"/dhcp.do?action=detail" , "0" }, 
							{"地址信息" ,"/dhcp.do?action=scope" , "1" },
							{"告警" ,"/dhcp.do?action=alarm" , "2"}
							};
String[][] dhcpDetailTitle = dhcpDetailTitleFlag;
StringBuffer dhcpDetailTitleSB = new StringBuffer();
dhcpDetailTitleSB.append("<table>");
dhcpDetailTitleSB.append("<tr>");
dhcpDetailTitleSB.append("<td width=\""+dhcpDetailTitle.length*72+"\">");
dhcpDetailTitleSB.append("<table>");
dhcpDetailTitleSB.append("<tr>");
for(int i = 0 ; i < dhcpDetailTitle.length ; i++){
	dhcpDetailTitleSB.append("<td id=\"dhcpDetailTitle-"+ dhcpDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + dhcpDetailTitle[i][1] + "\")'>" + dhcpDetailTitle[i][0]+ "</a></td>" );
}
dhcpDetailTitleSB.append("</tr>");
dhcpDetailTitleSB.append("</table>");
dhcpDetailTitleSB.append("</td>");
dhcpDetailTitleSB.append("</tr>");
dhcpDetailTitleSB.append("</table>");
String dhcpDetailTitleTable = dhcpDetailTitleSB.toString();


String[][] netDetailTitleFlag= {
                            
							{"系统信息" ,"/topology/network/networkview.jsp?flag="+flag , "44" },
							{"流速信息" ,"/monitor.do?action=speed&flag="+flag , "0" },
							{"性能信息" ,"/monitor.do?action=netcpu&flag="+flag , "1"} , 
							{"ARP信息","/monitor.do?action=netarp&flag="+flag , "2" } , 
//							{"FDB信息" ,"/monitor.do?action=netfdb&flag="+flag ,"3"},
							{"路由表" ,"/monitor.do?action=netroute&flag="+flag,"4" },
							{"IP列表" ,"/monitor.do?action=netiplist&flag="+flag,"5" },
//							{"动力环境" ,"/monitor.do?action=netenv&flag="+flag,"6" },
							{"告警信息" ,"/monitor.do?action=netevent&flag="+flag,"7" }
//							{"VPN信息" ,"/detail/net_ifvpn.jsp?flag="+flag ,"8" },
//							{"CBWFQ" ,"/monitor.do?action=telnetCfg&flag="+flag,"9"  },
//							{"ACL信息" ,"/monitor.do?action=telnetAcl&flag="+flag,"10"  },
//							{"流量统计" ,"/netflow.do?action=netflow&flag="+flag,"11"  }
							};
String[][] netDetailTitleFlag1 = {
							//{"流速信息" ,"/topology/network/networkview.jsp" , "0" }, 
							{"性能信息" ,"/monitor.do?action=netcpu&flag="+flag , "1"} , 
							//{"ARP信息","//monitor.do?action=netarp" , "2" } , 
							//{"FDB信息" ,"/monitor.do?action=netfdb" ,"3"},
							//{"路由表" ,"/monitor.do?action=netroute","4" },
							//{"IP列表" ,"/monitor.do?action=netiplist","5" },
							{"告警信息" ,"/monitor.do?action=netevent&flag="+flag,"7" }
							};
String[][] netDetailTitle = null;
if(hostDetail != null && hostDetail.getCollecttype()!=3 && hostDetail.getCollecttype()!=4 && hostDetail.getCollecttype()!=8 && hostDetail.getCollecttype()!=9){
	netDetailTitle = netDetailTitleFlag;
}else{
	netDetailTitle = netDetailTitleFlag1;
}
StringBuffer netDetailTitleSB = new StringBuffer();

netDetailTitleSB.append("<table>");
netDetailTitleSB.append("<tr>");
netDetailTitleSB.append("<td width=\""+netDetailTitle.length*72+"\">");
netDetailTitleSB.append("<table>");
netDetailTitleSB.append("<tr>");

for(int i = 0 ; i < netDetailTitle.length ; i++){
	netDetailTitleSB.append("<td id=\"netDetailTitle-"+ netDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + netDetailTitle[i][1] + "\")'>" + netDetailTitle[i][0]+ "</a></td>" );
}
netDetailTitleSB.append("</tr>");
netDetailTitleSB.append("</table>");
netDetailTitleSB.append("</td>");
netDetailTitleSB.append("</tr>");
netDetailTitleSB.append("</table>");

String netDetailTitleTable = netDetailTitleSB.toString();

//防火墙
String[][] firewallDetailTitleFlag= {
                            {"系统信息" ,"/topology/network/firewall.jsp?flag="+flag , "10" },
							{"流速信息" ,"/topology/network/firewallview.jsp?flag="+flag , "0" }, 
							{"性能信息" ,"/monitor.do?action=firewallcpu&flag="+flag , "1"} , 
							{"ARP信息","//monitor.do?action=firewallarp&flag="+flag , "2" } , 
							{"路由表" ,"/monitor.do?action=firewallroute&flag="+flag,"4" },
							{"IP列表" ,"/monitor.do?action=firewalliplist&flag="+flag,"5" },
							{"告警信息" ,"/monitor.do?action=firewallevent&flag="+flag,"7" }
							};
String[][] firewallDetailTitleFlag1 = {
							//{"流速信息" ,"/topology/network/networkview.jsp" , "0" }, 
							{"性能信息" ,"/monitor.do?action=netcpu&flag="+flag , "1"} , 
							//{"ARP信息","//monitor.do?action=netarp" , "2" } , 
							//{"FDB信息" ,"/monitor.do?action=netfdb" ,"3"},
							//{"路由表" ,"/monitor.do?action=netroute","4" },
							//{"IP列表" ,"/monitor.do?action=netiplist","5" },
							{"告警信息" ,"/monitor.do?action=netevent&flag="+flag,"7" }
							};
String[][] firewallDetailTitle = null;
if(hostDetail != null && hostDetail.getCollecttype()!=3 && hostDetail.getCollecttype()!=4 && hostDetail.getCollecttype()!=8 && hostDetail.getCollecttype()!=9){
	firewallDetailTitle = firewallDetailTitleFlag;
}else{
	firewallDetailTitle = firewallDetailTitleFlag1;
}
StringBuffer firewallDetailTitleSB = new StringBuffer();

firewallDetailTitleSB.append("<table>");
firewallDetailTitleSB.append("<tr>");
firewallDetailTitleSB.append("<td width=\""+firewallDetailTitle.length*72+"\">");
firewallDetailTitleSB.append("<table>");
firewallDetailTitleSB.append("<tr>");

for(int i = 0 ; i < firewallDetailTitle.length ; i++){
	firewallDetailTitleSB.append("<td id=\"firewallDetailTitle-"+ firewallDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + firewallDetailTitle[i][1] + "\")'>" + firewallDetailTitle[i][0]+ "</a></td>" );
}
firewallDetailTitleSB.append("</tr>");
firewallDetailTitleSB.append("</table>");
firewallDetailTitleSB.append("</td>");
firewallDetailTitleSB.append("</tr>");
firewallDetailTitleSB.append("</table>");

String firewallDetailTitleTable = firewallDetailTitleSB.toString(); 
  
  
//VMWare
String[][] vmwareDetailTitleFlag= {
							{"系统信息" ,"/vmware.do?action=showsys&flag="+flag , "0" },
							{"资源信息" ,"/vmware.do?action=showresource&flag="+flag , "1" }, 
							{"虚拟主机" ,"/vmware.do?action=showguesthost&flag="+flag , "2"} , 
							{"性能信息" ,"/vmware.do?action=showcap&flag="+flag , "3"} ,
							{"告警信息" ,"/vmware.do?action=vmwareevent&flag="+flag,"4" }
							};
String[][] vmwareDetailTitle = vmwareDetailTitleFlag;
StringBuffer vmwareDetailTitleSB = new StringBuffer();

vmwareDetailTitleSB.append("<table>");
vmwareDetailTitleSB.append("<tr>");
vmwareDetailTitleSB.append("<td width=\""+vmwareDetailTitle.length*72+"\">");
vmwareDetailTitleSB.append("<table>");
vmwareDetailTitleSB.append("<tr>");

for(int i = 0 ; i < vmwareDetailTitle.length ; i++){
	vmwareDetailTitleSB.append("<td id=\"vmwareDetailTitle-"+ vmwareDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + vmwareDetailTitle[i][1] + "\")'>" + vmwareDetailTitle[i][0]+ "</a></td>" );
}
vmwareDetailTitleSB.append("</tr>");
vmwareDetailTitleSB.append("</table>");
vmwareDetailTitleSB.append("</td>");
vmwareDetailTitleSB.append("</tr>");
vmwareDetailTitleSB.append("</table>");

String vmwareDetailTitleTable = vmwareDetailTitleSB.toString();   


String[][] oraDetailTitle = {
                    //        {"系统信息" ,"/oracle.do?action=oracle&urlflag=1&flag="+flag , "10" },
							{"性能信息" ,"/oracle.do?action=oracleping&urlflag=1&flag="+flag , "0" }, 
							{"锁信息" ,"/oracle.do?action=oraclelock&urlflag=1&flag="+flag , "1"} , 
							{"Session信息","/oracle.do?action=oraclesession&urlflag=1&flag="+flag , "2" } , 
							{"回滚段信息" ,"/oracle.do?action=oraclerollback&urlflag=1&flag="+flag ,"3"},
							{"表信息" ,"/oracle.do?action=oracletable&urlflag=1&flag="+flag ,"4"},
							{"表空间信息" ,"/oracle.do?action=oraclespace&urlflag=1&flag="+flag ,"5"},
							{"Top10Sql" ,"/oracle.do?action=oracletopsql&urlflag=1&flag="+flag ,"6"},
							{"用户信息" ,"/oracle.do?action=oracleuser&urlflag=1&flag="+flag ,"7"},
							{"WAIT信息" ,"/oracle.do?action=oraclewait&urlflag=1&flag="+flag ,"8"},
							{"基本信息" ,"/oracle.do?action=oraclebaseinfo&urlflag=1&flag="+flag ,"10"},
							//{"作业信息" ,"/oracle.do?action=oraclejob&urlflag=1&flag="+flag ,"11"},
							{"备份信息" ,"/oracle.do?action=oraclebackup&urlflag=1&flag="+flag ,"13"},
							{"asm信息" ,"/oracle.do?action=oracledisk&urlflag=1&flag="+flag ,"12"},							
							{"告警信息" ,"/oracle.do?action=oracleevent&urlflag=1&flag="+flag,"9" }
							};
StringBuffer oraDetailTitleSB = new StringBuffer();
 
oraDetailTitleSB.append("<table>");
oraDetailTitleSB.append("<tr>");
oraDetailTitleSB.append("<td width=\""+oraDetailTitle.length*72+"\">");
oraDetailTitleSB.append("<table>");
oraDetailTitleSB.append("<tr>");

for(int i = 0 ; i < oraDetailTitle.length ; i++){
	oraDetailTitleSB.append("<td id=\"oraDetailTitle-"+ oraDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + oraDetailTitle[i][1] + "\")'>" + oraDetailTitle[i][0]+ "</a></td>" );
}
oraDetailTitleSB.append("</tr>");
oraDetailTitleSB.append("</table>");
oraDetailTitleSB.append("</td>");
oraDetailTitleSB.append("</tr>");
oraDetailTitleSB.append("</table>");

String oraDetailTitleTable = oraDetailTitleSB.toString();



String[][] mysqlDetailTitle = {
							{"连通率" ,"/mysql.do?action=mysqlping&flag="+flag , "0" }, 
							{"连接信息" ,"/mysql.do?action=mysqlconnect&flag="+flag , "1"} , 
							{"表信息","/mysql.do?action=mysqltables&flag="+flag , "2" } , 
							{"性能信息" ,"/mysql.do?action=mysqlspace&flag="+flag ,"3"},
							{"状态信息" ,"/mysql.do?action=mysqlstatus&flag="+flag,"4" },
							{"配置信息" ,"/mysql.do?action=mysqlvariables&flag="+flag ,"5"},
							{"告警信息" ,"/mysql.do?action=mysqlevent&flag="+flag ,"6"}
							};
StringBuffer mysqlDetailTitleSB = new StringBuffer();

mysqlDetailTitleSB.append("<table>");
mysqlDetailTitleSB.append("<tr>");
mysqlDetailTitleSB.append("<td width=\""+mysqlDetailTitle.length*72+"\">");
mysqlDetailTitleSB.append("<table>");
mysqlDetailTitleSB.append("<tr>");

for(int i = 0 ; i < mysqlDetailTitle.length ; i++){
	mysqlDetailTitleSB.append("<td id=\"mysqlDetailTitle-"+ mysqlDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + mysqlDetailTitle[i][1] + "\")'>" + mysqlDetailTitle[i][0]+ "</a></td>" );
}
mysqlDetailTitleSB.append("</tr>");
mysqlDetailTitleSB.append("</table>");
mysqlDetailTitleSB.append("</td>");
mysqlDetailTitleSB.append("</tr>");
mysqlDetailTitleSB.append("</table>");

String mysqlDetailTitleTable = mysqlDetailTitleSB.toString();

////////////oraclerac
String[][] oracleRacDetailTitle = {
							{"RAC集群状态" ,"/oracle.do?action=oraclerac&flag="+flag , "0"} , 
							{"RAC集群资源" ,"/oracle.do?action=oracleraccrstatus&flag="+flag ,"1"},
							{"告警信息" ,"/oracle.do?action=oracleracevent&flag="+flag ,"2"}
							};
StringBuffer oracleRacDetailTitleSB = new StringBuffer();

oracleRacDetailTitleSB.append("<table>");
oracleRacDetailTitleSB.append("<tr>");
oracleRacDetailTitleSB.append("<td width=\""+oracleRacDetailTitle.length*72+"\">");
oracleRacDetailTitleSB.append("<table>");
oracleRacDetailTitleSB.append("<tr>");

for(int i = 0 ; i < oracleRacDetailTitle.length ; i++){
	oracleRacDetailTitleSB.append("<td id=\"mysqlDetailTitle-"+ oracleRacDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + oracleRacDetailTitle[i][1] + "\")'>" + oracleRacDetailTitle[i][0]+ "</a></td>" );
}
oracleRacDetailTitleSB.append("</tr>");
oracleRacDetailTitleSB.append("</table>");
oracleRacDetailTitleSB.append("</td>");
oracleRacDetailTitleSB.append("</tr>");
oracleRacDetailTitleSB.append("</table>");

String oracleRacDetailTitleTable = oracleRacDetailTitleSB.toString();


////////////dp
String[][] DpDetailTitle = {
							{"详细信息" ,"/dp.do?action=detail&flag="+flag , "0"} , 
							{"告警信息" ,"/dp.do?action=dpevent&flag="+flag ,"1"}
							};
StringBuffer DpDetailTitleSB = new StringBuffer();

DpDetailTitleSB.append("<table>");
DpDetailTitleSB.append("<tr>");
DpDetailTitleSB.append("<td width=\""+DpDetailTitle.length*72+"\">");
DpDetailTitleSB.append("<table>");
DpDetailTitleSB.append("<tr>");

for(int i = 0 ; i < DpDetailTitle.length ; i++){
	DpDetailTitleSB.append("<td id=\"dpDetailTitle-"+ DpDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + DpDetailTitle[i][1] + "\")'>" + DpDetailTitle[i][0]+ "</a></td>" );
}
DpDetailTitleSB.append("</tr>");
DpDetailTitleSB.append("</table>");
DpDetailTitleSB.append("</td>");
DpDetailTitleSB.append("</tr>");
DpDetailTitleSB.append("</table>");

String DpDetailTitleTable = DpDetailTitleSB.toString();

////////////ggsci
String[][] GgsciDetailTitle = {
							{"详细信息" ,"/ggsci.do?action=detail&flag="+flag , "0"} , 
							{"告警信息" ,"/ggsci.do?action=ggscievent&flag="+flag ,"1"}
							};
StringBuffer GgsciDetailTitleSB = new StringBuffer();

GgsciDetailTitleSB.append("<table>");
GgsciDetailTitleSB.append("<tr>");
GgsciDetailTitleSB.append("<td width=\""+GgsciDetailTitle.length*72+"\">");
GgsciDetailTitleSB.append("<table>");
GgsciDetailTitleSB.append("<tr>");

for(int i = 0 ; i < GgsciDetailTitle.length ; i++){
	GgsciDetailTitleSB.append("<td id=\"ggsciDetailTitle-"+ GgsciDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + GgsciDetailTitle[i][1] + "\")'>" + GgsciDetailTitle[i][0]+ "</a></td>" );
}
GgsciDetailTitleSB.append("</tr>");
GgsciDetailTitleSB.append("</table>");
GgsciDetailTitleSB.append("</td>");
GgsciDetailTitleSB.append("</tr>");
GgsciDetailTitleSB.append("</table>");

String GgsciDetailTitleTable = GgsciDetailTitleSB.toString();

////////////nas
String[][] NasDetailTitle = {
							{"详细信息" ,"/nas.do?action=detail&flag="+flag , "0"} , 
							{"告警信息" ,"/nas.do?action=nasevent&flag="+flag ,"1"}
							};
StringBuffer NasDetailTitleSB = new StringBuffer();

NasDetailTitleSB.append("<table>");
NasDetailTitleSB.append("<tr>");
NasDetailTitleSB.append("<td width=\""+NasDetailTitle.length*72+"\">");
NasDetailTitleSB.append("<table>");
NasDetailTitleSB.append("<tr>");

for(int i = 0 ; i < NasDetailTitle.length ; i++){
	NasDetailTitleSB.append("<td id=\"nasDetailTitle-"+ NasDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + NasDetailTitle[i][1] + "\")'>" + NasDetailTitle[i][0]+ "</a></td>" );
}
NasDetailTitleSB.append("</tr>");
NasDetailTitleSB.append("</table>");
NasDetailTitleSB.append("</td>");
NasDetailTitleSB.append("</tr>");
NasDetailTitleSB.append("</table>");

String NasDetailTitleTable = NasDetailTitleSB.toString();


////光纤交换机
String[][] netfcDetailTitle= {
                            
							{"系统信息" ,"/topology/network/networkview.jsp?flag="+flag , "44" },
							{"流速信息" ,"/monitor.do?action=speedfc&flag="+flag , "0" },
							//{"性能信息" ,"/monitor.do?action=netcpu&flag="+flag , "1"} , 
							//{"ARP信息","/monitor.do?action=netarpfc&flag="+flag , "2" } , 
							//{"FDB信息" ,"/monitor.do?action=netfdbfc&flag="+flag ,"3"},
							//{"路由表" ,"/monitor.do?action=netroutefc&flag="+flag,"4" },
							//{"IP列表" ,"/monitor.do?action=netiplist&flag="+flag,"5" },
							{"动力环境" ,"/monitor.do?action=netenvfc&flag="+flag,"6" },
							{"事件信息" ,"/monitor.do?action=neteventfc&flag="+flag,"8" },
							{"告警信息" ,"/monitor.do?action=neteventalarmfc&flag="+flag,"7" }
//							{"VPN信息" ,"/detail/net_ifvpn.jsp?flag="+flag ,"8" },
//							{"CBWFQ" ,"/monitor.do?action=telnetCfg&flag="+flag,"9"  },
//							{"ACL信息" ,"/monitor.do?action=telnetAcl&flag="+flag,"10"  },
//							{"流量统计" ,"/netflow.do?action=netflow&flag="+flag,"11"  }
							};

StringBuffer netfcDetailTitleSB = new StringBuffer();

netfcDetailTitleSB.append("<table>");
netfcDetailTitleSB.append("<tr>");
netfcDetailTitleSB.append("<td width=\""+netfcDetailTitle.length*72+"\">");
netfcDetailTitleSB.append("<table>");
netfcDetailTitleSB.append("<tr>");

for(int i = 0 ; i < netfcDetailTitle.length ; i++){
	netfcDetailTitleSB.append("<td id=\"netDetailTitle-"+ netfcDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + netfcDetailTitle[i][1] + "\")'>" + netfcDetailTitle[i][0]+ "</a></td>" );
}
netfcDetailTitleSB.append("</tr>");
netfcDetailTitleSB.append("</table>");
netfcDetailTitleSB.append("</td>");
netfcDetailTitleSB.append("</tr>");
netfcDetailTitleSB.append("</table>");

String netfcDetailTitleTable = netfcDetailTitleSB.toString();



String[][] sqlDetailTitle = {
							{"性能信息" ,"/sqlserver.do?action=sqlserverping&flag="+flag , "0" }, 
							{"系统信息" ,"/sqlserver.do?action=sqlserversys&flag="+flag , "1"} , 
							{"数据库信息","/sqlserver.do?action=sqlserverdb&flag="+flag , "2" } , 
							{"进程信息" ,"/sqlserver.do?action=sqlserverproc&flag="+flag ,"3"},
							{"锁信息" ,"/sqlserver.do?action=sqlserverlock&flag="+flag,"4" },
							{"WAIT信息" ,"/sqlserver.do?action=sqlserverwait&flag="+flag,"5" },
							{"告警信息" ,"/sqlserver.do?action=sqlserverevent&flag="+flag ,"6"}
							};
StringBuffer sqlDetailTitleSB = new StringBuffer();

sqlDetailTitleSB.append("<table>");
sqlDetailTitleSB.append("<tr>");
sqlDetailTitleSB.append("<td width=\""+sqlDetailTitle.length*72+"\">");
sqlDetailTitleSB.append("<table>");
sqlDetailTitleSB.append("<tr>");

for(int i = 0 ; i < sqlDetailTitle.length ; i++){
	sqlDetailTitleSB.append("<td id=\"sqlDetailTitle-"+ sqlDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + sqlDetailTitle[i][1] + "\")'>" + sqlDetailTitle[i][0]+ "</a></td>" );
}
sqlDetailTitleSB.append("</tr>");
sqlDetailTitleSB.append("</table>");
sqlDetailTitleSB.append("</td>");
sqlDetailTitleSB.append("</tr>");
sqlDetailTitleSB.append("</table>");

String sqlDetailTitleTable = sqlDetailTitleSB.toString();




String[][] sybDetailTitle = {
							{"性能信息" ,"/sybase.do?action=sybasecap&flag="+flag , "0" },
							{"数据库信息","/sybase.do?action=sybasedb&flag="+flag , "1" } , 
							{"设备信息" ,"/sybase.do?action=sybasedevice&flag="+flag ,"2"},
							{"告警信息" ,"/sybase.do?action=sybaseevent&flag="+flag ,"3"}
							};
StringBuffer sybDetailTitleSB = new StringBuffer();

sybDetailTitleSB.append("<table>");
sybDetailTitleSB.append("<tr>");
sybDetailTitleSB.append("<td width=\""+sybDetailTitle.length*72+"\">");
sybDetailTitleSB.append("<table>");
sybDetailTitleSB.append("<tr>");

for(int i = 0 ; i < sybDetailTitle.length ; i++){
	sybDetailTitleSB.append("<td id=\"sybDetailTitle-"+ sybDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + sybDetailTitle[i][1] + "\")'>" + sybDetailTitle[i][0]+ "</a></td>" );
}
sybDetailTitleSB.append("</tr>");
sybDetailTitleSB.append("</table>");
sybDetailTitleSB.append("</td>");
sybDetailTitleSB.append("</tr>");
sybDetailTitleSB.append("</table>");

String sybDetailTitleTable = sybDetailTitleSB.toString();




String[][] inforDetailTitle = {
							{"性能信息" ,"/informix.do?action=informixcap&flag="+flag , "0" },
							{"日志文件","/informix.do?action=informixdb&flag="+flag , "1" } , 
							{"配置信息" ,"/informix.do?action=informixdevice&flag="+flag ,"2"},
							{"IO信息" ,"/informix.do?action=informixio&flag="+flag ,"3"},
							{"日志信息" ,"/informix.do?action=informixlog&flag="+flag ,"4"},
							{"等待队列" ,"/informix.do?action=informixwaitingthread&flag="+flag ,"6"},
							{"onbar日志" ,"/informix.do?action=informixbaractlog&flag="+flag ,"7"},
							{"告警信息" ,"/informix.do?action=informixevent&flag="+flag ,"5"}
							};
StringBuffer inforDetailTitleSB = new StringBuffer();

inforDetailTitleSB.append("<table>");
inforDetailTitleSB.append("<tr>");
inforDetailTitleSB.append("<td width=\""+inforDetailTitle.length*72+"\">");
inforDetailTitleSB.append("<table>");
inforDetailTitleSB.append("<tr>");

for(int i = 0 ; i < inforDetailTitle.length ; i++){
	inforDetailTitleSB.append("<td id=\"inforDetailTitle-"+ inforDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + inforDetailTitle[i][1] + "\")'>" + inforDetailTitle[i][0]+ "</a></td>" );
}
inforDetailTitleSB.append("</tr>");
inforDetailTitleSB.append("</table>");
inforDetailTitleSB.append("</td>");
inforDetailTitleSB.append("</tr>");
inforDetailTitleSB.append("</table>");

String inforDetailTitleTable = inforDetailTitleSB.toString();




String[][] db2DetailTitle = {
							{"性能信息" ,"/db2.do?action=db2ping&flag="+flag , "0" }, 
							{"连接信息" ,"/db2.do?action=db2conn&flag="+flag , "1" }, 
							{"缓存信息" ,"/db2.do?action=db2pool&flag="+flag , "2"} , 
							{"锁信息" ,"/db2.do?action=db2lock&flag="+flag , "3"} , 
							{"日志信息" ,"/db2.do?action=db2log&flag="+flag , "4"} , 
							{"表性能信息" ,"/db2.do?action=db2table&flag="+flag , "5"} ,
							{"Session信息","/db2.do?action=db2session&flag="+flag , "6" } , 
							{"表空间信息" ,"/db2.do?action=db2space&flag="+flag ,"7"},
							{"告警信息" ,"/db2.do?action=db2event&flag="+flag ,"8"}
							};
StringBuffer db2DetailTitleSB = new StringBuffer();

db2DetailTitleSB.append("<table>");
db2DetailTitleSB.append("<tr>");
db2DetailTitleSB.append("<td width=\""+db2DetailTitle.length*72+"\">");
db2DetailTitleSB.append("<table>");
db2DetailTitleSB.append("<tr>");

for(int i = 0 ; i < db2DetailTitle.length ; i++){
	db2DetailTitleSB.append("<td id=\"db2DetailTitle-"+ db2DetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + db2DetailTitle[i][1] + "\")'>" + db2DetailTitle[i][0]+ "</a></td>" );
}
db2DetailTitleSB.append("</tr>");
db2DetailTitleSB.append("</table>");
db2DetailTitleSB.append("</td>");
db2DetailTitleSB.append("</tr>");
db2DetailTitleSB.append("</table>");

String db2DetailTitleTable = db2DetailTitleSB.toString();




String[][] netReportTitle = {
							{"连通率报表" ,"/netreport.do?action=list" , "0" }, 
							{"事件报表" ,"/netreport.do?action=eventlist" , "1"} , 
							{"综合报表","/netreport.do?action=netmultilist" , "2" } , 
							};
StringBuffer netReportTitleSB = new StringBuffer();

netReportTitleSB.append("<table>");
netReportTitleSB.append("<tr>");
netReportTitleSB.append("<td width=\""+netReportTitle.length*72+"\">");
netReportTitleSB.append("<table>");
netReportTitleSB.append("<tr>");

for(int i = 0 ; i < netReportTitle.length ; i++){
	netReportTitleSB.append("<td id=\"netReportTitle-"+ netReportTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + netReportTitle[i][1] + "\")'>" + netReportTitle[i][0]+ "</a></td>" );
}
netReportTitleSB.append("</tr>");
netReportTitleSB.append("</table>");
netReportTitleSB.append("</td>");
netReportTitleSB.append("</tr>");
netReportTitleSB.append("</table>");

String netReportTitleTable = netReportTitleSB.toString();

String[][] hostReportTitle = {
							{"连通率报表" ,"/hostreport.do?action=list" , "0" }, 
							{"内存报表" ,"/hostreport.do?action=memorylist" , "1"} , 
							{"磁盘报表","/hostreport.do?action=disklist" , "2" } ,
							{"CPU报表","/hostreport.do?action=cpulist" , "3" } ,
							{"事件报表","/hostreport.do?action=eventlist" , "4" } ,
							{"综合报表","/hostreport.do?action=multilist" , "5" } , 
							};
StringBuffer hostReportTitleSB = new StringBuffer();

hostReportTitleSB.append("<table>");
hostReportTitleSB.append("<tr>");
hostReportTitleSB.append("<td width=\""+hostReportTitle.length*72+"\">");
hostReportTitleSB.append("<table>");
hostReportTitleSB.append("<tr>");

for(int i = 0 ; i < hostReportTitle.length ; i++){
	hostReportTitleSB.append("<td id=\"hostReportTitle-"+ hostReportTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + hostReportTitle[i][1] + "\")'>" + hostReportTitle[i][0]+ "</a></td>" );
}
hostReportTitleSB.append("</tr>");
hostReportTitleSB.append("</table>");
hostReportTitleSB.append("</td>");
hostReportTitleSB.append("</tr>");
hostReportTitleSB.append("</table>");

String hostReportTitleTable = hostReportTitleSB.toString();

String[][] dbReportTitle = {
							{"连通率" ,"/dbreport.do?action=list&flag="+flag , "0" }, 
							{"ORACLE" ,"/dbreport.do?action=oraclelist&flag="+flag , "1"} , 
							{"DB2","/dbreport.do?action=db2list&flag="+flag , "2" } ,
							{"SQLSERVER","/dbreport.do?action=sqlserverlist&flag="+flag , "3" } ,
							{"SYBASE","/dbreport.do?action=sybaselist&flag="+flag , "4" } ,
							{"INFORMIX","/dbreport.do?action=informixlist&flag="+flag , "5" } ,
							{"MYSQL","/dbreport.do?action=mysqllist&flag="+flag , "7" } ,
							{"事件报表","/dbreport.do?action=eventlist&flag="+flag , "6" } , 
							};
StringBuffer dbReportTitleSB = new StringBuffer();

dbReportTitleSB.append("<table>");
dbReportTitleSB.append("<tr>");
dbReportTitleSB.append("<td width=\""+dbReportTitle.length*72+"\">");
dbReportTitleSB.append("<table>");
dbReportTitleSB.append("<tr>");

for(int i = 0 ; i < dbReportTitle.length ; i++){
	dbReportTitleSB.append("<td id=\"dbReportTitle-"+ dbReportTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + dbReportTitle[i][1] + "\")'>" + dbReportTitle[i][0]+ "</a></td>" );
}
dbReportTitleSB.append("</tr>");
dbReportTitleSB.append("</table>");
dbReportTitleSB.append("</td>");
dbReportTitleSB.append("</tr>");
dbReportTitleSB.append("</table>");

String dbReportTitleTable = dbReportTitleSB.toString();




String[][] linuxDetailTitle = {
                            {"系统信息" ,"/detail/host_linux.jsp?flag="+flag , "10" },
							{"性能信息" ,"/monitor.do?action=hostcpu&flag="+flag , "0"} ,
							{"流速信息" ,"/detail/host_linuxutilhdx.jsp?flag="+flag , "1" }, 
							{"文件系统","/monitor.do?action=hostping&flag="+flag , "2" } , 
							{"配置信息" ,"/monitor.do?action=hostconfig&flag="+flag ,"3"},
							{"进程信息" ,"/monitor.do?action=hostproc&flag="+flag ,"4" },
							{"服务信息" ,"/monitor.do?action=hostservice&flag="+flag ,"5" },
							{"syslog信息" ,"/monitor.do?action=hostsyslog&flag="+flag ,"6"},
							{"告警信息" ,"/monitor.do?action=hostevent&flag="+flag,"7" }
							};
StringBuffer linuxDetailTitleSB = new StringBuffer();  

linuxDetailTitleSB.append("<table>");
linuxDetailTitleSB.append("<tr>");
linuxDetailTitleSB.append("<td width=\""+linuxDetailTitle.length*72+"\">");
linuxDetailTitleSB.append("<table>");
linuxDetailTitleSB.append("<tr>");

for(int i = 0 ; i < linuxDetailTitle.length ; i++){
	linuxDetailTitleSB.append("<td id=\"linuxDetailTitle-"+ linuxDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + linuxDetailTitle[i][1] + "\")'>" + linuxDetailTitle[i][0]+ "</a></td>" );
}
linuxDetailTitleSB.append("</tr>");
linuxDetailTitleSB.append("</table>");
linuxDetailTitleSB.append("</td>");
linuxDetailTitleSB.append("</tr>");
linuxDetailTitleSB.append("</table>");

String linuxDetailTitleTable = linuxDetailTitleSB.toString();

String[][] scounixDetailTitle = {
							{"系统信息" ,"/monitor.do?action=hostwindows&flag="+flag , "0"} ,
							{"性能信息","/monitor.do?action=hostping&flag="+flag , "1" } , 
							{"磁盘信息","/monitor.do?action=hostcpu&flag="+flag , "2" } , 
							{"进程信息" ,"/monitor.do?action=hostproc&flag="+flag ,"3" },
							{"syslog信息" ,"/monitor.do?action=hostsyslog&flag="+flag ,"4"},
							{"告警信息" ,"/monitor.do?action=hostevent&flag="+flag,"5" }
							};
StringBuffer scounixDetailTitleSB = new StringBuffer();

scounixDetailTitleSB.append("<table>");
scounixDetailTitleSB.append("<tr>");
scounixDetailTitleSB.append("<td width=\""+scounixDetailTitle.length*72+"\">");
scounixDetailTitleSB.append("<table>");
scounixDetailTitleSB.append("<tr>");

for(int i = 0 ; i < scounixDetailTitle.length ; i++){
	scounixDetailTitleSB.append("<td id=\"scounixDetailTitle-"+ scounixDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + scounixDetailTitle[i][1] + "\")'>" + scounixDetailTitle[i][0]+ "</a></td>" );
}
scounixDetailTitleSB.append("</tr>");
scounixDetailTitleSB.append("</table>");
scounixDetailTitleSB.append("</td>");
scounixDetailTitleSB.append("</tr>");
scounixDetailTitleSB.append("</table>");

String scounixDetailTitleTable = scounixDetailTitleSB.toString();



String[][] aixDetailTitle = {
                            {"流速信息" ,"/detail/host_aix.jsp?flag="+flag , "10" }, 
							{"性能信息","/monitor.do?action=hostcpu&flag="+flag , "0" } ,
							{"流速信息" ,"/detail/host_aixutilhdx.jsp?flag="+flag , "1" }, 
							{"文件系统" ,"/monitor.do?action=hostping&flag="+flag , "2"} ,
							{"配置信息" ,"/monitor.do?action=hostconfig&flag="+flag ,"3"},
							{"进程信息" ,"/monitor.do?action=hostproc&flag="+flag ,"4" },
							{"服务信息" ,"/monitor.do?action=hostservice&flag="+flag ,"5" },
							{"syslog信息" ,"/monitor.do?action=hostsyslog&flag="+flag ,"6"},
							{"errpt信息" ,"/monitor.do?action=hosterrpt&flag="+flag ,"7"},
							{"路由信息" ,"/monitor.do?action=hostroute&flag="+flag ,"8"},
							{"告警信息" ,"/monitor.do?action=hostevent&flag="+flag,"9" }
							};
StringBuffer aixDetailTitleSB = new StringBuffer();

aixDetailTitleSB.append("<table>");
aixDetailTitleSB.append("<tr>");
aixDetailTitleSB.append("<td width=\""+aixDetailTitle.length*72+"\">");
aixDetailTitleSB.append("<table>");
aixDetailTitleSB.append("<tr>");

for(int i = 0 ; i < aixDetailTitle.length ; i++){
	aixDetailTitleSB.append("<td id=\"aixDetailTitle-"+ aixDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + aixDetailTitle[i][1] + "\")'>" + aixDetailTitle[i][0]+ "</a></td>" );
}
aixDetailTitleSB.append("</tr>");
aixDetailTitleSB.append("</table>");
aixDetailTitleSB.append("</td>");
aixDetailTitleSB.append("</tr>");
aixDetailTitleSB.append("</table>");

String aixDetailTitleTable = aixDetailTitleSB.toString();


String[][] upsDetailTitle = {
                            {"系统信息" ,"/ups.do?action=tosysinfo&flag="+flag , "0"} ,
							{"性能信息" ,"/ups.do?action=toDetail&flag="+flag , "1"} ,
							{"计量信息" ,"/ups.do?action=current&flag="+flag,"2" },
							{"告警信息" ,"/ups.do?action=event&flag="+flag,"3" }
							};

StringBuffer upsDetailTitleSB = new StringBuffer();

upsDetailTitleSB.append("<table>");
upsDetailTitleSB.append("<tr>");
upsDetailTitleSB.append("<td width=\""+upsDetailTitle.length*72+"\">");
upsDetailTitleSB.append("<table>");
upsDetailTitleSB.append("<tr>");

for(int i = 0 ; i < upsDetailTitle.length ; i++){
	upsDetailTitleSB.append("<td id=\"upsDetailTitle-"+ upsDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + upsDetailTitle[i][1] + "\")'>" + upsDetailTitle[i][0]+ "</a></td>" );
}
upsDetailTitleSB.append("</tr>");
upsDetailTitleSB.append("</table>");
upsDetailTitleSB.append("</td>"); 
upsDetailTitleSB.append("</tr>");
upsDetailTitleSB.append("</table>");

String upsDetailTitleTable = upsDetailTitleSB.toString();

String[][] sunDetailTitle = {  
                            {"系统信息" ,"/monitor.do?action=hostwindows&flag="+flag , "10"} ,
							{"性能信息" ,"/monitor.do?action=hostping&flag="+flag , "0"} ,
							{"流速信息" ,"/detail/host_solarisutilhdx.jsp?flag="+flag , "1" }, 
							{"文件系统","/monitor.do?action=hostcpu&flag="+flag , "2" } , 
							{"配置信息" ,"/monitor.do?action=hostconfig&flag="+flag ,"3"},
							{"进程信息" ,"/monitor.do?action=hostproc&flag="+flag ,"4" },
							{"syslog信息" ,"/monitor.do?action=hostsyslog&flag="+flag ,"5"},
							{"告警信息" ,"/monitor.do?action=hostevent&flag="+flag,"6" }
							};
StringBuffer sunDetailTitleSB = new StringBuffer();

sunDetailTitleSB.append("<table>");
sunDetailTitleSB.append("<tr>");
sunDetailTitleSB.append("<td width=\""+sunDetailTitle.length*72+"\">");
sunDetailTitleSB.append("<table>");
sunDetailTitleSB.append("<tr>");

for(int i = 0 ; i < sunDetailTitle.length ; i++){
	sunDetailTitleSB.append("<td id=\"sunDetailTitle-"+ sunDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + sunDetailTitle[i][1] + "\")'>" + sunDetailTitle[i][0]+ "</a></td>" );
}
sunDetailTitleSB.append("</tr>");
sunDetailTitleSB.append("</table>");
sunDetailTitleSB.append("</td>");
sunDetailTitleSB.append("</tr>");
sunDetailTitleSB.append("</table>");

String sunDetailTitleTable = sunDetailTitleSB.toString();

// hp unix ...

String[][] hpunixDetailTitle = {
                            {"系统信息" ,"/monitor.do?action=hostwindows&flag="+flag , "10" }, 
							{"连通率" ,"/monitor.do?action=hostping&flag="+flag , "0" }, 
							{"流速信息" ,"/monitor.do?action=hostutilhdx&flag="+flag , "1" }, 
							{"性能信息","/monitor.do?action=hostcpu&flag="+flag , "2" } , 
							{"进程信息" ,"/monitor.do?action=hostproc&flag="+flag ,"3"},
							{"syslog信息" ,"/monitor.do?action=hostsyslog&flag="+flag ,"4" },
							{"事件信息" ,"/monitor.do?action=hostevent&flag="+flag ,"5"},
							};
StringBuffer hpunixDetailTitleSB = new StringBuffer();

hpunixDetailTitleSB.append("<table>");
hpunixDetailTitleSB.append("<tr>");
hpunixDetailTitleSB.append("<td width=\""+hpunixDetailTitle.length*72+"\">");
hpunixDetailTitleSB.append("<table>");
hpunixDetailTitleSB.append("<tr>");

for(int i = 0 ; i < hpunixDetailTitle.length ; i++){
	hpunixDetailTitleSB.append("<td id=\"hpunixDetailTitle-"+ hpunixDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + hpunixDetailTitle[i][1] + "\")'>" + hpunixDetailTitle[i][0]+ "</a></td>" );
}
hpunixDetailTitleSB.append("</tr>");
hpunixDetailTitleSB.append("</table>");
hpunixDetailTitleSB.append("</td>");
hpunixDetailTitleSB.append("</tr>");
hpunixDetailTitleSB.append("</table>");

String hpunixDetailTitleTable = hpunixDetailTitleSB.toString();

String[][] compareDetailTitle = {
							{"网络设备" ,"/network.do?action=netChoce" , "0" }, 
							{"服务器" ,"/network.do?action=hostChoce" , "1" }
							};
StringBuffer compareDetailTitleSB = new StringBuffer();

compareDetailTitleSB.append("<table>");
compareDetailTitleSB.append("<tr>");
compareDetailTitleSB.append("<td width=\""+compareDetailTitle.length*72+"\">");
compareDetailTitleSB.append("<table>");
compareDetailTitleSB.append("<tr>");

for(int i = 0 ; i < compareDetailTitle.length ; i++){
	compareDetailTitleSB.append("<td id=\"compareDetailTitle-"+ compareDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + compareDetailTitle[i][1] + "\")'>" + compareDetailTitle[i][0]+ "</a></td>" );
}
compareDetailTitleSB.append("</tr>");
compareDetailTitleSB.append("</table>");
compareDetailTitleSB.append("</td>");
compareDetailTitleSB.append("</tr>");
compareDetailTitleSB.append("</table>");

String compareDetailTitleTable =compareDetailTitleSB.toString();

String[][] windowsDetailTitle = {
							{"流速信息" ,"/monitor.do?action=hostping&flag="+flag , "0" }, 
							{"性能信息" ,"/monitor.do?action=hostcpu&flag="+flag , "1"} , 
							{"ARP信息" ,"/monitor.do?action=hostarp&flag="+flag , "2"} , 
							{"进程信息","/monitor.do?action=hostproc&flag="+flag , "3" } , 
							{"服务信息","/monitor.do?action=hostservice&flag="+flag , "4" } ,
							{"配置信息","/monitor.do?action=hostconfig&flag="+flag , "5" } ,
							{"syslog信息" ,"/monitor.do?action=hostsyslog&flag="+flag ,"6"},
							{"告警信息" ,"/monitor.do?action=hostevent&flag="+flag,"7" }
							};
StringBuffer windowDetailTitleSB  = new StringBuffer();

windowDetailTitleSB.append("<table>");
windowDetailTitleSB.append("<tr>");
windowDetailTitleSB.append("<td width=\""+windowsDetailTitle.length*72+"\">");
windowDetailTitleSB.append("<table>");
windowDetailTitleSB.append("<tr>");

for(int i = 0 ; i < windowsDetailTitle.length ; i++){
	windowDetailTitleSB.append("<td id=\"windowsDetailTitle-"+ windowsDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + windowsDetailTitle[i][1] + "\")'>" + windowsDetailTitle[i][0]+ "</a></td>" );
}
windowDetailTitleSB.append("</tr>");
windowDetailTitleSB.append("</table>");
windowDetailTitleSB.append("</td>");
windowDetailTitleSB.append("</tr>");
windowDetailTitleSB.append("</table>");

String windowsDetailTitleTable  = windowDetailTitleSB.toString();


String[][] apacheDetailTitle = {
                            {"系统信息" ,"/apache.do?action=system&flag="+flag , "0" },
							{"服务流程" ,"/apache.do?action=server&flag="+flag , "1" }, 
							{"性能状态" ,"/apache.do?action=status&flag="+flag , "2"} , 
							{"进程信息" ,"/apache.do?action=process&flag="+flag , "3"} ,
							{"告警信息","/apache.do?action=alarm&flag="+flag , "4" } 
							};
StringBuffer apacheDetailTitleSB = new StringBuffer();

apacheDetailTitleSB.append("<table>");
apacheDetailTitleSB.append("<tr>");
apacheDetailTitleSB.append("<td width=\""+apacheDetailTitle.length*72+"\">");
apacheDetailTitleSB.append("<table>");
apacheDetailTitleSB.append("<tr>");

for(int i = 0 ; i < apacheDetailTitle.length ; i++){
	apacheDetailTitleSB.append("<td id=\"apacheDetailTitle-"+ apacheDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + apacheDetailTitle[i][1] + "\")'>" + apacheDetailTitle[i][0]+ "</a></td>" );
}
apacheDetailTitleSB.append("</tr>");
apacheDetailTitleSB.append("</table>");
apacheDetailTitleSB.append("</td>");
apacheDetailTitleSB.append("</tr>");
apacheDetailTitleSB.append("</table>");

String apacheDetailTitleTable = apacheDetailTitleSB.toString();

String[][] tuxedoDetailTitle = {
							//{"连通率信息"		,  	"/tuxedo.do?action=pingDetail" 		, 	"0" }, 
							{"服务器端" 	, 	"/tuxedo.do?action=serverDetail&flag="+flag 	, 	"1"} , 
							{"服务"	,	"/tuxedo.do?action=serviceDetail&flag="+flag 	, 	"2" } , 
							{"队列"		,"/tuxedo.do?action=queueDetail&flag="+flag 	, 	"3" } , 
							{"客户端" 	,	"/tuxedo.do?action=clientDetail&flag="+flag 	,	"4"},
							};
StringBuffer tuxedoDetailTitleSB = new StringBuffer();

tuxedoDetailTitleSB.append("<table>");
tuxedoDetailTitleSB.append("<tr>");
tuxedoDetailTitleSB.append("<td width=\""+tuxedoDetailTitle.length*72+"\">");
tuxedoDetailTitleSB.append("<table>");
tuxedoDetailTitleSB.append("<tr>");

for(int i = 0 ; i < tuxedoDetailTitle.length ; i++){
	tuxedoDetailTitleSB.append("<td id=\"tuxedoDetailTitle-"+ tuxedoDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + tuxedoDetailTitle[i][1] + "\")'>" + tuxedoDetailTitle[i][0]+ "</a></td>" );
}
tuxedoDetailTitleSB.append("</tr>");
tuxedoDetailTitleSB.append("</table>");
tuxedoDetailTitleSB.append("</td>");
tuxedoDetailTitleSB.append("</tr>");
tuxedoDetailTitleSB.append("</table>");

String tuxedoDetailTitleTable = tuxedoDetailTitleSB.toString();

String[][] weblogicDetailTitle = {
                            {"队 列" ,"/weblogic.do?action=detail&flag="+flag , "0" },
							{"JDBC连接池" ,"/weblogic.do?action=jdbcdetail&flag="+flag , "1" }, 
							{"Web应用" ,"/weblogic.do?action=webappdetail&flag="+flag , "2"} , 
							{"JVM堆","/weblogic.do?action=jvmheapdetail&flag="+flag , "3" } , 
							{"服 务" ,"/weblogic.do?action=serverdetail&flag="+flag ,"4"},
							{"Servlet" ,"/weblogic.do?action=servletdetail&flag="+flag ,"5"},
							{"交 易" ,"/weblogic.do?action=transdetail&flag="+flag ,"6"},
							{"告 警" ,"/weblogic.do?action=eventdetail&flag="+flag ,"7"}
							};
StringBuffer weblogicDetailTitleSB = new StringBuffer();

weblogicDetailTitleSB.append("<table>");
weblogicDetailTitleSB.append("<tr>");
weblogicDetailTitleSB.append("<td width=\""+weblogicDetailTitle.length*72+"\">");
weblogicDetailTitleSB.append("<table>");
weblogicDetailTitleSB.append("<tr>");

for(int i = 0 ; i < weblogicDetailTitle.length ; i++){
	weblogicDetailTitleSB.append("<td id=\"weblogicDetailTitle-"+ weblogicDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + weblogicDetailTitle[i][1] + "\")'>" + weblogicDetailTitle[i][0]+ "</a></td>" );
}
weblogicDetailTitleSB.append("</tr>");
weblogicDetailTitleSB.append("</table>");
weblogicDetailTitleSB.append("</td>");
weblogicDetailTitleSB.append("</tr>");
weblogicDetailTitleSB.append("</table>");

String weblogicDetailTitleTable = weblogicDetailTitleSB.toString();

String[][] storageDetailTitle = {
							{"连通率" ,"/storage.do?action=pingDetail&flag="+flag , "8" },
                            				{"矩阵位置" ,"/storage.do?action=lsarraysiteDetail&flag="+flag , "0" },                           				
							{"矩阵" ,"/storage.do?action=lsarrayDetail&flag="+flag , "1" }, 
							{"列" ,"/storage.do?action=lsrankDetail&flag="+flag , "2"} , 
							{"扩展域","/storage.do?action=lsextpoolDetail&flag="+flag , "3" } , 
							{"逻辑卷" ,"/storage.do?action=lsfbvolDetail&flag="+flag,"4"},
							{"卷组" ,"/storage.do?action=lsvolgrpDetail&flag="+flag ,"5"},
							{"I/O 端口" ,"/storage.do?action=lsioportDetail&flag="+flag ,"6"},
							{"连接主机" ,"/storage.do?action=lshostconnectDetail&flag="+flag ,"7"},
							};
StringBuffer storageDetailTitleSB = new StringBuffer();

storageDetailTitleSB.append("<table>");
storageDetailTitleSB.append("<tr>");
storageDetailTitleSB.append("<td width=\""+storageDetailTitle.length*72+"\">");
storageDetailTitleSB.append("<table>");
storageDetailTitleSB.append("<tr>");

for(int i = 0 ; i < storageDetailTitle.length ; i++){
	storageDetailTitleSB.append("<td id=\"storageDetailTitle-"+ storageDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + storageDetailTitle[i][1] + "\")'>" + storageDetailTitle[i][0]+ "</a></td>" );
}
storageDetailTitleSB.append("</tr>");
storageDetailTitleSB.append("</table>");
storageDetailTitleSB.append("</td>");
storageDetailTitleSB.append("</tr>");
storageDetailTitleSB.append("</table>");

String storageDetailTitleTable = storageDetailTitleSB.toString();

//用于中间件报表
String[][] middlewareReportTitle = {
							{"连通率信息"		,  	"/midcapreport.do?action=midlist" 				, 	"0" }, 
							{"Tomcat" 		, 	"/tomcatreport.do?action=tomcatlist" 			, 	"1"} , 
							{"IIS"			,	"/iiscatreport.do?action=iislist" 				, 	"2" } , 
							{"WEBLOGIC"		,	"/weblogiccapreport.do?action=weblogiclist" 	, 	"3" } , 
							{"事件报表" 		,	"/midcapreport.do?action=eventlist" 			,	"4"},
							};
StringBuffer middlewareReportTitleSB = new StringBuffer();

middlewareReportTitleSB.append("<table>");
middlewareReportTitleSB.append("<tr>");
middlewareReportTitleSB.append("<td width=\""+middlewareReportTitle.length*72+"\">");
middlewareReportTitleSB.append("<table>");
middlewareReportTitleSB.append("<tr>");

for(int i = 0 ; i < middlewareReportTitle.length ; i++){
	middlewareReportTitleSB.append("<td id=\"middlewareReportTitle-"+ middlewareReportTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + middlewareReportTitle[i][1] + "\")'>" + middlewareReportTitle[i][0]+ "</a></td>" );
}
middlewareReportTitleSB.append("</tr>");
middlewareReportTitleSB.append("</table>");
middlewareReportTitleSB.append("</td>");
middlewareReportTitleSB.append("</tr>");
middlewareReportTitleSB.append("</table>");

String middlewareReportTitleTable = middlewareReportTitleSB.toString();

String[][] AS400DetailTitle = {
							{"连通率" ,"/monitor.do?action=hostping&flag="+flag , "0" },
                            {"性能信息" ,"/monitor.do?action=AS400SystemStatusDetail&flag="+flag , "1" },
							{"池信息" ,"/monitor.do?action=AS400PoolDetail&flag="+flag , "2" }, 
							{"磁盘" ,"/monitor.do?action=AS400DiskDetail&flag="+flag , "3"} , 
							{"任务信息","/monitor.do?action=AS400JobsDetail&flag="+flag , "4" } , 
							{"子系统信息","/monitor.do?action=AS400SubsystemDetail&flag="+flag , "5" } ,
							{"网卡信息","/monitor.do?action=AS400NetworkDetail&flag="+flag , "6" } , 
							{"硬件配置信息","/monitor.do?action=AS400HardwareDetail&flag="+flag , "7" } , 
							{"服务信息","/monitor.do?action=AS400ServiceDetail&flag="+flag , "8" } , 
							{"告警信息","/monitor.do?action=AS400EventDetail&flag="+flag , "9" } 
							};
StringBuffer AS400DetailTitleSB = new StringBuffer();

AS400DetailTitleSB.append("<table>");
AS400DetailTitleSB.append("<tr>");
AS400DetailTitleSB.append("<td width=\""+AS400DetailTitle.length*72+"\">");
AS400DetailTitleSB.append("<table>");
AS400DetailTitleSB.append("<tr>");

for(int i = 0 ; i < AS400DetailTitle.length ; i++){
	AS400DetailTitleSB.append("<td id=\"AS400DetailTitle-"+ AS400DetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + AS400DetailTitle[i][1] + "\")'>" + AS400DetailTitle[i][0]+ "</a></td>" );
}
AS400DetailTitleSB.append("</tr>");
AS400DetailTitleSB.append("</table>");
AS400DetailTitleSB.append("</td>");
AS400DetailTitleSB.append("</tr>");
AS400DetailTitleSB.append("</table>");

String AS400DetailTitleTable = AS400DetailTitleSB.toString();

String[][] wasDetailTitle = {
                                                        {"JVM信息" ,"/was.do?action=detail&flag="+flag , "0" },
							{"JDBC信息" ,"/was.do?action=jdbcdetail&flag="+flag , "1" }, 
							{"Session信息" ,"/was.do?action=session&flag="+flag , "2"} , 
							{"System信息","/was.do?action=system&flag="+flag , "3" } , 
							{"Cache信息" ,"/was.do?action=cache&flag="+flag ,"4"},
							{"Service信息" ,"/was.do?action=service&flag="+flag ,"5"},
							{"ORB信息" ,"/was.do?action=orb&flag="+flag ,"6"},
							{"告警信息" ,"/was.do?action=event&flag="+flag ,"7"}
							};
StringBuffer wasDetailTitleSB = new StringBuffer();

wasDetailTitleSB.append("<table>");
wasDetailTitleSB.append("<tr>");
wasDetailTitleSB.append("<td width=\""+wasDetailTitle.length*72+"\">");
wasDetailTitleSB.append("<table>");
wasDetailTitleSB.append("<tr>");

for(int i = 0 ; i < wasDetailTitle.length ; i++){
	wasDetailTitleSB.append("<td id=\"wasDetailTitle-"+ wasDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + wasDetailTitle[i][1] + "\")'>" + wasDetailTitle[i][0]+ "</a></td>" );
}
wasDetailTitleSB.append("</tr>");
wasDetailTitleSB.append("</table>");
wasDetailTitleSB.append("</td>");
wasDetailTitleSB.append("</tr>");
wasDetailTitleSB.append("</table>");

String wasDetailTitleTable = wasDetailTitleSB.toString();

String[][] was5DetailTitle = {
                                                        {"System信息" ,"/was.do?action=detail&flag="+flag , "0" },
							{"JDBC信息" ,"/was.do?action=jdbcdetail&flag="+flag , "1" }, 
							{"Session信息" ,"/was.do?action=session&flag="+flag , "2"} , 
							{"JVM信息","/was.do?action=system&flag="+flag , "3" } , 
							{"Cache信息" ,"/was.do?action=cache&flag="+flag ,"4"},
							{"Thread信息" ,"/was.do?action=service&flag="+flag ,"5"},
							{"事务信息" ,"/was.do?action=orb&flag="+flag ,"6"},
							{"告警信息" ,"/was.do?action=event&flag="+flag ,"7"}
							};
StringBuffer was5DetailTitleSB = new StringBuffer();

was5DetailTitleSB.append("<table>");
was5DetailTitleSB.append("<tr>");
was5DetailTitleSB.append("<td width=\""+was5DetailTitle.length*72+"\">");
was5DetailTitleSB.append("<table>");
was5DetailTitleSB.append("<tr>");

for(int i = 0 ; i < was5DetailTitle.length ; i++){
	was5DetailTitleSB.append("<td id=\"was5DetailTitle-"+ was5DetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + was5DetailTitle[i][1] + "\")'>" + was5DetailTitle[i][0]+ "</a></td>" );
}
was5DetailTitleSB.append("</tr>");
was5DetailTitleSB.append("</table>");
was5DetailTitleSB.append("</td>");
was5DetailTitleSB.append("</tr>");
was5DetailTitleSB.append("</table>");

String was5DetailTitleTable = was5DetailTitleSB.toString();


String[][] was7DetailTitle = {
                                                        {"System信息" ,"/was.do?action=detail&flag="+flag , "0" },
							{"JDBC信息" ,"/was.do?action=jdbcdetail&flag="+flag , "1" }, 
							{"Session信息" ,"/was.do?action=session&flag="+flag , "2"} , 
							{"JVM信息","/was.do?action=system&flag="+flag , "3" } , 
							{"缓存信息" ,"/was.do?action=cache&flag="+flag ,"4"},
							{"Thread信息" ,"/was.do?action=service&flag="+flag ,"5"},
							{"事务信息" ,"/was.do?action=orb&flag="+flag ,"6"},
							{"告警信息" ,"/was.do?action=event&flag="+flag ,"7"}
							};
StringBuffer was7DetailTitleSB = new StringBuffer();

was7DetailTitleSB.append("<table>");
was7DetailTitleSB.append("<tr>");
was7DetailTitleSB.append("<td width=\""+was7DetailTitle.length*72+"\">");
was7DetailTitleSB.append("<table>");
was7DetailTitleSB.append("<tr>");

for(int i = 0 ; i < was7DetailTitle.length ; i++){
	was7DetailTitleSB.append("<td id=\"was7DetailTitle-"+ was7DetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + was7DetailTitle[i][1] + "\")'>" + was7DetailTitle[i][0]+ "</a></td>" );
}
was7DetailTitleSB.append("</tr>");
was7DetailTitleSB.append("</table>");
was7DetailTitleSB.append("</td>");
was7DetailTitleSB.append("</tr>");
was7DetailTitleSB.append("</table>");

String was7DetailTitleTable = was7DetailTitleSB.toString();

String[][] knowledgeDetailTitle = {
								  {"告警信息","myEvent()","0"},
								  {"参考方案","myKnowledge()","1"},
								  {"相关知识","myKnowledgebase()","2"}
								  };
StringBuffer knowledgeDetailTitleSB = new StringBuffer();

knowledgeDetailTitleSB.append("<table>");
knowledgeDetailTitleSB.append("<tr>");
knowledgeDetailTitleSB.append("<td width=\""+knowledgeDetailTitle.length*72+"\">");
knowledgeDetailTitleSB.append("<table>");
knowledgeDetailTitleSB.append("<tr>");

for(int i=0;i<knowledgeDetailTitle.length;i++){
	knowledgeDetailTitleSB.append("<td id=\"knowledgeDetailTitle-"+ knowledgeDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='" + knowledgeDetailTitle[i][1] + "'>" + knowledgeDetailTitle[i][0]+ "</a></td>" );
}
knowledgeDetailTitleSB.append("</tr>");
knowledgeDetailTitleSB.append("</table>");
knowledgeDetailTitleSB.append("</td>");
knowledgeDetailTitleSB.append("</tr>");
knowledgeDetailTitleSB.append("</table>");

String knowledgeDetailTitleTable = knowledgeDetailTitleSB.toString();



String[][] f5DetailTitle = {
                            {"流速信息" ,"/topology/network/f5serverview.jsp?flag="+flag , "0" }, 
						 
							{"Pool信息","/monitor.do?action=f5poolinfo&flag="+flag , "1" } , 
							
							{"策略信息" ,"/monitor.do?action=f5rulesinfo&flag="+flag,"2" },
							
							};
StringBuffer f5DetailTitleSB = new StringBuffer();

f5DetailTitleSB.append("<table>");
f5DetailTitleSB.append("<tr>");
f5DetailTitleSB.append("<td width=\""+f5DetailTitle.length*72+"\">");
f5DetailTitleSB.append("<table>");
f5DetailTitleSB.append("<tr>");

for(int i = 0 ; i < f5DetailTitle.length ; i++){
	f5DetailTitleSB.append("<td id=\"f5DetailTitle-"+ f5DetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + f5DetailTitle[i][1] + "\")'>" + f5DetailTitle[i][0]+ "</a></td>" );
}
f5DetailTitleSB.append("</tr>");
f5DetailTitleSB.append("</table>");
f5DetailTitleSB.append("</td>");
f5DetailTitleSB.append("</tr>");
f5DetailTitleSB.append("</table>");

String f5DetailTitleTable = f5DetailTitleSB.toString();

//domino
String[][] dominoDetailTitle = {
							{"性能信息" ,"/domino.do?action=domcpu&flag="+flag , "0" }, 
							{"邮件" ,"/domino.do?action=dommail&flag="+flag , "1"} , 
							{"LDAP","/domino.do?action=domldap&flag="+flag , "2" } , 
							{"HTTP" ,"/domino.do?action=domhttp&flag="+flag ,"3"},
							{"数据库" ,"/domino.do?action=domdb&flag="+flag,"4" },
							{"Cache" ,"/domino.do?action=domcache&flag="+flag ,"5"},
							{"服务器" ,"/domino.do?action=domserver&flag="+flag ,"6"}
							};
StringBuffer dominoDetailTitleSB = new StringBuffer();

dominoDetailTitleSB.append("<table>");
dominoDetailTitleSB.append("<tr>");
dominoDetailTitleSB.append("<td width=\""+dominoDetailTitle.length*72+"\">");
dominoDetailTitleSB.append("<table>");
dominoDetailTitleSB.append("<tr>");

for(int i = 0 ; i < dominoDetailTitle.length ; i++){
	dominoDetailTitleSB.append("<td id=\"hostDetailTitle-"+ dominoDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + dominoDetailTitle[i][1] + "\")'>" + dominoDetailTitle[i][0]+ "</a></td>" );
}
dominoDetailTitleSB.append("</tr>");
dominoDetailTitleSB.append("</table>");
dominoDetailTitleSB.append("</td>");
dominoDetailTitleSB.append("</tr>");
dominoDetailTitleSB.append("</table>");

String dominoDetailTitleTable = dominoDetailTitleSB.toString();
//配置规则 wxy add
String[][] configRuleTitle = {
							{"策略" ,"/configRule.do?action=strategyList" , "0" }, 
							{"规则组" ,"/configRule.do?action=groupRuleList" , "1"} , 
							{"规则","/configRule.do?action=ruleDetailList" , "2" } , 
							};
StringBuffer configRuleTitleSB = new StringBuffer();

configRuleTitleSB.append("<table>");
configRuleTitleSB.append("<tr>");
configRuleTitleSB.append("<td width=\""+configRuleTitle.length*72+"\">");
configRuleTitleSB.append("<table>");
configRuleTitleSB.append("<tr>");

for(int i = 0 ; i < configRuleTitle.length ; i++){
	configRuleTitleSB.append("<td id=\"configRuleTitle-"+ configRuleTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + configRuleTitle[i][1] + "\")'>" + configRuleTitle[i][0]+ "</a></td>" );
}
configRuleTitleSB.append("</tr>");
configRuleTitleSB.append("</table>");
configRuleTitleSB.append("</td>");
configRuleTitleSB.append("</tr>");
configRuleTitleSB.append("</table>");

String configRuleTitleTable = configRuleTitleSB.toString();
//邮件安全网关
String[][] gatewayDetailTitleFlag= {
							{"性能信息","/topology/network/gatewayview.jsp?flag="+flag , "0" }, 
							{"流速信息" ,"/topology/network/gatewayintview.jsp?flag="+flag , "1" }, 
						//	{"队列信息","/monitor.do?action=gatewayqueue&flag="+flag , "2" } , 
						//	{"磁盘整列信息","/monitor.do?action=gatewayraid&flag="+flag ,"3"},
						//	{"动力环境","/monitor.do?action=gatewayenv&flag="+flag,"4" },
							{"告警信息","/monitor.do?action=gatewayevent&flag="+flag,"5" }
							};
String[][] gatewayDetailTitle = gatewayDetailTitleFlag;

StringBuffer gatewayDetailTitleSB = new StringBuffer();

gatewayDetailTitleSB.append("<table>");
gatewayDetailTitleSB.append("<tr>");
gatewayDetailTitleSB.append("<td width=\""+gatewayDetailTitle.length*72+"\">");
gatewayDetailTitleSB.append("<table>");
gatewayDetailTitleSB.append("<tr>");

for(int i = 0 ; i < gatewayDetailTitle.length ; i++){
	gatewayDetailTitleSB.append("<td id=\"gatewayDetailTitle-"+ gatewayDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + gatewayDetailTitle[i][1] + "\")'>" + gatewayDetailTitle[i][0]+ "</a></td>" );
}
gatewayDetailTitleSB.append("</tr>");
gatewayDetailTitleSB.append("</table>");
gatewayDetailTitleSB.append("</td>");
gatewayDetailTitleSB.append("</tr>");
gatewayDetailTitleSB.append("</table>");

String gatewayDetailTitleTable = gatewayDetailTitleSB.toString();
//nokia防火墙
String[][] nokiaDetailTitleFlag= {
							{"性能信息","/topology/network/nokiafirewallview.jsp?flag="+flag , "0" }, 
							{"流速信息" ,"/topology/network/nokiaintview.jsp?flag="+flag , "1" }, 
							{"Image信息","/monitor.do?action=nokiaimage&flag="+flag , "2" } , 
							{"进程信息","/monitor.do?action=nokiaprocess&flag="+flag ,"3"},
							{"磁盘镜像","/monitor.do?action=nokiamirror&flag="+flag,"4" },
							{"动力环境","/monitor.do?action=nokiaenv&flag="+flag,"5" },
							{"告警信息","/monitor.do?action=nokiaevent&flag="+flag,"6" }
							};
String[][] nokiaDetailTitle = nokiaDetailTitleFlag;

StringBuffer nokiaDetailTitleSB = new StringBuffer();

nokiaDetailTitleSB.append("<table>");
nokiaDetailTitleSB.append("<tr>");
nokiaDetailTitleSB.append("<td width=\""+nokiaDetailTitle.length*72+"\">");
nokiaDetailTitleSB.append("<table>");
nokiaDetailTitleSB.append("<tr>");

for(int i = 0 ; i < nokiaDetailTitle.length ; i++){
	nokiaDetailTitleSB.append("<td id=\"nokiaDetailTitle-"+ nokiaDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + nokiaDetailTitle[i][1] + "\")'>" + nokiaDetailTitle[i][0]+ "</a></td>" );
}
nokiaDetailTitleSB.append("</tr>");
nokiaDetailTitleSB.append("</table>");
nokiaDetailTitleSB.append("</td>");
nokiaDetailTitleSB.append("</tr>");
nokiaDetailTitleSB.append("</table>");

String nokiaDetailTitleTable = nokiaDetailTitleSB.toString();
String[][] scriptTitle = {
							{"命令输入" ,"" , "0" }, 
							{"选择设备" ,"" , "1"} , 
							{"执行结果","" , "2" } , 
							};
StringBuffer scriptTitleSB = new StringBuffer();

scriptTitleSB.append("<table>");
scriptTitleSB.append("<tr>");
scriptTitleSB.append("<td width=\""+scriptTitle.length*72+"\">");
scriptTitleSB.append("<table>");
scriptTitleSB.append("<tr>");

for(int i = 0 ; i < scriptTitle.length ; i++){
	scriptTitleSB.append("<td id=\"scriptTitle-"+ scriptTitle[i][2]+ "\" class=\"detail-data-title-out\" >" + scriptTitle[i][0]+ "</td>" );
}
scriptTitleSB.append("</tr>");
scriptTitleSB.append("</table>");
scriptTitleSB.append("</td>");
scriptTitleSB.append("</tr>");
scriptTitleSB.append("</table>");

String scriptTitleTable = scriptTitleSB.toString();
String[][] scriptLogTitle = {
							{"命令输入" ,"" , "0" }, 
							{"选择设备" ,"" , "1"} , 
							
							};
StringBuffer scriptLogTitleSB = new StringBuffer();

scriptLogTitleSB.append("<table>");
scriptLogTitleSB.append("<tr>");
scriptLogTitleSB.append("<td width=\""+scriptLogTitle.length*72+"\">");
scriptLogTitleSB.append("<table>");
scriptLogTitleSB.append("<tr>");

for(int i = 0 ; i < scriptLogTitle.length ; i++){
	scriptLogTitleSB.append("<td id=\"scriptLogTitle-"+ scriptLogTitle[i][2]+ "\" class=\"detail-data-title-out\" >" + scriptLogTitle[i][0]+ "</td>" );
}
scriptLogTitleSB.append("</tr>");
scriptLogTitleSB.append("</table>");
scriptLogTitleSB.append("</td>");
scriptLogTitleSB.append("</tr>");
scriptLogTitleSB.append("</table>");

String scriptLogTitleTable = scriptLogTitleSB.toString();


//Vpn
String[][] vpnDetailTitleFlag= {
							{"流速信息" ,"/topology/network/vpnview.jsp?flag="+flag , "0" }, 
							//{"性能信息" ,"/monitor.do?action=vpncpu&flag="+flag , "1"} , 
							//{"ARP信息" ,"/monitor.do?action=vpnarp&flag="+flag , "2" } , 
							//{"配置信息" ,"/monitor.do?action=vpnconfig&flag="+flag ,"3"},
							//{"Session信息" ,"/monitor.do?action=vpnsession&flag="+flag,"4" },
							//{"VPN连接" ,"/monitor.do?action=vpnconn&flag="+flag,"5" },
							{"告警信息" ,"/monitor.do?action=vpnevent&flag="+flag,"6" },
							};

String[][] vpnDetailTitle = vpnDetailTitleFlag;

StringBuffer vpnDetailTitleSB = new StringBuffer();

vpnDetailTitleSB.append("<table>");
vpnDetailTitleSB.append("<tr>");
vpnDetailTitleSB.append("<td width=\""+vpnDetailTitle.length*72+"\">");
vpnDetailTitleSB.append("<table>");
vpnDetailTitleSB.append("<tr>");

for(int i = 0 ; i < vpnDetailTitle.length ; i++){
	vpnDetailTitleSB.append("<td id=\"vpnDetailTitle-"+ vpnDetailTitle[i][2]+ 
	"\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" "+
	"onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" 
	+ vpnDetailTitle[i][1] + "\")'>" + vpnDetailTitle[i][0]+ "</a></td>" );
}
vpnDetailTitleSB.append("</tr>");
vpnDetailTitleSB.append("</table>");
vpnDetailTitleSB.append("</td>");
vpnDetailTitleSB.append("</tr>");
vpnDetailTitleSB.append("</table>");

String vpnDetailTitleTable = vpnDetailTitleSB.toString();

//Syslog 重要事件及全部事件
String[][] netSyslogTitle = {
							{"重要事件" ,"/netsyslog.do?action=syslogimpevt" , "0" }, 
							{"全部事件" ,"/netsyslog.do?action=syslogallevt" , "1"}
							};
StringBuffer netSyslogTitleSB = new StringBuffer();

netSyslogTitleSB.append("<table>");
netSyslogTitleSB.append("<tr>");
netSyslogTitleSB.append("<td width=\""+netSyslogTitle.length*72+"\">");
netSyslogTitleSB.append("<table>");
netSyslogTitleSB.append("<tr>");

for(int i = 0 ; i < netSyslogTitle.length ; i++){
	netSyslogTitleSB.append("<td id=\"netSyslogTitle-"+ netSyslogTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + netSyslogTitle[i][1] + "\")'>" + netSyslogTitle[i][0]+ "</a></td>" );
}
netSyslogTitleSB.append("</tr>");
netSyslogTitleSB.append("</table>");
netSyslogTitleSB.append("</td>");
netSyslogTitleSB.append("</tr>");
netSyslogTitleSB.append("</table>");

String netSyslogTitleTable = netSyslogTitleSB.toString();

//Syslog 网络设备重要事件及全部事件
String[][] netDevSyslogTitle = {
							{"重要事件" ,"/netsyslog.do?action=syslogimpevt" , "0" }
							};
StringBuffer netDevSyslogTitleSB = new StringBuffer();

netDevSyslogTitleSB.append("<table>");
netDevSyslogTitleSB.append("<tr>");
netDevSyslogTitleSB.append("<td width=\""+netDevSyslogTitle.length*72+"\">");
netDevSyslogTitleSB.append("<table>");
netDevSyslogTitleSB.append("<tr>");

for(int i = 0 ; i < netDevSyslogTitle.length ; i++){
	netDevSyslogTitleSB.append("<td id=\"netSyslogTitle-"+ netDevSyslogTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + netDevSyslogTitle[i][1] + "\")'>" + netDevSyslogTitle[i][0]+ "</a></td>" );
}
netDevSyslogTitleSB.append("</tr>");
netDevSyslogTitleSB.append("</table>");
netDevSyslogTitleSB.append("</td>");
netDevSyslogTitleSB.append("</tr>");
netDevSyslogTitleSB.append("</table>");

String netDevSyslogTitleTable = netDevSyslogTitleSB.toString();

String[][] storageDetailTitleHdc = {                          				
							{"环境信息" ,"/storagehdc.do?action=running&flag="+flag , "0" }, 
							{"运行状态" ,"/storagehdc.do?action=environment&flag="+flag , "1"} , 
							{"系统信息","/storagehdc.do?action=system&flag="+flag , "2" } ,
							{"告警信息" ,"/storagehdc.do?action=event&flag="+flag , "3" }, 
							};
StringBuffer storageDetailTitleSBHdc = new StringBuffer();

storageDetailTitleSBHdc.append("<table>");
storageDetailTitleSBHdc.append("<tr>");
storageDetailTitleSBHdc.append("<td width=\""+storageDetailTitleHdc.length*72+"\">");
storageDetailTitleSBHdc.append("<table>");
storageDetailTitleSBHdc.append("<tr>");

for(int i = 0 ; i < storageDetailTitleHdc.length ; i++){
	storageDetailTitleSBHdc.append("<td id=\"storageDetailTitle-"+ storageDetailTitleHdc[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + storageDetailTitleHdc[i][1] + "\")'>" + storageDetailTitleHdc[i][0]+ "</a></td>" );
}
storageDetailTitleSBHdc.append("</tr>");
storageDetailTitleSBHdc.append("</table>");
storageDetailTitleSBHdc.append("</td>");
storageDetailTitleSBHdc.append("</tr>");
storageDetailTitleSBHdc.append("</table>");



String storageDetailTitleTableHdc = storageDetailTitleSBHdc.toString();

String[][] storageDetailTitleBorcade = {
							{"系统信息" ,"/storagehdc.do?action=syslist&flag="+flag , "0" },                           				
							{"Lun控制状态" ,"/storagehdc.do?action=lun&flag="+flag , "1" }, 
							{"WWN状态" ,"/storagehdc.do?action=wwn&flag="+flag , "2"} , 
							//{"WWN组状态","/storagehdc.do?action=lsextpoolDetailBorcade&flag="+flag , "3" } , 
							{"SLUN状态" ,"/storagehdc.do?action=slun&flag="+flag,"4"},
							//{"SLUN组状态" ,"/storagehdc.do?action=lsvolgrpDetailBorcade&flag="+flag ,"5"},
							//{"系统版本信息" ,"/storagehdc.do?action=pingDetailBorcade&flag="+flag , "6" },
							};
StringBuffer storageDetailTitleSBbrocade = new StringBuffer();

storageDetailTitleSBbrocade.append("<table>");
storageDetailTitleSBbrocade.append("<tr>");
storageDetailTitleSBbrocade.append("<td width=\""+storageDetailTitleBorcade.length*72+"\">");
storageDetailTitleSBbrocade.append("<table>");
storageDetailTitleSBbrocade.append("<tr>");

for(int i = 0 ; i < storageDetailTitleBorcade.length ; i++){
	storageDetailTitleSBbrocade.append("<td id=\"storageDetailTitle-"+ storageDetailTitleBorcade[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + storageDetailTitleBorcade[i][1] + "\")'>" + storageDetailTitleBorcade[i][0]+ "</a></td>" );
}
storageDetailTitleSBbrocade.append("</tr>");
storageDetailTitleSBbrocade.append("</table>");
storageDetailTitleSBbrocade.append("</td>");
storageDetailTitleSBbrocade.append("</tr>");
storageDetailTitleSBbrocade.append("</table>");

String Hdc = storageDetailTitleSBbrocade.toString();
String netReportPerformTitleTable="";
String hostReportPerformTitleTable="";
if(hostDetail != null){

String[][] netReportPerformTitle = {
							{"连通率报表" ,"/netreport.do?action=showPingReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "0" }, 
							{"事件报表" ,"/netreport.do?action=showEventReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "1"} , 
							{"综合报表","/netreport.do?action=showCompositeReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "2" } , 
							};
StringBuffer netReportPerformTitleSB = new StringBuffer();

netReportPerformTitleSB.append("<table>");
netReportPerformTitleSB.append("<tr>");
netReportPerformTitleSB.append("<td width=\""+netReportPerformTitle.length*72+"\">");
netReportPerformTitleSB.append("<table>");
netReportPerformTitleSB.append("<tr>");

for(int i = 0 ; i < netReportPerformTitle.length ; i++){
	netReportPerformTitleSB.append("<td id=\"netReportPerformTitle-"+ netReportPerformTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + netReportPerformTitle[i][1] + "\")'>" + netReportPerformTitle[i][0]+ "</a></td>" );
}
netReportPerformTitleSB.append("</tr>");
netReportPerformTitleSB.append("</table>");
netReportPerformTitleSB.append("</td>");
netReportPerformTitleSB.append("</tr>");
netReportPerformTitleSB.append("</table>");

 netReportPerformTitleTable = netReportPerformTitleSB.toString();

String[][] hostReportPerformTitle = {
							{"连通率报表" ,"/hostreport.do?action=showPingReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "0" }, 
							{"性能报表" ,"/hostreport.do?action=showCapacityReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "1"} , 
							{"磁盘报表","/hostreport.do?action=showDiskReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "2" } ,
							{"硬件报表","/hostreport.do?action=showHardwareReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "3" } ,
							{"综合报表","/hostreport.do?action=showCompositeReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "4" } ,
							{"决策报表","/hostreport.do?action=showAnalyseReport&ipaddress="+hostDetail.getIpAddress()+"&id="+hostDetail.getId() , "5" } , 
							};
StringBuffer hostReportPerformTitleSB = new StringBuffer();

hostReportPerformTitleSB.append("<table>");
hostReportPerformTitleSB.append("<tr>");
hostReportPerformTitleSB.append("<td width=\""+hostReportPerformTitle.length*72+"\">");
hostReportPerformTitleSB.append("<table>");
hostReportPerformTitleSB.append("<tr>");

for(int i = 0 ; i < hostReportPerformTitle.length ; i++){
	hostReportPerformTitleSB.append("<td id=\"hostReportPerformTitle-"+ hostReportPerformTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + hostReportPerformTitle[i][1] + "\")'>" + hostReportPerformTitle[i][0]+ "</a></td>" );
}
hostReportPerformTitleSB.append("</tr>");
hostReportPerformTitleSB.append("</table>");
hostReportPerformTitleSB.append("</td>");
hostReportPerformTitleSB.append("</tr>");
hostReportPerformTitleSB.append("</table>");

 hostReportPerformTitleTable= hostReportPerformTitleSB.toString();
}

String dbip = request.getParameter("ipaddress"); 

String[][] dbReportPerformTitle = {
							{"可用性报表" ,"/oracle.do?action=dboraReportdownusable&ipaddress="+dbip+"&id="+tmp1 , "0" }, 
							{"性能报表" ,"/oracle.do?action=dbReport&ipaddress="+dbip+"&id="+tmp1 , "1"} , 
							{"综合报表","/oracle.do?action=oracleCldReport&ipaddress="+dbip+"&id="+tmp1, "2" } ,
							{"事件报表","/oracle.do?action=oracleManagerEventReport&ipaddress="+dbip+"&id="+tmp1 , "3" } ,
							
							};
StringBuffer dbReportPerformTitleSB = new StringBuffer();

dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");
dbReportPerformTitleSB.append("<td width=\""+dbReportPerformTitle.length*72+"\">");
dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");

for(int i = 0 ; i < dbReportPerformTitle.length ; i++){
	dbReportPerformTitleSB.append("<td id=\"dbReportPerformTitle-"+ dbReportPerformTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + dbReportPerformTitle[i][1] + "\")'>" + dbReportPerformTitle[i][0]+ "</a></td>" );
}
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");
dbReportPerformTitleSB.append("</td>");
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");

String dbReportPerformTitleTable= dbReportPerformTitleSB.toString();
//sqlserver
String[][]  dbSqlserverReportPerformTitle = {
							{"可用性报表" ,"/sqlserver.do?action=SqlServerManagerRep&ipaddress="+dbip+"&id="+tmp1 , "0" }, 
							{"性能报表" ,"/sqlserver.do?action=SqlServerManagerNatureRep&ipaddress="+dbip+"&id="+tmp1 , "1"} , 
							{"综合报表","/sqlserver.do?action=SqlServerManagerCldRep&ipaddress="+dbip+"&id="+tmp1, "2" } ,
							{"事件报表","/sqlserver.do?action=SqlServerManagerEventReport&ipaddress="+dbip+"&id="+tmp1 , "3" } ,
							
							};
 dbReportPerformTitleSB = new StringBuffer();

dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");
dbReportPerformTitleSB.append("<td width=\""+dbSqlserverReportPerformTitle.length*72+"\">");
dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");

for(int i = 0 ; i < dbSqlserverReportPerformTitle.length ; i++){
	dbReportPerformTitleSB.append("<td id=\"dbReportPerformTitle-"+ dbSqlserverReportPerformTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + dbSqlserverReportPerformTitle[i][1] + "\")'>" + dbSqlserverReportPerformTitle[i][0]+ "</a></td>" );
}
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");
dbReportPerformTitleSB.append("</td>");
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");

String dbSqlserverReportPerformTitleTable= dbReportPerformTitleSB.toString();
//Informix
String[][]  dbInformixReportPerformTitle = {
							{"可用性报表" ,"/informix.do?action=informixManagerPingReport&ipaddress="+dbip+"&id="+tmp1 , "0" }, 
							{"性能报表" ,"/informix.do?action=informixManagerNatureReport&ipaddress="+dbip+"&id="+tmp1 , "1"} , 
							{"综合报表","/informix.do?action=informixManagerCldReport&ipaddress="+dbip+"&id="+tmp1, "2" } ,
							{"事件报表","/informix.do?action=informixManagerEventReport&ipaddress="+dbip+"&id="+tmp1 , "3" } ,
							
							};
 dbReportPerformTitleSB = new StringBuffer();

dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");
dbReportPerformTitleSB.append("<td width=\""+dbInformixReportPerformTitle.length*72+"\">");
dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");

for(int i = 0 ; i < dbInformixReportPerformTitle.length ; i++){
	dbReportPerformTitleSB.append("<td id=\"dbReportPerformTitle-"+ dbInformixReportPerformTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + dbInformixReportPerformTitle[i][1] + "\")'>" + dbInformixReportPerformTitle[i][0]+ "</a></td>" );
}
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");
dbReportPerformTitleSB.append("</td>");
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");

String dbInformixReportPerformTitleTable= dbReportPerformTitleSB.toString();
//db2
String[][]  db2ReportPerformTitle = {
							{"可用性报表" ,"/db2.do?action=db2ManagerPingReport&ipaddress="+dbip+"&id="+tmp1 , "0" }, 
							{"性能报表" ,"/db2.do?action=db2ManagerNatureReport&ipaddress="+dbip+"&id="+tmp1 , "1"} , 
							{"综合报表","/db2.do?action=db2ManagerCldReport&ipaddress="+dbip+"&id="+tmp1, "2" } ,
							{"事件报表","/db2.do?action=db2ManagerEventReport&ipaddress="+dbip+"&id="+tmp1 , "3" } ,
							
							};
 dbReportPerformTitleSB = new StringBuffer();

dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");
dbReportPerformTitleSB.append("<td width=\""+db2ReportPerformTitle.length*72+"\">");
dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");

for(int i = 0 ; i < db2ReportPerformTitle.length ; i++){
	dbReportPerformTitleSB.append("<td id=\"db2ReportPerformTitle-"+ db2ReportPerformTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + db2ReportPerformTitle[i][1] + "\")'>" + db2ReportPerformTitle[i][0]+ "</a></td>" );
}
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");
dbReportPerformTitleSB.append("</td>");
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");

String db2ReportPerformTitleTable= dbReportPerformTitleSB.toString();
//sysbase
String[][]  sysbaseReportPerformTitle = {
							{"可用性报表" ,"/sybase.do?action=sybaseManagerPingReport&ipaddress="+dbip+"&id="+tmp1 , "0" }, 
							{"性能报表" ,"/sybase.do?action=sybaseManagerNatureReport&ipaddress="+dbip+"&id="+tmp1 , "1"} , 
							{"综合报表","/sybase.do?action=sybaseManagerCldReport&ipaddress="+dbip+"&id="+tmp1, "2" } ,
							{"事件报表","/sybase.do?action=sybaseManagerEventReport&ipaddress="+dbip+"&id="+tmp1 , "3" } ,
							
							};
 dbReportPerformTitleSB = new StringBuffer();

dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");
dbReportPerformTitleSB.append("<td width=\""+sysbaseReportPerformTitle.length*72+"\">");
dbReportPerformTitleSB.append("<table>");
dbReportPerformTitleSB.append("<tr>");

for(int i = 0 ; i < sysbaseReportPerformTitle.length ; i++){
	dbReportPerformTitleSB.append("<td id=\"dbReportPerformTitle-"+ sysbaseReportPerformTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + sysbaseReportPerformTitle[i][1] + "\")'>" + sysbaseReportPerformTitle[i][0]+ "</a></td>" );
}
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");
dbReportPerformTitleSB.append("</td>");
dbReportPerformTitleSB.append("</tr>");
dbReportPerformTitleSB.append("</table>");

String sysbaseReportPerformTitleTable= dbReportPerformTitleSB.toString();
//mysql
String[][] dbMysqlReportPerformTitle = {
							{"可用性报表" ,"/mysql.do?action=mysqlManagerPingReport&ipaddress="+"&id="+tmp1, "0" }, 
							{"性能报表" ,"/mysql.do?action=mysqlManagerNatureReport&ipaddress="+dbip+"&id="+tmp1, "1"} , 
							{"综合报表","/mysql.do?action=mysqlManagerCldReport&ipaddress="+dbip+"&id="+tmp1, "2" } ,
							{"事件报表","/mysql.do?action=mysqlManagerEventReport&ipaddress="+dbip+"&id="+tmp1 , "3" }
							
							};
StringBuffer dbMysqlReportPerformTitleSB = new StringBuffer();

dbMysqlReportPerformTitleSB.append("<table>");
dbMysqlReportPerformTitleSB.append("<tr>");
dbMysqlReportPerformTitleSB.append("<td width=\""+dbMysqlReportPerformTitle.length*72+"\">");
dbMysqlReportPerformTitleSB.append("<table>");
dbMysqlReportPerformTitleSB.append("<tr>");

for(int i = 0 ; i < dbMysqlReportPerformTitle.length ; i++){
	dbMysqlReportPerformTitleSB.append("<td id=\"dbMysqlReportPerformTitle-"+ dbMysqlReportPerformTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + dbMysqlReportPerformTitle[i][1] + "\")'>" + dbMysqlReportPerformTitle[i][0]+ "</a></td>" );
}
dbMysqlReportPerformTitleSB.append("</tr>");
dbMysqlReportPerformTitleSB.append("</table>");
dbMysqlReportPerformTitleSB.append("</td>");
dbMysqlReportPerformTitleSB.append("</tr>");
dbMysqlReportPerformTitleSB.append("</table>");

String dbMysqlReportPerformTitleTable= dbMysqlReportPerformTitleSB.toString();



String[][] storageDetailTitleTableEmc = {
							{"系统信息" ,"/emc.do?action=system&flag="+flag , "0" },                           				
							{"Raid组信息" ,"/emc.do?action=raid&flag="+flag , "1" }, 
							//{"硬件状态" ,"/emc.do?action=hardware&flag="+flag , "2"} , 
							{"磁盘配置" ,"/emc.do?action=diskCon&flag="+flag,"3"},
							//{"LUN配置" ,"/emc.do?action=lunCon&flag="+flag ,"4"},
							{"磁盘性能" ,"/emc.do?action=diskPer&flag="+flag ,"5"},
							//{"LUN性能" ,"/emc.do?action=lunPer&flag="+flag ,"6"},
							{"环境信息" ,"/emc.do?action=environment&flag="+flag ,"7"},
							{"告警信息" ,"/emc.do?action=event&flag="+flag ,"8"},
							//{"系统版本信息" ,"/storagehdc.do?action=pingDetailBorcade&flag="+flag , "6" },
							};
							
StringBuffer storageEMCDetailTitleSBbrocade = new StringBuffer();

storageEMCDetailTitleSBbrocade.append("<table>");
storageEMCDetailTitleSBbrocade.append("<tr>");
storageEMCDetailTitleSBbrocade.append("<td width=\""+storageDetailTitleTableEmc.length*72+"\">");
storageEMCDetailTitleSBbrocade.append("<table>");
storageEMCDetailTitleSBbrocade.append("<tr>");

for(int i = 0 ; i < storageDetailTitleTableEmc.length ; i++){
	storageEMCDetailTitleSBbrocade.append("<td id=\"storageDetailTitle-"+ storageDetailTitleTableEmc[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + storageDetailTitleTableEmc[i][1] + "\")'>" + storageDetailTitleTableEmc[i][0]+ "</a></td>" );
}
storageEMCDetailTitleSBbrocade.append("</tr>");
storageEMCDetailTitleSBbrocade.append("</table>");
storageEMCDetailTitleSBbrocade.append("</td>");
storageEMCDetailTitleSBbrocade.append("</tr>");
storageEMCDetailTitleSBbrocade.append("</table>");

String emc = storageEMCDetailTitleSBbrocade.toString();													




String[][] webLoginDetailTitleFlag= {
							{"连通性" ,"/weblogin.do?action=detail" , "0" }, 
							{"告警" ,"/weblogin.do?action=alarm" , "1"}
							};
String[][] webLoginDetailTitle = webLoginDetailTitleFlag;
StringBuffer webLoginDetailTitleSB = new StringBuffer();
webLoginDetailTitleSB.append("<table>");
webLoginDetailTitleSB.append("<tr>");
webLoginDetailTitleSB.append("<td width=\""+webLoginDetailTitleFlag.length*72+"\">");
webLoginDetailTitleSB.append("<table>");
webLoginDetailTitleSB.append("<tr>");
for(int i = 0 ; i < webLoginDetailTitle.length ; i++){
	webLoginDetailTitleSB.append("<td id=\"webLoginDetailTitle-"+ webLoginDetailTitleFlag[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + webLoginDetailTitleFlag[i][1] + "\")'>" + webLoginDetailTitleFlag[i][0]+ "</a></td>" );
}
webLoginDetailTitleSB.append("</tr>");
webLoginDetailTitleSB.append("</table>");
webLoginDetailTitleSB.append("</td>");
webLoginDetailTitleSB.append("</tr>");
webLoginDetailTitleSB.append("</table>");
String webLoginDetailTitleTable = webLoginDetailTitleSB.toString();



String[][] JBOSSDetailTitleFlag= {
							{"系统信息" ,"/jboss.do?action=detail&flag="+flag , "0" }, 
							{"告警" ,"/jboss.do?action=alarm&flag="+flag  , "1"}
							};
String[][] JBOSSDetailTitle = JBOSSDetailTitleFlag;
StringBuffer JBOSSDetailTitleSB = new StringBuffer();
JBOSSDetailTitleSB.append("<table>");
JBOSSDetailTitleSB.append("<tr>");
JBOSSDetailTitleSB.append("<td width=\""+JBOSSDetailTitleFlag.length*72+"\">");
JBOSSDetailTitleSB.append("<table>");
JBOSSDetailTitleSB.append("<tr>");
for(int i = 0 ; i < JBOSSDetailTitle.length ; i++){
	JBOSSDetailTitleSB.append("<td id=\"JBOSSDetailTitle-"+ JBOSSDetailTitleFlag[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + JBOSSDetailTitleFlag[i][1] + "\")'>" + JBOSSDetailTitleFlag[i][0]+ "</a></td>" );
}
JBOSSDetailTitleSB.append("</tr>");
JBOSSDetailTitleSB.append("</table>");
JBOSSDetailTitleSB.append("</td>");
JBOSSDetailTitleSB.append("</tr>");
JBOSSDetailTitleSB.append("</table>");
String JBOSSDetailTitleTable = JBOSSDetailTitleSB.toString();



String[][] MQDetailTitleFlag= {
							{"系统信息" ,"/mq.do?action=detail&type=system&flag="+flag+"&id="+tmp1 , "0" },
							{"通道信息" ,"/mq.do?action=detail&type=channel&flag="+flag+"&id="+tmp1 , "1" }, 
							{"监听信息" ,"/mq.do?action=detail&type=monitor&flag="+flag+"&id="+tmp1 , "2" }, 
							{"远程队列" ,"/mq.do?action=detail&type=remoteQueue&flag="+flag+"&id="+tmp1 , "3" }, 
							{"本地队列" ,"/mq.do?action=detail&type=localQueue&flag="+flag+"&id="+tmp1 , "4" },  
							{"告警" ,"/mq.do?action=alarm&flag="+flag+"&id="+tmp1  , "5"}
							};
String[][] MQDetailTitle = MQDetailTitleFlag;
StringBuffer MQDetailTitleSB = new StringBuffer();
MQDetailTitleSB.append("<table>");
MQDetailTitleSB.append("<tr>");
MQDetailTitleSB.append("<td width=\""+MQDetailTitleFlag.length*72+"\">");
MQDetailTitleSB.append("<table>");
MQDetailTitleSB.append("<tr>");
for(int i = 0 ; i < MQDetailTitle.length ; i++){
	MQDetailTitleSB.append("<td id=\"MQDetailTitle-"+ MQDetailTitleFlag[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + MQDetailTitleFlag[i][1] + "\")'>" + MQDetailTitleFlag[i][0]+ "</a></td>" );
}
MQDetailTitleSB.append("</tr>");
MQDetailTitleSB.append("</table>");
MQDetailTitleSB.append("</td>");
MQDetailTitleSB.append("</tr>");
MQDetailTitleSB.append("</table>");
String MQDetailTitleTable = MQDetailTitleSB.toString();

String[][] dnsDetailTitle = {
                            {"系统信息" ,"/dns.do?action=system&flag="+flag , "0" },
							{"地址记录" ,"/dns.do?action=addr&flag="+flag , "1" }, 
							{"性能信息" ,"/dns.do?action=perform&flag="+flag , "2"} , 
							{"邮件服务" ,"/dns.do?action=mail&flag="+flag , "3"} ,
							{"名字服务" ,"/dns.do?action=name&flag="+flag , "4"} ,
							{"缓存信息" ,"/dns.do?action=cache&flag="+flag , "5"} ,
							{"告警信息","/dns.do?action=alarm&flag="+flag , "6" } 
							};
StringBuffer dnsDetailTitleSB = new StringBuffer();

dnsDetailTitleSB.append("<table>");
dnsDetailTitleSB.append("<tr>");
dnsDetailTitleSB.append("<td width=\""+dnsDetailTitle.length*72+"\">");
dnsDetailTitleSB.append("<table>");
dnsDetailTitleSB.append("<tr>");

for(int i = 0 ; i < dnsDetailTitle.length ; i++){
	dnsDetailTitleSB.append("<td id=\"dnsDetailTitle-"+ dnsDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + dnsDetailTitle[i][1] + "\")'>" + dnsDetailTitle[i][0]+ "</a></td>" );
}
dnsDetailTitleSB.append("</tr>");
dnsDetailTitleSB.append("</table>");
dnsDetailTitleSB.append("</td>");
dnsDetailTitleSB.append("</tr>");
dnsDetailTitleSB.append("</table>");

String dnsDetailTitleTable = dnsDetailTitleSB.toString();
//resin
String[][] resDetailTitleFlag= {
                            {"摘要信息" ,"system()", "0" },
							{"性能信息" ,"detail()", "1" },
							{"TCP端口","tcpport()","2"}, 
							{"线程池","pool()","3"}, 
							{"告警" ,"alarm()", "4"}
							};
String[][] resDetailTitle =resDetailTitleFlag;
StringBuffer resDetailTitleSB = new StringBuffer();
resDetailTitleSB.append("<table>");
resDetailTitleSB.append("<tr>");
resDetailTitleSB.append("<td width=\""+resDetailTitle.length*72+"\">");
resDetailTitleSB.append("<table>");
resDetailTitleSB.append("<tr>");
for(int i = 0 ; i < resDetailTitle.length ; i++){
	resDetailTitleSB.append("<td id=\"resDetailTitle-"+ resDetailTitle[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick=\"" + resDetailTitle[i][1] + "\">" + resDetailTitle[i][0]+ "</a></td>" );
}
resDetailTitleSB.append("</tr>");
resDetailTitleSB.append("</table>");
resDetailTitleSB.append("</td>");
resDetailTitleSB.append("</tr>");
resDetailTitleSB.append("</table>");
String resDetailTitleTable = resDetailTitleSB.toString();

	//netApp
	String[][] netAppDetailTitle = {
		 { "系统信息", "/netapp.do?action=system&flag=" + flag, "10" },
		  { "性能信息", "/netapp.do?action=performance&flag=" + flag, "0" },
		   { "流速信息", "/netapp.do?action=interface&flag=" + flag, "1" },
		    { "VFiler", "/netapp.do?action=vFiler&flag=" + flag, "2" },
		     { "Volume", "/netapp.do?action=volume&flag=" + flag, "3" },
		     { "Raid", "/netapp.do?action=raid&flag=" + flag, "4" }, 
		     { "Dump", "/netapp.do?action=dump&flag=" + flag, "5" },
		      { "Disk", "/netapp.do?action=disk&flag=" + flag, "6" },
		      { "Spare", "/netapp.do?action=spare&flag=" + flag, "7" }, 
		      { "Environment", "/netapp.do?action=environment&flag=" + flag, "8" }, 
		      { "Other", "/netapp.do?action=other&flag=" + flag, "9" }, 
		      { "告警信息", "/netapp.do?action=event&flag=" + flag, "10" } };
	StringBuffer netAppDetailTitleSB = new StringBuffer();
	netAppDetailTitleSB.append("<table>");
	netAppDetailTitleSB.append("<tr>");
	netAppDetailTitleSB.append("<td width=\"" + netAppDetailTitle.length * 72 + "\">");
	netAppDetailTitleSB.append("<table>");
	netAppDetailTitleSB.append("<tr>");
	for (int i = 0; i < netAppDetailTitle.length; i++) {
		netAppDetailTitleSB.append("<td id=\"hostDetailTitle-" + netAppDetailTitle[i][2] + "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + netAppDetailTitle[i][1] + "\")'>" + netAppDetailTitle[i][0] + "</a></td>");
	}
	netAppDetailTitleSB.append("</tr>");
	netAppDetailTitleSB.append("</table>");
	netAppDetailTitleSB.append("</td>");
	netAppDetailTitleSB.append("</tr>");
	netAppDetailTitleSB.append("</table>");
	
	String netAppDetailTitleTable = netAppDetailTitleSB.toString();
//String[][] airConditionDetailTitleFlag={
//                          {"状态信息" ,"/aircon.do?action=detail&id="+tmp1 , "0" }, 
//						   {"告警" ,"/aircon.do?action=alarm&id="+tmp1 , "1"}       
//};
//String[][] airConditionDetailTitle = airConditionDetailTitleFlag;
//StringBuffer airConditionDetailTitleSB = new StringBuffer();
//airConditionDetailTitleSB.append("<table>");
//airConditionDetailTitleSB.append("<tr>");
//airConditionDetailTitleSB.append("<td width=\""+airConditionDetailTitleFlag.length*72+"\">");
//airConditionDetailTitleSB.append("<table>");
//airConditionDetailTitleSB.append("<tr>");
//for(int i = 0 ; i < airConditionDetailTitle.length ; i++){
//	airConditionDetailTitleSB.append("<td id=\"airConditionDetailTitle-"+ airConditionDetailTitleFlag[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + airConditionDetailTitleFlag[i][1] + "\")'>" + airConditionDetailTitleFlag[i][0]+ "</a></td>" );
//}
//airConditionDetailTitleSB.append("</tr>");
//airConditionDetailTitleSB.append("</table>");
//airConditionDetailTitleSB.append("</td>");
//airConditionDetailTitleSB.append("</tr>");
//airConditionDetailTitleSB.append("</table>");
//String airConditionDetailTitleTable = airConditionDetailTitleSB.toString();
String[][] hpStorageDetailTileflag={
                        {"系统信息" ,"/hpstorage.do?action=system&id="+tmp1 , "0" },
                        {"阵列信息","/hpstorage.do?action=array&id="+tmp1,"1"}, 
                        {"附件信息","/hpstorage.do?action=enclosures&id="+tmp1,"2"},
                        {"控制器信息","/hpstorage.do?action=controllers&id="+tmp1,"3"},
                        {"端口信息","/hpstorage.do?action=ports&id="+tmp1,"4"},
                        {"磁盘信息","/hpstorage.do?action=disks&id="+tmp1,"5"},
                        {"逻辑单元信息","/hpstorage.do?action=luns&id="+tmp1,"6"},
                        {"VFP信息","/hpstorage.do?action=vfps&id="+tmp1,"7"},
                        {"子系统信息","/hpstorage.do?action=subsysteminfo&id="+tmp1,"8"},
						{"告警" ,"/hpstorage.do?action=alarm&id="+tmp1 , "9"}  
};
String[][] hpStorageDetailTile = hpStorageDetailTileflag;
StringBuffer hpStorageDetailTileSB = new StringBuffer();
hpStorageDetailTileSB.append("<table>");
hpStorageDetailTileSB.append("<tr>");
hpStorageDetailTileSB.append("<td width=\""+hpStorageDetailTileflag.length*72+"\">");
hpStorageDetailTileSB.append("<table>");
hpStorageDetailTileSB.append("<tr>");
for(int i = 0 ; i < hpStorageDetailTile.length ; i++){
	hpStorageDetailTileSB.append("<td id=\"hpStorageDetailTitle-"+ hpStorageDetailTileflag[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + hpStorageDetailTileflag[i][1] + "\")'>" + hpStorageDetailTileflag[i][0]+ "</a></td>" );
}
hpStorageDetailTileSB.append("</tr>");
hpStorageDetailTileSB.append("</table>");
hpStorageDetailTileSB.append("</td>");
hpStorageDetailTileSB.append("</tr>");
hpStorageDetailTileSB.append("</table>");
String hpStorageDetailTitleTable = hpStorageDetailTileSB.toString();

String[][] hpStorageSshDetailTileflag={
                        {"系统信息" ,"/hpstorage.do?action=system&id="+tmp1 , "0" },
                        {"raid信息","/hpstorage.do?action=array&id="+tmp1,"1"}, 
                        {"阵盘柜信息","/hpstorage.do?action=enclosures&id="+tmp1,"2"},
                        {"控制器信息","/hpstorage.do?action=controllers&id="+tmp1,"3"},
                        {"端口信息","/hpstorage.do?action=ports&id="+tmp1,"4"},
                        {"磁盘信息","/hpstorage.do?action=disks&id="+tmp1,"5"},
                        {"传感器信息","/hpstorage.do?action=sensor&id="+tmp1,"6"},
                        {"卷信息","/hpstorage.do?action=volume&id="+tmp1,"7"},
						{"告警" ,"/hpstorage.do?action=alarm&id="+tmp1 , "9"}  
};
String[][] hpStorageSshDetailTile = hpStorageSshDetailTileflag;
StringBuffer hpStorageSshDetailTileSB = new StringBuffer();
hpStorageSshDetailTileSB.append("<table>");
hpStorageSshDetailTileSB.append("<tr>");
hpStorageSshDetailTileSB.append("<td width=\""+hpStorageSshDetailTileflag.length*72+"\">");
hpStorageSshDetailTileSB.append("<table>");
hpStorageSshDetailTileSB.append("<tr>");
for(int i = 0 ; i < hpStorageSshDetailTile.length ; i++){
	hpStorageSshDetailTileSB.append("<td id=\"hpStorageDetailTitle-"+ hpStorageSshDetailTileflag[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + hpStorageSshDetailTileflag[i][1] + "\")'>" + hpStorageSshDetailTileflag[i][0]+ "</a></td>" );
}
hpStorageSshDetailTileSB.append("</tr>");
hpStorageSshDetailTileSB.append("</table>");
hpStorageSshDetailTileSB.append("</td>");
hpStorageSshDetailTileSB.append("</tr>");
hpStorageSshDetailTileSB.append("</table>");
String hpStorageSshDetailTitleTable = hpStorageSshDetailTileSB.toString();
String hpStorageReportDetailTileTable = "";
if(hostDetail != null){
String[][] hpStorageReportDetailTilefalg={
                       {"连通率报表" ,"/netreport.do?action=showHPstorPingReport&ipaddress=" + hostDetail.getIpAddress() + "&id="+tmp1 , "0" },
                       {"磁盘报表" ,"/netreport.do?action=showHPstorDiskReport&ipaddress=" + hostDetail.getIpAddress() + "&id="+tmp1 , "1" },
                       {"综合报表" ,"/netreport.do?action=showHPstorCompositeReport&ipaddress=" + hostDetail.getIpAddress() + "&id="+tmp1 , "2" },
};
String[][] hpStorageReportDetailTile = hpStorageReportDetailTilefalg;
StringBuffer hpStorageReportDetailTileSB = new StringBuffer();
hpStorageReportDetailTileSB.append("<table>");
hpStorageReportDetailTileSB.append("<tr>");
hpStorageReportDetailTileSB.append("<td width=\""+hpStorageReportDetailTilefalg.length*72+"\">");
hpStorageReportDetailTileSB.append("<table>");
hpStorageReportDetailTileSB.append("<tr>");
for(int i = 0 ; i < hpStorageReportDetailTile.length ; i++){
	hpStorageReportDetailTileSB.append("<td id=\"hpStorageReportDetailTile-"+ hpStorageReportDetailTilefalg[i][2]+ "\" class=\"detail-data-title-out\" onmouseover=\"this.className='detail-data-title-over'\" onmouseout=\"this.className='detail-data-title-out'\"><a href=\"#\" onclick='refer(\"" + hpStorageReportDetailTilefalg[i][1] + "\")'>" + hpStorageReportDetailTilefalg[i][0]+ "</a></td>" );
}
hpStorageReportDetailTileSB.append("</tr>");
hpStorageReportDetailTileSB.append("</table>");
hpStorageReportDetailTileSB.append("</td>");
hpStorageReportDetailTileSB.append("</tr>");
hpStorageReportDetailTileSB.append("</table>");
hpStorageReportDetailTileTable = hpStorageReportDetailTileSB.toString();
}

%>