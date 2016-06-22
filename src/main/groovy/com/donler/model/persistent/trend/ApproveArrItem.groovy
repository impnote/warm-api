package com.donler.model.persistent.trend

import com.donler.model.CreateAndModifyTimestamp
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/26/16.
 */
class ApproveArrItem {
    @ApiModelProperty(notes = "点赞者")
    String userId // 评论者
    @ApiModelProperty(notes = "点赞时间戳")
    CreateAndModifyTimestamp timestamp // 评论时间戳
}
