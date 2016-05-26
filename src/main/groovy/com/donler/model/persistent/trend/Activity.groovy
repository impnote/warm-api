package com.donler.model.persistent.trend

import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.ImageUrlUnit
import com.donler.model.SimpleShowtimeModel
import com.donler.model.SimpleUserModel
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id
/**
 * Created by jason on 5/26/16.
 */
@ToString(includeNames = true)
class Activity {
    @Id
    @ApiModelProperty("活动的id")
    String id
    @ApiModelProperty("活动封面的配图")
    ImageUrlUnit image
    @ApiModelProperty("指定可见群组id,为空默认为全体可见")
    List<String> obviousTeams // 指定可见群组id,为空默认为全体可见
    @ApiModelProperty(notes = "活动发起人")
    SimpleUserModel author
    @ApiModelProperty(notes = "参与者")
    List<SimpleUserModel> members
    @ApiModelProperty(notes = "精彩瞬间")
    List<SimpleShowtimeModel> showtimes
    @ApiModelProperty("活动开始时间")
    Date startTime
    @ApiModelProperty("活动结束时间")
    Date endTime
    @ApiModelProperty("活动报名截止时间")
    Date deadline
    @ApiModelProperty("最大人数")
    Integer memberMax
    @ApiModelProperty("最小人数")
    Integer memberMin
    @ApiModelProperty("活动描述")
    String description
    @ApiModelProperty("活动发布时间戳")
    CreateAndModifyTimestamp timestamp



}
