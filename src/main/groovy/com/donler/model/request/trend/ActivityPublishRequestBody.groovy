package com.donler.model.request.trend

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by jason on 5/25/16.
 */
@ToString(includeNames = true)
class ActivityPublishRequestBody {
    @NotNull(message = "活动名称不能为空")
    @ApiModelProperty(notes = "活动名称", example = "上海xx会所大保健")
    String name

}
