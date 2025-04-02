package com.automate.job.automatejobtest.model;

/**
 * @Description : 业务异常的错误代码接口
 * @Param :
 * @Return :
 * @Author : l-jiahui
 * @Date : 2020-10-11
 */

public interface BizExceptionCode {

    /**
     * @Description : 获取错误代码
     * @Param : []
     * @Return : java.lang.String
     * @Author : l-jiahui
     * @Date : 2020-10-11
     */
    String getCode();

    /**
     * @Description : 获取错误信息
     * @Param : []
     * @Return : java.lang.String
     * @Author : l-jiahui
     * @Date : 2020-10-11
     */
    String getMessage();

}

