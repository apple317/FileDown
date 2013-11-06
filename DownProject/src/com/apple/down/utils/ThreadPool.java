package com.apple.down.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

/**
 * 文件下载线程池类
 * @author 胡少平
 *
 */
public final class ThreadPool {
	
    private static ThreadPool instance = null;
    public static final int SYSTEM_BUSY_TASK_COUNT = 150;
    //文件下载线程数
    public static int worker_num = 1;

    public static boolean systemIsBusy = false;
    public static Object mLock = new Object();
    public static List<Task> taskQueue = Collections.synchronizedList(new LinkedList<Task>());
    public PoolWorker[] workers;
    public static Task curTask;
    private ThreadPool() {
        workers = new PoolWorker[worker_num];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new PoolWorker();
        }
    }

    public static synchronized ThreadPool getInstance() {
        if (instance == null){
        	synchronized (mLock) {
				if(instance == null){
					return new ThreadPool();
				}
			}
        }
        return instance;
    }
   
    /**
     * 添加下载线程数
     * @param newTask
     */
    public void addTask(Task newTask) {
        synchronized (taskQueue) {
        	taskQueue.add(newTask);
            taskQueue.notifyAll();
        }
    }
    
    /**
     * 删除所有下载线程数
     */
    public synchronized void destroy() {
        for (int i = 0; i < worker_num; i++) {
            workers[i].stopWorker();
            workers[i] = null;
        }
        taskQueue.clear();
    }
    
    /**
     *删除
     */
    public void stopTask(){
    	curTask.stop();
    }
    
    private class PoolWorker extends Thread {
        private boolean isRunning = true;
        private boolean isWaiting = true;

        public PoolWorker() {
            start();
        }

        public void stopWorker() {
            this.isRunning = false;
        }

        public boolean isWaiting() {
           return this.isWaiting;
        }

        public void run() {
        	this.setPriority(Thread.MIN_PRIORITY);
            while (isRunning) {
            	Task r = null;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException ie) {
                            Log.e("ThreadPool", "Wait Exception", ie);
                        }
                    }
                    r = (Task) taskQueue.remove(0);
                    curTask=r;
                }
                if (r != null) {
                    isWaiting = false;
                    try {
                    	r.run();
                    } catch (Exception e) {
                    	Log.e("ThreadPool", "Task Running Exception", e);
                    }
                    isWaiting = true;
                    r = null;
                }
            }
        }
    }
    

}