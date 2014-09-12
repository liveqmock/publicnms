/*
 * 
 * 
 * 
 * 	
		Hashtable typehashtable = new Hashtable();
		typehashtable.put("A" , "自动启动作业");
		typehashtable.put("B" , "批作业");
		typehashtable.put("H" , "水平特许内部代码(仅对于任务)");
		typehashtable.put("I" , "交互式作业");
		typehashtable.put("M" , "子系统监视作业");
		typehashtable.put("R" , "假脱机读程序作业");
		typehashtable.put("S" , "系统作业");
		typehashtable.put("V" , "特许内部代码任务");
		typehashtable.put("W" , "假脱机写程序作业");
		typehashtable.put("X" , "启动控制程序功能系统作业");
		
		Hashtable subtypehashtable = new Hashtable();
		subtypehashtable.put("N" , "无特殊子类型");
		subtypehashtable.put("D" , "立即");
		subtypehashtable.put("E" , "唤起作业或批通信");
		subtypehashtable.put("J" , "预启动作业");
		subtypehashtable.put("P" , "打印驱动程序作业");
		subtypehashtable.put("T" , "多请求终端(MRT)作业(仅系统/36 环境)");
		subtypehashtable.put("U" , "替代的假脱机用户");
		
		Hashtable statushashtable = new Hashtable();
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_DEQUEUE , "正在等待出队");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_SELECTION , "正在等待选择");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_CONDITION , "正在等待调节");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_EVENT , "正在等待事件");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_DELAY , "正在等待延迟");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_DELAYED , "正在等待已延迟");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_PRESTART , "正在等待请求");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_SIGNAL , "正在等待信号");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_SUSPENDED_SYSTEM_REQUEST , "已暂挂 - 系统请求");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_DISPLAY , "正在等待工作站 I/O");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_TIME_INTERVAL , "正在等待事件间隔");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_TIME_INTERVAL_AND_ACTIVE , "正在等待事件间隔");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_JAVA , "正在等待 Java 程序");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_WAIT_THREAD , "正在等待线程");
		statushashtable.put(Job.ACTIVE_JOB_STATUS_RUNNING , "正在运行");
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */



package com.afunms.topology.util;

import java.util.Enumeration;
import java.util.Hashtable;

public class JobConstantForAS400 {
	
	private static final String JOB_TYPE_AUTOSTART = "A";
	
	private static final String JOB_TYPE_BATCH = "B";
	
	private static final String JOB_TYPE_INTERACTIVE = "I";
	
	private static final String JOB_TYPE_SUBSYSTEM_MONITOR = "M";
	
	private static final String JOB_TYPE_SPOOLED_READER = "R";
	
	private static final String JOB_TYPE_SYSTEM = "S";
	
	private static final String JOB_TYPE_SPOOLED_WRITER = "W";
	
	private static final String JOB_TYPE_SCPF_SYSTEM = "X";
	
	private static final String JOB_TYPE_NOT_VALID = "";
	
	private static final String JOB_TYPE_ENHANCED = "1016";
	
	private static Hashtable<String, String> typehashtable = null;
	
	static {
		typehashtable = new Hashtable<String, String>();
		typehashtable.put(JOB_TYPE_AUTOSTART , "自动启动作业");
		typehashtable.put(JOB_TYPE_BATCH , "批作业");
		typehashtable.put("H" , "水平特许内部代码(仅对于任务)");
		typehashtable.put(JOB_TYPE_INTERACTIVE , "交互式作业");
		typehashtable.put(JOB_TYPE_SUBSYSTEM_MONITOR , "子系统监视作业");
		typehashtable.put(JOB_TYPE_SPOOLED_READER , "假脱机读程序作业");
		typehashtable.put(JOB_TYPE_SYSTEM , "系统作业");
		typehashtable.put("V" , "特许内部代码任务");
		typehashtable.put(JOB_TYPE_SPOOLED_WRITER , "假脱机写程序作业");
		typehashtable.put(JOB_TYPE_SCPF_SYSTEM , "启动控制程序功能系统作业");
		typehashtable.put(JOB_TYPE_NOT_VALID , "");
		typehashtable.put(JOB_TYPE_ENHANCED , "1016");
	};
	
	
	private static final String JOB_SUBTYPE_IMMEDIATE = "D";
	
