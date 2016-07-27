package com.donler.controller

import com.donler.exception.AttrNotValidException
import com.donler.exception.BadRequestException
import com.donler.exception.NotFoundException
import com.donler.exception.UnAuthException
import com.donler.model.*
import com.donler.model.persistent.company.Company
import com.donler.model.persistent.team.Team
import com.donler.model.persistent.trend.Activity
import com.donler.model.persistent.trend.CommentArrItem
import com.donler.model.persistent.trend.Showtime
import com.donler.model.persistent.trend.Vote
import com.donler.model.persistent.trend.VoteOptionInfo
import com.donler.model.persistent.user.User
import com.donler.model.request.trend.ActivityPublishRequestBody
import com.donler.model.request.trend.ShowtimePublishRequestBody
import com.donler.model.request.trend.TrendCommentPublishRequestBody
import com.donler.model.request.trend.VotePublishRequestBody
import com.donler.model.response.Activity as ResActivity
import com.donler.model.response.ApproveArrItem as ResApproveArrItem
import com.donler.model.response.CommentArrItem as ResCommentArrItem
import com.donler.model.response.ResponseMsg
import com.donler.model.response.Showtime as ResShowtime
import com.donler.model.response.Vote as ResVote
import com.donler.model.response.VoteOptionInfo as ResVoteOptionInfo
import com.donler.repository.company.CompanyRepository
import com.donler.repository.team.TeamRepository
import com.donler.repository.trend.ActivityRepository
import com.donler.repository.trend.ApproveArrItemRepository
import com.donler.repository.trend.CommentArrItemRepository
import com.donler.repository.trend.ShowtimeRepository
import com.donler.repository.trend.VoteOptionInfoRepository
import com.donler.repository.trend.VoteRepository
import com.donler.repository.user.UserRepository
import com.donler.service.OSSService
import com.donler.service.ValidationUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
/**
 * Created by jason on 5/25/16.
 */
@RestController
@RequestMapping("/trend")
@Api(value = "trend", tags = ["动态"], consumes = "application/json", produces = "application/json")
class TrendController {

    @Autowired
    ShowtimeRepository showtimeRepository

    @Autowired
    ActivityRepository activityRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TeamRepository teamRepository

    @Autowired
    CompanyRepository companyRepository

    @Autowired
    OSSService ossService

    @Autowired
    VoteRepository voteRepository

    @Autowired
    VoteOptionInfoRepository voteOptionInfoRepository

    @Autowired
    ApproveArrItemRepository approveArrItemRepository

    @Autowired
    CommentArrItemRepository commentArrItemRepository

