package com.automate.job.automatejobtest.model;

import lombok.Data;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
@Data
public class TestCase {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;
    public static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
             CORE_POOL_SIZE,
             MAX_POOL_SIZE,
             KEEP_ALIVE_TIME,
             TimeUnit.SECONDS,
             new ArrayBlockingQueue<>(QUEUE_CAPACITY),
             new ThreadPoolExecutor.CallerRunsPolicy());
}
