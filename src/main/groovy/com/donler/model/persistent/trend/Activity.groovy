package com.donler.model.persistent.trend

import com.donler.model.CreateAndModifyTimestamp
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
    @ApiModelProperty("活动名称")
    String name
    @ApiModelProperty("活动封面的配图")
    String image
    @ApiModelProperty("指定发布群组,为空默认为全体可见")
    String teamId // 指定发布群组,为空默认为全体可见
    @ApiModelProperty ("公司id,为当前用户所属公司" )
    String companyId
    @ApiModelProperty(notes = "活动发起人")
    String authorId
    @ApiModelProperty(notes = "参与者")
    List<String> members
    @ApiModelProperty(notes = "精彩瞬间")
    List<String> showtimes
    @ApiModelProperty("活动开始时间")
    Date startTime
    @ApiModelProperty("活动结束时间")
    Date endTime
    @ApiModelProperty("活动报名截止时间")
    Date deadline
    @ApiModelProperty("最大人数")
    Integer memberMax
    @ApiModelProperty("活动地址")
    String address
    @ApiModelProperty("最小人数")
    Integer memberMin
    @ApiModelProperty("活动描述")
    String desc
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间

    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间


}