    /**
     * 发布瞬间
     * @param body
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showtime/publish", method = RequestMethod.POST)
    @ApiOperation(value = "发布瞬间", notes = "根据传入信息发布瞬间, body example: {\"activityId\":\"string\",\"content\":\"马克飞象真好用\",\"teamId\":\"string\"}")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    ResShowtime publishShowtime(@RequestPart String body, @RequestPart MultipartFile[] files, HttpServletRequest req) {
        def currentUser = req.getAttribute("user") as User
        ShowtimePublishRequestBody newBody = ValidationUtil.validateModelAttribute(ShowtimePublishRequestBody.class, body)
        def activity = !!newBody?.activityId ? activityRepository.findOne(newBody?.activityId) : null
        def team = !!newBody?.teamId ? teamRepository.findOne(newBody?.teamId) : null
        Showtime showtime = showtimeRepository.save(new Showtime(
                content: newBody?.content,
                activityId: !!activity ? activity.id : null,
                teamId: !!team ? team?.id : null,
                companyId: currentUser?.companyId,
                images: ossService.uploadMultipartFilesToOSS(files),
                authorId: currentUser?.id,
                createdAt: new Date(),
                updatedAt: new Date()

        ))

        // 建立关联 更新活动中的showtimes字段
        if (!!showtime?.activityId) {
            def oldActivity = activityRepository.findOne(showtime?.activityId)
            !!oldActivity?.showtimes ? oldActivity.showtimes.push(showtime?.id) : oldActivity.setShowtimes([showtime?.id])
            activityRepository.save(oldActivity)
        }
        return generateResponseShowtimeByPersistentShowtime(showtime)

    }

    /**
     * 删除瞬间
     * @param showtimeId
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showtime/{showtimeId}", method = RequestMethod.DELETE)
    @ApiOperation(response = ResponseMsg.class, value = "删除瞬间", notes = "根据传入的瞬间id删除一个瞬间")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def deleteShowtimeById(@PathVariable("showtimeId") String showtimeId, HttpServletRequest req) {
        if (showtimeRepository.exists(showtimeId)) {
            def user = req.getAttribute('user') as User
            if (showtimeRepository.findOne(showtimeId).authorId != user.id) {
                throw new UnAuthException("您没有权限这么做!!!")
            }
            showtimeRepository.delete(showtimeId)
            return ResponseMsg.ok("删除成功")
        } else {
            throw new NotFoundException("id为${showtimeId}的瞬间")
        }
    }

    /**
     * 更新瞬间
     * @param showtimeId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showtime/{showtimeId}", method = RequestMethod.PUT)
    @ApiOperation(response = ResponseMsg.class, value = "更新瞬间", notes = "根据传入的瞬间的id更新一个瞬间")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def updateShowtimeById(
            @PathVariable("showtimeId") String showtimeId, @Valid @RequestBody ShowtimePublishRequestBody body) {
        def showtime = showtimeRepository.findOne(showtimeId)
        if (!showtime) {
            throw new NotFoundException("id为: ${showtimeId}的瞬间不存在")
        }
        def newShowtime = showtimeRepository.save(showtime)
        return ResponseMsg.ok(generateResponseShowtimeByPersistentShowtime(newShowtime))
    }

    /**
     * 获取单个瞬间
     * @param showtimeId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showtime/{showtimeId}", method = RequestMethod.GET)
    @ApiOperation(response = ResponseMsg.class, value = "获取指定瞬间", notes = "根据传入的瞬间的id获取一个瞬间")
    def getShowtimeById(@PathVariable("showtimeId") String showtimeId) {
        def showtime = showtimeRepository.findOne(showtimeId)
        if (!showtime) {
            throw new NotFoundException("id为: ${showtimeId}的瞬间不存在")
        }
        return generateResponseShowtimeByPersistentShowtime(showtime)
    }


    // TODO
    /**
     * 发表评论
     * @param body
     */
    @ResponseBody
    @RequestMapping(value = "/comment/to/trend", method = RequestMethod.POST)
    @ApiOperation(response = ResponseMsg.class, value = "发表评论", notes = "为指定的动态发表评论")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def commentToTrend(
            @Valid @RequestBody
                    TrendCommentPublishRequestBody body,
            @RequestParam(required = false)
            @ApiParam(value = "活动的id")
                    String activityId,
            @RequestParam(required = false)
            @ApiParam(value = "投票的id")
                    String voteId,
            @ApiParam(value = "瞬间的id")
            @RequestParam(required = false)
                    String showtimeId,
            @ApiParam(value = "话题的id")
            @RequestParam(required = false)
                    String topicId,
            HttpServletRequest req) {

        def user = req.getAttribute('user') as User
        def querys = [activityId: activityId, voteId: voteId, showtimeId: showtimeId, topicId: topicId]
        def newQuerys = [:]
        querys.each { key, value ->
            println("value${value}")
            if (value != null) {
                newQuerys.put(key, value)
            }
        }

        def result = ''
        if (newQuerys.size() == 1) {
            querys.each { key, value ->
                switch (key) {
                    case 'activityId':

                        break;
                    case 'showtimeId':
                        def showtime = showtimeRepository.findOne(value)
                        if (!!showtime) {
                            def item = commentArrItemRepository.save(new CommentArrItem(
                                    userId: user?.id,
                                    comment: body?.comment,
                                    createdAt: new Date(),
                                    updatedAt: new Date()
                            ))
                            !showtime.comments ? showtime.comments = [item?.id] : showtime.comments.push(item?.id)
                            result = generateResponseShowtimeByPersistentShowtime(showtimeRepository.save(showtime))
                        }else {
                            throw new AttrNotValidException("请输入正确的瞬间id")
                        }
                        break;
                    default: break;
                }
            }
        } else {
            throw new BadRequestException("只能传入一种动态id")
        }

        return ResponseMsg.ok(result)

    }


