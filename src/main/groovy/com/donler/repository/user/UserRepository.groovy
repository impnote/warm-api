package com.donler.repository.user

import com.donler.model.persistent.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
/**
 * Created by jason on 5/27/16.
 */
interface UserRepository extends MongoRepository<User, String>{


    User findByUsername(def username)

    User findByPhone(def phone)

    User findByEmail(def email)

    User findByUsernameOrPhoneOrEmail(def username, def phone, def email)

    Page<User> findByCompanyId(def companyId, Pageable pageable)

    List<User> findAllByCompanyId(def companyId)

}
