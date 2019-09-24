package com.example.demo.vo;

import lombok.Data;

@Data
public class AgentBaseRequest {
    /**
     * @description API请求的签名
     */
    private String sign;
    /**
     * @description 请求的业务数据，此处数据格式为Json封装。具体参数说明见详细接口
     */
    private String bizParams;

    /**
     * @description 13位时间戳，精确到秒
     */
    private String timestamp;
}
