package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Autor HumgTop
 * @Date 2021/6/29 10:53
 * @Version 1.0
 */
@Api(value = "用户中心", description = "用户中心管理")
public interface UcenterControllerApi {
    @ApiOperation("根据用户账号查询用户信息")
    XcUserExt getUserExt(String username);
}
