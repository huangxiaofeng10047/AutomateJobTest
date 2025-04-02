package com.automate.job.automatejobtest.controller;


import com.automate.job.automatejobtest.model.*;
import com.automate.job.automatejobtest.requestDto.PulsarDto;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
public class TestPulsarClient {

    @PostMapping("/testpulsar")
    public void testPulsarClient(@RequestBody PulsarDto pulsarDto) {
        // 配置 Pulsar 的服务地址和认证 Token
        try{
                String pulsarServiceUrl=pulsarDto.getPulsarServiceUrl();
                String pulsarHttpServiceUrl=pulsarDto.getPulsarHttpServiceUrl();
                String authToken=pulsarDto.getAuthToken();
            // 实例化 PulsarClient
            PulsarClient pulsarClient = PulsarClient.builder()
                    .serviceUrl(pulsarServiceUrl)
                    .authentication(
                            org.apache.pulsar.client.api.AuthenticationFactory.token(authToken)
                    )
                    .build();

            // 实例化 PulsarAdmin
            PulsarAdmin admin = PulsarAdmin.builder()
                    .serviceHttpUrl(pulsarHttpServiceUrl)
                    .authentication(
                            org.apache.pulsar.client.api.AuthenticationFactory.token(authToken)
                    )
                    .build();

            System.out.println("PulsarClient 和 PulsarAdmin 已成功实例化！");
            Event event= new DefaultEvent();
            AbstractJobDefine job5 =
                    new NormalTest(event, "普通消费测试", pulsarClient, 20, admin);
            CompletableFuture<Void> c5 = CompletableFuture.runAsync(job5::start, TestCase.EXECUTOR);
        AbstractJobDefine job6 = new FailoverConsumerTest(event,"schema测试",pulsarClient,20,admin);
        CompletableFuture<Void> c6 = CompletableFuture.runAsync(job6::start, TestCase.EXECUTOR);
//        AbstractJobDefine job7 = new NormalTest(event,"vlogs test",pulsarClient,20, admin);
//        CompletableFuture<Void> c7 = CompletableFuture.runAsync(job7::start, TestCase.EXECUTOR);

            CompletableFuture<Void> all = CompletableFuture.allOf( c5,c6);
            all.whenComplete((___, __) -> {
                event.FinshAll();
                pulsarClient.closeAsync();
                admin.close();
            }).get();
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }

    }

}
