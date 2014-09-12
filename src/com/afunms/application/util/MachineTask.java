package com.afunms.application.util;

public class MachineTask implements Runnable
{
	ControlServer cs = null;
	public MachineTask(ControlServer cs)
	{
		this.cs = cs;
	}
	public void run()
	{
		//cs.initServer();
	}
}
