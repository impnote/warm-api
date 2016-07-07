package com.donler.repository.company

import com.donler.model.persistent.company.Team
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by jason on 6/13/16.
 */
interface TeamRepository extends MongoRepository<Team, String> {
}
