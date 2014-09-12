<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.Menu"%>
<%@page import="java.util.List"%>
<%
   List list = (List)request.getAttribute("list");   
   int rc = list.size();
   String rootPath = request.getContextPath();
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />

<title>dhcnms</title>

<script language="JavaScript" type="text/javascript">
  function add()
  {
    var opt = new Option();
    var optText;
    var bExist;

    if ( mainForm.order1.selectedIndex == -1 ) return;

    optText = mainForm.order1.options[mainForm.order1.selectedIndex].text;
    bExist = false;
    for ( i=0; i < mainForm.order2.options.length; i++ )
    {
      if ( optText == mainForm.order2.options[i].text )
      {
          bExist = true;
          break;
      }
    }
    if ( bExist )
    {
       alert( "[" + optText + "]已经存在!" );
       return;
    }
    opt.text = optText
    opt.value = mainForm.order1.value;
    mainForm.order2.options.add(opt);
  }

  function del()
  {
    if ( mainForm.order2.selectedIndex !=-1 )
    {
      mainForm.order2.remove(mainForm.order2.selectedIndex);
    }
  }

  function selectAll()
  {
    var iLen = mainForm.order1.options.length;
    var opt;

    if ( mainForm.order2.options.length == 0 )
    {
      for ( i=0 ; i<iLen; i++ )
      {
        opt = new Option();
        opt.text = mainForm.order1[i].text;
        opt.value = mainForm.order1[i].value;
        mainForm.order2.options.add(opt);
      }
    }
    else
    {
      for ( i=0 ; i<iLen; i++ )
      {
        mainForm.order2.remove(mainForm.order2[i]);
      }
    }
  }

  function ok()
  {
     if ( mainForm.order2.options.length == 0 )
     {
       alert("右边下拉框为空,请增加记录!")
       return false
     }
     else if ( mainForm.order2.options.length != <%=rc%> )
     {
       alert("右边下拉框与左边下拉框记录数不等,请增加!")
       return false
     }

     var len = mainForm.order2.options.length; //右边下拉框全选
     for ( i=0 ; i < len; i++ )
        mainForm.order2.options[i].selected = true;

     mainForm.action = "<%=rootPath%>/menu.do?action=change_sort";
     mainForm.submit();
  }

  function goback()
  {
     mainForm.action = "<%=rootPath%>/menu.do?action=list_top";
     mainForm.submit();
  }
</script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<script language="JavaScript" type="text/JavaScript">
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
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">

<form name="mainForm" method="post">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="16">　</td>
		<td bgcolor="#9FB0C4" align="center">
		<br>
		<table width="95%" border=0 cellpadding=0 cellspacing=0>
			<tr>
				<td><img src="<%=rootPath%>/resource/image/main_frame_ltcorner.gif" width=12 height=34></td>
				<td background="<%=rootPath%>/resource/image/main_frame_tbg.gif" align="right">
				<img border="0" src="<%=rootPath%>/resource/image/main_frame_tflag.gif" width="66" height="34"></td>
				<td><img src="<%=rootPath%>/resource/image/main_frame_rtcorner.gif" width=13 height=34></td>
			</tr>
			<tr>
				<td background="<%=rootPath%>/resource/image/main_frame_lbg.gif" width=12>　</td>
				<td height=300 bgcolor="#FFFFFF" valign="top"  align="center">
				  <table border="0" id="table1" cellpadding="0" cellspacing="1"
						width="95%">
					<TBODY>
						<tr>
							<TD align="left" colspan="4" height="24">一级菜单--改变顺序</TD>
						</tr>
						<tr>
							<td nowrap colspan="4" height="3" bgcolor="#8EADD5"></td>
						</tr>
						<tr style="background-color: #ECECEC;">
						    <TD colspan=2>

<table width="300" align="center" border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>
 <tr>
   <td width=110 valign="top">
<select name="order1" size="<%=rc%>" style="width:100px;" multiple>
<%
  for(int i=0;i<rc;i++)
  {
     Menu vo = (Menu)list.get(i);
     out.print("<option value='" + vo.getId().substring(0,2) + "'>" + vo.getTitle() + "</option>");
  }
%>
</select>
</td>
<td align="center" width="50">
   <p align="center"><a href="#" onclick="add()">增加</a></p>
   <p align="center"><a href="#" onclick="del()">删除</a></p>
   <p align="center"><a href="#" onclick="selectAll()">全选</a></p>
</td>
<td width=110 valign="top">
  <select name="order2" size="<%=rc%>" style="width:100px;" multiple></select>
</td>
</tr>
</table>
						    
							</TD>	
						</tr>		
						<tr>
							<TD nowrap colspan="4">
								<br>
								<input type="button" value="保存" style="width:50" class="formStylebutton" onclick="ok()">&nbsp;&nbsp;
								<input type=reset class="formStylebutton" style="width:50" value="返回" onclick="javascript:history.back(1)">
							</TD>	
						</tr>						
										
					</TBODY>
				</TABLE>
				</td>
				<td background="<%=rootPath%>/resource/image/main_frame_rbg.gif" width=13 colspan="3">　</td>
			</tr>
			<tr>
				<td valign="top"><img src="<%=rootPath%>/resource/image/main_frame_lbcorner.gif" width=12 height=15></td>
				<td background="<%=rootPath%>/resource/image/main_frame_bbg.gif" height=15></td>
				<td valign="top"><img src="<%=rootPath%>/resource/image/main_frame_rbcorner.gif" width=13 height=15></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>	
</body>
</html>
