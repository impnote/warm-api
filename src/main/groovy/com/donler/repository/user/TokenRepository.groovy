package com.donler.repository.user

import com.donler.model.persistent.user.Token
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by jason on 5/30/16.
 */
interface TokenRepository extends MongoRepository<Token, String> {
    Token findByUserId(String UserId)
}
