package com.afunms.config.manage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import jxl.write.WriteException;

import com.afunms.application.dao.ClusterDao;
import com.afunms.application.dao.UpAndDownMachineDao;
import com.afunms.application.util.CompReportHelper;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.JspPage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.GeneratorKey;
import com.afunms.common.util.SessionConstant;
import com.afunms.config.dao.CheckResultDao;
import com.afunms.config.dao.CompCheckRuleDao;
import com.afunms.config.dao.CompGroupRuleDao;
import com.afunms.config.dao.CompRuleDao;
import com.afunms.config.dao.CompStrategyDao;
import com.afunms.config.dao.DetailCompRuleDao;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.StrategyIpDao;
import com.afunms.config.model.CheckResult;
import com.afunms.config.model.CompGroupRule;
import com.afunms.config.model.CompRule;
import com.afunms.config.model.CompStrategy;
import com.afunms.config.model.DetailCompRule;
import com.afunms.config.model.StrategyIp;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.telnet.DevicePolicyEngine;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.informix.msg.sql1_en_US;
import com.lowagie.text.DocumentException;

public class NetWorkConfigRuleManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		if(action.equals("ruleDetailList")){
			return ruleDetailList();
		}
		if(action.equals("createRule")){
			return createRule();
		}
		if(action.equals("deleteRule")){
			return deleteRule();
		}
		if(action.equals("save")){
			return save();
		}
		if(action.equals("edit")){//保存已修改的内容
			return edit();
		}
		if(action.equals("editRule")){//进入编辑界面
			return editRule();
		}
		////////////////规则组////////////////////
		if(action.equals("groupRuleList")){//规则组列表界面
			return groupRuleList();
		}
		
		if(action.equals("createGroupRule")){//创建规则组界面
			return createGroupRule();
		}
		if(action.equals("saveGroupRule")){//保存规则组列表界面
			return saveGroupRule();
		}
		if(action.equals("deleteGroupRule")){//删除规则组列表界面
			return deleteGroupRule();
		}
		if(action.equals("editGroupRule")){//编辑规则组列表界面
			return editGroupRule();
		}
		if(action.equals("updateGroupRule")){//修改规则组列表界面
			return updateGroupRule();
		}
		//策略
		if(action.equals("strategyList")){//策略列表界面
			return strategyList();
		}
		
		if(action.equals("createStrategy")){//创建策略列表界面
			return createStrategy();
		}
		if(action.equals("saveStrategy")){//保存策略列表界面
			return saveStrategy();
		}
		if(action.equals("pureRuleList")){//显示具体某个组的详细规则
			return pureRuleList();
		}
		if(action.equals("editStrategy")){//修改策略
			return editStrategy();
		}
		if(action.equals("updateStrategy")){//保存修改策略
			return updateStrategy();
		}
		if(action.equals("deleteStrategy")){//删除策略
			return deleteStrategy();
		}
		
		if(action.equals("ready_addip")){//显示保存ip界面
			return ready_addip();
		}
		if(action.equals("showDetailStrategy")){//保存ip
			return showDetailStrategy();
		}
		if(action.equals("showAllDevice")){//显示所有设备
			return showAllDevice();
		}
		if(action.equals("saveip")){//保存ip
			return saveip();
		}
		if(action.equals("exeRule")){//运行规则
			return exeRule();
		}
		if(action.equals("viewDetail")){//运行规则
			return viewDetail();
		}
		if(action.equals("showRule")){//运行规则
			return showRule();
		}
		if(action.equals("allDeviceReport")){//报表
			return allDeviceReport();
		}
		if(action.equals("downloadReport")){//报表
			return downloadReport();
		}
		
		return null;
	}
public String  ruleDetailList() {
	CompRuleDao dao=new CompRuleDao();
	List list=dao.loadAll();
	request.setAttribute("list", list);
	return "/automation/compliance/ruleDetaillist.jsp";
}
public String  createRule() {
	
	return "/automation/compliance/createRule.jsp";
}
public String  editRule() {
	CompRule rule=new CompRule();
	List detailRuleList=new ArrayList();
	String id=getParaValue("id");
	CompRuleDao dao=new CompRuleDao();
	DetailCompRuleDao detailDao=new DetailCompRuleDao();
	try {
		rule=(CompRule) dao.findByID(id);
		 detailRuleList=detailDao.findByCondition(" where RULEID="+id);
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		dao.close();
		detailDao.close();
	}
	request.setAttribute("vo", rule);
	request.setAttribute("detailRuleList", detailRuleList);
	return "/automation/compliance/editRule.jsp";
}

