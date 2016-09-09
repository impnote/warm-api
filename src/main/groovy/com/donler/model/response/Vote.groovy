package com.donler.model.response

import com.donler.model.SimpleCompanyModel
import com.donler.model.SimpleTeamModel
import com.donler.model.SimpleUserModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id
/**
 * Created by zhangjiasheng on 7/22/16.
 */
class Vote {
    @Id
    String id // 编号
    @ApiModelProperty("投票的配图url,可以为空")
    String image
    @ApiModelProperty("投票的所属的公司id")
    SimpleCompanyModel company
    @ApiModelProperty("指定群组id,为空默认为全体可见")
    SimpleTeamModel team // 指定群组id,为空默认为全体可见
    @ApiModelProperty("投票内容")
    String content // 投票内容
    @ApiModelProperty("投票选项")
    List<VoteOptionInfo> options
    @ApiModelProperty("投票的评论信息")
    List<CommentArrItem> comments
    @ApiModelProperty("投票发起人")
    SimpleUserModel author
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
    @ApiModelProperty("是否投过票")
    Boolean isVoted = false//是否投过票
}
