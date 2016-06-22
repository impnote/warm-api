package com.donler.model.persistent.trend

import com.donler.model.CreateAndModifyTimestamp
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/26/16.
 */
class CommentArrItem {
    @ApiModelProperty(notes = "评论者")
    String userId // 评论者
    @ApiModelProperty(notes = "评论 内容")
    String comment // 评论内容
    @ApiModelProperty(notes = "评论时间戳")
    CreateAndModifyTimestamp timestamp // 评论时间戳
}
