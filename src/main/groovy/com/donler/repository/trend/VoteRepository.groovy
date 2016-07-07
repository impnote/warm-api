package com.donler.repository.trend

import com.donler.model.persistent.trend.Vote
import org.springframework.data.mongodb.repository.MongoRepository
/**
 * Created by jason on 6/13/16.
 */
interface VoteRepository extends MongoRepository<Vote, String> {

}
