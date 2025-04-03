package com.automate.job.automatejobtest;

import com.automate.job.automatejobtest.model.TestCase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutomateJobTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomateJobTestApplication.class, args);
		ThreadUtils.printThreadPoolStatus(TestCase.EXECUTOR);
	}

}
