/**
 * <p>Description:mapping table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class AlertEmail extends BaseVo
{
    private int id;
    private String username;
    private String password;
    private String smtp;
    private int usedflag;
    private String mailAddress;
    
  
	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getUsername() 
	{
		return username;
	}

	public void setUsername(String username) 
	{
		this.username = username;
	}
  
    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setUsedflag(int usedflag)
    {
        this.usedflag = usedflag;
    }

    public int getUsedflag()
    {
        return usedflag;
    }


    public void setSmtp(String smtp)
    {
        this.smtp = smtp;
    }

    public String getSmtp()
    {
        return smtp;
    }


}
