package com.afunms.discovery;

import java.util.LinkedList;

/**
    线程池是一组线程，限制执行任务的线程数
*/
public class ThreadPool extends ThreadGroup {

    private boolean isAlive;
    private LinkedList taskQueue;
    private int threadID;
    private static int threadPoolID;

    /**
        创建新的线程池，numThreads是池中的线程数
    */
    public ThreadPool(int numThreads) {
        super("ThreadPool-" + (threadPoolID++));
        setDaemon(true);

        isAlive = true;

        taskQueue = new LinkedList();
        for (int i=0; i<numThreads; i++) {
            new PooledThread().start();
        }
    }
    /**
        请求新任务。人物在池中下一空闲线程中运行，任务按收到的顺序执行
    */
    public synchronized void runTask(Runnable task) {
        if (!isAlive) {
            throw new IllegalStateException();//线程被关则抛出IllegalStateException异常
        }
        if (task != null) {
            taskQueue.add(task);
            notify();
        }

    }


    protected synchronized Runnable getTask()
        throws InterruptedException
    {
        while (taskQueue.size() == 0) {
            if (!isAlive) {
                return null;
            }
            wait();
        }
        return (Runnable)taskQueue.removeFirst();
    }


    /**
        关闭线程池，所有线程停止，不再执行任务
    */
    public synchronized void close() {
        if (isAlive) {
            isAlive = false;
            taskQueue.clear();
            interrupt();
        }
    }


    /**
        关闭线程池并等待所有线程完成，执行等待的任务
    */
    public void join() {
        //告诉等待线程线程池已关
        synchronized (this) {
            isAlive = false;
            notifyAll();
        }

        // 等待所有线程完成
        Thread[] threads = new Thread[activeCount()];
        int count = enumerate(threads);
        for (int i=0; i<count; i++) {
            try {
                threads[i].join();
            }
            catch (InterruptedException ex) { }
        }
    }
    
    
private class PooledThread extends Thread {


        public PooledThread() {
            super(ThreadPool.this,
                "PooledThread-" + (threadID++));
        }


        public void run() {
            while (!isInterrupted()) {

                // 得到任务
                Runnable task = null;
                try {
                    task = getTask();
                }
                catch (InterruptedException ex) { }

                // 若getTask()返回null或中断，则关闭此线程并返回
                if (task == null) {
                    return;
                }

                // 运行任务，吸收异常
                try {
                    task.run();
                }
                catch (Throwable t) {
                    uncaughtException(this, t);
                }
            }
        }
    }
}
    