    @ResponseBody
    @RequestMapping(value = "/comment/by/trend/list", method = RequestMethod.GET)
    @ApiOperation( value = "获取某个动态的评论列表", notes = "根据传入的动态的类型和动态的id来获取该动态的评论列表,其中动态的id必须且只能传入一种和一个")
    ResCommentArrItem[] getCommentListByTrend(
            @ApiParam("活动id")
            @RequestParam(required = false)
                    String activityId,
            @ApiParam("瞬间id")
            @RequestParam(required = false)
                    String showtimeId,
            @ApiParam("投票id")
            @RequestParam(required = false)
                    String voteId,
            @ApiParam("话题id")
            @RequestParam(required = false)
                    String topicId) {
        def querys = [activityId: activityId, showtimeId: showtimeId, voteId: voteId, topicId: topicId]
        def newQuerys = [:]
        querys.each { key, value ->
            if (value != null) {
                newQuerys.put(key, value)
            }
        }

        def result = []
        if (newQuerys.size() == 1) {
            newQuerys.each { key, value ->
                switch (key) {
                    case 'activityId':
                        break
                    case 'showtimeId':
                        def showtime = !!value ? showtimeRepository.findOne(value) : null
                        if (!!showtime) {
                            result = !!showtime?.comments ? showtime?.comments.toArray().collect { inlineValue ->
                                def comment = !!inlineValue ? commentArrItemRepository.findOne(inlineValue) : null
                                if (!!inlineValue) {
                                    return generateResponseCommentArrItemByPersistentCommentArrItem(comment)
                                }
                            } : []
                        } else {
                            throw new AttrNotValidException("请检查传入的showtimeId是否正确")
                        }
                        break
                    default:
                        break
                }
            }
        }else {
            throw new BadRequestException("只能传入一种动态的id")
        }


        return result
    }


