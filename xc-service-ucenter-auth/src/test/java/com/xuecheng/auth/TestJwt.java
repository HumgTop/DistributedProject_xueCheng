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
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.PrivateKey;
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
}
