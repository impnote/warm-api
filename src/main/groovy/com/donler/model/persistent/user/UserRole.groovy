package com.donler.model.persistent.user

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by apple on 16/9/5.
 */

@ToString(includeNames = true)
class UserRole {
    @Id
    @ApiModelProperty(notes = "用户角色id")
    String id
    @ApiModelProperty(notes = "用户的id")
    String userId
    @ApiModelProperty(notes = "群组的id")
    String teamId
    @ApiModelProperty(notes = "公司的id")
    String companyId
    @ApiModelProperty(notes = "用户角色")
    RoleEnum roleEnum
}
enum RoleEnum{
    RoleNormalUser,
    RoleManagerUser,
    RoleCreateUser,
    RoleAdminUser,
}

