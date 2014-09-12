package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.polling.snmp.LoadDB2;

public class LoadDB2DataTask extends MonitorTask {
		
	/**
	 * 
	 */
	public LoadDB2DataTask() {
		super();
	}

	public void run() {
		DBDao dbdao = null;
		try{
			DBTypeVo db2typevo = null;
			DBTypeDao typedao = null;
			try {
				typedao = new DBTypeDao();
				db2typevo = (DBTypeVo) typedao.findByDbtype("db2");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (typedao != null)
					typedao.close();
			}
			List db2list = new ArrayList();
			try {
				dbdao = new DBDao();
				db2list = dbdao.getDbByTypeMonFlag(db2typevo.getId(), 1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dbdao != null)
					dbdao.close();
			}
    		// 生成线程池
    		ThreadPool threadPool = null;	
    		if(db2list != null && db2list.size()>0){
    			threadPool = new ThreadPool(db2list.size());	
    			// 运行任务
        		for (int i=0; i<db2list.size(); i++) {
        			DBVo node = (DBVo)db2list.get(i);
        			threadPool.runTask(createTask(node));
        		}
        		// 关闭线程池并等待所有任务完成
        		threadPool.join();
        		threadPool.close();
    		}
    		threadPool = null;
        		
		}catch(Exception e){					 	
			e.printStackTrace();
		}finally{
			//System.out.println("********Host Thread Count : "+Thread.activeCount());
		}
				
	} 
	
    /**
        创建任务
    */	
    private static Runnable createTask(final DBVo node) {
        return new Runnable() {
            public void run() {
                try {                	
                	Hashtable hashv = new Hashtable();
                	LoadDB2 db2=null;
                	db2 = new LoadDB2(node.getIpAddress());
					hashv=db2.collect_Data();
					db2 = null;
                }catch(Exception exc){
                	exc.printStackTrace();
                }
            }
        };
    }
	
}
