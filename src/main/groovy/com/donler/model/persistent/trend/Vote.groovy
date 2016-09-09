package com.donler.model.persistent.trend

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id
/**
 * Created by jason on 6/28/16.
 */
@ToString(includeNames = true)
class Vote {
    @Id
    String id // 编号
    @ApiModelProperty("投票的配图url,可以为空")
    String image
    @ApiModelProperty("投票的所属的公司id")
    String companyId
    @ApiModelProperty("指定群组id,为空默认为全体可见")
    String teamId // 指定群组id,为空默认为全体可见
    @ApiModelProperty("投票内容")
    String content // 投票内容
    @ApiModelProperty("投票选项")
    List<String> options
    @ApiModelProperty("投票的评论信息")
    List<String> comments
    @ApiModelProperty("投票发起人")
    String authorId
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
    @ApiModelProperty("是否投过票")
    Boolean isVoted = false//是否投过票
}
