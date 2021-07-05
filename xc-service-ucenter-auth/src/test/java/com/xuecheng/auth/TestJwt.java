package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;

/**
 * @Autor HumgTop
 * @Date 2021/6/25 11:19
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {
    //创建jwt令牌
    @Test
    public void testCreateJwt() {
        //密钥库文件
        String keystore = "xc.keystore";
        //密钥库的密码
        String keystore_password = "xuechengkeystore";
        //密钥库文件路径
        ClassPathResource classPathResource = new ClassPathResource(keystore);
        //密钥别名
        String alias = "xckey";
        //密钥的访问密码
        String key_password = "xuecheng";
        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, keystore_password.toCharArray());
        //密钥对：公钥和私钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, key_password.toCharArray());
        //获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //令牌的内容
        HashMap<String, String> body = new HashMap<>();
        body.put("name", "humgtop");
        String bodyString = JSON.toJSONString(body);
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(bodyString, new RsaSigner(privateKey));
        //生成jwt令牌编码
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

    @Test
    public void testBcrypt() {
        String password = "111111";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String[] res = new String[2];
        for (int i = 0; i < 2; i++) {
            String encode = passwordEncoder.encode(password);
            res[i] = encode;
        }
        for (String encode : res) {
            System.out.println(passwordEncoder.matches(password, encode));
        }
    }

    @Test
    public void testDecode() {
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        String jwtString = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE2MjU1MzA1MjUsImF1dGhvcml0aWVzIjpbInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfZGVsIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9saXN0IiwiY291cnNlX3RlYWNocGxhbl9saXN0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9waWMiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3BsYW4iLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlIiwiY291cnNlX2ZpbmRfbGlzdCIsInhjX3RlYWNobWFuYWdlciIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfbWFya2V0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9wdWJsaXNoIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9hZGQiXSwianRpIjoiZTMwM2E2YzAtMWUwYi00ZWIyLWFhZTctOGVhMjc1NDZhYjIzIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.h2P4QKOlfWw_1rJ6AJT_6pQZoqURP5xZqIPFUOHzcgheOaIBLNqgsMPJHVAbBYVIWohgh2_k4s-XKeSgv3FCnaIVmKgM390BpDqnJV3lBDXe1e1I9aFEjnlbfB2zbfdMhbwZN5coGBmyew2T4bFm44vOPWagTxacKCoYTF-wycTWU9w9eZEjGnodpmxOvedAFfA1G9Mrl4Ib_1Wj4ay8G5RhJLOVdc08JqHOEyvB8zNqJyMg4oHoKcRZNc-clids5-EISlLlnJidApyCb5_y9JoOwYFo849XlKPrgsZuOxo-RF9squcnqAy61_OWHo_3W5LIqnF34YiJnGIVmY8aDw";
        Jwt jwt = JwtHelper.decodeAndVerify(jwtString, new RsaVerifier(publicKey));
        System.out.println(jwt.getClaims());
    }
}
