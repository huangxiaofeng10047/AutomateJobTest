package com.automate.job.automatejobtest.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DefaultEvent implements Event {
    private final AtomicInteger totalJobs = new AtomicInteger(0);
    private final AtomicInteger completedJobs = new AtomicInteger(0);
    private final AtomicInteger failedJobs = new AtomicInteger(0);
    private final List<String> completionMessages = Collections.synchronizedList(new ArrayList<>());
    private final List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());
    private final List<TestCaseModel> testCases =Collections.synchronizedList(new ArrayList<>());
    private final List<TestCaseModel> testCasesErr =Collections.synchronizedList(new ArrayList<>());

    @Override
    public void addJob() {
        totalJobs.incrementAndGet();
    }



    @Override
    public void FinishOne(String jobName, String message) {
        completedJobs.incrementAndGet();
        completionMessages.add(String.format("任务名称 '%s'  %s", jobName, message));
        testCases.add(TestCaseModel.builder().name(jobName).message(message).build());
        log.info("任务名称 '{}' cost: {}", jobName, message);
        checkAllJobsCompleted();
    }

    @Override
    public void oneException(AbstractJobDefine jobDefine, Exception exception) {
        failedJobs.incrementAndGet();
        String errorMessage = String.format("任务名称 '%s' failed: %s", jobDefine.getJobName(), exception.getMessage());
        errorMessages.add(errorMessage);
//        testCases.removeIf(testCase -> testCase.getName().equals(jobDefine.getJobName()));
        testCasesErr.add(TestCaseModel.builder().name(jobDefine.getJobName()).message(exception.getMessage()).build());
        log.error(errorMessage, exception);

        checkAllJobsCompleted();
    }

    @Override
    public void FinshAll() {
//        cleanup();
        log.info("=== 所有作业执行完成 ===");
//        log.info("执行总结: {}", summary);
        log.info("总作业数: {}", totalJobs.get());
        log.info("成功数量: {}", completedJobs.get());
        log.info("失败数量: {}", failedJobs.get());

        // 计算成功率
        double successRate = totalJobs.get() > 0
                ? (completedJobs.get() * 100.0 / totalJobs.get())
                : 0;
        log.info(String.format("成功率: %.2f%%", successRate));
        // 如果有失败的作业，输出错误详情
        if (!errorMessages.isEmpty()) {
            log.error("失败作业详情:");
            errorMessages.forEach(log::error);
        }

        // 触发清理工作（如果需要）
//        cleanup();
    }

    @Override
    public List<TestCaseModel> toJsonString() {
        if (!testCasesErr.isEmpty()) {
            testCasesErr.stream().forEach(
                    testCaseModel -> {
                        testCases.removeIf(testCaseModel1 -> testCaseModel.getName().equals(testCaseModel1.getName()));
                        testCases.add(testCaseModel);
                    }
            );
            testCasesErr.clear();
        }
        return testCases;
    }

    @Override
    public void clear() {
       cleanup();
    }

    private void cleanup() {
        // 可以在这里添加资源清理的代码
        completionMessages.clear();
        errorMessages.clear();
        testCases.clear();
    }

    private void checkAllJobsCompleted() {
        int completed = completedJobs.get() + failedJobs.get();
        if (completed == totalJobs.get()) {
            summarizeResults();
        }
    }

    private void summarizeResults() {
        log.info("=== Job Execution Summary ===");
        log.info("Total jobs: {}", totalJobs.get());
        log.info("Completed jobs: {}", completedJobs.get());
        log.info("Failed jobs: {}", failedJobs.get());

        if (!completionMessages.isEmpty()) {
            log.info("=== Successful Jobs ===");
            completionMessages.forEach(log::info);
        }

        if (!errorMessages.isEmpty()) {
            log.info("=== Failed Jobs ===");
            errorMessages.forEach(log::info);
        }
    }

    public int getTotalJobs() {
        return totalJobs.get();
    }

    public int getCompletedJobs() {
        return completedJobs.get();
    }

    public int getFailedJobs() {
        return failedJobs.get();
    }

    public List<String> getCompletionMessages() {
        return new ArrayList<>(completionMessages);
    }

    public List<String> getErrorMessages() {
        return new ArrayList<>(errorMessages);
    }
}
