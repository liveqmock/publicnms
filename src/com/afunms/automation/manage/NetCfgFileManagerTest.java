package com.afunms.automation.manage;


import org.junit.Test;

public class NetCfgFileManagerTest {

	@Test
	public void testList() {
		NetCfgFileManager ncfm = new NetCfgFileManager();
		String str = ncfm.list();
		System.out.println(str);
	}

}
