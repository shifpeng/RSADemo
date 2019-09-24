package com.example.demo.vo;

import lombok.Data;

@Data
public class BaseResponse {
    private boolean success;
    private int code;
    private String msg;
}
