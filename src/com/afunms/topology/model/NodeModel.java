package com.afunms.topology.model;

public class NodeModel {

	private String id;// 编号

	private String fatherId;// 父结点编号

	private String name;// 显示名称

	private int state;// 运行状态(1=正常;0=故障)

	private float weight;// 线的粗细

	private String url;// 显示图片

	private String deviceInfo;// 显示设备信息

	private int x;// 节点横坐标

	private int y;// 节点纵坐标

	private String nodeMenuInfo;// 节点菜单信息

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFatherId() {
		return fatherId;
	}

	public void setFatherId(String fatherId) {
		this.fatherId = fatherId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getNodeMenuInfo() {
		return nodeMenuInfo;
	}

	public void setNodeMenuInfo(String nodeMenuInfo) {
		this.nodeMenuInfo = nodeMenuInfo;
	}

}
