package com.donler.model.persistent

import com.donler.model.CreateAndModifyTimeStamp

/**
 * Created by jason on 5/25/16.
 */
class CommentArrItem {
    String userId // 评论者 id
    String comment // 评论内容
    CreateAndModifyTimeStamp timeStamp // 评论时间戳
}