	private static final String JOB_SUBTYPE_PROCEDURE_START_REQUEST = "E";
	
	private static final String JOB_SUBTYPE_PRESTART = "J";
	
	private static final String JOB_SUBTYPE_PRINT_DRIVER = "P";
	
	private static final String JOB_SUBTYPE_MRT = "T";
	
	private static final String JOB_SUBTYPE_ALTERNATE_SPOOL_USER = "U";
	
	private static final String JOB_SUBTYPE_MACHINE_SERVER_JOB = "F";
	
	private static final String JOB_SUBTYPE_BLANK = "";
	
	private static Hashtable<String, String> subtypehashtable = null;
	
	static {
		subtypehashtable = new Hashtable<String, String>();
		subtypehashtable.put("N" , "无特殊子类型");
		subtypehashtable.put( JOB_SUBTYPE_IMMEDIATE , "立即");
		subtypehashtable.put( JOB_SUBTYPE_PROCEDURE_START_REQUEST , "唤起作业或批通信");
		subtypehashtable.put( JOB_SUBTYPE_PRESTART , "预启动作业");
		subtypehashtable.put( JOB_SUBTYPE_PRINT_DRIVER , "打印驱动程序作业");
		subtypehashtable.put( JOB_SUBTYPE_MRT , "多请求终端(MRT)作业(仅系统/36 环境)");
		subtypehashtable.put( JOB_SUBTYPE_ALTERNATE_SPOOL_USER , "替代的假脱机用户");
		subtypehashtable.put( JOB_SUBTYPE_BLANK , "");
		subtypehashtable.put( JOB_SUBTYPE_MACHINE_SERVER_JOB , "F");
	};
	/**
	 * 上述 类型为 IBM Toolbox for java 中所定义的类型 和 子类型
	 * 
	 * 而在 IBM PCOMM 中是将 类型和子类型 结合在一起 形成了 类型
	 * 
	 * ASJ: 自动启动 
	 * BCH: 批量 
	 * BCI: 批量即时 
	 * EVK: 由程序启动请求启动 
	 * INT: 交互 o M36: 高级36服务器作业 
	 * MRT: 多个请求终端 
	 * PJ: 启动前作业 
	 * PDJ: 打印驱动作业 
	 * RDR: 读取程序 
	 * SBS: 子系统监视器 
	 * SYS: 系统 
	 * WTR: 写入程序状态 
	 * 
	 * 上述类型为 IBM PCOMM 中所定义的类型
	 */

	/**
	 * Constant indicating that a job was disconnected form a work station display.
	 */
    public static final String ACTIVE_JOB_STATUS_DISCONNECTED = "DSC"; 
    
    /**
     * Constant indicating that a job has been ended with the *IMMED option, 
     * or its delay time has ended with the *CNTRLD option.
     */
    public static final String ACTIVE_JOB_STATUS_ENDED = "END";
    
    /**
     * Constant indicating that a job is ending for a reason other than 
     * running the End Job (ENDJOB) or End Subsystem (ENDSBS) commands, 
     * such as a SIGNOFF command, End Group Job (ENDGRPJOB) command, 
     * or an exception that is not handled.
     */
    public static final String ACTIVE_JOB_STATUS_ENDING = "EOJ"; 
    
    /**
     * Job attribute representing the status of what the initial thread 
     * of a job is currently doing, when the active job status is 
     * ACTIVE_JOB_STATUS_ENDED or ACTIVE_JOB_STATUS_ENDING. 
     */
    public static final int ACTIVE_JOB_STATUS_FOR_JOBS_ENDING = 103;
    
