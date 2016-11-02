package com.donler.repository.trend

import com.donler.model.persistent.trend.Activity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by jason on 6/13/16.
 */
interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findByCompanyId(def id)
    Page<Activity> findByCompanyId(def companyId, Pageable pageable)
    Page<Activity> findByCreatedAtBefore(Date createdAt,Pageable pageable)
}
