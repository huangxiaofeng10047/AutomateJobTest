package com.automate.job.automatejobtest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestCaseModel {
    private String name;
    private String message;
}
