package com.donler.model

import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/26/16.
 */
class ApproveArrItem {
    @ApiModelProperty(notes = "点赞者")
    SimpleUserModel user // 评论者
    @ApiModelProperty(notes = "点赞时间戳")
    CreateAndModifyTimestamp timestamp // 评论时间戳
}
