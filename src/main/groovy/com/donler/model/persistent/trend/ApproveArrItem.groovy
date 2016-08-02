package com.donler.model.persistent.trend

import com.donler.model.CreateAndModifyTimestamp
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by jason on 5/26/16.
 */
class ApproveArrItem {
    @Id
    @ApiModelProperty(notes = "点赞的id")
    String id
    @ApiModelProperty(notes = "点赞者")
    String userId // 评论者
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间

    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
}
