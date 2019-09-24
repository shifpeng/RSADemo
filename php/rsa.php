<?php
/**
 * 获取需要签名的内容
 * @param $request_param
 * @return string
 */
function getSignContent($request_param)
{
    ksort($request_param);
    $data = '';
    foreach ($request_param as $key => $val) {
        if ('' === strval($val) || 'sign' == $key) {
            continue;
        }
        $data .= "{$key}={$val}&";
    }
    $data = rtrim($data, '&');
    return $data;
}

/**
 * 利用约定数据和私钥生成数字签名
 * @param string $data  待签数据
 * @return bool|string  返回签名
 */
function getSign($data = '')
{
    if (empty($data)) {
        return False;
    }

    $private_key = <<<eof
-----BEGIN RSA PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAjF/Q5GklIHI83su58HJApZidmU7w
ZjS2hLdz6t25izzikT73uYI37C8dcG1FyxnJp1LSxajmM8bzT/mt25UDZwIDAQABAkA+N02Hnw6k
hd1yxgsAhjMe8jiPtYwZUK3Avuqyo1lUW5Z+4pY9yM+6TVf/LOk6CUY9MRkN/x7/L+iDJb+W680J
AiEA+a/riR3sQsObhTGRniSqaOS/XEKk316a7EfU9V8r5TMCIQCP7GCHCzSuQ2dSprA4KY4SgK0N
+fMfcg70fPYixv6A/QIhAO+LmmI7Rf6roZoAe18IeEEYLIr8GSd/oQxMwgACpZ9HAiAxK3qW6Hcp
ChIkpJoTte6514hH/BrZpWgCkyasHvwYKQIhAPcUkk+WY2f0D+Ujms+nGS9IUVqYdWZXr0SplIg7
NueK
-----END RSA PRIVATE KEY-----
eof;
    if (empty($private_key)) {
        throw new UserException("Private Key error!");
    }

    $pkeyid = openssl_get_privatekey($private_key);
    if (empty($pkeyid)) {
        throw new UserException("private key resource identifier False!");
    }

    $verify = openssl_sign($data, $signature, $pkeyid, OPENSSL_ALGO_MD5);
    openssl_free_key($pkeyid);
    return base64_encode($signature);
}

/**
 * 利用公钥和数字签名以及约定数据验证合法性
 * @param string $data 待验证数据
 * @return string -1:error验证错误 1:correct验证成功 0:incorrect验证失败
 */
function checkSign($data = '')
{
    if (empty($data)) {
        throw new UserException("参数缺失");
    }
    $signature = base64_decode($data['sign']);
    $data = getSignContent($data);

    $public_key = <<<eof
-----BEGIN PUBLIC KEY-----
MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIxf0ORpJSByPN7LufByQKWYnZlO8GY0toS3c+rduYs8
4pE+97mCN+wvHXBtRcsZyadS0sWo5jPG80/5rduVA2cCAwEAAQ==
-----END PUBLIC KEY-----
eof;
    if (empty($public_key)) {
        throw new UserException('Public Key error!');
    }

    $pkeyid = openssl_get_publickey($public_key);
    if (empty($pkeyid)) {
        throw new UserException('public key resource identifier False!');
    }

    $ret = openssl_verify($data, $signature, $pkeyid, OPENSSL_ALGO_MD5);
    if (!$ret) {
        return '签名验证失败';
    } else {
        return "验签签名成功";
    }
}

//DEMO
$post_param = array("appId" => "155901641411070798101",
    "productId"=>620,
    "bizEnc" => 0,
    "bizParams" => "{\"cardNo\":\"31515156515156\",\"cardType\":1,\"orderNo\":\"14201811161018036595\",\"reason\":\"\",\"status\":1}",
    "method" => "BIND_CARD_CALLBACK",
    "signType" => 1,
    "timestamp" => "1562757563000"
);
$txt_url = getSignContent($post_param);

//生成数字签名
echo "//get sign result\r\n";
$getsign_result = getSign($txt_url);
echo $getsign_result;
echo "\r\n\r\n";

//验签
$post_param['sign'] = $getsign_result;
echo "//check sign result\r\n";
echo checkSign($post_param);

