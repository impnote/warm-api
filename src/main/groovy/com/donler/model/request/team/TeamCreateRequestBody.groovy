package com.donler.model.request.team

import com.donler.model.CreateAndModifyTimestamp
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

import javax.validation.constraints.NotNull
/**
 * Created by jason on 6/13/16.
 */
@ToString(includeNames = true)
class TeamCreateRequestBody {
    @NotNull
    @ApiModelProperty(value = "群组名称", example = "篮球小队")
    String name // 群组名称
    @NotNull
    @ApiModelProperty("群组描述")
    String desc // 群组描述

}
