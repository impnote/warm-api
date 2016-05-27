package com.donler.repository.user

import com.donler.model.persistent.trend.User
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by jason on 5/27/16.
 */
interface UserRepository extends MongoRepository<User, String> {

}
