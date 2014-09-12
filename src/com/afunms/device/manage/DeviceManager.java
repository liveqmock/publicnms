package com.afunms.device.manage;

import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.device.dao.AuditLogDao;
import com.afunms.device.dao.CabinetDao;
import com.afunms.device.dao.ExternalDeviceDao;
import com.afunms.device.dao.TreeDao;
import com.afunms.device.model.AuditLog;
import com.afunms.device.model.Cabinet;
import com.afunms.device.model.ExternalDevice;
import com.afunms.device.model.Tree;
import com.afunms.system.model.User;

public class DeviceManager extends BaseManager implements ManagerInterface {

	@Override
	public String execute(String action) {
		if (action.equals("addCabinet")) {
			return addCabinet();
		} else if (action.equals("listDevice")) {
			return listDevice();
		} else if (action.equals("saveCabinet")) {
			return saveCabinet();
		} else if (action.equals("delete")) {
			return delete();
		} else if (action.equals("ready_update")) {
			return ready_update();
		} else if (action.equals("updateCabinet")) {
			return updateCabinet();
		} else if (action.equals("auditLogList")) {
			return auditLogList();
		} else if (action.equals("tree")) {
			return tree();
		}else if (action.equals("addExternalDevice")) {
			return  addExternalDevice();
		}else if (action.equals("updateExternalDevice")) {
			return  updateExternalDevice();
		}else if (action.equals("deleteDeviceById")) {
			return  deleteDeviceById();
		}else if (action.equals("externalDeviceList")) {
			return  externalDeviceList();
		}else if (action.equals("externalDeviceListById")) {
			return  externalDeviceListById();
		}else if (action.equals("deleteCabinetById")) {
			return  deleteCabinetById();
		}else if (action.equals("deleteAuditById")) {
			return  deleteAuditById();
		}
		
		return null;
	}
	private String deleteDeviceById() {
		String id = getParaValue("id");
		
		ExternalDeviceDao deviceDao=null;
		TreeDao treeDao=null;
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		int userid = user.getId();
		String username = user.getName();
		String[] ids={id};
		AuditLogDao logDao=null;
			try {
			
				deviceDao=new ExternalDeviceDao();
				logDao=new AuditLogDao();
				ExternalDevice device=(ExternalDevice)deviceDao.findByID(id);
				AuditLog log = new AuditLog();
				log.setIp(device.getIpaddress());
				log.setOperateType("删除外部设备");
				log.setType("2");
				log.setUsername(username);
				logDao.save(log);
				treeDao=new TreeDao();
				deviceDao.delete(ids);
				treeDao.deleteByExternalDeviceId(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				
				if (deviceDao != null)
					deviceDao.close();
				if(treeDao!=null)
					treeDao.close();
				if(logDao!=null){
					logDao.close();
				}
			}
			return tree();
	}
	private String updateExternalDevice() {
		int id = getParaIntValue("id");
		
		String osType = getParaValue("osType");
		String brand = getParaValue("brand");
		String deviceType = getParaValue("deviceType");
		String specification = getParaValue("specification");
		String supplier = getParaValue("supplier");
		String insureder = getParaValue("insureder");
		String ipaddress = getParaValue("ipaddress");
		String deviceId = getParaValue("deviceId");
		int cabinetId = getParaIntValue("cabinetId");
		ExternalDevice device=new ExternalDevice();
		device.setId(id);
		device.setOsType(osType);
		device.setBrand(brand);
		device.setDeviceType(deviceType);
		device.setSpecification(specification);
		device.setSupplier(supplier);
		device.setInsureder(insureder);
		device.setIpaddress(ipaddress);
		device.setDeviceId(deviceId);
		device.setCabinetId(cabinetId);
	
		Tree tree=new Tree();
		tree.setPid(cabinetId+"");
		tree.setCabinetId(cabinetId);
		//tree.setExternalDeviceId(externalDeviceId);
		
		ExternalDeviceDao deviceDao=null;
	//	TreeDao treeDao=null;
		AuditLogDao logDao = null;
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		int userid = user.getId();
		String username = user.getName();
		AuditLog log = new AuditLog();
		log.setIp(ipaddress);
		log.setOperateType("修改外部设备");
		log.setType("1");
		log.setUsername(username);
		try {
			deviceDao=new ExternalDeviceDao();
			logDao=new AuditLogDao();
			//treeDao=new TreeDao();
			deviceDao.update(device);
			logDao.save(log);
			//treeDao.save(tree);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if (deviceDao != null)
				deviceDao.close();
			if (logDao != null)
				logDao.close();
			//if (treeDao != null)
			//	treeDao.close();
		}
		return externalDeviceList();
	}
	private String addExternalDevice() {
		int id = getParaIntValue("id");
		String osType = getParaValue("osType");
		String brand = getParaValue("brand");
		String deviceType = getParaValue("deviceType");
		String specification = getParaValue("specification");
		String supplier = getParaValue("supplier");
		String insureder = getParaValue("insureder");
		String ipaddress = getParaValue("ipaddress");
		String deviceId = getParaValue("deviceId");
		int cabinetId=getParaIntValue("cabinetId");
		ExternalDevice device=new ExternalDevice();
		device.setOsType(osType);
		device.setBrand(brand);
		device.setDeviceType(deviceType);
		device.setSpecification(specification);
		device.setSupplier(supplier);
		device.setInsureder(insureder);
		device.setIpaddress(ipaddress);
		device.setDeviceId(deviceId);
		device.setCabinetId(cabinetId);
		Tree tree=new Tree();
		tree.setPid(id+"");
		tree.setCabinetId(cabinetId);
		
		//tree.setExternalDeviceId(externalDeviceId);
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		int userid = user.getId();
		String username = user.getName();
		AuditLog log = new AuditLog();
		log.setIp(ipaddress);
		log.setOperateType("添加外部设备");
		log.setType("2");
		log.setUsername(username);
		AuditLogDao logDao = null;
		ExternalDeviceDao deviceDao=null;
		TreeDao treeDao=null;
		try {
			logDao = new AuditLogDao();
			deviceDao=new ExternalDeviceDao();
			int nextId=deviceDao.getNextId();
			device.setId(nextId);
			tree.setExternalDeviceId(nextId);
			treeDao=new TreeDao();
			deviceDao.save(device);
			treeDao.save(tree);
			logDao.save(log);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if (deviceDao != null)
				deviceDao.close();
			if (treeDao != null)
				treeDao.close();
			if(logDao!=null)
				logDao.close();
		}
		return externalDeviceList();
	}
	private String tree() {
		TreeDao treeDao = null;
		CabinetDao cabinetDao=null;
		ExternalDeviceDao deviceDao=null;
		List list = null;
		List cabinetList=null;
		List deviceList=null;
		try {
			treeDao = new TreeDao();
			cabinetDao=new CabinetDao();
			deviceDao=new ExternalDeviceDao();
			list = treeDao.loadAll();
			cabinetList=cabinetDao.loadAll();
			deviceList=deviceDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (treeDao != null)
				treeDao.close();
			if (cabinetDao != null)
				cabinetDao.close();
			if (deviceDao != null)
				deviceDao.close();
			
		}
		Hashtable cabinetHashtable=new Hashtable();
		Cabinet cabinet1=new Cabinet();
		cabinet1.setIpaddress("资源树");

		cabinetHashtable.put(0, cabinet1);
		if(cabinetList!=null){
			for (int i = 0; i < cabinetList.size(); i++) {
				Cabinet cabinet=(Cabinet)cabinetList.get(i);
				cabinetHashtable.put(cabinet.getId(), cabinet);
			}
		}
		
		Hashtable deviceHashtable=new Hashtable();
		if(deviceList!=null){
			for (int i = 0; i < deviceList.size(); i++) {
				ExternalDevice device=(ExternalDevice)deviceList.get(i);
				deviceHashtable.put(device.getId(), device);
			}
		}
		request.setAttribute("list", list);
		request.setAttribute("cabinetHashtable", cabinetHashtable);
		request.setAttribute("deviceHashtable", deviceHashtable);
		return "/device/tree.jsp";
	}

	private String auditLogList() {
		AuditLogDao logDao = null;

		setTarget("/device/deviceAudit.jsp");
		try {
			logDao = new AuditLogDao();
			return list(logDao, "where 1=1 ");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (logDao != null) {
				logDao.close();
			}
		}
		return "/device/deviceAudit.jsp";
	}

	private String ready_update() {
		int id = getParaIntValue("id");
		Cabinet cabinet = null;
		CabinetDao dao = null;
		try {
			dao = new CabinetDao();
			cabinet = (Cabinet) dao.findByID(id + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		request.setAttribute("cabinet", cabinet);
		return "/device/ready_update.jsp";
	}

	private String updateCabinet() {
		int id = getParaIntValue("id");
		String city = getParaValue("city");
		String roadInfo = getParaValue("roadInfo");
		String affiliation = getParaValue("affiliation");
		String belong = getParaValue("belong");
		String latitude = getParaValue("latitude");
		String longitude = getParaValue("longitude");
		String ipaddress = getParaValue("ipaddress");
		String rackNumber = getParaValue("rackNumber");
		Cabinet cabinet = new Cabinet();
		cabinet.setId(id);
		cabinet.setCity(city);
		cabinet.setRoadInfo(roadInfo);
		cabinet.setAffiliation(affiliation);
		cabinet.setBelong(belong);
		cabinet.setLatitude(latitude);
		cabinet.setLongitude(longitude);
		cabinet.setIpaddress(ipaddress);
		cabinet.setRackNumber(rackNumber);
		CabinetDao dao = null;
		AuditLogDao logDao = null;
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		int userid = user.getId();
		String username = user.getName();
		AuditLog log = new AuditLog();
		log.setIp(ipaddress);
		log.setOperateType("修改机柜");
		log.setType("1");
		log.setUsername(username);
		try {
			dao = new CabinetDao();
			logDao = new AuditLogDao();
			
			dao.update(cabinet);
			logDao.save(log);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
			if(logDao!=null)
				logDao.close();
		}
		return listDevice();
	}

	private String delete() {
		SysLogger.info("开始删除设备................");

		String[] ids = getParaArrayValue("checkbox");

		if (ids != null && ids.length > 0) {
			CabinetDao dao = null;
			TreeDao treeDao=null;
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			int userid = user.getId();
			String username = user.getName();
			AuditLogDao logDao = null;
			ExternalDeviceDao externalDeviceDao =null;
			try {

				logDao = new AuditLogDao();
				dao = new CabinetDao();
				
				
				for (int i = 0; i < ids.length; i++) {
					Cabinet cabinet = (Cabinet) dao.findByID(ids[i] + "");
					AuditLog log = new AuditLog();
					log.setIp(cabinet.getIpaddress());
					log.setOperateType("删除机柜");
					log.setType("1");
					log.setUsername(username);
					logDao.save(log);
					externalDeviceDao = new ExternalDeviceDao();
					
					try {
						externalDeviceDao.deleteByCabinetId(ids[i]+"");
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(externalDeviceDao!=null)
						 externalDeviceDao.close();
					}
					treeDao=new TreeDao();
					try {
						treeDao.deleteByCabinetId(cabinet.getId()+"");
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if (treeDao != null)
							treeDao.close();
					}
				}
				dao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (logDao != null)
					logDao.close();
				if (dao != null)
					dao.close();
				
			}

			try {
				dao = new CabinetDao();
				dao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null)
					dao.close();
			}

		}
		return listDevice();
	}
	private String deleteCabinetById() {

		String id= getParaValue("cabinetId");
String[] ids={id};
		if (id != null ) {
			CabinetDao dao = null;
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			int userid = user.getId();
			String username = user.getName();
			AuditLogDao logDao = null;
			ExternalDeviceDao externalDeviceDao = null;
			try {

				logDao = new AuditLogDao();
				dao = new CabinetDao();
				externalDeviceDao = new ExternalDeviceDao();
					Cabinet cabinet = (Cabinet) dao.findByID(id + "");
					AuditLog log = new AuditLog();
					log.setIp(cabinet.getIpaddress());
					log.setOperateType("删除机柜");
					log.setType("1");
					log.setUsername(username);
					logDao.save(log);
					externalDeviceDao.deleteByCabinetId(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (logDao != null)
					logDao.close();
				if (dao != null)
					dao.close();
				if(externalDeviceDao!=null)
					externalDeviceDao.close();
			}

			try {
				dao = new CabinetDao();
				dao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null)
					dao.close();
			}

		}
		return externalDeviceList();
	}

	private String ready_add() {

		String target = "/device/ready_addDevice.jsp";

		return target;
	}

	private String addCabinet() {

		String target = "/device/addCabinet.jsp";

		return target;
	}

	private String saveCabinet() {
		String city = getParaValue("city");
		String roadInfo = getParaValue("roadInfo");
		String affiliation = getParaValue("affiliation");
		String belong = getParaValue("belong");
		String latitude = getParaValue("latitude");
		String longitude = getParaValue("longitude");
		String ipaddress = getParaValue("ipaddress");
		String rackNumber = getParaValue("rackNumber");
		Cabinet cabinet = new Cabinet();
		cabinet.setCity(city);
		cabinet.setRoadInfo(roadInfo);
		cabinet.setAffiliation(affiliation);
		cabinet.setBelong(belong);
		cabinet.setLatitude(latitude);
		cabinet.setLongitude(longitude);
		cabinet.setIpaddress(ipaddress);
		cabinet.setRackNumber(rackNumber);
		
		CabinetDao dao = null;
		
		AuditLogDao logDao = null;
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		int userid = user.getId();
		String username = user.getName();
		AuditLog log = new AuditLog();
		log.setIp(ipaddress);
		log.setOperateType("添加机柜");
		log.setType("1");
		log.setUsername(username);
		
		Tree tree=new Tree();
		tree.setPid("1");
		TreeDao treeDao=null;
		try {
			dao = new CabinetDao();
			int cabinetId=dao.getNextId();
			tree.setCabinetId(cabinetId);
			tree.setExternalDeviceId(0);
			logDao = new AuditLogDao();
			treeDao=new TreeDao();
			treeDao.save(tree);
			cabinet.setId(cabinetId);
			dao.save(cabinet);
			logDao.save(log);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
			if (logDao != null) {
				logDao.close();
			}
			if(treeDao!=null)treeDao.close();
		}
		return listDevice();
	}

	private String listDevice() {
		String target = "/device/cabinetList.jsp";
		setTarget(target);
		request.setAttribute("actionlist", "list");

		CabinetDao dao = new CabinetDao();

		return list(dao, "where 1=1 ");

	}
	private String externalDeviceList() {
		String target = "/device/externalDevicelist.jsp";
		setTarget(target);
		request.setAttribute("actionlist", "list");

		ExternalDeviceDao dao = new ExternalDeviceDao();

		return list(dao, "where 1=1 ");

	}
	private String externalDeviceListById() {
		String cabinetId=getParaValue("id");
		String target = "/device/externalDevicelist.jsp";
		setTarget(target);
		request.setAttribute("actionlist", "list");
		String where="";
       if(cabinetId!=null&&!cabinetId.equals("0")){
    	   where=" where cabinetId="+cabinetId;
       }
		ExternalDeviceDao dao = new ExternalDeviceDao();
		return list(dao, where);

	}
	private String deleteAuditById() {
		String[] ids=getParaArrayValue("id");
		AuditLogDao dao=null;
		try {
			dao = new AuditLogDao();
			dao.delete(ids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		return "/device/deviceAudit.jsp";
	}
}
