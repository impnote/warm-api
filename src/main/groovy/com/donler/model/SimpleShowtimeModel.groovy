package com.donler.model

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
/**
 * Created by jason on 5/23/16.
 */
@ToString(includeNames = true)
class SimpleShowtimeModel {
    @ApiModelProperty("瞬间id")
    String id // 瞬间id
    @ApiModelProperty("瞬间的内容")
    String content // 瞬间的内容
    @ApiModelProperty("配图url数组")
    List<ImageUrlUnit> images // 配图url数组
    @ApiModelProperty("发布者")
    SimpleUserModel author // 发布者
}
