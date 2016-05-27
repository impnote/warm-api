package com.donler.model.persistent.trend

import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by jason on 5/27/16.
 */
class Token {
    @Id
    @ApiModelProperty(notes = "token的id")
    String id
    @ApiModelProperty(notes = "用户的id")
    String userId
    @ApiModelProperty(notes = "token字符串")
    String token
    @ApiModelProperty(notes = "过期时间")
    String expiredTime
}
