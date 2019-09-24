package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.demo.utils.AesECBUtil;
import com.example.demo.utils.RSACoderUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import com.tiefan.infra.corekit.security.AesECBUtil;
import java.util.Map;

/**
 * 机构接受贷超的请求并做验签 demo
 * 需要提供公钥给到贷超
 */

@Controller
@RequestMapping("/test1")
public class AgencyAction {
    //贷超提供给机构的公钥
    private static final String pubKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIW5aYjYEc5sHtKSVgzbk3hLo2sOim84vkKLa6G0FopjHzYj48S9TLM8q9pOFgPdUo7Nb0kei9I6Cf4kzC0MqskCAwEAAQ==";

    /**
     * 机构验签方法
     *
     * @throws \Exception
     */
    @RequestMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public void verify() {
        //示例
        String jsonString = "{\"appId\":\"156514348756227557598\",\"bizParams\":\"{\\\"md5\\\":\\\"65a121ce3ab245453b39b232d267a7bd\\\",\\\"name\\\":\\\"石头\\\"}\",\"productId\":656,\"sign\":\"fhQDyie8Tc7EA2ZtPiTG0Eqi/phOpv9wwz+LJ6PvxOdVHj6K0A43Fzt1ZMKlt1VSNR8ti0eFrosXabmKRFUfwg==\",\"timestamp\":\"1565776786615\"}";
        Map<String, String> signMap = JSON.parseObject(jsonString, new TypeReference<Map<String, String>>() {
        });
        String sign = signMap.get("sign");
        signMap.remove("sign");
        boolean verifyRes = RSACoderUtil.verify(signMap, pubKey, sign);
        System.out.printf(verifyRes ? "通过" : "不通过");
    }

    public static void main(String[] args) throws Exception {
        AgencyAction agencyAction = new AgencyAction();
        agencyAction.verify();
    }
}
