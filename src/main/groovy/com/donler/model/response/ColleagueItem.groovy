package com.donler.model.response

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by yali-04 on 2016/10/24.
 */
@ToString(includeNames = true)
class ColleagueItem {
    @Id
    @ApiModelProperty("id")
    String id
    @ApiModelProperty("同事id")
    String colleagueId //同事id
    @ApiModelProperty("同事昵称")
    String colNickName //同事昵称
    @ApiModelProperty("备注")
    String memo      //备注
    @ApiModelProperty("电话")
    String phoneNum     //电话
    @ApiModelProperty("头像")
    String avatar       //头像
}
