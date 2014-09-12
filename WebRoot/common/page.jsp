<%@page language="java" contentType="text/html;charset=GB2312"%>
<%
    String rootPath = request.getContextPath();
%>
<table width="100%" class="pageTable">
	<tr>
		<td width="60%" height="30" align="left">
			第<%=request.getParameter("curpage")%>页 / 共<%=request.getParameter("pagetotal")%>页
			&nbsp;跳到&nbsp;
			<input name="jp" type="text" maxLength="3">
			&nbsp;页
			<input type="button" value="GO" name="bjp" onclick="goto_page()">

			<input type="hidden" name="perpagenum"
				value="<%=request.getParameter("perpagenum") == null ? "" : request.getParameter("perpagenum")%>">
		<td width="25%">
			<div align="center">
				每页显示条数&nbsp;[
				<a href="#" onclick="gotoPerPage(30)"><font color="#0000FF"
					face="Times New Roman, Times, serif"><strong>30</strong> </font> </a>
				<a href="#" onclick="gotoPerPage(50)"><font color="#0000FF"
					face="Times New Roman, Times, serif"><strong>50</strong> </font> </a>
				<a href="#" onclick="gotoPerPage(80)"><font color="#0000FF"
					face="Times New Roman, Times, serif"><strong>80</strong> </font> </a>
				<a href="#" onclick="gotoPerPage(100)"><font color="#0000FF"
					face="Times New Roman, Times, serif"><strong>100</strong> </font> </a>
				<a href="#" onclick="gotoPerPage(200)"><font color="#0000FF"
					face="Times New Roman, Times, serif"><strong>200</strong> </font> </a>]
			</div>
		</td>
		<td width="15%" align="right">
			<img src="<%=rootPath%>/resource/image/microsoftLook/first(on).gif"
				border="0" onclick="firstPage()" style="CURSOR: hand" alt="首页" />
			<img
				src="<%=rootPath%>/resource/image/microsoftLook/previous(on).gif"
				border="0" onclick="precedePage()" style="CURSOR: hand" alt="上一页" />
			<img src="<%=rootPath%>/resource/image/microsoftLook/forward(on).gif"
				border="0" onclick="nextPage()" style="CURSOR: hand" alt="下一页" />
			<img src="<%=rootPath%>/resource/image/microsoftLook/last(on).gif"
				border="0" onclick="lastPage()" style="CURSOR: hand" alt="尾页" />
		</td>
	</tr>
</table>
