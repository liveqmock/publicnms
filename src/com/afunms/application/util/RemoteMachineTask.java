package com.afunms.application.util;

class RemoteMachineTask implements Runnable
{
	ControlServer cs = null;
	public RemoteMachineTask(ControlServer cs)
	{
		this.cs = cs;
	}
	public void run()
	{
		cs.initServer();
	}
}