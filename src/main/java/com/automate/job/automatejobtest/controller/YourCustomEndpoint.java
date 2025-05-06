package com.automate.job.automatejobtest.controller;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
@Component
public class YourCustomEndpoint {

    private final Counter myCounter;

    public YourCustomEndpoint(MeterRegistry registry) {
        myCounter = Counter.builder("my.custom.counter")
                .description("Counts something important")
                .register(registry);
    }

    public void incrementCounter() {
        myCounter.increment();
    }
}
