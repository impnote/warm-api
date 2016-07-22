package com.donler.repository.trend

import com.donler.model.persistent.trend.ApproveArrItem
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by zhangjiasheng on 7/22/16.
 */
interface ApproveArrItemRepository extends MongoRepository<ApproveArrItem, String>{
}
