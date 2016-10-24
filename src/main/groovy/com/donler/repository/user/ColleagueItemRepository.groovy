package com.donler.repository.user

import com.donler.model.persistent.user.ColleagueItem
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by yali-04 on 2016/10/24.
 */
interface ColleagueItemRepository extends MongoRepository<ColleagueItem, String> {
    ColleagueItem findByColleagueId(String colleagueId)

}