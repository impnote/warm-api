package com.donler.repository.trend

import com.donler.model.persistent.trend.CommentArrItem
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by zhangjiasheng on 7/22/16.
 */
interface CommentArrItemRepository extends MongoRepository<CommentArrItem, String>{

}