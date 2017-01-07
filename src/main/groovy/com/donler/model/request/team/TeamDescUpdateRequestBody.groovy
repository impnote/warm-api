package com.donler.model.request.team

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by yali-04 on 2017/1/3.
 */
@ToString(includeNames = true)
class TeamDescUpdateRequestBody {

    @NotNull
    @ApiModelProperty(value = "群组id")
    String teamId
    @NotNull
    @ApiModelProperty(value = "群组公告")
    String desc
}
