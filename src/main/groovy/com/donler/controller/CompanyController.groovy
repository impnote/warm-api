package com.donler.controller

import com.donler.exception.AttrNotValidException
import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.persistent.company.Company
import com.donler.model.request.company.CompanyCreateRequestBody
import com.donler.model.response.Company as ResCompany
import com.donler.repository.company.CompanyRepository
import com.donler.service.OSSService
import com.donler.service.ValidationUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Path

/**
 * Created by jason on 5/27/16.
 */
@RestController
@RequestMapping("/company")
@Api(value = "company", tags = ["公司"])
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
            @RequestPart MultipartFile[] files) {

        CompanyCreateRequestBody company = ValidationUtil.validateModelAttribute(CompanyCreateRequestBody.class, body)

        def count = companyRepository.countByName(company?.name)
        if (count > 0) {
            throw new AttrNotValidException("该公司已经存在")
        }
        Company saveCompany = companyRepository.save(new Company(
                name: company?.name,
                emailSuffix: company?.emailSuffix,
                image: ossService.uploadFileToOSS(files.first()),
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

    /**
     * 获取公司列表
     * @param emailSuffix
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "获取公司列表(分页)", notes = "获取指定邮箱后缀的公司列表或者全部公司列表")
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    Page<ResCompany> searchCompanyByEmailSuffix(
            @ApiParam(value = "待查询的邮箱后缀,为空则返回所有公司")
            @RequestParam(required = false)
                    String emailSuffix,
            @ApiParam(value = "待查询的页码,默认为第0页")
            @RequestParam(required = false)
                    Integer page,
            @ApiParam(value = "每一页的个数,默认为10")
            @RequestParam(required = false)
                    Integer limit
    ) {
        def list = !!emailSuffix ? companyRepository.findByEmailSuffix(emailSuffix, new PageRequest(page ?: 0, limit ?: 10)) : companyRepository.findAll(new PageRequest(page ?: 0, limit ?: 10))
        return list.map(new Converter<Company, ResCompany>() {
            @Override
            ResCompany convert(Company source) {
                return new ResCompany(
                        id: source?.id,
                        name: source?.name,
                        emailSuffix: source?.emailSuffix,
                        image: source?.image,
                        timestamp: source?.timestamp
                )
            }
        })
    }



}
