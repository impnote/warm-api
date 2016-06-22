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
    @ApiModelProperty("活动发布时间戳")
    CreateAndModifyTimestamp timestamp
}
