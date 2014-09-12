package com.afunms.topology.manage;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.topology.util.IPAllotUtil;

public class IpallotManager extends BaseManager implements ManagerInterface {

	public String list() {
		IpAliasDao dao = new IpAliasDao();
		IPAllotUtil ipa = new IPAllotUtil();
		Map<String, List<String>> map = ipa.sort();
		Object str = null;
		String[] key = new String[map.size()];
		int i = 0;
		Iterator iter = map.keySet().iterator();
		while (iter.hasNext()) {
			str = iter.next();
			key[i] = str.toString().substring(0, str.toString().length());
			i++;
		}
		key = ipa.ipsort(key); 		// ≈≈–Ú

		request.setAttribute("key", key);
		setTarget("/topology/ipallot/list.jsp");

		return list(dao);
	}

	private String read() {
		String targetJsp = "/topology/ipallot/read.jsp";
		Object key = getParaValue("key");

		IPAllotUtil ipa = new IPAllotUtil();
		List<String> _value = null;
		
		
		Map<String, List<String>> map = ipa.sort();
		Iterator iter = map.keySet().iterator();
		_value = map.get(key);
		
		String[] value = new String[_value.size()];
		for(int i = 0;i<_value.size();i++){
			value[i] = _value.get(i) ;
		}
		
		value = ipa.ipsort(value); 		
	 
		request.setAttribute("value", value);
		request.setAttribute("key", key);

		return targetJsp;
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();

		if (action.equals("read"))
			return read();

		if (action.equals("downloadnetworklistfuck"))
			return downloadnetworklistfuck();

		if (action.equals("reportlist")) {
			return reportList();
		}

		if (action.equals("readword"))
			return readWord();

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;

	}

	private String downloadnetworklistfuck() {
		Hashtable reporthash = new Hashtable();
		Object key = getParaValue("key");

		IPAllotUtil ipa = new IPAllotUtil();
		List<String> _value = null;
		
		Map<String, List<String>> map = ipa.sort();
		Iterator iter = map.keySet().iterator();
		_value = map.get(key);
		
		String[] value = new String[_value.size()];
		for(int i = 0;i<_value.size();i++){
			value[i] = _value.get(i) ;
		}
		
		value = ipa.ipsort(value); 
		
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),reporthash);

		report.createReport_ipallotlist("/temp/ipallotlist_report.xls", value,key);

		request.setAttribute("filename", report.getFileName());
		return "/topology/ipallot/downloadreport.jsp";
	}

	private String reportList() {
		Object key = getParaValue("key");

		IPAllotUtil ipa = new IPAllotUtil();
		List<String> _value = null;
		
		Map<String, List<String>> map = ipa.sort();
		Iterator iter = map.keySet().iterator();
		_value = map.get(key);
		
		String[] value = new String[_value.size()];
		for(int i = 0;i<_value.size();i++){
			value[i] = _value.get(i) ;
		}
		
		value = ipa.ipsort(value); 
		
		request.setAttribute("value", value);
		request.setAttribute("key", key);

		return "/topology/ipallot/reportlist.jsp";

	}

	private String readWord() {

		Object key = getParaValue("key");

		IPAllotUtil ipa = new IPAllotUtil();
		List<String> _value = null;
		
		Map<String, List<String>> map = ipa.sort();
		Iterator iter = map.keySet().iterator();
		_value = map.get(key);
		
		String[] value = new String[_value.size()];
		for(int i = 0;i<_value.size();i++){
			value[i] = _value.get(i) ;
		}
		
		value = ipa.ipsort(value); 
		
		request.setAttribute("value", value);
		request.setAttribute("key", key);
		return "/topology/ipallot/readWord.jsp";
	}
}
