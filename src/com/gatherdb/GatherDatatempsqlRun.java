package com.gatherdb;

import java.util.Iterator;
import java.util.TimerTask;
import com.database.DBManager;
import com.gatherResulttosql.NetHostDatatempResultTosql;

import java.util.Vector;

public class GatherDatatempsqlRun  extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Vector alldata=null;
		//NetHostDatatempResultTosql datatemp=new NetHostDatatempResultTosql();
		//datatemp.ResulttoSql();
		
		if(!GathersqlListManager.idbdatatempstatus)
		{
			
	    //System.out.println("=====开始临时数据入库======"+GathersqlListManager.idbdatatempstatus);		
		GathersqlListManager.AdddateTempsql("DHCC-DB", alldata);
		}
			
	}

}
