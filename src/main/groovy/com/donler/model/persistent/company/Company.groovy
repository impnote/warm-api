package com.donler.model.persistent.company

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by jason on 6/13/16.
 */
@ToString(includeNames = true)
class Company {
    @Id
    @ApiModelProperty("公司id")
    String id // 公司id

    @ApiModelProperty("公司名称")
    String name // 公司名称

    @ApiModelProperty("邮箱后缀")
    String emailSuffix // 邮箱后缀

    @ApiModelProperty("公司配图")
    String image // 公司配图

    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间

    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间



}
