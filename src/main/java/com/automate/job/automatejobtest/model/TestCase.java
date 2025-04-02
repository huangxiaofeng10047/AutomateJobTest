package com.automate.job.automatejobtest.model;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TestCase {
     public static Executor EXECUTOR = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(), // 根据CPU核心数动态分配
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true); // 设置为守护线程
                    thread.setName("CustomExecutorThread"); // 为线程命名
                    return thread;
                }
            }
    );
}
