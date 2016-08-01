package com.donler.model.request.trend

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Length

import javax.validation.constraints.Max
import javax.validation.constraints.NotNull
/**
 * Created by jason on 7/27/16.
 */
@ToString(includeNames = true)
class TopicPublishRequestBody {
    @ApiModelProperty(value = "话题内容",example = "这是话题内容")
    @NotNull
    @Max(value =5000L)
    String content // 投票内容

    @ApiModelProperty(value = "话题标题",example = "这是一个话题的标题")
    @Length(min = 1,max = 30)
    String title

    @ApiModelProperty(value = "群组ID",example = "ABC",required = false)
    String teamId
    }
