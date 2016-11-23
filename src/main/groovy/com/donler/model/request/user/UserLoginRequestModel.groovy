package com.donler.model.request.user

import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/27/16.
 */
class UserLoginRequestModel {
    @ApiModelProperty(value = "登录信息(用户名或者手机号码或者邮箱)", example = "zhang")
    String loginInfo
    @ApiModelProperty(value = "密码", example = "55yali")
    String password
}
