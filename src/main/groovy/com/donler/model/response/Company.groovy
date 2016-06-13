package com.donler.model.response

import com.donler.model.CreateAndModifyTimestamp
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 6/13/16.
 */
@ToString(includeNames = true)
class Company {
    @ApiModelProperty("公司id")
    String id // 公司id

    @ApiModelProperty("公司名称")
    String name // 公司名称

    @ApiModelProperty("邮箱后缀")
    String emailSuffix // 邮箱后缀

    @ApiModelProperty("公司配图")
    String image // 公司配图

    @ApiModelProperty("时间戳")
    CreateAndModifyTimestamp timestamp
}
