package com.afunms.application.weblogicmonitor;

public class WeblogicHeap {
	String jvmRuntimeName = null; //名称 .1.3.6.1.4.1.140.625.340.1.15
	String jvmRuntimeHeapSizeCurrent = null; //堆大小 .1.3.6.1.4.1.140.625.340.1.30
	String jvmRuntimeHeapFreeCurrent = null; //当前空闲堆大小  .1.3.6.1.4.1.140.625.340.1.25  
	public String getJvmRuntimeName() {
		return jvmRuntimeName;
	}
	public void setJvmRuntimeName(String jvmRuntimeName) {
		this.jvmRuntimeName = jvmRuntimeName;
	}
	public String getJvmRuntimeHeapSizeCurrent() {
		return jvmRuntimeHeapSizeCurrent;
	}
	public void setJvmRuntimeHeapSizeCurrent(String jvmRuntimeHeapSizeCurrent) {
		this.jvmRuntimeHeapSizeCurrent = jvmRuntimeHeapSizeCurrent;
	}
	public String getJvmRuntimeHeapFreeCurrent() {
		return jvmRuntimeHeapFreeCurrent;
	}
	public void setJvmRuntimeHeapFreeCurrent(String jvmRuntimeHeapFreeCurrent) {
		this.jvmRuntimeHeapFreeCurrent = jvmRuntimeHeapFreeCurrent;
	}
	
	
}
