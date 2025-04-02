package com.automate.job.automatejobtest.model;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.*;

@Slf4j
public class FailoverConsumerTest extends AbstractJobDefine{
    public FailoverConsumerTest(Event event, String jobName, PulsarClient pulsarClient, int timeout, PulsarAdmin pulsarAdmin) {
        super(event, jobName, pulsarClient, timeout, pulsarAdmin);
    }

    @Override
    public void run(PulsarClient pulsarClient, PulsarAdmin admin) throws Exception {
        String topic ="persistent://public/default/test-failover";
        // 构建生产者
        Producer<byte[]> producer=pulsarClient.newProducer()
                .topic(topic)
                .enableBatching(false)  // 禁用batch
                .create();
// 发送消息时设置key
        int number=10;
        for (int i = 0; i < number; i++) {
            String msg = "hello pulsar-user " + i;
            MessageId send = producer.newMessage().value(msg.getBytes()).send();
            log.info("Sending message {} ", send.toString());
        }
        producer.close();
        // 构建消费者
        Consumer<byte[]> consumer = pulsarClient.newConsumer()
                // topic完整路径，格式为persistent://集群（租户）ID/命名空间/Topic名称，从【Topic管理】处复制
                .topic(topic)
                // 需要在控制台Topic详情页创建好一个订阅，此处填写订阅名
                .subscriptionName("test-failover")
                // 声明消费模式为Failover模式
                .subscriptionType(SubscriptionType.Failover)
                .subscribe();
        int receiveNumber=0;
        for (int i =0;i < number;i++) {
            Message msg = consumer.receive();
            log.info("Received message {} ", msg.getMessageId().toString());
            consumer.acknowledge(msg);
            receiveNumber++;
        }
        if (number!=receiveNumber){
            log.error("Received number of messages not equal to received number of messages");
            throw  new BizException("消费者获得的消息与发送消息不一致");
        }
        consumer.close();

    }
}
