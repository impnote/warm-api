package com.donler.model.request.trend

import io.swagger.annotations.ApiModelProperty

/**
 * Created by apple on 16/9/18.
 */
class VoteOperationRequestBody {

    @ApiModelProperty(value = "投票的id", required = true)
    String voteId

    @ApiModelProperty(value = "投票选项的id", required = true)
    String voteOptionInfoId

}