    /**
     * 获取指定活动的瞬间
     * @param activityId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showtime/by/activity/{activityId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定活动瞬间", notes = "根据活动的id获取该活动的所有瞬间")
    def getShowtimeByActivityId(@PathVariable("activityId") String activityId) {
        def list = showtimeRepository.findByActivityId(activityId)
        def newList = []
        list.each {
            newList << generateResponseShowtimeByPersistentShowtime(it)
        }
        return newList
    }

    @ResponseBody
    @RequestMapping(value = "/showtime/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有活动", notes = "获取所有瞬间信息")
    List<ResShowtime> getAllShowtimes() {
        List<Showtime> list = showtimeRepository.findAll()
        def newList = []
        list.each {
            newList << generateResponseShowtimeByPersistentShowtime(it)
        }
        return newList
    }

    /**
     * 发布活动
     * @param body
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/activity/publish", method = RequestMethod.POST)
    @ApiOperation(value = "发布活动", notes = "根据传入的信息发布活动, body example: {\"address\":\"string\",\"deadline\":\"2016-07-11T07:38:32.641Z\",\"desc\":\"string\",\"endTime\":\"2016-07-11T07:38:32.641Z\",\"memberMax\":0,\"memberMin\":0,\"name\":\"string\",\"startTime\":\"2016-07-11T07:38:32.641Z\",\"teamId\":\"string\"} ")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    ResActivity publishActivity(@RequestPart String body, @RequestPart MultipartFile[] files, HttpServletRequest req) {
        def currentUser = req.getAttribute("user") as User
        ActivityPublishRequestBody newBody = ValidationUtil.validateModelAttribute(ActivityPublishRequestBody.class, body) as ActivityPublishRequestBody
        def teamId = !!newBody?.teamId ? teamRepository.findOne(newBody?.teamId) : null

        Activity activity = activityRepository.save(new Activity(
                name: newBody?.name,
                image: ossService.uploadFileToOSS(files?.first()),
                teamId: teamId,
                authorId: currentUser?.id,
                companyId: currentUser?.companyId,
                startTime: newBody?.startTime,
                endTime: newBody?.endTime,
                deadline: newBody?.deadline,
                memberMax: newBody?.memberMax,
                memberMin: newBody?.memberMin,
                address: newBody?.address,
                desc: newBody?.desc,
                createdAt: new Date(),
                updatedAt: new Date()
        ))
        return generateResponseActivityByPersistentActivity(activity)
    }



    @ResponseBody
    @RequestMapping(value = "/activity/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有活动", notes = "获取所有活动信息")
    List<ResActivity> getAllActivitys() {
        List<Activity> list = activityRepository.findAll()
        def newList = []
        list.each {
            newList << generateResponseActivityByPersistentActivity(it)
        }
        return newList
    }

    /**
     * 获取指定活动的详情
     * @param activityId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/activity/{activityId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取活动详情", notes = "根据活动的id获取活动的详情")
    ResActivity getActivityById(@PathVariable("activityId") String activityId) {
        return generateResponseActivityByPersistentActivity(activityRepository.findOne(activityId))
    }


    /**
     * 发布投票
     * @param body
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/vote/publish", method = RequestMethod.POST)
    @ApiOperation(value = "发布投票", notes = "根据传入实体生成投票,teamId不传为默认全体可见 body example: {\"content\":\"小张我帅不帅\",\"options\":[\"帅\",\"不帅\"],\"teamId\":\"string\"}")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    ResVote publishVote(@RequestPart String body, @RequestPart MultipartFile[] files, HttpServletRequest req) {

        def currentUser = req.getAttribute("user") as User
        def company = !!currentUser?.companyId ? companyRepository.findOne(currentUser?.companyId) : null
        VotePublishRequestBody newBody = ValidationUtil.validateModelAttribute(VotePublishRequestBody.class, body) as VotePublishRequestBody
        def team = !!newBody?.teamId ? teamRepository.findOne(newBody?.teamId) : null

        def savedVote = voteRepository.save(new Vote(
                image: ossService.uploadFileToOSS(files?.first()),
                teamId: !!team ? team?.id : null,
                companyId: !!company ? company?.id : null,
                content: newBody?.content,
                options: newBody?.options.collect {
                    return (voteOptionInfoRepository.save(
                            new VoteOptionInfo(
                                    option: it,
                                    votedUserIds: []
                            )
                    ) as VoteOptionInfo)?.id
                },
                comments: [],
                authorId: (req.getAttribute("user") as User).id,
                createdAt: new Date(),
                updatedAt: new Date()

        ))

        return generateResponseVoteByPersistentVote(savedVote)

    }



    /**
     * 根据查询id等参数来筛选投票
     * @param teamId
     * @param page
     * @param limit
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/vote/search", method = RequestMethod.GET)
    @ApiOperation(value = "获取投票列表", notes = "根据用户传入的查询参数来进行搜索,如果teamId为空,则默认返回该用户所在公司的所有投票,分页参数中,默认为第0页,并且每一页默认为10条记录")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    Page<ResVote> searchVoteByTeamId(
            @ApiParam(value = "待查询的群组id,为空默认为用户所在公司全部的投票")
            @RequestParam(required = false)
                    String teamId,
            @ApiParam(value = "待查询的页码,默认为第0页")
            @RequestParam(required = false)
                    Integer page,
            @ApiParam(value = "每一页的个数,默认为10")
            @RequestParam(required = false)
                    Integer limit,
            HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def list = !!teamId ? voteRepository.findByTeamId(teamId, new PageRequest(page ?: 0, limit ?: 10)) : voteRepository.findByCompanyId(user?.companyId, new PageRequest(page ?: 0, limit ?: 10))
        return list.map(new Converter<Vote, ResVote>() {
            @Override
            ResVote convert(Vote source) {
                return generateResponseVoteByPersistentVote(source)
            }
        })

    }

    /**
     * 根据传入的持久化瞬间生成res瞬间
     * @param showtime
     * @return
     */
    ResShowtime generateResponseShowtimeByPersistentShowtime(Showtime showtime) {
        Activity activity = !!showtime?.activityId ? activityRepository.findOne(showtime?.activityId) : null
        User showtimeAuthor = userRepository.findOne(showtime?.authorId)
        def activityAuthor = !!activity?.authorId ? userRepository.findOne(activity?.authorId) : null
        Team showtimeTeam = !!showtime?.teamId ? teamRepository.findOne(showtime?.teamId) : null
        Company showtimeCompany = companyRepository.findOne(showtime?.companyId)
        return new ResShowtime(
                id: showtime?.id,
                content: showtime?.content,
                images: showtime?.images,
                activity: !!activity ? new SimpleActivityModel(
                        id: activity?.id,
                        name: activity?.name,
                        image: activity?.image,
                        updatedAt: activity?.updatedAt,
                        createdAt: activity?.createdAt,
                        author: new SimpleUserModel(
                                id: activityAuthor?.id,
                                nickname: activityAuthor?.nickname,
                                avatar: activityAuthor?.avatar
                        )
                ) : null,
                team: !!showtimeTeam ? new SimpleTeamModel(
                        id: showtimeTeam?.id,
                        name: showtimeTeam?.name,
                        imageUrl: showtimeTeam?.image
                ) : null,
                company: !!showtimeCompany ? new SimpleCompanyModel(
                        id: showtimeCompany?.id,
                        name: showtimeCompany?.name,
                        imageUrl: showtimeCompany?.image
                ) : null,
                author: new SimpleUserModel(
                        id: showtimeAuthor?.id,
                        nickname: showtimeAuthor?.nickname,
                        avatar: showtimeAuthor?.avatar
                ),
                createdAt: showtime?.createdAt,
                updatedAt: showtime?.updatedAt,
                approves: showtime?.approves?.collect {
                    def approve = approveArrItemRepository.findOne(it)
                    def user = userRepository.findOne(approve?.userId)
                    return new ResApproveArrItem(
                            id: approve?.id,
                            user: new SimpleUserModel(
                                    id: user?.id,
                                    nickname: user?.nickname,
                                    avatar: user?.avatar
                            ),
                            createdAt: approve?.createdAt,
                            updatedAt: approve?.updatedAt
                    )
                },
                comments: showtime?.comments?.collect {
                    def comment = commentArrItemRepository.findOne(it)
                    def user = userRepository.findOne(comment?.userId)
                    return new ResCommentArrItem(
                            id: comment?.id,
                            user: new SimpleUserModel(
                                    id: user?.id,
                                    nickname: user?.nickname,
                                    avatar: user?.avatar
                            ),
                            comment: comment?.comment,
                            createdAt: comment?.createdAt,
                            updatedAt: comment?.updatedAt
                    )
                }
        )
    }

