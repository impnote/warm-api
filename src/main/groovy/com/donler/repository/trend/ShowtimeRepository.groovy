package com.donler.repository.trend

import com.donler.model.persistent.trend.Showtime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository


/**
 * Created by jason on 5/23/16.
 */
//@RepositoryRestResource(collectionResourceRel = "showtime", path = "showtime")
interface ShowtimeRepository extends MongoRepository<Showtime, String> {
    List<Showtime> findByActivityId(def id)
    List<Showtime> findByCompanyId(def id)
    Page<Showtime> findByCompanyId(def id, Pageable pageable)
    Page<Showtime> findByUpdatedAtBefore(Date updatedAt, Pageable pageable)
    Page<Showtime> findByCompanyIdAndCreatedAtBefore(def companyId, Date createdAt, Pageable pageable)
}