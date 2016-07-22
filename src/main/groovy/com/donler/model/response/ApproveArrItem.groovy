package com.donler.model.response

import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.SimpleUserModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/26/16.
 */
class ApproveArrItem {
    @ApiModelProperty(notes = "点赞id")
    String id
    @ApiModelProperty(notes = "点赞者")
    SimpleUserModel user // 点赞者
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
}
