package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Autor HumgTop
 * @Date 2021/7/5 13:32
 * @Version 1.0
 */
@Mapper
@Repository
public interface XcMenuMapper {
    List<XcMenu> selectPermissionByUserId(@Param("userId") String userId);
}
