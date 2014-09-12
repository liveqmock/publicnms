package com.afunms.detail.service.checkValueInfo;

import java.util.ArrayList;
import java.util.List;

import com.afunms.event.dao.CheckValueDao;
import com.afunms.event.model.CheckValue;

/**
 * 
 * @author Administrator  Jan 18, 2011
 *
 */
public class CheckValueService {
	private String type;
	
	private String subtype;
	
	private String nodeid;

	public CheckValueService(String type, String subtype, String nodeid) {
		super();
		this.type = type;
		this.subtype = subtype;
		this.nodeid = nodeid;
	}

	public CheckValueService() {
		super();
	}
	
	/**
	 * 得到当前连通率
	 * @param nodeId
	 * @param type
	 * @param subtype
	 * @return
	 */
	public String getPingNow(String nodeId, String type,
			String subtype) {
		String pingnow = "0";
		try {
			CheckValueDao checkValueDao = new CheckValueDao();
			List<CheckValue> checkValueList = new ArrayList();
			try{
				checkValueList = checkValueDao.findCheckValue(nodeId, type, subtype,"ping");
			}catch(Exception e){
				
			}finally{
				checkValueDao.close();
			}
			if(checkValueList != null && checkValueList.size()>0){
				CheckValue checkValue = checkValueList.get(0);
				pingnow = checkValue.getThevalue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pingnow;
	}
}
