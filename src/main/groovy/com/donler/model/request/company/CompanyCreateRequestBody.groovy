package com.donler.model.request.company

import com.donler.model.request.ImageUploadDataUnit
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by jason on 6/13/16.
 */
@ToString(includeNames = true)
class CompanyCreateRequestBody {
    @NotNull
    @ApiModelProperty(value = "公司名称", example = "动梨软件科技有限公司")
    String name // 公司名称

    @NotNull
    @ApiModelProperty(value = "邮箱后缀", example = "donler.com")
    String emailSuffix // 邮箱后缀

    @NotNull
    @ApiModelProperty("公司配图")
    ImageUploadDataUnit image // 公司配图

}
