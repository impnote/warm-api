package com.donler.model.persistent.trend

import com.donler.model.CreateAndModifyTimestamp
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
    @ApiModelProperty("指定群组id,为空默认为全体可见")
    String teamId // 指定群组id,为空默认为全体可见
    @ApiModelProperty("投票内容")
    String content // 投票内容
    @ApiModelProperty("投票选项")
    List<VoteOptionInfo> options
    @ApiModelProperty("投票的评论信息")
    List<CommentArrItem> comments
    @ApiModelProperty("投票发起人")
    String authorId
    @ApiModelProperty("投票时间戳")
    CreateAndModifyTimestamp timestamp
}
