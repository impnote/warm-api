package com.donler.repository.company

import com.donler.model.persistent.company.Company
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by jason on 6/13/16.
 */
interface CompanyRepository extends MongoRepository<Company, String> {
    Page<Company> findByEmailSuffix(String emailSuffix, Pageable pageable)

    Long countByName(String name)

}