    /**
     *
     * 根据持久化活动生成res活动
     * @param activity
     * @return
     */
    ResActivity generateResponseActivityByPersistentActivity(Activity activity) {
        if (!activity) return new ResActivity()
        def author = userRepository.findOne(activity?.authorId) as User
        def team = !!activity?.teamId ? teamRepository.findOne(activity?.teamId) : null
        def company = companyRepository.findOne(activity?.companyId) as Company
        return new ResActivity(
                id: activity?.id,
                name: activity?.name,
                image: activity?.image,
                team: !!team ? new SimpleTeamModel(
                        id: team?.id,
                        name: team?.name,
                        imageUrl: team?.image
                ) : null,
                company: new SimpleCompanyModel(
                        id: company?.id,
                        name: company?.name,
                        imageUrl: company?.image
                ),
                author: new SimpleUserModel(
                        id: author?.id,
                        nickname: author?.nickname,
                        avatar: author?.avatar
                ),
                members: activity?.members?.collect {
                    def user = userRepository.findOne(it)
                    return !!user ? new SimpleUserModel(
                            id: user?.id,
                            nickname: user?.nickname,
                            avatar: user?.avatar
                    ) : null
                },
                showtimes: activity?.showtimes?.collect {
                    def showtime = showtimeRepository.findOne(it)
                    def showtimeAuthor = userRepository.findOne(showtime?.authorId)
                    return new SimpleShowtimeModel(
                            id: showtime?.id,
                            content: showtime?.content,
                            images: showtime?.images,
                            author: new SimpleUserModel(
                                    id: showtimeAuthor?.id,
                                    nickname: showtimeAuthor?.nickname,
                                    avatar: showtimeAuthor?.avatar
                            )
                    )
                },
                startTime: activity?.startTime,
                endTime: activity?.endTime,
                deadline: activity?.deadline,
                memberMax: activity?.memberMax,
                memberMin: activity?.memberMin,
                address: activity?.address,
                desc: activity?.desc,
                createdAt: activity?.createdAt,
                updatedAt: activity?.updatedAt
        )
    }

