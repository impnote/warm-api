package com.donler.controller

import com.donler.exception.AttrNotValidException
import com.donler.model.Constants
import com.donler.model.SimpleCompanyModel
import com.donler.model.SimpleTeamModel
import com.donler.model.SimpleUserModel
import com.donler.model.persistent.team.Team
import com.donler.model.persistent.user.User
import com.donler.model.request.team.TeamCreateRequestBody
import com.donler.model.request.team.TeamDescUpdateRequestBody
import com.donler.model.request.team.TeamInviteMembersRequestBody
import com.donler.model.response.ResponseMsg
import com.donler.model.response.Team as ResTeam
import com.donler.model.response.TeamHomePageModel
import com.donler.repository.company.CompanyRepository
import com.donler.repository.team.TeamRepository
import com.donler.repository.trend.ActivityRepository
import com.donler.repository.trend.ShowtimeRepository
import com.donler.repository.trend.TopicRepository
import com.donler.repository.trend.TrendItemRepository
import com.donler.repository.trend.VoteRepository
import com.donler.repository.user.ColleagueItemRepository
import com.donler.repository.user.UserRepository
import com.donler.service.OSSService
import com.donler.service.ValidationUtil
import com.donler.thirdparty.easemob.server.comm.body.IMUserBody
import com.donler.thirdparty.easemob.server.comm.body.IMUsersBody
import com.donler.thirdparty.easemob.server.comm.body.UserNamesBody
import com.donler.thirdparty.easemob.server.comm.wrapper.BodyWrapper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ContainerNode
import com.fasterxml.jackson.jaxrs.json.annotation.JSONP.Def
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import net.sf.json.JSON
import net.sf.json.JSONString
import org.json.simple.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.sql.Wrapper

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

    @Autowired
    ColleagueItemRepository colleagueItemRepository

    @Autowired
    TrendItemRepository trendItemRepository

    @Autowired
    ActivityRepository activityRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    VoteRepository voteRepository

    @Autowired
    ShowtimeRepository showtimeRepository

    @Autowired
    TrendController trendController

    @Autowired
    EasemobController easemobController




    @ApiOperation(value = "创建群组", notes = "根据传入的信息创建一个群组,body example: {\"name\": \"篮球小队\", \"desc\": \"我们是一个积极向上的团体\"}")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    def createTeam(
            @RequestPart
                    String body,
            @ApiParam(value = "只上传单张图片,此处为了统一接口")
            @RequestPart MultipartFile[] files, HttpServletRequest req) {

        def currentUser = req.getAttribute("user") as User
        if (!currentUser?.companyId ) {
            return ResponseMsg.error("请先加入公司,再来创建群组",200)
        }
        if (!companyRepository.findOne(currentUser?.companyId)) {
            return ResponseMsg.error("请先加入公司,再来创建群组",200)
        }
        TeamCreateRequestBody team = ValidationUtil.validateModelAttribute(TeamCreateRequestBody.class, body) as TeamCreateRequestBody

        def count = teamRepository.countByName(team?.name)
        if (count > 0) {
            throw new AttrNotValidException("群组名称重复")
        }
        Team savedTeam = teamRepository.save(new Team(
                name: team?.name,
                image: ossService.uploadFileToOSS(files?.first()),
                peopleCount: 1,
                authorId: currentUser?.id,
                companyId: currentUser?.companyId,
                desc: team?.desc,
                createdAt: new Date(),
                updatedAt: new Date(),
                members: [currentUser?.id]

        ))
        currentUser.myGroup.add(savedTeam?.id)
        currentUser.myGroup.unique()
        userRepository.save(currentUser)
        def result = easemobController.createChatgroups(savedTeam,currentUser)
        teamRepository.save(result)
        return generateResponseByPersistentTeam(result,currentUser)
    }

    /**
     * 获取群组列表
     * @param emailSuffix
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "获取群组列表(分页)", notes = "获取包含某关键字的群组列表或者该用户所在公司的全部群组列表")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    Page<SimpleTeamModel> searchCompanyByEmailSuffix(
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
        return list.map(new Converter<Team, SimpleTeamModel>() {
            @Override
            SimpleTeamModel convert(Team source) {
                return generateResponseSimpleTeamModelByPersistentTeam(source, currentUser)
            }
        })
    }

    /**
     * 修改群组封面
     * @param teamId
     * @param file
     * @param req
     * @return
     */
    @RequestMapping(path = "/update/picture/{teamId}", method = RequestMethod.POST)
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @ApiOperation(value = "修改群组封面", notes = "修改群组的封面")
    def updateTeamPicture(@PathVariable String teamId, @RequestPart MultipartFile[] files,HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def team = teamRepository.findOne(teamId)
        if (!team) {
            return ResponseMsg.error("请传入正确的群组Id",200)
        }
        if (team.authorId != user.id) {
            return ResponseMsg.error("你不是群主,没有权限进行该操作",200)
        }
        team.image = ossService.uploadFileToOSS(files?.first())
        teamRepository.save(team)
        return ResponseMsg.ok(team.image)
    }

    /**
     * 修改群组公告
     * @param body
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/update/desc", method = RequestMethod.POST)
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @ApiOperation(value = "修改群组公告", notes = "修改群组的公告")
    def updateTeamDesc(@RequestBody TeamDescUpdateRequestBody body, HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def team = teamRepository.findOne(body?.teamId)
        if (!team) {
            return ResponseMsg.error("请传入正确的群组Id",200)
        }
        if (team.authorId != user.id) {
            return ResponseMsg.error("你不是群主,没有权限进行该操作",200)
        }
        team.desc = body?.desc
        teamRepository.save(team)
        return ResponseMsg.ok(team.desc)
    }

    /**
     * 邀请成员
     * @param userId
     * @param teamId
     * @param req
     * @return
     */
    @RequestMapping(path = "invite/member", method = RequestMethod.POST,consumes = "application/json")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @ApiOperation(value = "邀请成员", notes = "邀请成员")
    def inviteMember(@Valid @RequestBody TeamInviteMembersRequestBody body, HttpServletRequest req) {
        def currentUser = req.getAttribute("user") as User
        def currentTeam = teamRepository.findOne(body?.teamId)
        try {
            body?.membersId?.each {
                    def newMember = userRepository.findOne(it)

                    if (!newMember) {
                        throw new Exception("请传入正确的用户ID")
                    }
                    if (!currentTeam) {
                        throw new Exception("请传入正确的群组ID")
                    }
                    if (!currentTeam.companyId.equals(currentUser.companyId)) {
                        throw new Exception("该成员不属于此公司,请检查")
                    }
                    if (currentTeam.members.contains(it)) {
                        throw new Exception("该成员id=${it}已经存在,请检查")
                    }
                    if (!currentTeam.members.contains(currentUser.id)) {
                        throw new Exception("该成员不在该群内,没有邀请权限")
                    }

            }
            def message = easemobController.inviteChatGroupMembers(currentTeam.easemobId, body)
            if (message.statusCode.equals(200)) {
                return ResponseMsg.error("邀请失败", 400)
            }
            body?.membersId?.each {
                currentTeam.members.add(it)
                currentTeam.members.unique()
                currentUser.myGroup.add(body?.teamId)
                currentUser.myGroup.unique()
                userRepository.save(currentUser)
                teamRepository.save(currentTeam)
            }
            return ResponseMsg.error(message.msg,400)
        } catch (Exception ex) {
            return ex.message
        }
    }

    /**
     * 删除群组成员
     * @param body
     * @param req
     * @return
     */
    @RequestMapping(path = "delete/member", method = RequestMethod.DELETE)
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @ApiOperation(value = "删除成员", notes = "删除成员")
    def deleteMember(@Valid @RequestBody TeamInviteMembersRequestBody body, HttpServletRequest req) {
        def currentUser = req.getAttribute("user") as User
        def currentTeam = teamRepository.findOne(body?.teamId)
        if (!currentTeam?.authorId?.equals(currentUser?.id)) {
            return ResponseMsg.error("你无权进行删除操作",200)
        }
        def message = easemobController.deleteChatGroupMembers(currentTeam.easemobId,body)
        if (!message.statusCode.equals(200)) {
            return ResponseMsg.error(message.msg,400)
        }
        body?.membersId?.each {
            currentTeam.members.remove(it)
        }
        teamRepository.save(currentTeam)
         return ResponseMsg.ok("删除成功",200,currentTeam)
    }



    /**
     * 获取群组主页
     * @param teamId
     * @param trendId
     * @param page
     * @param limit
     * @param req
     * @return
     */
    @ApiOperation(value = "获取群组主页", notes = "根据群组Id获取群组首页内容")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    @RequestMapping(path = "/team/detail", method = RequestMethod.GET)
    def getTeamDetail(
            @ApiParam(value = "")
            @RequestParam(required = true)
                    String teamId,
            @ApiParam(value = "")
            @RequestParam(required = false)
                    String trendId,
            @ApiParam(value = "")
            @RequestParam(required = false)
                    Integer page,
            @ApiParam(value = "每一页的个数,默认为10")
            @RequestParam(required = false)
                    Integer limit,
            HttpServletRequest req
    ) {
        def user = req.getAttribute("user") as User
        def team = teamId ? teamRepository.findOne(teamId) : null
        def perTrend = trendId ? trendItemRepository.findOne(trendId) : null
        if (!team) {
            return ResponseMsg.error("请传入正确的群组id", 200)
        }
        def result = generateResponseByPersistentTeam(team, user)
        def list
        def newList = []
        if (!perTrend) {
            list = trendItemRepository.findByTeamId(teamId,new PageRequest(
                    page ?: 0 ,
                    limit ?: 10,
                    new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "createdAt")))))
        } else {
          list =   trendItemRepository.findByTeamIdAndCreatedAt(teamId,perTrend.createdAt,new PageRequest(
                  page ?: 0,
                  limit ?: 10,
                  new Sort(Arrays.asList(new  Sort.Order(Sort.Direction.DESC, "createdAt")))))
        }

        list.each {
            switch (it.typeEnum) {
                case Constants.TypeEnum.Showtime:
                    def newShowtime = showtimeRepository.findOne(it.trendId)
                    def res = trendController.generateResponseShowtimeByPersistentShowtime(newShowtime, user)
                    res.team.isJoined = !!team.members ? team.members.contains(user?.id) : false
                    newList.add(res)
                    break
                case Constants.TypeEnum.Activity:
                    def newActivity = activityRepository.findOne(it.trendId)
                    def res = trendController.generateResponseActivityByPersistentActivity(newActivity, user)
                    res.team.isJoined = !!team.members ? team.members.contains(user?.id) : false
                    newList.add(res)
                    break
                case Constants.TypeEnum.Topic:
                    def newTopic = topicRepository.findOne(it.trendId)
                    def res = trendController.generateResponseTopicByPersistentTopic(newTopic)
                    res.team.isJoined = !!team.members ? team.members.contains(user?.id) : false
                    newList.add(res)
                    break
                case Constants.TypeEnum.Vote:
                    def newVote = voteRepository.findOne(it.trendId)
                    def res = trendController.generateResponseVoteByPersistentVote(newVote, user)
                    res.team.isJoined = !!team.members ? team.members.contains(user?.id) : false
                    newList.add(res)
                    break
            }
        }
        def dic = [:]
        dic["votesNumber"] = user?.votes?.size()?:0
        dic["topicsNumber"] = user?.topics?.size()?:0
        dic["activitiesNumber"] = user?.activities?.size()?:0
        dic["content"] = newList
        dic["last"] = list.last
        dic["first"] = list.first
        dic["totalElement"] = list.totalElements
        dic["totalPages"] = list.totalPages
        dic["size"] = list.size
        dic["number"] = list.number
        dic["numberOfElements"] = list.numberOfElements
        def sort
        sort = list.sort
        dic["sort"] = sort
        result.teamTrend = dic
        return result


    }

    /**
     * 更具持久化生成响应的team model
     * @param team
     * @return
     */
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
                ) : null,
                company: !!company ? new SimpleCompanyModel(
                        id: company?.id,
                        name: company?.name,
                        imageUrl: company?.image
                ) : null,
                peopleCount: team?.members?.size(),
                desc: team?.desc,
                image: team?.image,
                createdAt: team?.createdAt,
                updatedAt: team?.updatedAt,
                easemobId: team?.easemobId

        )
    }

    /**
     * 根据持久化的生成相应的详细群组model
     * @param team
     * @param user
     * @return
     */
    TeamHomePageModel generateResponseByPersistentTeam(Team team, User user) {
        def author = userRepository.findOne(team?.authorId)
        def company = companyRepository.findOne(team?.companyId)
        return new TeamHomePageModel(
                id: team?.id,
                name: team?.name,
                author: !!author ? new SimpleUserModel(
                        id: author?.id,
                        nickname: author?.nickname,
                        avatar: author?.avatar,
                        remark: !!user?.addressBook ? {
                            for (int i = 0; i < user.addressBook.size(); i++) {
                                def item = colleagueItemRepository.findOne(user.addressBook[i])
                                if (item.colleagueId == it) {
                                    return item.memo
                                }
                            }} : null
                ) : null,
                company: !!company ? new SimpleCompanyModel(
                        id: company?.id,
                        name: company?.name,
                        imageUrl: company?.image
                ) : null,
                peopleCount: team?.members?.size(),
                desc: team?.desc,
                image: team?.image,
                createdAt: team?.createdAt,
                updatedAt: team?.updatedAt,
                members: !!team?.members ? team?.members?.collect {
                    def currentUser = userRepository.findOne(it)
                    if (!currentUser) {
                        return
                    }
                        return new SimpleUserModel(
                                id: currentUser?.id,
                                nickname: currentUser?.nickname,
                                avatar: currentUser?.avatar,
                                realname: currentUser?.realname,
                                phone: currentUser?.phone,
                                job: currentUser?.job,
                                remark: !!user?.addressBook ? getRemark(user,it) : null
                        )


                } : null,
                easemobId: team?.easemobId,
                teamTrend: [:]

        )
    }

    def getRemark(User user,String colleagueId) {
        for (int i = 0; i < user.addressBook.size(); i++) {
            def item = colleagueItemRepository.findOne(user.addressBook[i])
            println(item.memo)
            if (item.colleagueId.equals(colleagueId)) {
                return item.memo
            }
        }

    }

    static def generateResponseSimpleTeamModelByPersistentTeam(Team team, User user) {

        return new SimpleTeamModel(
                id: team?.id,
                name: team?.name,
                imageUrl: team?.image,
                easemobId: team?.easemobId,
                isJoined: !!team.members ? team.members.contains(user.id) : false)
    }


}
