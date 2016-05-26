package com.donler.model.request.trend

import com.donler.model.request.ImageUploadDataUnit
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by jason on 5/25/16.
 */
@ToString(includeNames = true)
class ShowTimePublishRequestBody {
    @NotNull(message = "请输入评论的内容")
    @ApiModelProperty(notes = "瞬间内容", example = "马克飞象真好用")
    String content // 瞬间内容
    @NotNull(message = "请传入您要上传的图片的base64数组")
    @ApiModelProperty(notes = "图片base64数组")
    List<ImageUploadDataUnit> imagesData // 图片base64数组
    @ApiModelProperty("属于的活动,为空则为非活动瞬间")
    String activityId // 属于的活动,为空则为非活动瞬间
    @ApiModelProperty("指定可见群组id,为空默认为全体可见")
    List<String> obviousTeamIds // 指定可见群组id,为空默认为全体可见
}
