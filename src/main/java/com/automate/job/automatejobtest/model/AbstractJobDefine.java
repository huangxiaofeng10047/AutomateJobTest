package com.automate.job.automatejobtest.model;


import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.util.StopWatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Data
public abstract class AbstractJobDefine {
    private Event event;
    private String jobName;
    private PulsarClient pulsarClient;
    private int timeout;
    private PulsarAdmin pulsarAdmin;

    //构造方法

    public AbstractJobDefine(Event event, String jobName, PulsarClient pulsarClient, int timeout, PulsarAdmin pulsarAdmin) {
        this.event = event;
        this.jobName = jobName;
        this.pulsarClient = pulsarClient;
        this.timeout = timeout;
        this.pulsarAdmin = pulsarAdmin;
    }

    public void start(){
        event.addJob();
        try {
            CompletableFuture.runAsync(() -> {
                StopWatch watch = new StopWatch();
                try {
                    watch.start(jobName);
                    run(pulsarClient, pulsarAdmin);
                } catch (Exception e) {
                    event.oneException(this, e);
                } finally {
                    watch.stop();
                    event.FinishOne(jobName, StrUtil.format("cost: {}s", watch.getTotalTimeSeconds()));
                }
            }, TestCase.EXECUTOR).get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            event.oneException(this, e);
        }
    }
    /** run busy code
     * @param pulsarClient pulsar client
     * @param admin pulsar admin client
     * @throws Exception e
     */
    public abstract void run(PulsarClient pulsarClient, PulsarAdmin admin) throws Exception;
}
