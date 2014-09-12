package com.afunms.topology.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class HintItem extends BaseVo implements Serializable {
	
	private String iconId;
	private String iconPath;
	private String sortName;
	private String iconName;
	private String id;
	private byte[] file;
	private String webIconPath;

	  public String getWebIconPath()
	  {
	    return this.webIconPath;
	  }

	  public void setWebIconPath(String webIconPath)
	  {
	    this.webIconPath = webIconPath;
	  }

	  public String getIconId()
	  {
	    return this.iconId;
	  }

	  public void setIconId(String iconId)
	  {
	    this.iconId = iconId;
	  }

	  public String getIconPath()
	  {
	    return this.iconPath;
	  }

	  public void setIconPath(String iconPath)
	  {
	    this.iconPath = iconPath;
	  }

	  public String getId()
	  {
	    return this.id;
	  }

	  public void setId(String id)
	  {
	    this.id = id;
	  }

	  public String getSortName()
	  {
	    return this.sortName;
	  }

	  public void setSortName(String sortName)
	  {
	    this.sortName = sortName;
	  }

	  public String getIconName()
	  {
	    return this.iconName;
	  }

	  public void setIconName(String iconName)
	  {
	    this.iconName = iconName;
	  }

	  public byte[] getFile()
	  {
	    return ((byte[])this.file.clone());
	  }

	  public void setFile(byte[] file)
	  {
	    this.file = ((byte[])file.clone());
	  }
}
