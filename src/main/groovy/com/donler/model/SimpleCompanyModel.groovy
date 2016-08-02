package com.donler.model

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/23/16.
 */
@ToString(includeNames = true)
class SimpleCompanyModel {
    @ApiModelProperty("公司id")
    String id // 公司id
    @ApiModelProperty("公司名称")
    String name // 公司名称
    @ApiModelProperty("公司配图url")
    String imageUrl
}
