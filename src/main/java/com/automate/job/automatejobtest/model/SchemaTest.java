package com.automate.job.automatejobtest.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.*;
@Slf4j
public class SchemaTest extends AbstractJobDefine{
    public SchemaTest(Event event, String jobName, PulsarClient pulsarClient, int timeout, PulsarAdmin pulsarAdmin) {
        super(event, jobName, pulsarClient, timeout, pulsarAdmin);
    }

    @Override
    public void run(PulsarClient pulsarClient, PulsarAdmin admin) throws Exception {
        String topic ="persistent://public/default/test-schema";
        int number = 10;


// 指定消费者的 schema
        Producer<User> producer = pulsarClient.newProducer(Schema.AVRO(User.class))
                .topic(topic)
                .create();

        for (int i = 0; i < number; i++) {
            String msg = "hello pulsar-user " + i;
            User user=new User();
            user.setName("user"+i);
            user.setAge(i);
            MessageId send = producer.newMessage().value(user).send();
            log.info("Sending message {} ", send.toString());
        }
        producer.close();
        // 指定生产者的 schema
        Consumer<User> consumer = pulsarClient.newConsumer(Schema.AVRO(User.class))
                .subscriptionType(SubscriptionType.Shared)
                .topic(topic)
                .subscriptionName("test-schema")
                .subscribe();
        int receiveNumber=0;
        for (int i =0;i < number;i++) {
            Message<User> msg = consumer.receive();
            log.info("Received message {} ", msg.getValue().getName()+":"+msg.getValue().getAge());
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
