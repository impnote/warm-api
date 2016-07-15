package com.donler.controller

import com.donler.exception.AttrNotValidException
import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.SimpleCompanyModel
import com.donler.model.SimpleUserModel
import com.donler.model.persistent.company.Company
import com.donler.model.persistent.team.Team
import com.donler.model.persistent.user.User
import com.donler.model.response.Team as ResTeam
import com.donler.model.request.company.CompanyCreateRequestBody
import com.donler.model.request.team.TeamCreateRequestBody
import com.donler.repository.company.CompanyRepository
import com.donler.repository.team.TeamRepository
import com.donler.repository.user.UserRepository
import com.donler.service.OSSService
import com.donler.service.ValidationUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest

/**
 * Created by jason on 5/27/16.
 */
@RestController
@RequestMapping("/team")
@Api(value = "team", tags = ["群组"])
class TeamController {

    @Autowired
    TeamRepository teamRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CompanyRepository companyRepository

    @Autowired
    OSSService ossService


    @ApiOperation(value = "创建群组", notes = "根据传入的信息创建一个群组,body example: {\"name\": \"篮球小队\", \"desc\": \"我们是一个积极向上的团体\"}")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    ResTeam createTeam(
            @RequestPart
                    String body,
            @ApiParam(value = "只上传单张图片,此处为了统一接口")
            @RequestPart MultipartFile[] files, HttpServletRequest req) {

        def currentUser = req.getAttribute("user") as User
        println(currentUser)
        TeamCreateRequestBody team = ValidationUtil.validateModelAttribute(TeamCreateRequestBody.class, body)

        def count = teamRepository.countByName(team?.name)
        if (count > 0) {
            throw new AttrNotValidException("群组名称重复")
        }
        Team savedTeam = teamRepository.save(new Team(
                name: team?.name,
                image: ossService.uploadFileToOSS(files.first()),
                peopleCount: 1,
                authorId: currentUser?.id,
                companyId: currentUser?.companyId,
                desc: team?.desc,
                timestamp: new CreateAndModifyTimestamp(updatedAt: new Date(), createdAt: new Date())
        ))
        return generateResponseByPersistentTeam(savedTeam)
    }

    /**
     * 获取公司列表
     * @param emailSuffix
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "获取群组列表(分页)", notes = "获取包含某关键字的群组列表或者该用户所在公司的全部群组列表")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    Page<ResTeam> searchCompanyByEmailSuffix(
            @ApiParam(value = "待查询的群组名称关键字,为空则返回该用户所在公司的所有群组")
            @RequestParam(required = false)
                    String teamKeyword,
            @ApiParam(value = "待查询的页码,默认为第0页")
            @RequestParam(required = false)
                    Integer page,
            @ApiParam(value = "每一页的个数,默认为10")
            @RequestParam(required = false)
                    Integer limit,
            HttpServletRequest req
    ) {
        def currentUser = req.getAttribute("user") as User
        def list = !!teamKeyword ? teamRepository.findByNameLike(teamKeyword, new PageRequest(page ?: 0, limit ?: 10)) : teamRepository.findByCompanyId(currentUser?.companyId, new PageRequest(page ?: 0, limit ?: 10))
        return list.map(new Converter<Team, ResTeam>() {
            @Override
            ResTeam convert(Team source) {
                return generateResponseByPersistentTeam(source)
            }
        })
    }


    ResTeam generateResponseByPersistentTeam(Team team) {
        def author = userRepository.findOne(team?.authorId)
        def company = companyRepository.findOne(team?.companyId)
        return new ResTeam(
                id: team?.id,
                name: team?.name,
                author: !!author ? new SimpleUserModel(
                        id: author?.id,
                        nickname: author?.nickname,
                        avatar: author?.avatar
                ): null,
                company: !!company ? new SimpleCompanyModel(
                        id: company?.id,
                        name: company?.name,
                        imageUrl: company?.image
                ) : null,
                peopleCount: team?.peopleCount,
                desc: team?.desc,
                image: team?.image,
                timestamp: team?.timestamp
        )
    }



}
