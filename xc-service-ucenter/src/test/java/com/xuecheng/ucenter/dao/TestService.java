package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Autor HumgTop
 * @Date 2021/7/5 13:59
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestService {
    @Autowired
    XcMenuMapper xcMenuMapper;

    @Test
    public void testXcMenuMapper() {
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId("49");
        for (XcMenu xcMenu : xcMenus) {
            System.out.println(xcMenu);
        }
    }
}
