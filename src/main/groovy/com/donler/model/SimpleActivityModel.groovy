package com.donler.model

import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 6/21/16.
 */
class SimpleActivityModel {
    @ApiModelProperty("活动的id")
    String id
    @ApiModelProperty("活动名称")
    String name
    @ApiModelProperty("活动封面的配图")
    String image
    @ApiModelProperty(notes = "活动发起人")
    SimpleUserModel author
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
}
