package com.raisecom.concurrent;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public abstract class XPONThreadTask implements Runnable {
    public void run() {
        execute();
    }

    public abstract void execute();

    /**
     * 线程名称
     * @return
     */
    public abstract String getThreadName();

}
