package com.donler.model.request.user

import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/27/16.
 */
class UserLoginRequestModel {
    @ApiModelProperty(value = "用户名", example = "jasonzhang")
    String username
    @ApiModelProperty(value = "密码", example = "jasonzhang")
    String password
}
