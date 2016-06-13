package com.donler.controller

import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.persistent.trend.Company
import com.donler.model.response.Company as ResCompany
import com.donler.model.request.company.CompanyCreateRequestBody
import com.donler.repository.company.CompanyRepository
import com.donler.service.OSSService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

/**
 * Created by jason on 5/27/16.
 */
@RestController
@RequestMapping("/company")
@Api(value = "company", tags = ["公司"], consumes = "application/json", produces = "application/json")
class CompanyController {

    @Autowired
    CompanyRepository companyRepository

    @Autowired
    OSSService ossService


    @ApiOperation(value = "创建公司", notes = "根据传入的信息创建一个公司")
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    ResCompany createCompany(@Valid @RequestBody CompanyCreateRequestBody body) {
        // TODO 唯一性检查
        Company company = companyRepository.save(new Company(
                name: body?.name,
                emailSuffix: body?.emailSuffix,
                image: ossService.uploadFileToOSS(body?.image?.imageData),
                timestamp: new CreateAndModifyTimestamp(updatedAt: new Date(), createdAt: new Date())
        ))
        return new ResCompany(
                id: company?.id,
                name: company?.name,
                emailSuffix: company?.emailSuffix,
                image: company?.image,
                timestamp: company?.timestamp
        )
    }



}
