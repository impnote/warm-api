package com.donler.model.response

import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.SimpleUserModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/26/16.
 */
class CommentArrItem {
    @ApiModelProperty(notes = "评论id")
    String id
    @ApiModelProperty(notes = "评论者")
    SimpleUserModel user // 评论者
    @ApiModelProperty(notes = "评论 内容")
    String comment // 评论内容
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
}
