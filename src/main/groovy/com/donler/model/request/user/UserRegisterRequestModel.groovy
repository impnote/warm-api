package com.donler.model.request.user

import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Email

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

/**
 * Created by jason on 5/27/16.
 */
class UserRegisterRequestModel {
    @ApiModelProperty(notes = "用户名", example = "jasonzhang")
    String username
    @ApiModelProperty(notes = "昵称", example = "小张")
    String nickname
    @ApiModelProperty(notes = "号码", example = "17051024060")
    @Pattern(regexp = "^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}\$", message = "手机号码不正确")
    String phone
    @Email(message = "请输入正确的邮箱")
    @ApiModelProperty(notes = "邮箱", example = "lefttjs@gmail.com")
    String email
    @NotNull
    @ApiModelProperty(notes = "密码", example = "leftjs")
    String password
    @NotNull
    @ApiModelProperty(notes = "公司id")
    String companyId
}
