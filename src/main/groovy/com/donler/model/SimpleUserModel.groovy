package com.donler.model

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/23/16.
 */
@ToString(includeNames = true)
class SimpleUserModel {
    @ApiModelProperty("用户id")
    String id // 用户id
    @ApiModelProperty("用户昵称")
    String nickname // 用户昵称
    @ApiModelProperty("用户头像")
    String avatar // 用户头像
    @ApiModelProperty("手机号")
    String phone
    @ApiModelProperty("备注")
    String remark
}
