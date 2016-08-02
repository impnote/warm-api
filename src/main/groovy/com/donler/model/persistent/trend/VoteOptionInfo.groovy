package com.donler.model.persistent.trend

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by jason on 6/28/16.
 */
@ToString(includeNames = true)
class VoteOptionInfo {
    @Id
    @ApiModelProperty("投票id")
    String id
    @ApiModelProperty("投票选项")
    String option
    @ApiModelProperty("已投用户ids")
    List<String> votedUserIds

}