    /**
     * Constant indicating that a job is held.
     */
    public static final String ACTIVE_JOB_STATUS_HELD = "HLD"; 
    
    /**
     * Constant indicating that a job is held due to a suspended thread.
     */
    public static final String ACTIVE_JOB_STATUS_HELD_THREAD = "HLDT"; 
    
    /**
     * Constant indicating that a job is ineligible and not currently 
     * in a pool activity level.
     */
    public static final String ACTIVE_JOB_STATUS_INELIGIBLE = "INEL";
    
    /**
     * Constant indicating that a job is either in transition or not active.
     */
    public static final String ACTIVE_JOB_STATUS_NONE = ""; 
    
    /**
     * 正在运行
     * 
     * Constant indicating that a job is currently running in a pool activity level.
     */
    public static final String ACTIVE_JOB_STATUS_RUNNING = "RUN"; 
    
    /**
     * Constant indicating that a job has stopped as the result of a signal.
     */
    public static final String ACTIVE_JOB_STATUS_STOPPED = "SIGS"; 
    
    /**
     * Constant indicating that a job is suspended by a Transfer Group Job (TFRGRPJOB) command
     */
    public static final String ACTIVE_JOB_STATUS_SUSPENDED = "GRP"; 
    
    /**
     * 已暂挂 - 系统请求
     * 
     * Constant indicating that a job is the suspended half of a system request job pair.
     */
    public static final String ACTIVE_JOB_STATUS_SUSPENDED_SYSTEM_REQUEST = "SRQ";
    
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an I/O operation to a binary synchronous device.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_BIN_SYNCH_DEVICE = "BSCW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of an I/O operation to a binary synchronous device.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_BIN_SYNCH_DEVICE_AND_ACTIVE = "BSCA"; 
    
