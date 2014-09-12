package com.afunms.application.manage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.MediaPlayerDao;
import com.afunms.application.model.MediaPlayer;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.BusinessSystemDao;
import com.afunms.config.model.BusinessSystem;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

public class MediaPlayerManager extends BaseManager implements ManagerInterface{

	public String execute(String action){
		// TODO Auto-generated method stub
		if(action.equals("list")){
			return list();
		}
		else if(action.equals("listView")){
			return listView();
		}
		else if(action.equals("ready_add")){
			return "/config/vpntelnet/mediaPlayer/add.jsp";
		}
		else if(action.equals("add")){
			return add();
		}
		else if(action.equals("delete")){
			return delete();
		}
		else if(action.equals("ready_edit")){
			MediaPlayerDao dao = new MediaPlayerDao();
			setTarget("/config/vpntelnet/mediaPlayer/edit.jsp");
			return readyEdit(dao);
		}
		else if(action.equals("update")){
			return update();
		}
		else if(action.equals("find")){
			return find();
		}
		else if(action.equals("findView")){
			return findView();
		}
		else if(action.equals("download")){
			return download();
		}
		return null;
	}

	private String download(){
		String realPath = request.getRealPath("");
		String name = getParaValue("fileName");
		String path = realPath + "/config/vpntelnet/mediaPlayer/flv/"+name;
		request.setAttribute("filename", path);
		return "/config/vpntelnet/mediaPlayer/download.jsp";
	}
	
	private String list(){
		MediaPlayerDao dao = new MediaPlayerDao();
		setTarget("/config/vpntelnet/mediaPlayer/list.jsp");
		BusinessSystemDao bd = new BusinessSystemDao();
		List list = null;
		try{
			list =  bd.loadAll();
		}catch(Exception e){
		}finally{
			bd.close();
		}
		UserDao userdao = new UserDao();
		List userlist = null;
		try{
			userlist =  userdao.loadAll();
		}catch(Exception e){
		}finally{
			userdao.close();
		}
		Hashtable userHash = new Hashtable();
		if(userlist != null && userlist.size()>0){
			for(int i=0;i<userlist.size();i++){
				User user = (User)userlist.get(i);
				userHash.put(user.getId(), user);
			}
		}
		
		Hashtable bsHash = new Hashtable();
		if(list != null && list.size()>0){
			for(int i=0;i<list.size();i++){
				BusinessSystem bs = (BusinessSystem)list.get(i);
				bsHash.put(bs.getId(), bs);
			}
		}
		
		request.setAttribute("bslist", list);
		request.setAttribute("userhash", userHash);
		request.setAttribute("bshash", bsHash);
		
		
		return list(dao);
	}
	private String listView(){
		
		BusinessSystemDao bd = new BusinessSystemDao();
		List list = null;
		try{
			list =  bd.loadAll();
		}catch(Exception e){
		}finally{
			bd.close();
		}
		Hashtable bsHash = new Hashtable();
		if(list != null && list.size()>0){
			for(int i=0;i<list.size();i++){
				BusinessSystem bs = (BusinessSystem)list.get(i);
				bsHash.put(bs.getId(), bs);
			}
		}
		
		setTarget("/config/vpntelnet/mediaPlayer/listView.jsp");
		request.setAttribute("bshash", bsHash);
		MediaPlayerDao dao = new MediaPlayerDao();
		return list(dao);
	}
	private String find(){
		MediaPlayerDao dao = new MediaPlayerDao();
		setTarget("/config/vpntelnet/mediaPlayer/list.jsp");
		String where = "";
		int bsid = getParaIntValue("bsid");
		if(bsid != -1){
			where = where + " where bsid = "+ bsid;
		}
		BusinessSystemDao bd = new BusinessSystemDao();
		List list = null;
		try{
			list =  bd.loadAll();
		}catch(Exception e){
		}finally{
			bd.close();
		}
		
		UserDao userdao = new UserDao();
		List userlist = null;
		try{
			userlist =  userdao.loadAll();
		}catch(Exception e){
		}finally{
			userdao.close();
		}
		Hashtable userHash = new Hashtable();
		if(userlist != null && userlist.size()>0){
			for(int i=0;i<userlist.size();i++){
				User user = (User)userlist.get(i);
				userHash.put(user.getId(), user);
			}
		}
		
		Hashtable bsHash = new Hashtable();
		if(list != null && list.size()>0){
			for(int i=0;i<list.size();i++){
				BusinessSystem bs = (BusinessSystem)list.get(i);
				bsHash.put(bs.getId(), bs);
			}
		}
		request.setAttribute("bslist", list);
		request.setAttribute("userhash", userHash);
		request.setAttribute("bshash", bsHash);
		return list(dao,where);
	}
	private String findView(){
		MediaPlayerDao dao = new MediaPlayerDao();
		setTarget("/config/vpntelnet/mediaPlayer/listView.jsp");
		String where = " where ";
		int bsid = getParaIntValue("bsid");
		//SysLogger.info("bsid==============="+bsid);
		if(bsid != -1 && bsid != 0){
			where = where + " bsid = "+ bsid;
		}else{
			where = "";
		}
		return list(dao,where);
	}
	private String add(){
		User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		MediaPlayer vo = new MediaPlayer();
		MediaPlayerDao dao = new MediaPlayerDao();
		String fname = (String) session.getAttribute("fname");
		vo.setName(getParaValue("name"));
		vo.setFileName(fname);
		vo.setDesc(getParaValue("desc"));
		vo.setBsid(getParaIntValue("bsid"));
		vo.setOperid(current_user.getId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date=Calendar.getInstance();
	 	String time = sdf.format(date.getTime());
	 	vo.setDotime(time);
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return list();
	}
	private String delete(){
		String realPath = request.getRealPath("");
		MediaPlayerDao dao = new MediaPlayerDao();
		String targetJsp = null;
		setTarget("/mediaPlayer.do?action=list&jp=1");
		String[] id = getParaArrayValue("checkbox");
		for(int i=0;i<id.length;i++){
			String name = ((MediaPlayer)dao.findByID(id[i])).getName();
			dao.close();
			String path = realPath + "/config/vpntelnet/mediaPlayer/flv/"+name;
			File file = null;
			try {
				file = new File(path);
				if(file.exists()){
					if(file.delete()){
						dao = new MediaPlayerDao();
						if(dao.deleteById(id[i])){
							targetJsp = getTarget();
						}
						dao.close();
					}
				}
				else{
					dao = new MediaPlayerDao();
					if(dao.deleteById(id[i])){
						targetJsp = getTarget();
					}
					dao.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    return targetJsp;
	}
	public String update(){
		User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		MediaPlayer vo = new MediaPlayer();
		vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setFileName(getParaValue("fileName"));
		vo.setDesc(getParaValue("desc"));
		vo.setBsid(getParaIntValue("bsid"));
		vo.setOperid(current_user.getId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date=Calendar.getInstance();
	 	String time = sdf.format(date.getTime());
	 	vo.setDotime(time);
		MediaPlayerDao dao = new MediaPlayerDao();
		String target = null;
		try{
			dao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		target = "/mediaPlayer.do?action=list";
		return target;
	}
}
