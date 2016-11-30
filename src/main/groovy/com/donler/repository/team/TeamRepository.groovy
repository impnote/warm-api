package com.donler.repository.team

import com.donler.model.persistent.team.Team
import com.google.common.base.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository


/**
 * Created by jason on 6/13/16.
 */
interface TeamRepository extends MongoRepository<Team, String> {
    Long countByName (String name)
    Page<Team> findByNameLike(String keyword, Pageable pageable)
    Page<Team> findByMembersContaining(String uid, Pageable pageable)
    Page<Team> findByCompanyId(String companyId, Pageable pageable)
    Page<Team> findByMembersContainingAndCreatedAtBefore(String uid,Date createdAt, Pageable pageable)
}
