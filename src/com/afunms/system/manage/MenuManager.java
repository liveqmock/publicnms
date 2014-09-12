/**
 * <p>Description:MenuManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */
package com.afunms.system.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.system.dao.FunctionDao;
import com.afunms.system.model.Function;
import com.afunms.system.util.CreateRoleFunctionTable;
import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;

public class MenuManager extends BaseManager implements ManagerInterface
{  
	private List<Function> allFunction;
	
	
	public String execute(String action)
	{
		FunctionDao functionDao = null;
		try{
			functionDao = new FunctionDao();
			allFunction = (List<Function>)functionDao.loadAll();
		}catch(Exception e){
			SysLogger.error("MenuManager.getAllMenuList()",e); 
			e.printStackTrace();
	    }finally{
		   functionDao.close();
	    }
		if("list".equals(action)){
			  return getAllMenuList();
		}
		if("ready_add".equals(action)){
			  return add();
		}
		if("addUpdate".equals(action)){
			return addUpdate();
		}
		if("delete".equals(action)){
			return delete();
		}
		if("ready_edit".equals(action)){
			return edit();
		}
		if("edit".equals(action)){
			return editUpdate();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
   
    private String getAllMenuList(){
	    request.setAttribute("allFunction", allFunction);
    return "/system/menu/list.jsp";
    }
   
    private String add(){
    	request.setAttribute("allFunction", allFunction);
	    return "/system/menu/add.jsp";
    }
   
    private String addUpdate(){
    	String level_desc = request.getParameter("level_desc");
	    String ch_desc = request.getParameter("ch_desc");
	    String fatherNode = request.getParameter("fatherNode");
	    String url = request.getParameter("url");
	    String img_url = request.getParameter("img_url");
	    String isCurrentWindow = request.getParameter("isCurrentWindow");
	    String width = request.getParameter("width");
	    String height = request.getParameter("height");
	    String clientX = request.getParameter("clientX");
	    String clientY = request.getParameter("clientY");
	    int fatherId= 0;
	    List<Function> functionList = null;
	    Function functionFather = null;
	    CreateRoleFunctionTable crft = new CreateRoleFunctionTable();
	    if(!level_desc.equals("1")&&fatherNode!=null){
		    for(int i = 0 ; i<allFunction.size();i++){
			    if(allFunction.get(i).getId()==Integer.valueOf(fatherNode)){
			   	    fatherId = allFunction.get(i).getId();
				    functionFather = allFunction.get(i);
			    }
		    }
		    functionList = crft.getFunctionChild(functionFather, allFunction);
	    }else if(level_desc.equals("1")&&fatherNode==null){
		    fatherId = 0;
		    functionList = crft.getAllMenuRoot(allFunction);
	    }	   
	    String nextFunc_desc = getNextFunc_desc(functionList);
	    if(nextFunc_desc == null){
	    	if("1".equals(level_desc)){
	    		nextFunc_desc = "0A";
	    	}else if("2".equals(level_desc)){
	    		nextFunc_desc = functionFather.getFunc_desc()+"0A";
	    	}else if("3".equals(level_desc)){
	    		nextFunc_desc = functionFather.getFunc_desc()+"01";
	    	}	
	    }
	    Function addFunction = new Function();
	    addFunction.setId(0);
	    addFunction.setFunc_desc(nextFunc_desc);
	    addFunction.setLevel_desc(Integer.valueOf(level_desc));
	    addFunction.setCh_desc(ch_desc);
	    addFunction.setFather_node(fatherId);
	    addFunction.setUrl(url);
	    addFunction.setImg_url(img_url);
	    addFunction.setIsCurrentWindow(Integer.valueOf(isCurrentWindow));
	    addFunction.setWidth(width);
	    addFunction.setHeight(height);
	    addFunction.setClientX(clientX);
	    addFunction.setClientY(clientY);
	    boolean result = false;
	    FunctionDao functionDao = null; 
	    try{
		    functionDao = new FunctionDao();
		    result = functionDao.save(addFunction);   
	    }catch(Exception e){
		    result = false;
		    SysLogger.error("MenuManager.addUpdate()",e); 
		    e.printStackTrace();
	    }finally{
		    functionDao.close();
	    }
	    if(result){
		    return "/system/menu/saveok.jsp";
	    }else{
		    return "/system/menu/saveFail.jsp";
	    }
	 
   }
   
  
    private String delete(){
    	String level_desc = request.getParameter("level_desc");
	    String rootMenu = request.getParameter("rootMenuSelect");
	    String secondMenu = request.getParameter("secondMenuSelect");
	    System.out.println(secondMenu);
	    String thirdMenu = request.getParameter("thirdMenuSelect");
	    Function rootFunction = null;
	    Function secondFunction = null;
	    Function thirdFunction = null;
	    FunctionDao functionDao = null;
	    try{
		    functionDao = new FunctionDao();
	 	    rootFunction = (Function)functionDao.findByID(rootMenu);
		    secondFunction = (Function)functionDao.findByID(secondMenu);
		    thirdFunction =(Function)functionDao.findByID(thirdMenu);
	    }catch(Exception e){
		    SysLogger.error("MenuManager.delete()",e); 
		    e.printStackTrace();
	    }finally{
		    functionDao.close();
	    }
	    CreateRoleFunctionTable crft = new CreateRoleFunctionTable();
	    List<Function> deleteFunctionList = new ArrayList<Function>();;
	    if("1".equals(level_desc)){
		    deleteFunctionList = crft.getAllFuctionChildByRoot(rootFunction, allFunction);
		    deleteFunctionList.add(rootFunction);
	    }else if("2".equals(level_desc)){
		    deleteFunctionList = crft.getAllFuctionChildByRoot(secondFunction, allFunction);
		    deleteFunctionList.add(secondFunction);
	    }else if("3".equals(level_desc)){
		    deleteFunctionList.add(thirdFunction);
	    }
	    String[] id = new String[deleteFunctionList.size()];
	    for(int i = 0 ; i < deleteFunctionList.size() ; i++){
		    id[i] = String.valueOf(deleteFunctionList.get(i).getId());
	    }
	    boolean result = false;
	    try{
		    functionDao = new FunctionDao();
		    result = functionDao.deletelist(id);
	    }catch(Exception e){
		    result = false;
		    SysLogger.error("MenuManager.delete()",e); 
		    e.printStackTrace();
	    }finally{
		    functionDao.close();
	    }
	    if(result){
		    return "/system/menu/saveok.jsp";
	    }else{
		    return "/system/menu/saveFail.jsp";
	    }
	   
	  
    }
   
    private String edit(){
    	ready();
	    return "/system/menu/edit.jsp";
    }
   
    private String editUpdate(){
    	FunctionDao addFunctionDao =null;
 	    FunctionDao functionDao = null;
 	    boolean result = false;
 	    try{
		    String id = request.getParameter("id");
		    String level_desc = request.getParameter("level_desc");
		    String ch_desc = request.getParameter("ch_desc");
		    String fatherNode = request.getParameter("fatherNode");
		    String url = request.getParameter("url");
		    String img_url = request.getParameter("img_url");
		    String isCurrentWindow = request.getParameter("isCurrentWindow");
		    String width = request.getParameter("width");
		    String height = request.getParameter("height");
		    String clientX = request.getParameter("clientX");
		    String clientY = request.getParameter("clientY");
	    	functionDao = new FunctionDao();
		    Function function = (Function)functionDao.findByID(id);
		    CreateRoleFunctionTable crft = new CreateRoleFunctionTable();
		    List<Function> allFunctionChildList = crft.getAllFuctionChildByRoot(function, allFunction);
		    if(Integer.valueOf(level_desc)!=function.getLevel_desc()){
		    	String[] idArray = new String[allFunctionChildList.size()+1];
		    	idArray[allFunctionChildList.size()] = id;
		    	functionDao.delete(idArray);
		    }
		    if(level_desc.equals("1")){
			   fatherNode = "0";
		    }
		    Function functionFather = null;
		    allFunction = functionDao.loadAll();
		    List<Function> functionList = new ArrayList<Function>();
		    for(int i = 0 ; i<allFunction.size(); i++){
		    	if(allFunction.get(i).getId()==Integer.valueOf(fatherNode)){
		    		functionFather = allFunction.get(i);
		    	}
		    	if(allFunction.get(i).getFather_node()==Integer.valueOf(fatherNode)){
		    		functionList.add(allFunction.get(i));
		    	}
		    }
		    String nextFunc_desc = getNextFunc_desc(functionList);
		    if(nextFunc_desc == null){
		    	if("1".equals(level_desc)){
		    		nextFunc_desc = "0A";
		    	}else if("2".equals(level_desc)){
		    		nextFunc_desc = functionFather.getFunc_desc()+"0A";
		    	}else if("3".equals(level_desc)){
		    		nextFunc_desc = functionFather.getFunc_desc()+"01";
		    	}	
		    }
		    Function editFunction = new Function();
		    editFunction.setId(Integer.valueOf(id));
		    editFunction.setFunc_desc(nextFunc_desc);
		    editFunction.setLevel_desc(Integer.valueOf(level_desc));
		    editFunction.setCh_desc(ch_desc);
		    editFunction.setFather_node(Integer.valueOf(fatherNode));
		    editFunction.setUrl(url);
		    editFunction.setImg_url(img_url);
		    editFunction.setIsCurrentWindow(Integer.valueOf(isCurrentWindow));
		    editFunction.setWidth(width);
		    editFunction.setHeight(height);
		    editFunction.setClientX(clientX);
		    editFunction.setClientY(clientY);
		    
		    addFunctionDao= new FunctionDao();
		    addFunctionDao.update(editFunction);
		    result = true;
	    }catch(Exception e){
	    	result = false;
	    	SysLogger.error("MenuManager.delete()",e); 
		    e.printStackTrace();
	    }finally{
	    	functionDao.close();
	    	addFunctionDao.close();
	    }
	    if(result){
		    return "/system/menu/saveok.jsp";
	    }else{
		    return "/system/menu/saveFail.jsp";
	    }
    }
   
    private void ready(){
    	String level_desc = request.getParameter("level_desc");
	    String editId = null ;
	    if("1".equals(level_desc)){
	    	editId = request.getParameter("rootMenuSelect");
	    }else if("2".equals(level_desc)){
	    	editId= request.getParameter("secondMenuSelect");
	    }else if("3".equals(level_desc)){
	    	editId = request.getParameter("thirdMenuSelect");
	    }
	    FunctionDao functionDao = null;
	    Function function =null;
	    try{
	    	functionDao = new FunctionDao();
	    	function = (Function)functionDao.findByID(editId);   
	    }catch(Exception e){
		    SysLogger.error("MenuManager.add()",e); 
		    e.printStackTrace();
	    }finally{
		    functionDao.close();
	    }
	    request.setAttribute("level_desc", level_desc);
	    request.setAttribute("function", function);
	    request.setAttribute("allFunction", allFunction);
   }
   
   private String getNextFunc_desc(List<Function> functionList){
	   if(functionList == null || functionList.size()==0){
		   return null;
	   }
	   CreateRoleFunctionTable crft = new CreateRoleFunctionTable();
	   List<Function> compareFunctionList = crft.compareToByFunc_desc(functionList);
	   Function FunctionEnd = compareFunctionList.get(compareFunctionList.size()-1);
	   String Func_desc = FunctionEnd.getFunc_desc();
	   char charEnd = Func_desc.charAt(Func_desc.length()-1);
	   char nextChar = (char)(charEnd+1);
	   String nextFunc_desc = Func_desc.substring(0, Func_desc.length()-1)+nextChar; 
	   return nextFunc_desc;
   }
}
