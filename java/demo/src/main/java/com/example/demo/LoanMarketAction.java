package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.demo.utils.EntityUtils;
import com.example.demo.utils.HttpUtils;
import com.example.demo.utils.RSACoderUtil;
import com.example.demo.vo.BaseRequest;
import com.example.demo.vo.BindCardRequestVo;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

/**
 * 回调接口 机构调用贷超 demo
 */
@Controller
@RequestMapping("/test")
public class LoanMarketAction extends HttpServlet {

    //请求method
    private static final String BIND_CARD_METHOD = "BIND_CARD_CALLBACK";
    //贷超提供给机构的appid
    private static final String AppID = "156352765992451452585";
    private static final String pubKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJKMe3dLkQ7iS6h/f3lsFnPNS6tlZBTIq3OrwIn9NgkT\n" +
            "F+aElRSGw288ffaZ/5Oepby4tOD1D92P4mSAdO7kwG0CAwEAAQ==";

    private static final Integer ProductID = 620;
    //机构的私钥
    private static final String priKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAkox7d0uRDuJLqH9/eWwWc81Lq2Vk\n" +
            "FMirc6vAif02CRMX5oSVFIbDbzx99pn/k56lvLi04PUP3Y/iZIB07uTAbQIDAQABAkAMPiY2Yh69\n" +
            "MpSFgBlDKI97nyP6Lp88yULhh/Cu1nOEMG7HyBA/pkewPJetmhBAtvt4UI/tDTt54euNEmaG4nq1\n" +
            "AiEAxwsmgpVmobYRujO+h2WTq/V+UtN0uzF+xY+OjggqivcCIQC8e9kjS86p28E8Yz/Ro2T555VE\n" +
            "8MjT5S/D/fQ2PHsyuwIgEhhbaee3KuBogCsQGTMM18c7sr/yjsoTIlbIuPTZGUUCIA9G+pKh0l5y\n" +
            "MXdzk/iqBcU7wB2WZrDGItj//Ito25OBAiA9Sa/p+fIx4YjWZlxS7UGl7eWalUvrufBa3gV7cWDD\n" +
            "Sg==";

    private static final String URL = "http://XXXXX.XXXXXXX.XXXXX/v1/callback";

    @RequestMapping(value = "/dopost", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public void doPost() {
        BindCardRequestVo bindCardRequestVo = new BindCardRequestVo();
        bindCardRequestVo.setOrderNo("654995446866462865666");
        bindCardRequestVo.setStatus(2);
        bindCardRequestVo.setCardType(1);
        bindCardRequestVo.setCardNo("6228480458525401071");

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setMethod(BIND_CARD_METHOD);
        baseRequest.setSignType(1);
        baseRequest.setBizParams(JSON.toJSONString(bindCardRequestVo));
        baseRequest.setBizEnc(0);
        baseRequest.setProductId(ProductID);
        baseRequest.setAppId(AppID);
        baseRequest.setTimestamp(String.valueOf(System.currentTimeMillis()));

        Map<String, String> map = EntityUtils.entityToMap(baseRequest);
        String sign;
        try {
            sign = RSACoderUtil.getSign(map, priKey);
            baseRequest.setSign(sign);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            //本示例中的POST方法仅作为示例工具，安全起见，请合作方确认或者自行实现
            String response = HttpUtils.doPostByJson(URL, headers, baseRequest);
            System.out.printf(response);
//            ----------------------自测验签--------------------------------
            /**
             * 请仔细查看以上的加签和验签规则，验签失败请自己检查自己的加签规则，（构造业务参数（业务参数为jsonString作为整体参数加签）和基础参数并对其按key值按ASCII进⾏排序）这一点非常重要，烦请不要在群里一直咨询关于加签、验签的问题，谢谢配合
             */
            Map<String, String> signMap = JSON.parseObject(JSON.toJSONString(baseRequest), new TypeReference<Map<String, String>>() {
            });
            signMap.remove("sign");
            boolean verifyRes = RSACoderUtil.verify(signMap, pubKey, baseRequest.getSign());
            System.out.printf(verifyRes ? "通过" : "不通过");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        LoanMarketAction loanMarketAction = new LoanMarketAction();
        loanMarketAction.doPost();
    }
}
