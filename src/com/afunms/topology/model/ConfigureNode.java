package com.afunms.topology.model;
import com.afunms.common.base.BaseVo;

public class ConfigureNode extends BaseVo{ 

	private int id;

	private String name;

	private String text;

	private int fatherId;//父节点id
	
	private boolean leaf;//是否叶子
	 
	private String descn;
	
	String PID; 
	
    public String getPID() { 
        return PID; 
    } 
    public void setPID(String PID) { 
        this.PID = PID; 
    }
	
	public int getFatherId() {
		return fatherId;
	}

	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
     
	public boolean getLeaf(){
		return leaf;
	}
	
	public void setLeaf(boolean leaf){
		this.leaf=leaf;
	}
	
	public String getDescn(){
		return descn;
	}
	
	public void setDescn(String descn){
		this.descn=descn;
	}
} 