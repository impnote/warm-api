package com.donler.model.request.user

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
/**
 * Created by apple on 16/8/15.
 */
@ToString
class UserProfileModifyRequestModel {
    @ApiModelProperty("昵称")
    String nickname // 昵称
    @ApiModelProperty("姓名")
    String realname //姓名
    @ApiModelProperty("星座")
    String constellation //星座
    @ApiModelProperty("生日")
    String birthday //生日
    @ApiModelProperty(notes = "职位")
    String job

}
