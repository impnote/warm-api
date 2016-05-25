package com.donler.model.persistent.trend

import com.donler.model.CreateAndModifyTimeStamp
import com.donler.model.ImageUrlUnit
import com.donler.model.persistent.CommentArrItem
import groovy.transform.ToString
import org.springframework.data.annotation.Id
/**
 * Created by jason on 5/23/16.
 */
/**
 * 瞬间
 */
@ToString(includeNames = true)
class ShowTime {
    @Id
    String id // 瞬间的id
    String content // 瞬间的内容
    List<ImageUrlUnit> images // 配图的url数组
    String activityId  // 属于的活动,为空则为非活动瞬间
    List<String> obviousTeamIds // 指定可见群组id,为空默认为全体可见
    String authorId // 发布者
    List<String> approves // 点赞数组
    List<CommentArrItem> comments // 评论数组
    CreateAndModifyTimeStamp timeStamp // 时间戳

}
