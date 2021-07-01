package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/6/29 14:36
 * @Version 1.0
 */
@Repository
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser, String> {
    //根据userId查找
    XcCompanyUser findByUserId(String userId);
}
