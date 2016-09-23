package com.donler.model.persistent.trend

import com.donler.model.ImageUrlUnit
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by jason on 5/23/16.
 */
/**
 * 瞬间
 */
@ToString(includeNames = true)
class Showtime {
    @Id
    @ApiModelProperty(notes = "瞬间的id")
    String id // 瞬间的id
    @ApiModelProperty(notes = "瞬间的内容")
    String content // 瞬间的内容
    @ApiModelProperty(notes = "配图的url数组")
    List<ImageUrlUnit> images // 配图的url数组
    @ApiModelProperty(notes = "属于的活动,为空则为非活动瞬间")
    String activityId  // 属于的活动,为空则为非活动瞬间
    @ApiModelProperty(notes = "指定可见群组id,为空默认为全体可见")
    String teamId // 指定群组id,为空默认为全体可见
    @ApiModelProperty("公司id")
    String companyId // 公司id
    @ApiModelProperty(notes = "发布者")
    String authorId // 发布者
    @ApiModelProperty(notes = "点赞数组")
    List<String> approves // 点赞数组
    @ApiModelProperty(notes = "评论")
    List<String> comments // 评论数组
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间

    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间

}
