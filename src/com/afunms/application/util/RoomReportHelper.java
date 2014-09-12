package com.afunms.application.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.RoomGuestDao;
import com.afunms.cabinet.dao.RoomLawDao;
import com.afunms.cabinet.dao.RoomVideoDao;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.RoomGuest;
import com.afunms.cabinet.model.RoomLaw;
import com.afunms.cabinet.model.RoomVideo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

/**
 * 机房报表
 * 
 * @author wxy
 * @version Dec 23, 2011 12:09:07 PM
 */
public class RoomReportHelper {
	public ArrayList<String[]> exportRoomData(String type, String filePath, int cabinetid, String startdate, String todate) {
		ArrayList<String[]> tableList = new ArrayList<String[]>();
		String[] roomTitle = { "视频名称", "机房位置", "上传人", "描述", "操作时间" };
		tableList.add(roomTitle);
		// //////////////////////////////////////
		List<EqpRoom> eqpRoomlist = new ArrayList<EqpRoom>();
		Hashtable<Integer, EqpRoom> eqpRoomHash = new Hashtable<Integer, EqpRoom>();
		UserDao userdao = new UserDao();
		List userlist = null;
		EqpRoomDao eqpRoomDao = null;
		try {
			userlist = userdao.loadAll();
			eqpRoomDao = new EqpRoomDao();
			eqpRoomlist = eqpRoomDao.loadAll();
			if (eqpRoomlist != null && eqpRoomlist.size() > 0) {
				for (int i = 0; i < eqpRoomlist.size(); i++) {
					EqpRoom eqpRoom = (EqpRoom) eqpRoomlist.get(i);
					eqpRoomHash.put(eqpRoom.getId(), eqpRoom);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userdao.close();
		}
		Hashtable<Integer, User> userHash = new Hashtable<Integer, User>();
		if (userlist != null && userlist.size() > 0) {
			for (int i = 0; i < userlist.size(); i++) {
				User user = (User) userlist.get(i);
				userHash.put(user.getId(), user);
			}
		}

		StringBuffer where = new StringBuffer();

		where.append(" where 1=1");

		if (cabinetid != -1) {
			where.append(" and cabinetid=" + cabinetid);
		}

		if (startdate != null && todate != null && !"".equals(startdate) && !"".equals(todate) && !"null".equals(startdate) && !"null".equals(todate)) {
			where.append(" and dotime>'" + startdate + " 00:00:00' and dotime<'" + todate + " 23:59:59'");
		} else {
			String currentDateString = "";// 初始页面开始日期为空
			String perWeekDateString = "";// 初始页面结束日期为空
			todate = currentDateString;
			startdate = perWeekDateString;
		}
		RoomVideoDao dao = new RoomVideoDao();
		List<RoomVideo> list = null;
		// JspPage jp=null;
		try {
			// list = dao.listByPage(dao.getCurrentPage(),where,perpage);
			// jp=dao.getPage();
			list = dao.findByCondition(where + " order by dotime desc");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				RoomVideo vo = (RoomVideo) list.get(i);
				if (vo == null)
					continue;
				User user = null;
				String userName = "";
				String room = "";
				EqpRoom eqpRoom = null;
				if (userHash.containsKey(vo.getUserid())) {
					user = userHash.get(vo.getUserid());
					userName = user.getName();
				}
				if (eqpRoomHash.containsKey(vo.getCabinetid())) {
					eqpRoom = eqpRoomHash.get(vo.getCabinetid());
					room = eqpRoom.getName();
				}
				
				String[] roomData={vo.getName(),room,userName,vo.getDescription(),vo.getDotime()};
				tableList.add(roomData);
			}
			
		}
		return tableList;
	}
	/**
	 * 机房管理制度报表
	 *@return ArrayList<String[]>
	 */
	public ArrayList<String[]> exportRoomLawData(String type, String filePath, int cabinetid, String startdate, String todate) {
		ArrayList<String[]> tableList = new ArrayList<String[]>();
		String[] roomTitle = { "名称", "机房位置", "上传人","附件", "描述", "操作时间" };
		tableList.add(roomTitle);
		// //////////////////////////////////////
		List<EqpRoom> eqpRoomlist = new ArrayList<EqpRoom>();
		Hashtable<Integer, EqpRoom> eqpRoomHash = new Hashtable<Integer, EqpRoom>();
		UserDao userdao = new UserDao();
		List userlist = null;
		EqpRoomDao eqpRoomDao = null;
		try {
			userlist = userdao.loadAll();
			eqpRoomDao = new EqpRoomDao();
			eqpRoomlist = eqpRoomDao.loadAll();
			if (eqpRoomlist != null && eqpRoomlist.size() > 0) {
				for (int i = 0; i < eqpRoomlist.size(); i++) {
					EqpRoom eqpRoom = (EqpRoom) eqpRoomlist.get(i);
					eqpRoomHash.put(eqpRoom.getId(), eqpRoom);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userdao.close();
		}
		Hashtable<Integer, User> userHash = new Hashtable<Integer, User>();
		if (userlist != null && userlist.size() > 0) {
			for (int i = 0; i < userlist.size(); i++) {
				User user = (User) userlist.get(i);
				userHash.put(user.getId(), user);
			}
		}

		StringBuffer where = new StringBuffer();

		where.append(" where 1=1");

		if (cabinetid != -1) {
			where.append(" and cabinetid=" + cabinetid);
		}

		if (startdate != null && todate != null && !"".equals(startdate) && !"".equals(todate) && !"null".equals(startdate) && !"null".equals(todate)) {
			where.append(" and dotime>'" + startdate + " 00:00:00' and dotime<'" + todate + " 23:59:59'");
		} else {
			String currentDateString = "";// 初始页面开始日期为空
			String perWeekDateString = "";// 初始页面结束日期为空
			todate = currentDateString;
			startdate = perWeekDateString;
		}
		RoomLawDao dao = new RoomLawDao();
		List<RoomLaw> list = null;
		// JspPage jp=null;
		try {
			// list = dao.listByPage(dao.getCurrentPage(),where,perpage);
			// jp=dao.getPage();
			list = dao.findByCondition(where + " order by dotime desc");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				RoomLaw vo = (RoomLaw) list.get(i);
				if (vo == null)
					continue;
				User user = null;
				String userName = "";
				String room = "";
				EqpRoom eqpRoom = null;
				if (userHash.containsKey(vo.getUserid())) {
					user = userHash.get(vo.getUserid());
					userName = user.getName();
				}
				if (eqpRoomHash.containsKey(vo.getCabinetid())) {
					eqpRoom = eqpRoomHash.get(vo.getCabinetid());
					room = eqpRoom.getName();
				}
				
				String[] roomlawData={vo.getName(),room,userName,vo.getFilename(),vo.getDescription(),vo.getDotime()};
				tableList.add(roomlawData);
			}
			
		}
		return tableList;
	}
	/**
	 * 机房来宾登记报表
	 *@return ArrayList<String[]>
	 */
	public ArrayList<String[]> exportRoomGuestData(String type, String filePath, int cabinetid, String startdate, String todate) {
		ArrayList<String[]> tableList = new ArrayList<String[]>();
		String[] roomTitle = { "姓名", "单位", "进场时间","离场时间", "机房位置", "审批人"  };
		tableList.add(roomTitle);
		// //////////////////////////////////////
		List<EqpRoom> eqpRoomlist = new ArrayList<EqpRoom>();
		Hashtable<Integer, EqpRoom> eqpRoomHash = new Hashtable<Integer, EqpRoom>();
		UserDao userdao = new UserDao();
		List userlist = null;
		EqpRoomDao eqpRoomDao = null;
		try {
			userlist = userdao.loadAll();
			eqpRoomDao = new EqpRoomDao();
			eqpRoomlist = eqpRoomDao.loadAll();
			if (eqpRoomlist != null && eqpRoomlist.size() > 0) {
				for (int i = 0; i < eqpRoomlist.size(); i++) {
					EqpRoom eqpRoom = (EqpRoom) eqpRoomlist.get(i);
					eqpRoomHash.put(eqpRoom.getId(), eqpRoom);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userdao.close();
		}
		Hashtable<Integer, User> userHash = new Hashtable<Integer, User>();
		if (userlist != null && userlist.size() > 0) {
			for (int i = 0; i < userlist.size(); i++) {
				User user = (User) userlist.get(i);
				userHash.put(user.getId(), user);
			}
		}

		StringBuffer where = new StringBuffer();

		where.append(" where 1=1");

		if (cabinetid != -1) {
			where.append(" and cabinetid=" + cabinetid);
		}

		if (startdate != null && todate != null && !"".equals(startdate) && !"".equals(todate) && !"null".equals(startdate) && !"null".equals(todate)) {
			where.append(" and dotime>'" + startdate + " 00:00:00' and dotime<'" + todate + " 23:59:59'");
		} else {
			String currentDateString = "";// 初始页面开始日期为空
			String perWeekDateString = "";// 初始页面结束日期为空
			todate = currentDateString;
			startdate = perWeekDateString;
		}
		RoomGuestDao dao = new RoomGuestDao();
		List<RoomGuest> list = null;
		// JspPage jp=null;
		try {
			// list = dao.listByPage(dao.getCurrentPage(),where,perpage);
			// jp=dao.getPage();
			list = dao.findByCondition(where + " order by dotime desc");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				RoomGuest vo = (RoomGuest) list.get(i);
				if (vo == null)
					continue;
				
				String room = "";
				EqpRoom eqpRoom = null;
				
				if (eqpRoomHash.containsKey(vo.getCabinetid())) {
					eqpRoom = eqpRoomHash.get(vo.getCabinetid());
					room = eqpRoom.getName();
				}
				
				String[] roomlawData={vo.getName(),vo.getUnit(),vo.getInTime(),vo.getOutTime(),room,vo.getAudit()};
				tableList.add(roomlawData);
			}
			
		}
		return tableList;
	}
}