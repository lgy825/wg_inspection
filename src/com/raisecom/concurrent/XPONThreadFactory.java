package com.raisecom.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class XPONThreadFactory implements ThreadFactory {
    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;
    private String threadName = "thread";

    public XPONThreadFactory(String name)
    {
        SecurityManager s = System.getSecurityManager();
        group = (s != null)? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "xPON_"+ name +"_pool-" +
                poolNumber.getAndIncrement();

    }

    /**
     * 创建一个新线程
     * ThreadPoolExcecutor.addThread调用
     */
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + "-"+threadName+"-"
                + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

    public void setThreadName(String threadName)
    {
        this.threadName = threadName;
    }
}
