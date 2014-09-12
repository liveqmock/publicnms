package com.afunms.sysset.manage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.sysset.dao.DBBackupAutoDao;
import com.afunms.sysset.dao.DBBackupDao;
import com.afunms.sysset.model.DBBackup;
import com.afunms.sysset.model.DBBackupAuto;

public class DBBackupManager extends BaseManager implements ManagerInterface
{
	
	public String execute(String action) {
		// 数据库 表 列表
		if ("list".equals(action)) 
		{	
			request.setAttribute("result", "false");
			return getTableList();			
		}
		// 备份文件列表
		if ("dbbackuplist".equals(action)) 
		{	
			request.setAttribute("result", "false");
			return getDBBackupList();
		}
		// 调用备份
		if("backup".equals(action))  
		{
			return backup();
		}
		// 调用导入备份
		if("load".equals(action))
		{
			return load();
		}
		// 删除备份
		if("delete".equals(action)){
			//return deleteDBBackupFile();
		}
		//自动备份列表
		if("autobackuplist".equals(action)){
			return autobackuplist();
		}
		if("addbackup".equals(action)){
			return addbackup();
		}
		if("autobackup".equals(action)){
			return autobackup();
		}
		if("downloadDBFile".equals(action)){
			return downloadDBFile();
		}
		if("deleteDBFile".equals(action)){
			return deleteDBFile();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
		
	}
	
	/**
	 * 删除
	 * @return
	 */
	private String deleteDBFile(){
    	boolean result = false;
		String[] id = getParaArrayValue("checkbox");
		List<String> list = new ArrayList<String>();
		DBBackupAutoDao dao = new DBBackupAutoDao();
		try{
			for(int i = 0 ; i< id.length;i++ ){
				DBBackupAuto dbBackup = (DBBackupAuto)dao.findByID(id[i]);
				list.add(dbBackup.getFilename());
			}
			result = dao.delete(id);
			result = true;
		}catch(Exception e){
			result = false;
			e.printStackTrace();
		}finally{
			dao.close();
		}
		for(int i = 0 ; i< list.size();i++ ){
			new File(dao.getFilepath() + list.get(i)).delete();
		}
		if(result){
			request.setAttribute("result", "true");
			request.setAttribute("msg", "数据库备份文件删除成功！");
			return autobackuplist();
		}else{
			setErrorCode(-1);
	    	request.setAttribute(SessionConstant.ERROR_INFO, "数据库备份文件删除失败！");
	    	return null;
		}
	}
	
	/**
	 * 添加备份
	 * @return
	 */
	private String addbackup(){
		return "/sysset/dbbackup/addbackup.jsp";
	}
	
	/**
	 * 下载备份的数据库文件
	 * @return
	 */
	private String downloadDBFile(){
		String id = request.getParameter("id");
		DBBackupAuto dbBackupAuto = null;
		DBBackupAutoDao dao = new DBBackupAutoDao();
		try {
			dbBackupAuto = (DBBackupAuto)dao.findByID(id);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		String filename = dao.getFilepath()+File.separator+dbBackupAuto.getFilename();
		request.setAttribute("filename", filename);
		return "/sysset/dbbackup/download.jsp";
	}
	
	/**
	 * 执行备份数据库功能  
	 * @return
	 */
	private String autobackup(){
		//
		String time = getParaValue("time");
		String filename = getParaValue("filename");
		String description = getParaValue("description");
		//检查是否有相同名字的文件
		DBBackupAutoDao dao = new DBBackupAutoDao();
		List<DBBackupAuto> dbBackupAutoList = null;
		try {
			dbBackupAutoList = dao.findByCondition(" where filename='"+filename.trim()+".sql'");
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		if(dbBackupAutoList.size() > 0){
			//该文件已存在
			setErrorCode(ErrorMessage.FILENAME_EXIST);
			return null;
		}
		
		//执行备份的逻辑
		//取出所表名称集合
		dao = new DBBackupAutoDao();
		List<String> list = null;
		try {
			list = dao.findByCriteria("show tables");
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		backupTables(filename,time, list);
		System.out.println(filename+" "+description);
		return autobackuplist();
	}
	
	/**
	 * @param filename       数据库文件名
	 * @param time           备份的时间
	 * @param tablenameList  表名称
	 */
	private String backupTables(String filename,String time, List<String> tablenameList){
		String[] tables = new String[tablenameList.size()];
		for(int i=0; i<tablenameList.size(); i++){
			tables[i] = tablenameList.get(i);
		}
		HashMap<String, String[]> tableMap = getConfigTables(tablenameList);
		//取出需要备份表数据的配置表
		String[] configTables = tableMap.get("configTables");
		String[] tempTables = tableMap.get("tempTables");
		boolean result = false;
		//插入配置表的表数据和表结构
		DBBackupAutoDao dao = new DBBackupAutoDao();
		try {
			result = dao.backup(configTables, 2, filename);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		//插入临时表的表结构
		dao = new DBBackupAutoDao();
		try {
			result = dao.backup(tempTables, 0, filename);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		if(result){
			DBBackupAuto dbBackupAuto = new DBBackupAuto();
			dbBackupAuto.setFilename(filename+".sql");
			dbBackupAuto.setTime(time);
			try{
				dao = new DBBackupAutoDao();
				result = dao.save(dbBackupAuto);
			}catch(Exception e){
				new File(dao.getFilepath() + filename+".sql").delete();
			}finally{
				dao.close();
			}
		}
		if(result){
			request.setAttribute("result", "true");
			request.setAttribute("msg", "数据库备份成功！");
			return autobackuplist();
		} else {
			setErrorCode(-1);
	    	request.setAttribute(SessionConstant.ERROR_INFO, "数据库备份发生错误，请检查日志文件！");
	    	return null;
		}
	}
	
	/**
	 * 根据所有的表，过滤出需要备份的配置表
	 * @param tablenameList
	 * @return
	 */
	private HashMap<String, String[]> getConfigTables(List<String> tablenameList){
		List<String> configTablesList = new ArrayList<String>();
		Pattern pattern = Pattern.compile("\\d+_\\d");//两数字以下划线相连的表 为历史表，不保存其表数据
		Pattern dataTempPattern = Pattern.compile("nms_(.*)_data_temp");//类似nms_cpu_data_temp的临时表，不保存其表数据
		Vector<String> nmsTableVector = new Vector<String>();//一部分不需要备份数据的表名称组成的集合
		nmsTableVector.add("nms_alarminfo");
		nmsTableVector.add("nms_checkevent");
		nmsTableVector.add("nms_errptlog");
		nmsTableVector.add("node_indicator_alarm");
		nmsTableVector.add("storageping");
		nmsTableVector.add("system_eventlist");
		List<String> nmsTempTables = new ArrayList<String>();
		
		for(int i=0; i<tablenameList.size(); i++){
			//是否是配置表的标志位
			boolean isConfigTableFlag = true;
			String tableName = tablenameList.get(i);
			Matcher matcher = pattern.matcher(tableName);
			if(matcher.find()){
				isConfigTableFlag = false;
			}
			Matcher dataTempMatcher = dataTempPattern.matcher(tableName);
			if(dataTempMatcher.find()){
				isConfigTableFlag = false;
			}
			if(tableName.startsWith("nms_apache_(.*)")){
				isConfigTableFlag = false;
			}
			if(tableName.matches("nms_as400_(.*)")){
				isConfigTableFlag = false;
			}
			if(tableName.matches("nms_db2(.*)")){
				isConfigTableFlag = false;
			}
			if(tableName.matches("nms_domino(.*)_realtime")){
				isConfigTableFlag = false;
			}
			if(tableName.matches("nms_informix(.*)")){
				isConfigTableFlag = false;
			}
			if(tableName.matches("nms_ora(.*)")){
				isConfigTableFlag = false;
			}
			if(tableName.matches("nms_sqlserver(.*)")){
				isConfigTableFlag = false;
			}
			if(tableName.matches("nms_sybase(.*)")){
				isConfigTableFlag = false;
			}
			if(tableName.matches("nms_weblogic_(.*)")){
				isConfigTableFlag = false;
			}
			if(nmsTableVector.indexOf(tableName) != -1){
				isConfigTableFlag = false;
			}
			if(isConfigTableFlag){
				configTablesList.add(tableName);
			}else{
				nmsTempTables.add(tableName);
			}
		}
		String[] configTables = new String[configTablesList.size()];
		for(int i=0; i<configTablesList.size(); i++){
			configTables[i] = configTablesList.get(i);
		}
		String[] tempTables = new String[nmsTempTables.size()];
		for(int i=0; i<nmsTempTables.size(); i++){
			tempTables[i] = nmsTempTables.get(i);
		}
		HashMap<String, String[]> tableMap = new HashMap<String, String[]>();
		tableMap.put("tempTables", tempTables);
		tableMap.put("configTables", configTables);
		return tableMap;
	}
	
	public static void main(String[] args){
//		String tableName = "allutilhdxday20_10_1_2";
//		Pattern pattern = Pattern.compile("\\d+_\\d");
//		 Matcher matcher = pattern.matcher(tableName);
//		 System.out.println(matcher.find());
//		 while(matcher.find()){
//			 System.out.println(matcher.group(0));
//		 }
		
		String tableName = "nms_db2lock";
		System.out.println(tableName.matches("nms_db2(.*)"));
	}
	
	
	/**
	 * 跳转到自动备份列表
	 * @return
	 */
	private String autobackuplist(){
		DBBackupAutoDao dBBackupAutoDao = new DBBackupAutoDao();
		List backList = null;
		try{
			backList = dBBackupAutoDao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dBBackupAutoDao.close();
		}
		request.setAttribute("backList", backList);
		return "/sysset/dbbackup/autobackuplist.jsp";
	}
	
	//获取数据库中所有的表名
	private String getTableList()
	{
		DBBackupDao dao = new DBBackupDao();
		List<String> list = dao.findByCriteria("show tables");
		request.setAttribute("tablesname", list);
		return "/sysset/dbbackup/list.jsp";
	}
	
	/** 
	 * 获取 所有表的列表
	 * @return
	 */
	private String getDBBackupList(){
		DBBackupDao dao = new DBBackupDao();
		setTarget("/sysset/dbbackup/dbbackuplist.jsp");
		return list(dao);
	}
	
	/**
	 * 删除备份文件
	 * @return
	 */
	private String deleteDBBackupFile(){
		boolean result = false;
		String[] id = request.getParameterValues("id");
		List<String> list = new ArrayList<String>();
		DBBackupDao dao = new DBBackupDao();
		try{
			for(int i = 0 ; i< id.length;i++ ){
				DBBackup dbBackup = (DBBackup)dao.findByID(id[i]);
				list.add(dbBackup.getFilename());
			}
			result = dao.delete(id);
			result = true;
		}catch(Exception e){
			result = false;
			e.printStackTrace();
		}finally{
			dao.close();
		}
		for(int i = 0 ; i< list.size();i++ ){
			new File(dao.getFilepath() + list.get(i)).delete();
		}
		if(result){
			request.setAttribute("result", "true");
			request.setAttribute("msg", "数据库备份文件删除成功！");
			return getDBBackupList();
		}else{
			setErrorCode(-1);
	    	request.setAttribute(SessionConstant.ERROR_INFO, "数据库备份文件删除失败！");
	    	return null;
		}
	}
	
	/**
	 * 备份  如果备份成功后 则在数据库中添加记录
	 * 如果添加记录失败 则删除备份文件
	 * @return
	 */
	private String backup(){
		String[] tables = getParaArrayValue("checkbox");
		int radio = getParaIntValue("radio");
		DBBackupDao dao = new DBBackupDao();
		boolean result = dao.backup(tables,radio);
		if(result)
		{
			DBBackup dbBackup = new DBBackup();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
			String time = sdf.format(new Date());
			String filename = dao.getFilename();
			dbBackup.setFilename(filename+".sql");
			dbBackup.setTime(time);
			try{
				dao = new DBBackupDao();
				result = dao.save(dbBackup);
			}catch(Exception e){
				new File(dao.getFilepath() + filename+".sql").delete();
			}finally{
				dao.close();
			}
		}
		if(result)
		{
			request.setAttribute("result", "true");
			request.setAttribute("msg", "数据库备份成功！");
			return getDBBackupList();
		}
		else
		{
			setErrorCode(-1);
	    	request.setAttribute(SessionConstant.ERROR_INFO, "数据库备份发生错误，请检查日志文件！");
	    	return null;
		}
		
	}
	/**
	 * 导入备份文件
	 * @return
	 */
	private String load(){
		DBBackupDao dao = new DBBackupDao();
		String filename = request.getParameter("filename");
		boolean result = dao.load(dao.getFilepath()+filename);
		if(result)
		{
			request.setAttribute("msg", "数据库导入成功！");
			request.setAttribute("result", "true");
			return getDBBackupList();
		}
		else
		{
			setErrorCode(-1);
	    	request.setAttribute(SessionConstant.ERROR_INFO, "数据库导入发生错误，请检查日志文件！");
	    	return null;
		}
	}
	
	
	
	

}
