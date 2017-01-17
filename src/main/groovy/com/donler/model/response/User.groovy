package com.donler.model.response

import com.donler.model.SimpleCompanyModel
import com.donler.model.persistent.user.ColleagueItem
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id
/**
 * Created by jason on 5/27/16.
 */
@ToString(includeNames = true)
class User {
    @Id
    @ApiModelProperty(notes = "用户id")
    String id
    @ApiModelProperty("昵称")
    String nickname // 昵称
    @ApiModelProperty("姓名")
    String realname //姓名
    @ApiModelProperty("星座")
    String constellation //星座
    @ApiModelProperty("生日")
    String birthday //生日
    @ApiModelProperty("性别")
    String gender //性别
    @ApiModelProperty("头像url")
    String avatar // 头像
    @ApiModelProperty(notes = "职位")
    String job
    @ApiModelProperty(notes = "用户名")
    String username
    @ApiModelProperty(notes = "电话号码")
    String phone
    @ApiModelProperty(notes = "邮箱")
    String email
    @ApiModelProperty(notes = "公司")
    SimpleCompanyModel company
    @ApiModelProperty("发布的话题数量")
    int topicsNum
    @ApiModelProperty("发布的投票数量")
    int votesNum
    @ApiModelProperty("发布的活动数量")
    int activitiesNum
    @ApiModelProperty("我的同事/通讯录")
    List<ColleagueItem> addressBook
    @ApiModelProperty("备注")
    String remark
    @ApiModelProperty("环信用户id")
    String easemobid //环信用户id
}
