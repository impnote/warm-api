package com.donler.repository.user

import com.donler.model.persistent.trend.Token
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by jason on 5/30/16.
 */
interface TokenRepository extends MongoRepository<Token, String> {
    Token findByTokenAndExpiredTimeLessThan(def token, def time)
}
