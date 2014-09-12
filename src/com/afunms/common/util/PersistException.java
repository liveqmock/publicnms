/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

/*
 * @(#)PersistException.java   1.00 2006/03/04  
 *
 * Copyright (c) 2001-2008  dhcc      
 * All rights reserved.
 *
 */

/**
 * persistence“Ï≥£¿‡      
 * @version  1.00 4 Mar. 2006 17:19    
 * @author 	 netcomm                  
 */
public class PersistException extends Exception
{
    /** 
     * Constructs a ENSPersException with no specified detail message. 
     *
     */
    public PersistException() 
    {
        super();
    }
    
    /** 
     * Constructs a ENSPersException with the specified detail
     * message. 
     *
     * @param str The detail message.
     */
    public PersistException(String str) 
    {
        super(str);
    }
    
}