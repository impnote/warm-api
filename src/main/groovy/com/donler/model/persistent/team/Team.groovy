package com.donler.model.persistent.team

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by jason on 5/26/16.
 */
@ToString(includeNames = true)
class Team {
    @Id
    @ApiModelProperty("群组的id")
    String id
    @ApiModelProperty("群组名称")
    String name
    @ApiModelProperty("群组封面的配图")
    String image
    @ApiModelProperty("群组人数")
    Integer peopleCount
    @ApiModelProperty("群组描述")
    String desc
    @ApiModelProperty("创建者")
    String authorId
    @ApiModelProperty("所属公司")
    String companyId
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间

    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间


}
