package com.donler.model.response

import com.donler.model.SimpleCompanyModel
import com.donler.model.SimpleUserModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by yali-04 on 2016/12/14.
 */
class TeamHomePageModel {
    @Id
    @ApiModelProperty("群组的id")
    String id
    @ApiModelProperty("群组名称")
    String name
    @ApiModelProperty("群组封面的配图")
    String image
    @ApiModelProperty("群组人数")
    Integer peopleCount
    @ApiModelProperty("群组描述")
    String desc
    @ApiModelProperty("创建者")
    SimpleUserModel author
    @ApiModelProperty("所属公司")
    SimpleCompanyModel company
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间
    @ApiModelProperty("群组成员")
    List<SimpleUserModel> members
    @ApiModelProperty("群组动态")
    Map teamTrend
    @ApiModelProperty("环信群组id")
    String easemobId //环信群组id
}
