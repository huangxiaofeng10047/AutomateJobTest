package com.automate.job.automatejobtest.controller;


import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.automate.job.automatejobtest.model.*;
import com.automate.job.automatejobtest.model.response.ResponseData;
import com.automate.job.automatejobtest.requestDto.PulsarDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.web.bind.annotation.*;
import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@RestController
@Slf4j
public class TestPulsarClient {
    // 用于存储任务状态
    private static final Map<String, CompletableFuture<ResponseData<Object>>> taskMap = new HashMap<>();
    @PostMapping("/startTestPulsar")
    @ResponseBody
    public ResponseData<String> startTestPulsarClient(@RequestBody PulsarDto pulsarDto) {
        String taskId = UUID.randomUUID().toString();
        CompletableFuture<ResponseData<Object>> future = CompletableFuture.supplyAsync(() -> {
            // 配置 Pulsar 的服务地址和认证 Token
            String result = "";
            PulsarClient pulsarClient = null;
            PulsarAdmin admin = null;
            try {
                String pulsarServiceUrl = pulsarDto.getPulsarServiceUrl();
                String pulsarHttpServiceUrl = pulsarDto.getPulsarHttpServiceUrl();
                String authToken = pulsarDto.getAuthToken();
                // 实例化 PulsarClient
                pulsarClient = PulsarClient.builder()
                        .serviceUrl(pulsarServiceUrl)
                        .authentication(
                                org.apache.pulsar.client.api.AuthenticationFactory.token(authToken)
                        )
                        .operationTimeout(60, TimeUnit.SECONDS)      // 操作超时时间
                        .connectionTimeout(60, TimeUnit.SECONDS)     // 连接超时时间
                        .keepAliveInterval(30, TimeUnit.SECONDS)     // 保活间隔
                        .maxNumberOfRejectedRequestPerConnection(100)
                        .enableTcpNoDelay(true)
                        // 启用TCP无延迟
//                     .enableTransaction(true)                    // 启用事务支持
                        .startingBackoffInterval(100, TimeUnit.MILLISECONDS)
                        .maxBackoffInterval(60, TimeUnit.SECONDS)
                        .build();

                // 实例化 PulsarAdmin
                admin = PulsarAdmin.builder()
                        .serviceHttpUrl(pulsarHttpServiceUrl)
                        .authentication(
                                org.apache.pulsar.client.api.AuthenticationFactory.token(authToken)
                        )
                        .build();

                System.out.println("PulsarClient 和 PulsarAdmin 已成功实例化！");
                Event event = new DefaultEvent();
                event.clear();
                AbstractJobDefine job5 =
                        new NormalTest(event, "普通消费测试", pulsarClient, 600, admin);
                CompletableFuture<Void> c5 = CompletableFuture.runAsync(job5::start, TestCase.EXECUTOR);
                AbstractJobDefine job6 = new FailoverConsumerTest(event, "失败转移模式的测试", pulsarClient, 600, admin);
                CompletableFuture<Void> c6 = CompletableFuture.runAsync(job6::start, TestCase.EXECUTOR);
                AbstractJobDefine job7 = new SchemaTest(event, "schema测试", pulsarClient, 600, admin);
                CompletableFuture<Void> c7 = CompletableFuture.runAsync(job7::start, TestCase.EXECUTOR);

                CompletableFuture<Void> all = CompletableFuture.allOf(c5, c6, c7);
                all.whenComplete((___, __) -> {
                    event.FinshAll();
//                pulsarClient.closeAsync();
//                admin.close();
                }).get();
//            // 最后才关闭连接
//            if (pulsarClient != null) {
//                pulsarClient.closeAsync();
//            }
//            if (admin != null) {
//                admin.close();
//            }
                return ResponseData.success(event.toJsonString());

            } catch (Exception e) {
                log.error("测试执行失败", e);
                return ResponseData.error(500, "测试执行失败: " + e.getMessage());
            } finally {
                try {
                    if (pulsarClient != null) {
                        pulsarClient.closeAsync();
                    }
                    if (admin != null) {
                        admin.close();
                    }
                } catch (Exception e) {
                    log.error("关闭连接失败", e);
                }
            }
        });
        taskMap.put(taskId, future);
        return ResponseData.success(taskId);
    }

    @GetMapping("/checkTestPulsarStatus/{taskId}")
    @ResponseBody
    public ResponseData<Object> checkTestPulsarStatus(@PathVariable String taskId) {
        CompletableFuture<ResponseData<Object>> future = taskMap.get(taskId);
        if (future == null) {
            return ResponseData.error(404, "任务 ID 不存在");
        }
        if (future.isDone()) {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("获取任务结果失败", e);
                return ResponseData.error(500, "获取任务结果失败: " + e.getMessage());
            } finally {
                taskMap.remove(taskId);
            }
        } else {
            return ResponseData.success("任务正在执行中");
        }
    }



}
