/**
 * <p>Description:used in CreateRoleTable</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-08
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class Accredit extends BaseVo
{
    private int role;
    private String menu;
    private String title;
    private int operate;

    public void setMenu(String menu)
    {
       this.menu = menu;
    }

    public String getMenu()
    {
       return menu;
    }

    public void setTitle(String title)
    {
       this.title = title;
    }

    public String getTitle()
    {
       return title;
    }

    public void setRole(int role)
    {
       this.role = role;
    }

    public int getRole()
    {
       return role;
    }

    public void setOperate(int operate)
    {
       this.operate = operate;
    }

    public int getOperate()
    {
       return operate;
    }
}
