/**
 * <p>Description:mapping table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class User extends BaseVo
{
    private int id;
    private String userid;
    private String name;
    private String password;
    private int sex;
    private int role;
    private String phone;
    private String mobile;
    private String email;
    private int dept;
    private int position;
    private String businessids;
    private String skins;
    private String group;
    private String deptname;
    private String positionname;
    private String rolename;
    
    
	public String getSkins() {
		return skins;
	}

	public void setSkins(String skins) {
		this.skins = skins;
	}
  
	public int getDept() 
	{
		return dept;
	}

	public void setDept(int dept) 
	{
		this.dept = dept;
	}

	public int getPosition() 
	{
		return position;
	}

	public void setPosition(int position) 
	{
		this.position = position;
	}
  
    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setSex(int sex)
    {
        this.sex = sex;
    }

    public int getSex()
    {
        return sex;
    }

    public void setRole(int role)
    {
        this.role = role;
    }

    public int getRole()
    {
        return role;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmail()
    {
        return email;
    }
    
    public void setBusinessids(String businessids)
    {
        this.businessids = businessids;
    }

    public String getBusinessids()
    {
        return businessids;
    }

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public String getPositionname() {
		return positionname;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

}
