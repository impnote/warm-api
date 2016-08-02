package com.donler.model.persistent.trend

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id
/**
 * Created by jason on 7/27/16.
 */
@ToString(includeNames = true)
class Topic {
    @Id
    String id // 编号
    @ApiModelProperty("话题的配图url,可以为空")
    String image
    @ApiModelProperty("话题的所属的公司id")
    String companyId
    @ApiModelProperty("指定话题所在群组的id,为空默认为全体可见")
    String teamId // 指定群组id,为空默认为全体可见
    @ApiModelProperty("话题内容")
    String content // 投票内容
    @ApiModelProperty("话题标题")
    String title
    @ApiModelProperty("话题的评论信息")
    List<String> comments
    @ApiModelProperty("话题发起人")
    String authorId
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
}
