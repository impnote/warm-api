package com.donler.model.response

import com.donler.model.*
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/26/16.
 */
@ToString(includeNames = true)
class Activity {
    @ApiModelProperty("活动的id")
    String id
    @ApiModelProperty("活动名称")
    String name
    @ApiModelProperty("活动封面的配图")
    String image
    @ApiModelProperty("可见群组信息,为空默认为全体可见")
    SimpleTeamModel team // 可见群组信息,为空默认为全体可见
    @ApiModelProperty("公司信息")
    SimpleCompanyModel company // 公司信息
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
    @ApiModelProperty("活动地点")
    String address
    @ApiModelProperty("活动描述")
    String description
    @ApiModelProperty("活动发布时间戳")
    CreateAndModifyTimestamp timestamp


}
