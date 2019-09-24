package com.example.demo.vo;

import lombok.Data;

@Data
public class BaseRequest {
    /**
     * @description 要请求的API方法名称
     */
    private String method;
    /**
     * @description API请求的签名
     */
    private String sign;
    /**
     * @description 1:RSA
     */
    private Integer signType;
    /**
     * @description 请求的业务数据，此处数据格式为Json封装。具体参数说明见详细接口
     */
    private String bizParams;
    /**
     * @description bizParams加密方式（0不加密，1加密:采用DES加密算法）
     */
    private Integer bizEnc;
    /**
     * @description RSA加密后的密钥（bizEnc为1时为必传）
     */
    private String aesKey;
    /**
     * @description 分配给应用的唯一标识
     */
    private String appId;
    /**
     * @description 产品ID
     */
    private Integer productId;
    /**
     * @description 13位时间戳，精确到秒
     */
    private String timestamp;
    /**
     * @description 回调url
     */
    private String callbackUrl;
}
