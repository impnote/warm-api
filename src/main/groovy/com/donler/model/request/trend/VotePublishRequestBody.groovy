package com.donler.model.request.trend

import com.donler.model.request.ImageUploadDataUnit
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
/**
 * Created by jason on 6/28/16.
 */
@ToString(includeNames = true)
class VotePublishRequestBody {
    @ApiModelProperty("投票的配图,可以为空")
    ImageUploadDataUnit image
    @ApiModelProperty("指定群组id,为空默认为全体可见")
    String teamId // 指定群组id,为空默认为全体可见
    @NotNull
    @ApiModelProperty(value = "投票内容", example = "小张我帅不帅")
    String content // 投票内容
    @NotNull
    @Size(min = 1, max = 10)
    @ApiModelProperty(value = "投票选项")
    List<String> options
}
