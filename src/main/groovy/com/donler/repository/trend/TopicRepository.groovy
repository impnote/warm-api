package com.donler.repository.trend

import com.donler.model.persistent.trend.Topic
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
/**
 * Created by jason on 6/13/16.
 */
interface TopicRepository extends MongoRepository<Topic, String> {
    List<Topic> findByCompanyId(def id)
    List<Topic> findByAuthorId(def authorId)
    Page<Topic> findByCompanyId(def companyId, Pageable pageable)
    Page<Topic> findByCreatedAtBefore(Date createdAt, Pageable pageable)



}
