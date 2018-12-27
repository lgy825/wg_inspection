package com.raisecom.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class XPONThreadPool {
    private ThreadPoolExecutor executor;
    private XPONThreadFactory		threadFactory;

    /**
     * 获取一个新的线程池，该线程池名字和线程池的大小时固定的
     * @param poolName	线程池名
     * @param threadCount	线程池的大小 （并发量）
     * @return
     */
    public static XPONThreadPool getNewPool(String poolName, int threadCount)
    {
        return new XPONThreadPool(poolName, threadCount);
    }

    public XPONThreadPool(String poolName, int threadCount)
    {
        threadFactory = new XPONThreadFactory(poolName);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount, threadFactory);
    }

    public void addTask(Runnable task)
    {
        execute(task);
    }

    public void addTask(XPONThreadTask task)
    {
        if(task.getThreadName() != null)
            threadFactory.setThreadName(task.getThreadName());
        execute(task);
    }

    private void execute(Runnable task){
        try{

            executor.execute(task);
        }catch(Exception e){

        }
    }

    public Future submitTask(Callable task)
    {
        return submit(task);
    }

    private Future submit(Callable task) {
        // TODO Auto-generated method stub
        try{
            return executor.submit(task);
        }catch(Exception e){
            return null;
        }
    }
    /**
     * 关闭线程池
     * 线程池用完后需关闭，否则会占用系统资源
     */
    public void shutDown()
    {
        executor.shutdown();
    }

    public ThreadPoolExecutor getExecutor(){
        return this.executor;
    }

}