    /**
     * 根据持久化投票模型生成resVote
     * @param vote
     * @return
     */
    ResVote generateResponseVoteByPersistentVote(Vote vote) {
        if (!vote) return new ResVote()
        def company = !!vote?.companyId ? companyRepository.findOne(vote?.companyId) : null
        def team = !!vote?.teamId ? teamRepository.findOne(vote?.teamId) : null
        def user = !!vote?.authorId ? userRepository.findOne(vote?.authorId) : null

        return new ResVote(
                id: vote?.id,
                image: vote?.image,
                company: !!company ? new SimpleCompanyModel(
                        id: company?.id,
                        name: company?.name,
                        imageUrl: company?.image
                ) : null,
                team: !!team ? new SimpleTeamModel(
                        id: team?.id,
                        name: team?.name,
                        imageUrl: team?.image
                ) : null,
                content: vote?.content,
                options: vote?.options?.collect {
                    VoteOptionInfo voteOptionInfo = voteOptionInfoRepository.findOne(it)
                    return new ResVoteOptionInfo(
                            id: voteOptionInfo?.id,
                            option: voteOptionInfo?.option,
                            votedUsers: voteOptionInfo?.votedUserIds?.collect {
                                    User votedUser = userRepository.findOne(it)
                                    return new SimpleUserModel(
                                            id: votedUser?.id,
                                            nickname: votedUser?.nickname,
                                            avatar: votedUser?.avatar
                                    )
                            }
                    )
                },
                comments: vote?.comments?.collect {
                    def comment = commentArrItemRepository.findOne(it)
                    def commentUser = !!comment?.userId ? userRepository.findOne(comment?.userId) : null
                    return new ResCommentArrItem(
                            id: comment?.id,
                            user: new SimpleUserModel(
                                    id: commentUser?.id,
                                    nickname: commentUser?.nickname,
                                    avatar: commentUser?.avatar
                            ),
                            comment: comment?.comment,
                            createdAt: comment?.createdAt,
                            updatedAt: comment?.updatedAt
                    )
                },
                author: !!user ? new SimpleUserModel(
                        id: user?.id,
                        nickname: user?.nickname,
                        avatar: user?.avatar
                ) : null,
                createdAt: vote?.createdAt,
                updatedAt: vote?.updatedAt


        )
    }

    /**
     * 根据持久化的评论item生成res评论item
     * @param commentArrItem
     * @return
     */
    ResCommentArrItem generateResponseCommentArrItemByPersistentCommentArrItem(CommentArrItem commentArrItem) {
        def user = !!commentArrItem?.userId ? userRepository.findOne(commentArrItem?.userId) : null
        return new ResCommentArrItem(
                id: commentArrItem?.id,
                comment: commentArrItem?.comment,
                user: !!user ? new SimpleUserModel(
                        id: user?.id,
                        nickname: user?.nickname,
                        avatar: user?.avatar
                ) : null,
                updatedAt: commentArrItem?.updatedAt,
                createdAt: commentArrItem?.createdAt
        )
    }



}
