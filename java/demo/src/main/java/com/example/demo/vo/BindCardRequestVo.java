package com.example.demo.vo;

import lombok.Data;

@Data
public class BindCardRequestVo {
    private String orderNo;
    private int status;
    private int cardType;
    private String cardNo;
    private String reason;
}
