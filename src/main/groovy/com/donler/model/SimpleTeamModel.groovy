package com.donler.model

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/23/16.
 */
@ToString(includeNames = true)
class SimpleTeamModel {
    @ApiModelProperty("群组id")
    String id // 群组id
    @ApiModelProperty("群组名称")
    String name // 群组名称
    @ApiModelProperty("群组配图url")
    String imageUrl
    @ApiModelProperty("环信群组id")
    String easemobId
    @ApiModelProperty("是否已经加入")
    boolean isJoined

}
