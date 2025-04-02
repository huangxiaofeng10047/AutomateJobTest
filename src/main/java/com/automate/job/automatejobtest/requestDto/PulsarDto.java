package com.automate.job.automatejobtest.requestDto;

import lombok.Data;

@Data
public class PulsarDto {
    String pulsarServiceUrl;
    String pulsarHttpServiceUrl;
    String authToken;
}
