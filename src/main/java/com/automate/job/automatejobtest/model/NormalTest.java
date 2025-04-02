package com.automate.job.automatejobtest.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.*;

@Slf4j
public class NormalTest extends AbstractJobDefine{
    public NormalTest(Event event, String jobName, PulsarClient pulsarClient, int timeout, PulsarAdmin pulsarAdmin) {
        super(event, jobName, pulsarClient, timeout, pulsarAdmin);
    }

    @Override
    public void run(PulsarClient pulsarClient, PulsarAdmin admin) throws Exception {
        System.out.println("##########-PulsarClient <UNK> PulsarAdmin <UNK>");
        String topic ="persistent://public/default/test-normal";
        int number = 10;
        Producer<byte[]> producer = pulsarClient.newProducer()
                .topic(topic).create();
        for (int i = 0; i < number; i++) {
            String msg = "hello pulsar-user " + i;
            MessageId send = producer.newMessage().value(msg.getBytes()).send();
            log.info("Sending message {} ", send.toString());
        }
        producer.close();
        Consumer consumer = pulsarClient.newConsumer()
                .topic(topic)
                .subscriptionName("test-normal")
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
