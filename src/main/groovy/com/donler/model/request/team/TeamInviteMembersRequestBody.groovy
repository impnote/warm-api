package com.donler.model.request.team

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by yali-04 on 2017/1/12.
 */
@ToString(includeNames = true)
class TeamInviteMembersRequestBody {
    @NotNull
    @ApiModelProperty(value = "成员id数组")
    List<String> membersId

    @NotNull
    @ApiModelProperty(value = "群组id")
    String teamId

}
