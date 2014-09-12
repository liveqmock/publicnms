package com.afunms.discovery;

public class TemporaryLink
{
    private Point start;
    private Point end; 
    private Point dissimilar;
    private boolean del;
    
    public Point getDissimilar()
    {
        return dissimilar;	
    }
    
    public void setDissimilar(Point dissimilar)
    {
    	this.dissimilar = dissimilar;
    }
    
    public boolean isDel() {
		return del;
	}

	public void setDel(boolean del) {
		this.del = del;
	}

	public TemporaryLink(int id1,String ifIndex1,int id2,String ifIndex2)
    {
    	start = new Point(id1,ifIndex1);
    	end = new Point(id2,ifIndex2);
    }
    
	public Point getEnd() 
	{
		return end;
	}

	public Point getStart() 
	{
		return start;
	}
	
    public boolean equals(Object obj)
    {
       if (obj == null)
          return false;
       if (!(obj instanceof TemporaryLink))
          return false;
      
       TemporaryLink that = (TemporaryLink) obj;
       if (start.equals(that.start) && end.equals(that.end))
    	  return true; 
       else if(start.equals(that.end) && end.equals(that.start))	                	   
          return true;
       else 
          return false;
    }

    public int hashCode()
    {
  	   int result = 1;
  	   result = result * 31 + start.hashCode();
  	   result = result * 31 + end.hashCode();
  	   return result;
    }  
    
    public String toString()
    {
    	return start.toString() + "<--->" + end.toString();
    }
    
    public boolean halfEquals(TemporaryLink that)
    {
        if (that == null) return false;
        
        boolean result = false;
        if (start.equals(that.start) && !end.equals(that.end))
        {
            dissimilar = end;
            that.setDissimilar(that.end);
            result = true;
        }      	    
        else if(start.equals(that.end) && !end.equals(that.start))	                	   
        {
            dissimilar = end;
            that.setDissimilar(that.start);
            result = true;
        }      	    
        else if(end.equals(that.start) && !start.equals(that.end))
        {
            dissimilar = start;
            that.setDissimilar(that.end);
            result = true;
        }      	    
        else if(end.equals(that.end) && !start.equals(that.start))
        {
            dissimilar = start;
            that.setDissimilar(that.start);
            result = true;
        }      	    	
        else
           result = false;
        return result; 	
    }
    
    public boolean halfEquals(Point point)
    {
        if (point == null) return false;
        
        boolean result = false;
        if (start.equals(point))
        {
            dissimilar = end;
            result = true;
        }      	    
        else if(end.equals(point))	                	   
        {
            dissimilar = start;
            result = true;
        }
        else
           result = false;
        return result;
    }
}
