/**
 * <p>Description:generate key for discover node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.discovery;

public class KeyGenerator
{
  private static KeyGenerator keygen = new KeyGenerator();
  private static int subNet = 60;
  private static int host = 45;
  private KeyGenerator() {}
  
  public static KeyGenerator getInstance()
  {
	  return keygen;
  }

  public synchronized int getSubNetKey()
  {
      return subNet ++;
  }
  
  public synchronized int getHostKey()
  {
      return host ++;
  }    
}
