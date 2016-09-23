package com.donler.model.request.trend

import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by zhangjiasheng on 7/25/16.
 */
class TrendCommentPublishRequestBody extends TrendTypeRequestBody{
    @NotNull
    @ApiModelProperty(notes = "评论的内容", example = "他么的非要我评论,我又么话说,到15个字了么~~~~~~~~")
    String comment

    @ApiModelProperty(value = "要回复的评论的id", required = false)
    String replyToCommentId
}
