package com.donler.model.request.trend

import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by zhangjiasheng on 7/25/16.
 */
class TrendTypeRequestBody {

    @ApiModelProperty(notes = "活动的id", required = false)
    String activityId

    @ApiModelProperty(value = "投票的id", required = false)
    String voteId

    @ApiModelProperty(value = "瞬间的id", required = false)
    String showtimeId

    @ApiModelProperty(value = "话题的id", required = false)
    String topicId

}
