package com.donler.model.persistent

import com.donler.model.CreateAndModifyTimeStamp
import io.swagger.annotations.ApiModelProperty

/**
 * Created by jason on 5/25/16.
 */
class CommentArrItem {
    @ApiModelProperty(notes = "评论者 id")
    String userId // 评论者 id
    @ApiModelProperty(notes = "评论 内容")
    String comment // 评论内容
    @ApiModelProperty(notes = "评论时间戳")
    CreateAndModifyTimeStamp timeStamp // 评论时间戳
}
