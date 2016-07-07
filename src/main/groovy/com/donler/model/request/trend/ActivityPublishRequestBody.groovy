package com.donler.model.request.trend

import com.donler.model.request.ImageUploadDataUnit
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull
/**
 * Created by jason on 5/25/16.
 */
@ToString(includeNames = true)
class ActivityPublishRequestBody {

    @NotNull(message = "活动名称不能为空")
    String name
    @ApiModelProperty("活动封面的配图")
    ImageUploadDataUnit image
    @ApiModelProperty("指定群组id,为空默认为全体可见")
    String teamId // 指定群组id,为空默认为全体可见
    @ApiModelProperty(value = "活动开始时间")
    Date startTime
    @ApiModelProperty("活动结束时间")
    Date endTime
    @ApiModelProperty("活动报名截止时间")
    Date deadline
    @ApiModelProperty("最大人数")
    Integer memberMax
    @ApiModelProperty("最小人数")
    Integer memberMin
    @ApiModelProperty("活动地点")
    String address
    @ApiModelProperty("活动描述")
    String desc

}
