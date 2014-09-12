<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.alarm.model.AlarmIndicators"%>
<%@page import="java.util.List"%>

<%
    String rootPath = request.getContextPath();
    String menuTable = (String) request.getAttribute("menuTable");
    List list = (List) request.getAttribute("list");

    JspPage jp = (JspPage) request.getAttribute("page");
    String alarmfindselect = (String) session.getAttribute("alarmfindselect");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link
			href="<%=rootPath%>/common/contextmenu/skins/default/contextmenu.css"
			rel="stylesheet">
		<script src="<%=rootPath%>/common/contextmenu/js/jquery-1.8.2.min.js"
			type="text/javascript"></script>
		<script src="<%=rootPath%>/common/contextmenu/js/contextmenu.js"
			type="text/javascript"></script>
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript">
		 	
			var show = true;
			var hide = false;
			//修改菜单的上下箭头符号
			function my_on(head,body)
			{
				var tag_a;
				for(var i=0;i<head.childNodes.length;i++)
				{
					if (head.childNodes[i].nodeName=="A")
					{
						tag_a=head.childNodes[i];
						break;
					}
				}
				tag_a.className="on";
			}
			function my_off(head,body)
			{
				var tag_a;
				for(var i=0;i<head.childNodes.length;i++)
				{
					if (head.childNodes[i].nodeName=="A")
					{
						tag_a=head.childNodes[i];
						break;
					}
				}
				tag_a.className="off";
			}
			//添加菜单	
			function initmenu()
			{
				var idpattern=new RegExp("^menu");
				var menupattern=new RegExp("child$");
				var tds = document.getElementsByTagName("div");
				for(var i=0,j=tds.length;i<j;i++){
					var td = tds[i];
					if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
						menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
						menu.init();		
					}
				}
			
			}
		</script>
		<script language="JavaScript">
			
			function add(){
				mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=add";
				mainForm.submit();
			}
			
			  function toFind()
			  {
			     mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=find";
			     mainForm.submit();
			  }
			
			function viewAlarmNode(){
				var nodeid = "201";
				var type = "db";
				var subtype = "Oracle";
				var url = "<%=rootPath%>/alarmIndicatorsNode.do?action=list&nodeid=" + nodeid + "&type=" + type + "&subtype=" + subtype;
				window.open(url,"protypeWindow","toolbar=no,width=900,height=500,directories=no,status=no,scrollbars=yes,menubar=no");
			}
			
			
			  
			
		</script>
		<script type="text/javascript">
		
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
  			var delAction = "<%=rootPath%>/alarmIndicators.do?action=delete";
  			var listAction = "<%=rootPath%>/alarmIndicators.do?action=list";
  			
  			function(){
  			  if(confirm('是否确定删除这条记录?')) {
  			    mainForm.action="<%=rootPath%>/alarmIndicators.do?action=delete";
  			    mainForm.submit();
  			  }
  			}
		</script>

		<!-- 二级级级联菜单 -->
		<script type="text/javascript">
			SPT="--请选择类型--";
			SCT="--请选择类别--";
			SAT="--请选择指标--";
			ShowT=1;		//提示文字 0:不显示 1:显示
			PCAD="<%=alarmfindselect%>";
			if(ShowT)PCAD=SPT+"$"+SCT+","+SAT+"#"+PCAD;
			PCAArea=[];
			PCAP=[];
			PCAC=[];
			PCAA=[];
			PCAN=PCAD.split("#");
			for(i=0;i<PCAN.length;i++)
			{
				PCAA[i]=[];TArea=PCAN[i].split("$")[1].split("|");
				for(j=0;j<TArea.length;j++)
				{
					PCAA[i][j]=TArea[j].split(",");
					if(PCAA[i][j].length==1)
					PCAA[i][j][1]=SAT;TArea[j]=TArea[j].split(",")[0]
				}
					PCAArea[i]=PCAN[i].split("$")[0]+","+TArea.join(",");
					PCAP[i]=PCAArea[i].split(",")[0];
					PCAC[i]=PCAArea[i].split(',')
			}
	
			function PCAS()
			{
				this.SelP=document.getElementsByName(arguments[0])[0];
				this.SelC=document.getElementsByName(arguments[1])[0];
				this.SelA=document.getElementsByName(arguments[2])[0];
				this.DefP=this.SelA?arguments[3]:arguments[2];
				this.DefC=this.SelA?arguments[4]:arguments[3];
				this.DefA=this.SelA?arguments[5]:arguments[4];
				this.SelP.PCA=this;
				this.SelC.PCA=this;
				this.SelP.onchange=function(){PCAS.SetC(this.PCA)};
				if(this.SelA)
				this.SelC.onchange=function(){PCAS.SetA(this.PCA)};
				PCAS.SetP(this)
			};
				
				PCAS.SetP=function(PCA){
				for(i=0;i<PCAP.length;i++){PCAPT=PCAPV=PCAP[i];
				if(PCAPT==SPT)PCAPV="";
				PCA.SelP.options.add(new Option(PCAPT,PCAPV));
				if(PCA.DefP==PCAPV)PCA.SelP[i].selected=true}PCAS.SetC(PCA)
				};
				
				PCAS.SetC=function(PCA){
				PI=PCA.SelP.selectedIndex;
				PCA.SelC.length=0;
				for(i=1;i<PCAC[PI].length;i++){PCACT=PCACV=PCAC[PI][i];
				if(PCACT==SCT)PCACV="";
				PCA.SelC.options.add(new Option(PCACT,PCACV));
				if(PCA.DefC==PCACV)
				PCA.SelC[i-1].selected=true}
				if(PCA.SelA)PCAS.SetA(PCA)
				};
				
				PCAS.SetA=function(PCA){
				PI=PCA.SelP.selectedIndex;
				CI=PCA.SelC.selectedIndex;
				PCA.SelA.length=0;
				for(i=1;i<PCAA[PI][CI].length;i++){
				PCAAT=PCAAV=PCAA[PI][CI][i];
				if(PCAAT==SAT)
				PCAAV="";
				PCA.SelA.options.add(new Option(PCAAT,PCAAV));
				if(PCA.DefA==PCAAV)PCA.SelA[i-1].selected=true}
				}
			</script>

	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content"
										class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
													<tr>
														<td align="left" width="5">
															<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																width="5" height="29" />
														</td>
														<td class="content-title">
															&nbsp;告警 >> 告警配置 >> 告警指标配置 >> 告警指标列表
														</td>
														<td align="right">
															<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																width="5" height="29" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="content-body" class="content-body">
													<tr>
														<td>
															<table>
																<tr>
																	<td class="body-data-title">
																		<jsp:include page="../../common/page.jsp">
																			<jsp:param name="curpage"
																				value="<%=jp.getCurrentPage()%>" />
																			<jsp:param name="pagetotal"
																				value="<%=jp.getPageTotal()%>" />
																		</jsp:include>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
																	<%
																	    String select1 = "--请选择类型--";
																	    String select2 = "--请选择类型--";
																	    select1 = (String) request.getAttribute("con1");
																	    select2 = (String) request.getAttribute("con2");
																	%>
																	<td class="body-data-title" style="text-align: right;">
																		类别:
																		<select size="1" name="categorycon">
																		</select>
																	</td>
																	<td class="body-data-title" style="text-align: right;">
																		类型：
																		<select size="1" name="entitycon">
																		</select>
																	</td>
																	<script language="javascript" defer>
																		new PCAS("categorycon","entitycon","<%=select1%>","<%=select2%>");
																	</script>
																	<td class="body-data-title" style="text-align: right;">
																		<input type="button" name="find" value="查询"
																			onclick="toFind()">
																	</td>
																	<td class="body-data-title" style="text-align: right;">
																		<a href="#" onclick="viewAlarmNode()">查看告警阀值</a>&nbsp;&nbsp;&nbsp;
																		<a href="#" onclick="add()">添加</a>&nbsp;&nbsp;&nbsp;
																		<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
																	<td class="body-data-title">
																		<INPUT type="checkbox" name="checkall"
																			onclick="javascript:chkall()">
																		序号
																	</td>
																	<td class="body-data-title">
																		名称
																	</td>
																	<td class="body-data-title">
																		类型
																	</td>
																	<td class="body-data-title">
																		子类型
																	</td>
																	<td class="body-data-title">
																		数据类型
																	</td>
																	<td class="body-data-title">
																		单位
																	</td>
																	<td class="body-data-title">
																		监视
																	</td>
																	<td class="body-data-title">
																		比较方式
																	</td>
																	<td class="body-data-title">
																		一级阀值
																	</td>
																	<td class="body-data-title">
																		次数
																	</td>
																	<td class="body-data-title">
																		是否发送短信
																	</td>
																	<td class="body-data-title">
																		二级阀值
																	</td>
																	<td class="body-data-title">
																		次数
																	</td>
																	<td class="body-data-title">
																		是否发送短信
																	</td>
																	<td class="body-data-title">
																		三级阀值
																	</td>
																	<td class="body-data-title">
																		次数
																	</td>
																	<td class="body-data-title">
																		是否发送短信
																	</td>
																	<td class="body-data-title">
																		详情
																	</td>
																</tr>
																<%
																    if (list != null && list.size() > 0) {
																        for (int i = 0; i < list.size(); i++) {
																            AlarmIndicators alarmIndicators = (AlarmIndicators) list.get(i);

																            String isMonitor = "否";
																            if ("1".equals(alarmIndicators.getEnabled())) {
																                isMonitor = "是";
																            }

																            //System.out.println(alarmIndicators.getDatatype()+ "====================");

																            String datatype = "字符串";
																            if ("Number".equals(alarmIndicators.getDatatype())) {
																                datatype = "数字";
																            }

																            String sms0 = "否";
																            if ("1".equals(alarmIndicators.getSms0())) {
																                sms0 = "是";
																            }

																            String sms1 = "否";
																            if ("1".equals(alarmIndicators.getSms1())) {
																                sms1 = "是";
																            }

																            String sms2 = "否";
																            if ("1".equals(alarmIndicators.getSms2())) {
																                sms2 = "是";
																            }
																            String compare = "升序";
																            String bgcol = "#ffffff";
																            if (alarmIndicators.getCompare() == 0) {
																                compare = "降序";
																                bgcol = "gray";
																            } else if (alarmIndicators.getCompare() == 1) {
																                compare = "升序";
																                bgcol = "#ffffff";
																            } else if (alarmIndicators.getCompare() == 2) {
																                compare = "相等";
																                bgcol = "gray";
																            }
																%>

																<tr>
																	<td class="body-data-list">
																		<INPUT type="checkbox" name="checkbox"
																			value="<%=alarmIndicators.getId()%>"><%=jp.getStartRow() + i%></td>
																	<td class="body-data-list"><%=alarmIndicators.getName()%></td>
																	<td class="body-data-list"><%=alarmIndicators.getType()%></td>
																	<td class="body-data-list"><%=alarmIndicators.getSubtype()%></td>
																	<td class="body-data-list"><%=datatype%></td>
																	<td class="body-data-list"><%=alarmIndicators.getThreshlod_unit()%></td>
																	<td class="body-data-list"><%=isMonitor%></td>
																	<td class="body-data-list" bgcolor="<%=bgcol%>"><%=compare%></td>
																	<td class="body-data-list"><%=alarmIndicators.getLimenvalue0()%></td>
																	<td class="body-data-list"><%=alarmIndicators.getTime0()%></td>
																	<td class="body-data-list"><%=sms0%></td>
																	<td class="body-data-list"><%=alarmIndicators.getLimenvalue1()%></td>
																	<td class="body-data-list"><%=alarmIndicators.getTime1()%></td>
																	<td class="body-data-list"><%=sms1%></td>
																	<td class="body-data-list"><%=alarmIndicators.getLimenvalue2()%></td>
																	<td class="body-data-list"><%=alarmIndicators.getTime2()%></td>
																	<td class="body-data-list"><%=sms2%></td>
																	<td align="center" class="body-data-list">
																		<input type="hidden" id="id" name="id"
																			value="<%=alarmIndicators.getId()%>">
																		<img class="img" src="<%=rootPath%>/resource/image/status.gif"
																			border="0" width=15>
																	</td>
																</tr>
																<%
																    }
																    }
																%>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="content-footer" class="content-footer">
													<tr>
														<td>
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0">
																<tr>
																	<td align="left" valign="bottom">
																		<img src="<%=rootPath%>/common/images/right_b_01.jpg"
																			width="5" height="12" />
																	</td>
																	<td></td>
																	<td align="right" valign="bottom">
																		<img src="<%=rootPath%>/common/images/right_b_03.jpg"
																			width="5" height="12" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</form>
	</body>
	<script type="text/javascript">  
			$('.img').contextmenu({
				height:35,
				width:100,
				items : [{
					text :'编辑',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/edit.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
						edit(id);
					}
				}]
			});	
			
		//右键菜单方法开始
			function edit(node){
				mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=edit&id=" + node;
				mainForm.submit();
			}	
		//右键菜单方法结束
        </script>
</html>
