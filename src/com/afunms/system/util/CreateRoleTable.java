/**
 * <p>Description:create role table</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-08
 */

package com.afunms.system.util;

import java.util.List;

import com.afunms.system.dao.AccreditDao;
import com.afunms.system.dao.RoleDao;
import com.afunms.system.model.Accredit;
import com.afunms.system.model.Role;
import com.afunms.common.util.SysLogger;

public class CreateRoleTable
{
  private int rows;

  //用于编辑
  public String getTable(int role_id,boolean isAdmin)
  {
     StringBuffer table = new StringBuffer(2000);
     table.append("<table border=1 cellspacing=0 cellpadding=5 bordercolorlight='#000000' bordercolordark='#FFFFFF'>");
     table.append("<tr bgcolor='#D4E1D5'><td width=100 align='center'>一级菜单</td><td width=300 align='center'>");
     table.append("二级菜单</td><td width=150 align='center'>权 限</td></tr>");

     AccreditDao dao = null;
     try
     {
     	dao = new AccreditDao();
     	int[] menusNum = dao.getMenuNumByFirst(isAdmin);                
     	List list = dao.loadRoleMenu(role_id);            
        rows = 0;
        int first = 0;
        boolean beforeFirst = false; //是否紧接在一级菜单之后
        for(int i=0;i<list.size();i++)
        {
        	
           Accredit vo = (Accredit)list.get(i);
           if(vo.getMenu().substring(2,4).equals("00")) //说明是一级菜单
           {
              table.append("<tr class='othertr' align='center'><td rowspan='");
              table.append(menusNum[first]);
              table.append("' bgcolor='#D4E1D5'>");
              table.append(vo.getTitle());
              table.append("</td>");
              first++;
              beforeFirst = true;
           }
           else
           {
              if(!beforeFirst)
              {
                 table.append("<tr class='othertr'>");
                 beforeFirst = true;
              }
              table.append("<td align='center' class='othertr' bgcolor='#ECECEC'>");
              table.append("<font color='#8A2BE2'>");
              table.append(vo.getTitle());
              table.append("</font>");
              table.append("</td><td align='center' bgcolor='#ECECEC'>");
              if(isAdmin)
              	 table.append(getAdminOperate(vo.getOperate(),rows,vo.getMenu()));
              else
                 table.append(getOperate(vo.getOperate(),rows,vo.getMenu()));
              table.append("</td></tr>");
              rows++;
           } //end_if_else
        } //end_for
        table.append("</table>");
     }
     catch (Exception e)
     {
        SysLogger.error("CreateRoleTable.getTable()",e);
     }
     finally
     {
        dao.close();
     }
     return table.toString();
  }

  //返回表格总的行数
  public int getRows()
  {
     return rows;
  }

  /**
   * @param opr 可用操作
   * @param index 第几个select框
   * @param menuid 菜单id
   * @return
   */
  private String getOperate(int opr, int index, String menu)
  {
     StringBuffer selOpr = new StringBuffer(2000);
     selOpr.append("<select size=1 name='selectopr");
     selOpr.append(index);
     selOpr.append("' style='width:60px;'>");
     selOpr.append("<option value='");
     selOpr.append(menu);
     if(opr==1)
        selOpr.append(",1' selected>");
     else
        selOpr.append(",1'>");
     selOpr.append("屏蔽</option><option value='");
     selOpr.append(menu);
     if(opr==2)
        selOpr.append(",2' selected>");
     else
        selOpr.append(",2'>");
     selOpr.append("只读</option><option value='");
     selOpr.append(menu);
     if(opr==3)
        selOpr.append(",3' selected>");
     else
        selOpr.append(",3'>");
     selOpr.append("完全</option></select>");

     return selOpr.toString();
  }

  //专用于superadmin
  private String getAdminOperate(int opr, int index, String menu)
  {
     StringBuffer selOpr = new StringBuffer(2000);
     selOpr.append("<select size=1 name='selectopr");
     selOpr.append(index);
     selOpr.append("' style='width:60px;'>");
     selOpr.append("<option value='");
     selOpr.append(menu);
     if(opr==0)
        selOpr.append(",0' selected>");
     else
        selOpr.append(",0'>");
     selOpr.append("禁用</option><option value='");
     selOpr.append(menu);
     if(opr!=0)
        selOpr.append(",1' selected>");
     else
        selOpr.append(",1'>");
     selOpr.append("开放</option></select>");
     return selOpr.toString();
  }
  
  //角色选择 下拉框
  public String getRoleBox(int index)
  {
     StringBuffer sb = new StringBuffer(1000);
     sb.append("<select size=1 name='role' style='width:100px;' onchange='toRole()'>");

     RoleDao dao = new RoleDao();
     List list = dao.loadAll(false);
     dao.close();
     Role vo = null;
     for(int i=0;i<list.size();i++)
     {
        vo = (Role)list.get(i);
        if(index==vo.getId())
            sb.append("<option value='" + vo.getId() + "' selected>");
        else
            sb.append("<option value='" + vo.getId() + "'>");
        sb.append(vo.getRole());
        sb.append("</option>");
     }
     sb.append("</select>");
     return sb.toString();
  }
}
