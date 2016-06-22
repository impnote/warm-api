package com.donler.model.response

import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.SimpleUserModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/26/16.
 */
class ApproveArrItem {
    @ApiModelProperty(notes = "点赞者")
    SimpleUserModel user // 点赞者
    @ApiModelProperty(notes = "点赞时间戳")
    CreateAndModifyTimestamp timestamp // 点赞时间戳
}
