package com.donler.model.persistent.trend

import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id
/**
 * Created by jason on 5/26/16.
 */
class CommentArrItem {
    @Id
    @ApiModelProperty(notes = "评论id")
    String id

    @ApiModelProperty(notes = "评论者")
    String userId // 评论者

    @ApiModelProperty(notes = "评论 内容")
    String comment // 评论内容

    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间

    @ApiModelProperty("回复某条评论")
    String replyToCommentId

    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
}
