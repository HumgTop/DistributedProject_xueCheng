package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/6/29 11:00
 * @Version 1.0
 */
@Repository
public interface XcUserRepository extends JpaRepository<XcUser, String> {
    //根据账号查询用户信息
    XcUser findByUsername(String username);
}
