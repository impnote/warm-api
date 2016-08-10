package com.donler.model.persistent.user

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by jason on 5/27/16.
 */
@ToString(includeNames = true)
class User {
    @Id
    @ApiModelProperty(notes = "用户id")
    String id
    @ApiModelProperty("昵称")
    String nickname // 昵称
    @ApiModelProperty("性别")
    String gender //性别
    @ApiModelProperty("头像url")
    String avatar // 头像
    @ApiModelProperty(notes = "职位")
    String job
    @ApiModelProperty(notes = "用户名")
    String username
    @ApiModelProperty(notes = "电话号码")
    String phone
    @ApiModelProperty(notes = "邮箱")
    String email
    @ApiModelProperty(notes = "公司")
    String companyId
    @JsonIgnore
    String password
    @JsonIgnore
    String tokenId // token id
}
