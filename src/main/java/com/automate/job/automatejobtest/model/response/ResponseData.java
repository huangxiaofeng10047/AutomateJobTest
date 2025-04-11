package com.automate.job.automatejobtest.model.response;

import lombok.Data;

@Data
public class ResponseData<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResponseData<T> success(T data) {
        ResponseData<T> response = new ResponseData<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ResponseData<T> error(Integer code, String message) {
        ResponseData<T> response = new ResponseData<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
