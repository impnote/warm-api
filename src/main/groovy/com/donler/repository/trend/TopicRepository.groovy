package com.donler.repository.trend

import com.donler.model.persistent.trend.Topic
import org.springframework.data.mongodb.repository.MongoRepository
/**
 * Created by jason on 6/13/16.
 */
interface TopicRepository extends MongoRepository<Topic, String> {
    List<Topic> findByCompanyId(def id)

}
