package com.donler.model

import groovy.transform.ToString
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/25/16.
 */
@ToString(includeNames = true)
class CreateAndModifyTimestamp {
//    @ApiModelProperty("所属的id")
//    String belongToId
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间


}
