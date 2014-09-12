package com.database;

/**
 * 数据库工厂实现类
 * @author itims
 *
 */


public class DatabaseAccessFactory {
	
	
	
    private static DatabaseAccessInterface databaseai = null;
    
    public static DatabaseAccessInterface getDataAccessInstance() {
        if (databaseai == null) {
            databaseai = new DatabaseAccessImpl();
        }
        return databaseai;
    }
    
    
}

