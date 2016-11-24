package com.donler.model.request.user

import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by yali-04 on 2016/11/24.
 */
class UserAddMemoRequestModel {
    @NotNull
    @ApiModelProperty(value = "添加的新备注")
    String memo
    @NotNull
    @ApiModelProperty(value = "某同事的id")
    String colleagueId
}
