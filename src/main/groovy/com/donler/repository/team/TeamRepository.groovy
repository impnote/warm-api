package com.donler.repository.team

import com.donler.model.persistent.team.Team
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by jason on 6/13/16.
 */
interface TeamRepository extends MongoRepository<Team, String> {
    Long countByName (String name)
    Page<Team> findByNameLike(String keyword, Pageable pageable)
    Page<Team> findByCompanyId(String companyId, Pageable pageable)
}
