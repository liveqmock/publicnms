<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%
			String rootPath = request.getContextPath();
			String compareCfgName = (String) request.getAttribute("compareCfgName");
			String baseCfgName = (String) request.getAttribute("baseCfgName");
		%>
<html>
	<head>
		
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
		<title>备份配置文件比对</title>
	    <link rel="stylesheet" href="<%=rootPath%>/automation/css/style.css" type="text/css">
       
		<link href="<%=rootPath%>/automation/css/diff.css" rel="stylesheet" type="text/css" charset="UTF-8" />
		<script type="text/javascript" charset="UTF-8" src="<%=rootPath%>/automation/js/diff.js"></script>
        <script type="text/javascript" charset="UTF-8" src="<%=rootPath%>/automation/js/jquery-1.7.1.min.js"></script>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<style>
		<!--
body {
	height: 100%;
	TEXT-ALIGN: center;
	background: #f5f5f5;
}
-->
</style>
<script>
function compare(){

	$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/netCfgFileAjaxManager.ajax?action=compare&f="+Math.random(),
			success:function(result){
           $('#codeTextarea1').html(result.records[0].basic);
           $('#codeTextarea2').html(result.records[1].diff);
			}
		});
		createTextAreaWithLines('codeTextarea1',10000);
     	createTextAreaWithLines('codeTextarea2',10000);
     	$('#codeTextarea1').scroll( function() { 
		$('#codeTextarea2').scrollTop($(this).scrollTop()); 
		$('#codeTextarea2').scrollLeft($(this).scrollLeft()); 
		}); 
		$('#codeTextarea2').scroll( function() { 
		$('#codeTextarea1').scrollTop($(this).scrollTop()); 
		$('#codeTextarea1').scrollLeft($(this).scrollLeft()); 
		}); 
}

</script>
	</head>
	<body id="body" class="body" onload="compare()">
		<table id="container-main" class="container-main">
			<tr>
				<td >
					<table id="container-main-win" class="container-main-win">
						<tr>
							<td align=center>
								<table id="win-content" class="win-content">
									<tr>
										<td>
											<table id="win-content-header" class="win-content-header">
												<tr>
													<td align="left" width="5">
														<img src="<%=rootPath%>/common/images/right_t_01.jpg"
															width="5" height="29" />
													</td>
													<td class="win-content-title">
														自动化 >> 配置文件管理 >> 备份配置文件比对
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
										<td width="100%">
											<table id="win-content-body" class="win-content-body">


												<tr align="left" valign="middle">
													<td align="left" width="50%">
														<table>
															<tr>
																<td align=center width="90%">
																	基线文件(<%=baseCfgName%>)
																</td>
															</tr>
															<tr>
																<td width="90%" height="90%">
																	<div id="codeTextarea1"  style="overflow-x: auto; overflow-y: auto;font-size: 13px;width:95%;height:600px" ></div>
																</td>

															</tr>
														</table>
													</td>
													
													
													<td width="50%">
														<table>
															<tr>
																<td align=center width="90%">
																	比对文件(<%=compareCfgName%>)
																</td>
															</tr>
															<tr>
																<td width="90%">
																	<div id="codeTextarea2" style="overflow-x: auto; overflow-y: auto;font-size: 13px;width:95%;height:600px"></div>
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
       
		<div align=center>
		
			<input type=button value="关  闭" onclick="window.close()">
		</div>
		<div align=center style="font-size:14px">
		<font color="red">红色代表文字内容不同; </font>
		<font color="green" >绿色代表新增加的文字内容; </font>
		<font color="blue">蓝色代表减少的文字内容。</font>
		</div>
		</body>
</html>