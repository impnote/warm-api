package com.donler.model.persistent.trend

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

/**
 * Created by jason on 6/28/16.
 */
@ToString(includeNames = true)
class VoteOptionInfo {
    @ApiModelProperty("投票选项")
    String name
    @ApiModelProperty("已投用户ids")
    List<String> votedUserIds
}