    /**
     * Constant indicating that a job is waiting for the completion of save-while-active 
     * checkpoint processing in another job.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_CHECKPOINT = "CMTW";
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an I/O operation to a communications device.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_COMM_DEVICE = "CMNW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of an I/O operation to a communications device.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_COMM_DEVICE_AND_ACTIVE = "CMNA";
    
    /** 
     * 正在等待调节 
     * 
     * Constant indicating that a job is waiting on a handle-based condition.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_CONDITION = "CNDW"; 
    
    /**
     * Constant indicating that a job is waiting for the completion of a CPI communications call.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_CPI_COMM = "CPCW"; 
    
    /**
     * Constant indicating that a job is waiting to try a read operation again 
     * on a database file after the end-of-file (EOF) has been reached.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DATABASE_EOF = "EOFW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level to 
     * try a read operation again on a database file after the end-of-file (EOF) has been reached.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DATABASE_EOF_AND_ACTIVE = "EOFA";
    
    /**
     * 正在等待延迟
     * 
     * Constant indicating that a job is delayed for a time interval to end, 
     * or for a specific delay end time, by the Delay Job (DLYJOB) command. 
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DELAY = "DLYW"; 
    
    /**
     * 正在等待已延迟
     * 
     * Constant indicating that a job is waiting for a specified time interval to end,
     * or for a specific delay end time, as specified on the Delay Job (DLYJOB) command. 
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DELAYED = "DLYW";
    
    /** 
     * 正在等待出队 
     * 
     * Constant indicating that a job is waiting for the completion of a dequeue operation.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DEQUEUE = "DEQW";
    
    public static final String ACTIVE_JOB_STATUS_WAIT_MSGUEUE = "MSGW";
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of a dequeue operation.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DEQUEUE_AND_ACTIVE = "DEQA"; 
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an I/O operation to a diskette unit.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DISKETTE = "DKTW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of an I/O operation to a diskette unit.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DISKETTE_AND_ACTIVE = "DKTA"; 
    
    /**
     * 正在等待工作站 I/O
     *
     * Constant indicating that a job is waiting for input from a work station display.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DISPLAY = "DSPW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * input from a work station display.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_DISPLAY_AND_ACTIVE = "DSPA";
    
    /** 
     * 正在等待事件 
     * 
     * Constant indicating that a job is waiting for an event. 
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_EVENT = "EVTW"; 
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an I/O operation to an intersystem communications function (ICF) file.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_ICF_FILE = "ICFW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of an I/O operation to an intersystem communications function (ICF) file.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_ICF_FILE_AND_ACTIVE = "ICFA"; 
    
    /**
     * 正在等待 Java 程序
     * 
     * Constant indicating that a job is waiting for the completion of a Java program.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_JAVA = "JVAW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of a Java program.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_JAVA_AND_ACTIVE = "JVAA"; 
    
    /**
     * Constant indicating that a job is waiting for a lock.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_LOCK = "LCKW";
    
    /**
     * Constant indicating that a job is waiting for a message from a message queue.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_MESSAGE = "MSGW"; 
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an I/O operation to a mixed device file.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_MIXED_DEVICE_FILE = "MXDW"; 
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an I/O operation to multiple files.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_MULTIPLE_FILES = "MLTW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of an I/O operation to multiple files.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_MULTIPLE_FILES_AND_ACTIVE = "MLTA";
    
    /**
     * Constant indicating that a job is waiting for a mutex. 
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_MUTEX = "MTXW";
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an I/O operation to an optical device.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_OPTICAL_DEVICE = "OPTW"; 
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of an I/O operation to an optical device.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_OPTICAL_DEVICE_AND_ACTIVE = "OPTA"; 
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an OSI Communications Subsystem for OS/400 operation.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_OSI = "OSIW"; 
    
    /**
     * 正在等待请求
     * 
     * Constant indicating that a prestart job is waiting for a program start request.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_PRESTART = "PSRW";
    
    /**
     * Constant indicating that a job is waiting for the completion of printer output.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_PRINT = "PRTW";
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of printer output.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_PRINT_AND_ACTIVE = "PRTA";
    
    /**
     * Constant indicating that a job is waiting for the completion of a save file operation.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_SAVE_FILE = "SVFW";
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of a save file operation.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_SAVE_FILE_AND_ACTIVE = "SVFA"; 
    
    /** 
     * 正在等待选择 
     * 
     * Constant indicating that a job is waiting for the completion of a selection.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_SELECTION = "SELW"; 
    
    /**
     * Constant indicating that a job is waiting for a semaphore. 
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_SEMAPHORE = "SEMW"; 
    
    /**
     * 正在等待信号
     * 
     * Constant indicating that a job is waiting for a signal.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_SIGNAL = "SIGW";
    
    /**
     * Constant indicating that a job is waiting for the completion of 
     * an I/O operation to a tape device.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_TAPE_DEVICE = "TAPW";
    
    /**
     * Constant indicating that a job is waiting in a pool activity level for 
     * the completion of an I/O operation to a tape device.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_TAPE_DEVICE_AND_ACTIVE = "TAPA";
    
    /**
     * 正在等待线程
     * 
     * Constant indicating that a job is waiting for another thread to complete an operation.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_THREAD = "THDW"; 
    
    /**
     * 正在等待事件间隔
     * 
     * Constant indicating that a job is waiting for a time interval to end.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_TIME_INTERVAL = "TIMW"; 
    
    /**
     * 正在等待事件间隔
     * 
     * Constant indicating that a job is waiting in a pool activity level for a time interval to end.
     */
    public static final String ACTIVE_JOB_STATUS_WAIT_TIME_INTERVAL_AND_ACTIVE = "TIMA"; 

	
	private static Hashtable<String, String> activeStatushashtable = null;
	static {
		activeStatushashtable = new Hashtable<String, String>();
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_DEQUEUE , "正在等待出队");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_SELECTION , "正在等待选择");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_CONDITION , "正在等待调节");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_EVENT , "正在等待事件");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_DELAY , "正在等待延迟");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_DELAYED , "正在等待已延迟");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_PRESTART , "正在等待请求");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_SIGNAL , "正在等待信号");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_SUSPENDED_SYSTEM_REQUEST , "已暂挂 - 系统请求");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_DISPLAY , "正在等待工作站 I/O");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_TIME_INTERVAL , "正在等待事件间隔");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_TIME_INTERVAL_AND_ACTIVE , "正在等待事件间隔");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_JAVA , "正在等待 Java 程序");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_THREAD , "正在等待线程");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_RUNNING, "正在运行");
		activeStatushashtable.put(ACTIVE_JOB_STATUS_WAIT_MESSAGE, "(消息等待)该文件有一个消息，需要回复或采取动作");
	};
	
	/**
	 * 上述 为 IBM Toolbox for java 中所定义所有状态 只翻译的一部分
	 * 
	 */

	
	/**
	 * 通过类型获取类型中文描述
	 * 
	 * @param type
	 * @return
	 */
	public static String getTypeDescription(String type){
		return typehashtable.get(type);
	}
	
	/**
	 * 通过子类型获取子类型中文描述
	 * 
	 * @param subtype
	 * @return
	 */
	public static String getSubtypeDescription(String subtype){
		return subtypehashtable.get(subtype);
	}
	
	/**
	 * 通过状态获取状态中文描述
	 * 
	 * @param activeStatus
	 * @return
	 */
	public static String getActiveStatusDescription(String activeStatus){
		return activeStatushashtable.get(activeStatus);
	}

	/**
	 * 获取所有类型的 hashtable
	 * 
	 * @return the typehashtable
	 */
	public static Hashtable<String, String> getTypehashtable() {
		return typehashtable;
	}

	/**
	 * 设置所有类型的 hashtable
	 * 
	 * @param typehashtable the typehashtable to set
	 */
	public static void setTypehashtable(Hashtable<String, String> typehashtable) {
		JobConstantForAS400.typehashtable = typehashtable;
	}

	/**
	 * 获取所有子类型的 hashtable
	 * 
	 * @return the subtypehashtable
	 */
	public static Hashtable<String, String> getSubtypehashtable() {
		return subtypehashtable;
	}

	/**
	 * 设置所有子类型的 hashtable
	 * 
	 * @param subtypehashtable the subtypehashtable to set
	 */
	public static void setSubtypehashtable(
			Hashtable<String, String> subtypehashtable) {
		JobConstantForAS400.subtypehashtable = subtypehashtable;
	}

	/**
	 * 获取所有状态的 hashtable
	 * 
	 * @return the activeStatushashtable
	 */
	public static Hashtable<String, String> getActiveStatushashtable() {
		return activeStatushashtable;
	}

	/**
	 * 获取设置状态的 hashtable
	 * 
	 * @param activeStatushashtable the activeStatushashtable to set
	 */
	public static void setActiveStatushashtable(Hashtable<String, String> activeStatushashtable) {
		JobConstantForAS400.activeStatushashtable = activeStatushashtable;
	}
	
	/**
	 * 获取所有类型的枚举
	 * 
	 * @return typeEnumeration
	 */
	public static Enumeration<String> getTypeEnumeration(){
		return typehashtable.keys();
	}
	
	/**
	 * 获取所有子类型的枚举
	 * 
	 * @return subtypeEnumeration
	 */
	public static Enumeration<String> getSubtypeEnumeration(){
		return subtypehashtable.keys();
	}
	
	/**
	 * 获取所有状态的枚举
	 * 
	 * @return activeStatusEnumeration
	 */
	public static Enumeration<String> getActiveStatusEnumeration(){
		return activeStatushashtable.keys();
	}
	
		
}
