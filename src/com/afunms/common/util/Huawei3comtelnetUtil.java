package com.afunms.common.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.model.Huaweitelnetconf;

/**
 * 
 * telnet 读取配置文件把vpn配置信息工具类
 * 
 * @author konglq
 * 
 */
public class Huawei3comtelnetUtil {

	public static Hashtable telnetconf = new Hashtable();

	/**
	 * 
	 * 初始化telnet连接的内存列表 是用网元的id做为key，数据库表对象为值
	 * 
	 */
	public static void inittelnetlist() 
	{
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		// Huaweitelnetconf mo=new Huaweitelnetconf();

		List list = new ArrayList();
		try{
			list = dao.loadEnableVpn();
		}catch(Exception e){
			
		}finally{
			dao.close();
		}
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				Huaweitelnetconf mo = new Huaweitelnetconf();
				mo = (Huaweitelnetconf) list.get(i);
				telnetconf.put(mo.getId(), mo);
			}
		}
		//dao.close();
	}

	private static String readfile(String filename) 
	{
		StringBuffer buffer = new StringBuffer();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));

			String cc = br.readLine();

			while (cc != null) {
				buffer.append(cc);
				buffer.append("\r\n");
				cc = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 文件输入流为

		//System.out.println(buffer.toString());
		return buffer.toString();
	}

	/**
	 * 读取配置文件找配置有vpn的端口 使用接口的名称为key ，vpn的名称作为value
	 * 
	 * @param conf
	 *            采集的配置字符串
	 * @return 返回vpn的配置列表
	 */
	public static Hashtable Getvpnlist(String conf) 
	{
		Hashtable list = new Hashtable();
		// System.out.println("======================="+conf);
		if (null != conf) {
			// 解析配置文件
			String[] datelist = conf.split("\r\n");
			//System.out.println(datelist.length);
			//
			if (null != datelist && datelist.length > 0) 
			{

				boolean flgvpn = false;// 找到端口的标记

				String infname = "";

				for (int i = 0; i < datelist.length; i++) 
				{

					// System.out.println("=&&&&====="+datelist[i]);
					// 先找到网口，然后根据端口再找到对应的配置，重新把端口标记设置为false
					String inf = datelist[i].trim();
					if (inf.indexOf("interface") == 0) 
					{
						// System.out.println("---"+i);
						infname = inf.replaceAll("interface", "").trim();
						flgvpn = true;
					}
					// 查找端口的vpn配置信息
					if (flgvpn && inf.indexOf("interface") == -1) {// 接口
						if (inf.indexOf("ip binding vpn-instance") >= 0) 
						{
							list.put(infname, inf.replace("ip binding vpn-instance", "").trim());
							flgvpn = false;
						}
					}
				}// 循环结束
			}
		}
		return list;
	}
	public static void main(String[] ars) {
		// Huawei3comtelnetUtil
		String ss = Huawei3comtelnetUtil.readfile("d://NE_08config");
		Huawei3comtelnetUtil.Getvpnlist(ss);
	}
}
