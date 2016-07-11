package com.donler.controller

import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.persistent.company.Company
import com.donler.model.request.company.CompanyCreateRequestBody
import com.donler.model.response.Company as ResCompany
import com.donler.repository.company.CompanyRepository
import com.donler.service.OSSService
import com.donler.service.ValidationUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
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


    @ApiOperation(value = "创建公司", notes = "根据传入的信息创建一个公司,body example: {\"name\": \"动梨软件有限公司\", \"emailSuffix\": \"donler\"}")
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    ResCompany createCompany(
            @RequestPart
                    String body,
            @RequestPart MultipartFile file) {



//        CompanyCreateRequestBody company
//        try {
//            company = new ObjectMapper().readValue(body, CompanyCreateRequestBody)
//        } catch (Exception ex) {
//            throw new AttrNotValidException(ex.localizedMessage)
//        }
//
//        ValidatorFactory vf = Validation.buildDefaultValidatorFactory()
//        Validator vd = vf.getValidator()
//        def set = vd.validate(company)
//        if (set.size() > 0) {
//            def errorMsg = []
//            set.forEach {
//                errorMsg << "[${it.propertyPath}] ${it.message}"
//            }
//            def errMsg = errorMsg.join(',')
//            throw new AttrNotValidException(errMsg)
//        }
        CompanyCreateRequestBody company = ValidationUtil.validateModelAttribute(CompanyCreateRequestBody.class, body)

        // TODO 唯一性检查
        Company saveCompany = companyRepository.save(new Company(
                name: company?.name,
                emailSuffix: company?.emailSuffix,
                image: ossService.uploadFileToOSS(file),
                timestamp: new CreateAndModifyTimestamp(updatedAt: new Date(), createdAt: new Date())
        ))
        return new ResCompany(
                id: saveCompany?.id,
                name: saveCompany?.name,
                emailSuffix: saveCompany?.emailSuffix,
                image: saveCompany?.image,
                timestamp: saveCompany?.timestamp
        )
    }



}