public String  deleteRule() {
	String[] ids = getParaArrayValue("checkbox");
	if (ids != null && ids.length > 0) {
		CompRuleDao dao = new CompRuleDao();
		DetailCompRuleDao detailCompRuleDao=new DetailCompRuleDao();
		try {
			dao.delete(ids);
			detailCompRuleDao.delete(ids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			detailCompRuleDao.close();
		}

	 }
	return ruleDetailList();
}
public String  save() {
	
	CompRule rule=loadRule();
	CompRuleDao dao=new CompRuleDao();
	boolean isSucess=dao.save(rule);
	//保存详细的规则
	if (isSucess) {
		saveDetailRule(rule.getSelect_type(),-1);
	}
	return ruleDetailList();
}
//修改并保存规则
public String  edit() {
	boolean isSucess=false;
	CompRule vo=loadRule();
	int id=getParaIntValue("id");
	vo.setId(id);
	CompRuleDao dao=new CompRuleDao();
	isSucess=dao.update(vo);
	
	DetailCompRuleDao detailDao=new DetailCompRuleDao();
	 
	if (isSucess) {
		
	isSucess=detailDao.deleteDetailRule(vo);
	saveDetailRule(vo.getSelect_type(),id);
	}
	return ruleDetailList();
}
public void saveDetailRule(int standard,int id){

	int key=0;
	if (id==-1) {
		key=GeneratorKey.getInstance().getKey();	
	}else {
		key=id;
	}
	
	
	DetailCompRuleDao detailDao=new DetailCompRuleDao();
	String express="";
	
	//int standard=rule.getSelect_type();//包括简单（0），高级（1）及自定义（2）
	int relation=-1;//-1示意为无
	int isContain=0;
	
	String beginBlock="";
	String endBlock="";
	String extraBlock="";
	int isExtraContain=-1;//表示无
	DetailCompRule vo=new DetailCompRule();
	if(standard==0){
		
		express=getParaValue("content");
		//express=express.replaceAll("\r\n", ";;;;");
		isContain=getParaIntValue("simple_config");
	}else if (standard==1) {
		express=getParaValue("advance_value");
		isContain=getParaIntValue("advance_config");
		
	}else if (standard==2) {
		express=getParaValue("custom_value");
		isContain=getParaIntValue("custom_config");
	}

	
	if (standard==2) {
		 beginBlock=getParaValue("begin");
		 endBlock=getParaValue("end");
		 isExtraContain=getParaIntValue("isExtraContain");
		 extraBlock=getParaValue("extra");
		vo.setBeginBlock(beginBlock);
		vo.setEndBlock(endBlock);
		
		vo.setExtraBlock(extraBlock);
	}
	vo.setRuleId(key);
	vo.setRelation(relation);
	vo.setIsContain(isContain);
	vo.setExpression(express);
	vo.setIsExtraContain(isExtraContain);
	detailDao.save(vo);
	//////////////////////////////////////
	
	
	if (standard==1||standard==2) {
	DBManager db=new DBManager();
	String[] selVals=null;
	String[] textVals=null;
	String selVal=getParaValue("selVal");
	String textVal=getParaValue("textVal");
		if(selVal!=null&&textVal!=null){
	 selVals=new String[selVal.split(",").length];
	 textVals=new String[textVal.split(",").length];
		selVals=selVal.split(",");
		textVals=textVal.split(",");
		if(selVals.length==textVals.length*2){
			
		try {
		for (int i = 0; i < selVals.length; i++) {
			
			if(i%2==1){
				vo.setIsContain(Integer.parseInt(selVals[i]));
				vo.setExpression(textVals[i/2]);
				String sql=getSql(vo);
				db.addBatch(sql);
			}else {
				vo=new DetailCompRule();
				if (standard==2) {
					vo.setBeginBlock(beginBlock);
					vo.setEndBlock(endBlock);
					vo.setIsExtraContain(isExtraContain);
					vo.setExtraBlock(extraBlock);
				}
				vo.setRuleId(key);
				vo.setRelation(Integer.parseInt(selVals[i]));
			}
		
		 }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.executeBatch();
		}
		
		}	
	}

	
	}
//return true;
}
public String getSql(DetailCompRule rule){
	StringBuffer sql=new StringBuffer();
	sql.append("insert into nms_comp_detail_rule(RULEID,RELATION,ISCONTAIN,EXPRESSION,BEGINBLOCK,ENDBLOCK,ISEXTRACONTAIN,EXTRABLOCK)values(");
	sql.append(rule.getRuleId());
	sql.append(",");
	sql.append(rule.getRelation());
	sql.append(",");
	sql.append(rule.getIsContain());
	sql.append(",'");
	sql.append(rule.getExpression());
	sql.append("','");
	sql.append(rule.getBeginBlock());
	sql.append("','");
	sql.append(rule.getEndBlock());
	sql.append("',");
	sql.append(rule.getIsExtraContain());
	sql.append(",'");
	sql.append(rule.getExtraBlock());
	sql.append("')");
	return sql.toString();
}

public CompRule loadRule(){

	
	String rule_name=getParaValue("rule_name");
	String des=getParaValue("des");
	int standard=getParaIntValue(("standard"));
//	String content=getParaValue("content");
	
	int level=getParaIntValue("level");
	String add_des=getParaValue("add_des");
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date date = new Date();
	User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	CompRule rule=new CompRule();
	rule.setComprule_name(rule_name);
	rule.setDescription(des);
	rule.setSelect_type(standard);
	//rule.setRule_content(content);
	rule.setCreate_time(sdf.format(date));
	rule.setLast_modified_time(sdf.format(date));
	rule.setCreated_by(user.getName());
	rule.setLast_modified_by(user.getName());//在修改时不变
	rule.setViolation_severity(level);
	rule.setRemediation_descr(add_des);
	return rule;
}
///////////////////////规则组//////////////////////////////
public String  groupRuleList() {
	CompGroupRuleDao dao=new CompGroupRuleDao();
	List list=dao.loadAll();
	request.setAttribute("list", list);
	return "/automation/compliance/groupRuleList.jsp";
}
//创建工作组
public String createGroupRule(){
	CompRuleDao dao=new CompRuleDao();
	List list=dao.loadAll();
	
	request.setAttribute("list", list);
	return "/automation/compliance/createGroupRule.jsp";
}
//保存工作组
public String saveGroupRule(){
	String name=getParaValue("name");
	String desciption=getParaValue("description");
	String[] id=getParaArrayValue("checkbox");
	String ids="";
	if (ids!=null) {
		for (int i = 0; i < id.length; i++) {
			ids=ids+id[i]+",";
		}
	}
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date date = new Date();
	User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	CompGroupRule vo=new CompGroupRule();
	vo.setName(name);
	
	vo.setDescription(desciption);
	vo.setRuleId(ids);
	vo.setCreatedBy(user.getName());
	vo.setCreatedTime(sdf.format(date));
	vo.setLastModifiedBy(user.getName());
	vo.setLastModifiedTime(sdf.format(date));
	CompGroupRuleDao dao=new CompGroupRuleDao();
	boolean isSucess=dao.save(vo);
	return null;
}
public String deleteGroupRule(){
	String[] ids = getParaArrayValue("checkbox");
	if (ids != null && ids.length > 0) {
		CompGroupRuleDao dao = new CompGroupRuleDao();
		try {
			dao.delete(ids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

	 }
return groupRuleList();	
}
//编辑规则组信息
public String editGroupRule(){
	String id=getParaValue("id");
	CompRuleDao dao=new CompRuleDao();
	List list=dao.loadAll();
	CompGroupRuleDao groupRuleDao=new CompGroupRuleDao();
	
	CompGroupRule rule=(CompGroupRule) groupRuleDao.findByID(id);
	groupRuleDao.close();
	request.setAttribute("list", list);
	request.setAttribute("compGroupRule", rule);
	return "/automation/compliance/editGroupRule.jsp";
}
//修改工作组
public String updateGroupRule(){
    int id=getParaIntValue("id");
	String name=getParaValue("name");
	String desciption=getParaValue("description");
	String[] temp=getParaArrayValue("checkbox");
	
	StringBuffer ids=new StringBuffer();
	if (temp!=null) {
		for (int i = 0; i < temp.length; i++) {
			ids.append(temp[i]+",");
		}
	}
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date date = new Date();
	User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	CompGroupRule vo=new CompGroupRule();
	vo.setId(id);
	vo.setName(name);
	
	vo.setDescription(desciption);
	vo.setRuleId(ids.toString());
	//vo.setCreatedBy(user.getName());
	//vo.setCreatedTime(sdf.format(date));
	vo.setLastModifiedBy(user.getName());
	vo.setLastModifiedTime(sdf.format(date));
	CompGroupRuleDao dao=new CompGroupRuleDao();
	boolean isSucess=dao.update(vo);
	return null;
	
}
//////////////////////////策略////////////////////////////
public String strategyList(){
	CompStrategyDao dao=new CompStrategyDao();
	List list=dao.loadAll();
	
	request.setAttribute("list", list);
	
	return "/automation/compliance/strategyList.jsp";
}
public String createStrategy(){
	CompGroupRuleDao dao=new CompGroupRuleDao();
	List list=dao.loadAll();
	request.setAttribute("list", list);
	return "/automation/compliance/createStrategy.jsp";
}

//显示具体某个组的详细规则
public String pureRuleList(){
	String id=getParaValue("id");
	CompGroupRuleDao groupRuleDao=new CompGroupRuleDao();
	CompGroupRule rule=(CompGroupRule) groupRuleDao.findByID(id);
	groupRuleDao.close();
	CompRuleDao dao=new CompRuleDao();
	String temp=rule.getRuleId();
	String ids=temp.substring(0, temp.lastIndexOf(","));
	List list=dao.findByCondition(" where ID in("+ids+")");
	
	request.setAttribute("list", list);
	return "/automation/compliance/pureRuleList.jsp";
}
//保存策略
public String saveStrategy(){
	String name=getParaValue("name");
	String description=getParaValue("description");
	int type=getParaIntValue("type");
	int violateType=getParaIntValue("violateType");
	String[] temp=getParaArrayValue("checkbox");
	StringBuffer ids=new StringBuffer();
	if (temp!=null) {
		for (int i = 0; i < temp.length; i++) {
			ids.append(temp[i]+",");
		}	
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date date = new Date();
	User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	CompStrategy vo=new CompStrategy();
	vo.setName(name);
	vo.setDescription(description);
	vo.setType(type);
	vo.setViolateType(violateType);
	vo.setGroupId(ids.toString());
	vo.setCreateBy(user.getName());
	vo.setCreateTime(sdf.format(date));
	vo.setLastModifiedBy(user.getName());
	vo.setLastModifiedTime(sdf.format(date));
	CompStrategyDao dao=new CompStrategyDao();
	boolean isSuccess=dao.save(vo);
	
	return null;
}
//修改策略
public String editStrategy(){
	String id=getParaValue("id");
	CompGroupRuleDao dao=new CompGroupRuleDao();
	List list=dao.loadAll();
	 CompStrategyDao strategyDao=new CompStrategyDao();
	CompStrategy vo=(CompStrategy) strategyDao.findByID(id);
	request.setAttribute("list", list);
	request.setAttribute("vo", vo);
	return "/automation/compliance/editStrategy.jsp";	
}
//保存修改策略
public String updateStrategy(){
	int id=getParaIntValue("id");
	String name=getParaValue("name");
	String description=getParaValue("description");
	int type=getParaIntValue("type");
	int violateType=getParaIntValue("violateType");
	String[] temp=getParaArrayValue("checkbox");
	StringBuffer ids=new StringBuffer();
	if (temp!=null) {
		for (int i = 0; i < temp.length; i++) {
			ids.append(temp[i]+",");
		}	
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date date = new Date();
	User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	CompStrategy vo=new CompStrategy();
	vo.setId(id);
	vo.setName(name);
	vo.setDescription(description);
	vo.setType(type);
	vo.setViolateType(violateType);
	vo.setGroupId(ids.toString());
	vo.setLastModifiedBy(user.getName());
	vo.setLastModifiedTime(sdf.format(date));
	CompStrategyDao dao=new CompStrategyDao();
	boolean isSuccess=dao.update(vo);	
	return null;
}
//删除策略
public String deleteStrategy(){
	String[] ids=getParaArrayValue("checkbox");
	if (ids != null && ids.length > 0) {
		CompStrategyDao dao = new CompStrategyDao();
		
		try {
			dao.delete(ids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

	 }
	return strategyList();
}

public String ready_addip() {
	String id=getParaValue("id");
	HaweitelnetconfDao haweitelnetconfDao=null;
	List iplist=null;
	try {
		 haweitelnetconfDao = new HaweitelnetconfDao();
		 iplist=haweitelnetconfDao.getAllTelnetConfig();
	} catch (Exception e) {
		haweitelnetconfDao.close();
	}
	
	int listsize=iplist.size();
	StrategyIpDao ipDao=new StrategyIpDao();
	List list=ipDao.findByCondition(" where STRATEGY_ID="+id);
	request.setAttribute("iplist",iplist);
	request.setAttribute("strategyList",list);
	request.setAttribute("id",id);
	HostNodeDao listdao=new HostNodeDao();
	setTarget("/automation/compliance/showip.jsp");
	String page=list(listdao);
	JspPage jp = (JspPage)request.getAttribute("page");
	jp.setTotalRecord(listsize);
	request.setAttribute("page", jp);
	return page;
}
public String showDetailStrategy() {
	String id=getParaValue("id");
	CheckResultDao dao=new CheckResultDao();
	List<CheckResult> list=dao.getResultById(id);
	request.setAttribute("list", list);
	request.setAttribute("id", id);
	StrategyIpDao ipDao=new StrategyIpDao();
	List ipList=ipDao.findByCondition(" where STRATEGY_ID="+id);
	
	request.setAttribute("ipList", ipList);
	return "/automation/compliance/detailStrategyList.jsp";
}
public String showAllDevice() {
	CheckResultDao dao=new CheckResultDao();
	List<CheckResult> list=dao.getAllResult();
	request.setAttribute("list", list);
	StrategyIpDao ipDao=new StrategyIpDao();
	List deviceList=ipDao.findByCondition(" where AVAILABILITY=0");
	request.setAttribute("deviceList", deviceList);
	return "/automation/compliance/showAllDevice.jsp";
}
public String saveip() {
	String[] ips=getParaArrayValue("checkbox");
	int id=getParaIntValue("id");
	List list=null;
	StrategyIpDao dao=new StrategyIpDao();
	  dao.saveBatch(ips,id);
	request.setAttribute("list", list);
	return null;
}
public String exeRule(){
	String id=getParaValue("id");
	DevicePolicyEngine engine=new DevicePolicyEngine();
	engine.executePolicey(id);
	CheckResultDao dao=new CheckResultDao();
	List<CheckResult> list=dao.getResultById(id);
	StrategyIpDao ipDao=new StrategyIpDao();
	List ipList=ipDao.findByCondition(" where STRATEGY_ID="+id);
	
	request.setAttribute("list", list);
	request.setAttribute("ipList", ipList);
	request.setAttribute("id", id);
	return "/automation/compliance/detailStrategyList.jsp";
}
public String viewDetail(){
	String id=getParaValue("id");
	String ip=getParaValue("ip");
	
	CheckResultDao dao=new CheckResultDao();
	List list=dao.getReslutByIdAndIp(id,ip);
	request.setAttribute("list", list);
	request.setAttribute("id", id);
	return "/automation/compliance/viewDetail.jsp";
}
public String showRule(){
	int strategyId=getParaIntValue("strategyId");
	int groupId=getParaIntValue("groupId");
	int ruleId=getParaIntValue("ruleId");
	String ip=getParaValue("ip");
	String isVor=getParaValue("isVor");
	CompCheckRuleDao dao=new CompCheckRuleDao();
	List list=dao.loadByCondition(strategyId,groupId,ruleId,ip);
	request.setAttribute("list", list);
	request.setAttribute("ruleId", ruleId+"");
	request.setAttribute("isVor", isVor);
	return "/automation/compliance/showRule.jsp";
}
public String allDeviceReport(){
	CompReportHelper helper=new CompReportHelper();
	Hashtable allData=helper.getAllDevice();
	request.setAttribute("allData", allData);
	return "/automation/compliance/allDeviceReport.jsp";
}
public String downloadReport(){
	String type=getParaValue("type");
	CompReportHelper helper=new CompReportHelper();
	Hashtable allData=helper.getAllDevice();
	Vector vector=null;
	List deviceList=new ArrayList();
	String file = "";// 保存到项目文件夹下的指定文件夹
	String filePath ="";// 获取系统文件夹路径
	if (allData!=null) {
		if (allData.containsKey("deviceVec")) {
			vector=(Vector)allData.get("deviceVec");
			helper.createPie(vector);
		}
		if (allData.containsKey("deviceList")) {
			deviceList=(List)allData.get("deviceList");
			if (type.equals("doc")) {
				file= "/temp/allDevice.doc";
				filePath= ResourceCenter.getInstance().getSysPath() + file;
				try {
					helper.createDoc(vector,deviceList,filePath);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (type.equals("xls")) {

				 file = "/temp/allDevice.xls";
				 filePath= ResourceCenter.getInstance().getSysPath() + file;
				
					try {
						helper.createExcel(vector,deviceList,filePath);
					} catch (WriteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}else if (type.equals("pdf")) {
				 file = "/temp/allDevice.pdf";
				 filePath= ResourceCenter.getInstance().getSysPath() + file;
				try {
					helper.createPdf(vector,deviceList,filePath);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	request.setAttribute("filename", filePath);
	return "/capreport/net/download.jsp";
}

}
