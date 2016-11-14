package com.donler.controller

import com.donler.exception.AttrNotValidException
import com.donler.exception.BadRequestException
import com.donler.exception.NotFoundException
import com.donler.exception.UnAuthException
import com.donler.model.*
import com.donler.model.persistent.company.Company
import com.donler.model.persistent.team.Team
import com.donler.model.persistent.trend.*
import com.donler.model.persistent.user.User
import com.donler.model.request.trend.*
import com.donler.model.response.Activity as ResActivity
import com.donler.model.response.ApproveArrItem as ResApproveArrItem
import com.donler.model.response.CommentArrItem as ResCommentArrItem
import com.donler.model.response.ResponseMsg
import com.donler.model.response.Showtime as ResShowtime
import com.donler.model.response.Topic as ResTopic
import com.donler.model.response.Vote as ResVote
import com.donler.model.response.VoteOptionInfo as ResVoteOptionInfo
import com.donler.repository.company.CompanyRepository
import com.donler.repository.team.TeamRepository
import com.donler.repository.trend.*
import com.donler.repository.user.UserRepository
import com.donler.service.OSSService
import com.donler.service.ValidationUtil
import com.sun.scenario.effect.Offset
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import net.sf.json.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import springfox.documentation.spring.web.json.Json

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
    TopicRepository topicRepository

    @Autowired
    VoteOptionInfoRepository voteOptionInfoRepository

    @Autowired
    ApproveArrItemRepository approveArrItemRepository

    @Autowired
    CommentArrItemRepository commentArrItemRepository

    @Autowired
    TrendItemRepository trendItemRepository

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
        trendItemRepository.save(new TrendItem(
                typeEnum: Constants.TypeEnum.Showtime,
                trendId: showtime.id,
                companyId: currentUser.companyId,
                createdAt: showtime.createdAt,
                updatedAt: showtime.updatedAt
        ))
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

    /**
     * 根据指定id分页获取该瞬间之前的瞬间
     * @param showtimeId
     * @param page
     * @param limit
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showtime/list", method = RequestMethod.GET)
    @ApiOperation(value = "分片加载瞬间", notes = "获取指定瞬间之前的瞬间,如果需要查询的瞬间的id没有传或者不存在,则返回最新的n条记录,limit为限制本次返回的记录条数")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    Page<ResShowtime> listShowtimeByLastItemId(
            @RequestParam(required = false)
            @ApiParam("指定瞬间的id")
                    String showtimeId,
            @RequestParam(required = false)
            @ApiParam("页数,默认第0页")
                    Integer page,
            @RequestParam(required = false)
            @ApiParam("每页条数,默认第10条")
                    Integer limit, HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def perShowtime = !!showtimeId ? showtimeRepository.findOne(showtimeId) : null
        def list
        if (!perShowtime) {
            // 为空则返回最新的n条
//            list = showtimeRepository.findAll(
//                    new PageRequest(
//                            page ?: 0,
//                            limit ?: 10,
//                            new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "updatedAt")))))
            list = showtimeRepository.findByCompanyId(user.companyId, new PageRequest(
                    page ?: 0,
                    limit ?: 10,
                    new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "updatedAt")))))
        } else {
            // 不为空返回指定瞬间的前n条记录
            list = showtimeRepository.findByUpdatedAtBefore(
                    perShowtime?.updatedAt,
                    new PageRequest(
                            page ?: 0,
                            limit ?: 10,
                            new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "updatedAt")))))
        }
        return list.map(new Converter() {
            @Override
            Object convert(Object source) {
                return generateResponseShowtimeByPersistentShowtime(source as Showtime, user)
            }
        })
    }

    // TODO 其余点赞
    /**
     * 点赞功能
     * @param body
     * @param req
     * @return
     */
    @RequestMapping(value = "/approve/to/trend", method = RequestMethod.POST)
    @ApiOperation(response = ResponseMsg.class, value = "点赞", notes = "为指定的动态点赞,四个动态的id只能传入一个,如果该用户已经点过赞,则为取消点赞")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def approveToTrend(
            @RequestBody
                    TrendTypeRequestBody body,
            HttpServletRequest req
    ) {
        def user = req.getAttribute("user") as User
        def querys = [activityId: body?.activityId, voteId: body?.voteId, showtimeId: body?.showtimeId, topicId: body?.topicId]
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
                        if (!showtime) {
                            throw new NotFoundException("瞬间id不存在")
                        }
                        def approves = showtime?.approves ?: []
                        def needDeleteApproveId = ''  //记录需要取消点赞的approveId
                        approves.each {
                            def approve = approveArrItemRepository.findOne(it)
                            if (approve?.userId == user?.id) {
                                needDeleteApproveId = approve?.id
                            }
                        }
                        if (!needDeleteApproveId) {
                            def approve = !!showtime ? approveArrItemRepository.save(new ApproveArrItem(
                                    userId: user?.id,
                                    createdAt: new Date(),
                                    updatedAt: new Date()
                            )) : null
                            approves.add(approve?.id)
                            showtime.approves = approves
                            result = generateResponseShowtimeByPersistentShowtime(showtimeRepository.save(showtime), user)

                        } else {
                            approves.remove(needDeleteApproveId)
                            approveArrItemRepository.delete(needDeleteApproveId)
                            showtime.approves = approves
                            result = generateResponseShowtimeByPersistentShowtime(showtimeRepository.save(showtime), user)
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

    // TODO 其余评论
    /**
     * 发表评论
     * @param body
     */
    @ResponseBody
    @RequestMapping(value = "/comment/to/trend", method = RequestMethod.POST)
    @ApiOperation(response = ResponseMsg.class, value = "发表评论", notes = "为指定的动态发表评论,四个动态的id只能传一个,replyToCommentId可不传,不传为发表评论,传入则为为指定评论进行回复")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def commentToTrend(
            @Valid @RequestBody
                    TrendCommentPublishRequestBody body,

            HttpServletRequest req) {
        def user = req.getAttribute('user') as User
        def querys = [activityId: body?.activityId, voteId: body?.voteId, showtimeId: body?.showtimeId, topicId: body?.topicId]
        def newQuerys = [:]
        querys.each { key, value ->
            println("value${value}")
            if (value != null) {
                newQuerys.put(key, value)
            }
        }

        def result = ''
        if (newQuerys.size() == 1) {
            newQuerys.each { key, value ->
                switch (key) {
                    case 'activityId':
                        def activity = activityRepository.findOne(value as String)
                        def comment = !!body?.replyToCommentId ? commentArrItemRepository.findOne(body?.replyToCommentId) : null

                        if (!!activity) {
                            def item = commentArrItemRepository.save(new CommentArrItem(
                                    userId: user?.id,
                                    comment: body?.comment,
                                    createdAt: new Date(),
                                    updatedAt: new Date(),
                                    replyToCommentId: !!comment ? comment?.id : null
                            ))
                            !activity.comments ? activity.comments = [item?.id] : activity.comments.push(item?.id)
                            result = generateResponseActivityByPersistentActivity(activityRepository.save(activity))
                        } else {
                            throw new AttrNotValidException("请输入正确的活动id")
                        }

                        break;
                    case 'showtimeId':
                        def showtime = showtimeRepository.findOne(value as String)
                        def comment = !!body?.replyToCommentId ? commentArrItemRepository.findOne(body?.replyToCommentId) : null

                        if (!!showtime) {
                            def item = commentArrItemRepository.save(new CommentArrItem(
                                    userId: user?.id,
                                    comment: body?.comment,
                                    createdAt: new Date(),
                                    updatedAt: new Date(),
                                    replyToCommentId: !!comment ? comment?.id : null
                            ))
                            !showtime.comments ? showtime.comments = [item?.id] : showtime.comments.push(item?.id)
                            result = generateResponseShowtimeByPersistentShowtime(showtimeRepository.save(showtime))
                        } else {
                            throw new AttrNotValidException("请输入正确的瞬间id")
                        }
                        break;
                    case 'voteId':
                        def vote = voteRepository.findOne(value as String)
                        def comment = !!body?.replyToCommentId ? commentArrItemRepository.findOne(body?.replyToCommentId) : null

                        if (!!vote) {
                            def item = commentArrItemRepository.save(new CommentArrItem(
                                    userId: user?.id,
                                    comment: body?.comment,
                                    createdAt: new Date(),
                                    updatedAt: new Date(),
                                    replyToCommentId: !!comment ? comment?.id : null
                            ))
                            !vote.comments ? vote.comments = [item?.id] : vote.comments.push(item?.id)
                            result = generateResponseVoteByPersistentVote(voteRepository.save(vote))
                        } else {
                            throw new AttrNotValidException("请输入正确的投票id")
                        }
                        break;
                    case 'topicId':
                        def topic = topicRepository.findOne(value as String)
                        def comment = !!body?.replyToCommentId ? commentArrItemRepository.findOne(body?.replyToCommentId) : null

                        if (!!topic) {
                            def item = commentArrItemRepository.save(new CommentArrItem(
                                    userId: user?.id,
                                    comment: body?.comment,
                                    createdAt: new Date(),
                                    updatedAt: new Date(),
                                    replyToCommentId: !!comment ? comment?.id : null
                            ))
                            !topic.comments ? topic.comments = [item?.id] : topic.comments.push(item?.id)
                            result = generateResponseTopicByPersistentTopic(topicRepository.save(topic))
                        } else {
                            throw new AttrNotValidException("请输入正确的投票id")
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

    // TODO 仿造获取某个动态的评论列表接口写获取某个动态的点赞数组接口

    /**
     * 获取某个动态的评论列表
     * @param activityId
     * @param showtimeId
     * @param voteId
     * @param topicId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/comment/by/trend/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取某个动态的评论列表", notes = "根据传入的动态的类型和动态的id来获取该动态的评论列表,其中动态的id必须且只能传入一种和一个")
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
                    String topicId,
            HttpServletRequest req
    ) {
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
                        def activity = !!value ? activityRepository.findOne(value as String) : null
                        if (!!activity) {
                            result = !!activity?.comments ? activity?.comments.toArray().collect { inlineValue ->
                                def comment = !!inlineValue ? commentArrItemRepository.findOne(inlineValue) : null
                                if (!!inlineValue) {
                                    return generateResponseCommentArrItemByPersistentCommentArrItem(comment)
                                }
                            } : []
                        } else {
                            throw new AttrNotValidException("请检查传入的activityId是否正确")
                        }
                        break
                    case 'showtimeId':

                        def showtime = !!value ? showtimeRepository.findOne(value as String ) : null
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
                    case 'voteId':
                        def vote = !!value ? voteRepository.findOne(value as String) : null
                        if (!!vote) {
                            result = !!vote?.comments ? vote?.comments.toArray().collect { inlineValue ->
                                def comment = !!inlineValue ? commentArrItemRepository.findOne(inlineValue) : null
                                if (!!inlineValue) {
                                    return generateResponseCommentArrItemByPersistentCommentArrItem(comment)
                                }
                            } : []
                        } else {
                            throw new AttrNotValidException("请检查传入的voteId是否正确")
                        }
                        break
                    case 'topicId':
                        def topic = !!value ? topicRepository.findOne(value as String) : null
                        if (!!topic) {
                            result = !!topic.comments ? topic?.comments.toArray().collect { inlineValue ->
                                def comment = !!inlineValue ? commentArrItemRepository.findOne(inlineValue) : null
                                if (!!inlineValue) {
                                    return generateResponseCommentArrItemByPersistentCommentArrItem(comment)
                                }

                            } : []
                        }else {
                            throw new AttrNotValidException("请检查传入的voteId是否正确")
                        }
                        break
                    default:
                        break
                }
            }
        } else {
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

    /**
     * 获取所有瞬间
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showtime/list/all", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有瞬间", notes = "获取所有瞬间信息")
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
        trendItemRepository.save(new TrendItem(
                typeEnum: Constants.TypeEnum.Activity,
                trendId: activity.id,
                companyId: currentUser.companyId,
                createdAt: activity.createdAt,
                updatedAt: activity.updatedAt
        ))
        def activityList = []
        !!currentUser?.activities ? activityList = currentUser?.activities : null
        activityList.add(activity?.id)
        currentUser.activities = activityList
        userRepository.save(currentUser)
        return generateResponseActivityByPersistentActivity(activity)
    }

    /**
     * 获取所有活动
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/activity/list/all", method = RequestMethod.GET)
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
    * 分页加载活动
    * @param activityId
    * @param page
    * @param limit
    * @param req
    * @return
     */
    @ResponseBody
    @RequestMapping(value = "//activity/list", method = RequestMethod.GET)
    @ApiOperation(value = "分页加载活动", notes = "如果传入置顶的activityId则加载该Id之前的活动,否则获取最新的activity")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    Page<ResActivity> getActivityByLastActivityId(
            @RequestParam(required = false)
            @ApiParam("指定活动的id")
                    String activityId,
            @RequestParam(required = false)
            @ApiParam("页数,默认第0页")
                    Integer page,
            @RequestParam(required = false)
            @ApiParam("每页条数,默认10条")
                    Integer limit, HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def perActivity = !!activityId ? activityRepository.findOne(activityId) : null
        def list
        if (!perActivity) {
            // 为空则返回最新的n条
            list = activityRepository.findByCompanyId(user?.companyId, new PageRequest(
                    page ?: 0,
                    limit ?: 10,
                    new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "createdAt"))

                    )))
        } else {
            //不为空返回指定瞬间的前n条记录
            list =activityRepository.findByCreatedAtBefore(perActivity?.createdAt,
                    new PageRequest(
                            page ?: 0,
                            limit ?: 10,
                            new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "createdAt"))))
            )
        }
        return  list.map(new Converter() {
            @Override
            Object convert(Object source) {
                return generateResponseActivityByPersistentActivity(source as Activity,user)
            }
        })

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
* 报名活动
* @param activityId
* @param req
* @return
 */
    @ResponseBody
    @RequestMapping(value = "/activity/sign-up", method = RequestMethod.POST)
    @ApiOperation(value = "报名活动", notes = "根据活动id进行报名,首次调用为报名,再次调用为取消报名,是否报名的字段为isSignUp")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def signUpActivity(@RequestParam String activityId, HttpServletRequest req ) {
        def user = req.getAttribute("user") as User
        def currentActivity = !!activityId ? activityRepository.findOne(activityId) : null
        def members = !!currentActivity.members ?  currentActivity.members : []
        if (generateResponseActivityByPersistentActivity(currentActivity, user).isSignUp) {
            members.remove(user?.id)
            currentActivity.members = members
            activityRepository.save(currentActivity)
        } else {
            members.add(user?.id)
            currentActivity.members = members
            activityRepository.save(currentActivity)
        }

        return generateResponseActivityByPersistentActivity(currentActivity,user)
    }

    /**
     * 发布投票
     * @param body
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/vote/publish", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
    @ApiOperation(value = "发布投票", notes = "根据传入实体生成投票,teamId不传为默认全体可见 body example: {\"content\":\"小张我帅不帅\",\"options\":[\"帅\",\"不帅\"],\"teamId\":\"string\"}")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    ResVote publishVote(
            @RequestPart String body, @RequestPart(required = false) MultipartFile[] files, HttpServletRequest req) {

        def currentUser = req.getAttribute("user") as User
        def company = !!currentUser?.companyId ? companyRepository.findOne(currentUser?.companyId) : null
        VotePublishRequestBody newBody = ValidationUtil.validateModelAttribute(VotePublishRequestBody.class, body) as VotePublishRequestBody
        def team = !!newBody?.teamId ? teamRepository.findOne(newBody?.teamId) : null

        def savedVote = voteRepository.save(new Vote(
                image: !!files && files.length > 0 ? ossService.uploadFileToOSS(files?.first()) : null,
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
        trendItemRepository.save(new TrendItem(
                typeEnum: Constants.TypeEnum.Vote,
                trendId: savedVote.id,
                companyId: currentUser.companyId,
                createdAt: savedVote.createdAt,
                updatedAt: savedVote.updatedAt
        ))
        def votesList = []
        !!currentUser.votes ? votesList = currentUser?.votes : null
        votesList.add(savedVote?.id)
        currentUser?.votes = votesList
        userRepository.save(currentUser)
        return generateResponseVoteByPersistentVote(savedVote,currentUser)

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
                return generateResponseVoteByPersistentVote(source,user)
            }
        })

    }

    /**
     * 获取投票列表
     * @param voteId
     * @param page
     * @param limit
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/vote/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取投票列表", notes = "根据用户传入的查询参数进行搜索,如果voteId为空,则默认返回该用户所在公司的所有投票")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    Page<ResVote> getVoteByLastItemId(
            @RequestParam(required = false)
            @ApiParam("指定投票的id")
                    String voteId,
            @RequestParam(required = false)
            @ApiParam("页数,默认第0页")
                    Integer page,
            @RequestParam(required = false)
            @ApiParam("每页条数,默认10条")
                    Integer limit, HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def perVote = !!voteId ? voteRepository.findOne(voteId) : null
        def list
        if (!perVote) {
            // 为空则返回最新的n条
            list = voteRepository.findByCompanyId(user?.companyId, new PageRequest(
                    page ?: 0,
                    limit ?: 10,
                    new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "updatedAt")))))
        } else {
            //不为空返回指定瞬间的前n条记录
            list = voteRepository.findByUpdatedAtBefore(
                    perVote?.updatedAt,
                    new PageRequest(
                            page ?: 0,
                            limit ?: 10,
                            new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "updateAt")))))
        }
        return list.map(new Converter() {
            @Override
            Object convert(Object source) {
                return generateResponseVoteByPersistentVote(source as Vote,user)
            }
        })
    }

    /**
     * 投票操作
     * @param voteOptionInfoId
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/vote/voteOption", method = RequestMethod.POST)
    @ApiOperation(value = "投票操作", notes = "根据投票Id进行投票操作, body example: {\"voteId\":\"string\",\"voteOptionInfoId\":\"string\"}")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def voteOperation(@Valid @RequestBody VoteOperationRequestBody body, HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def vote = voteRepository.findOne(body?.voteId)
        //判断投票是否投过
        for (int i = 0; vote.options.size() > i; i++) {
            def currentOption = voteOptionInfoRepository.findOne(vote.options.get(i))
            for (int j = 0; currentOption.votedUserIds.size() > j; j++) {
                if (user?.id == currentOption.votedUserIds.get(j)) {
                    return ResponseMsg.ok(["errMsg": "已经投过票", "errNo": 404])
                }
            }

        }
//        if (vote?.isVoted) {
//            return ResponseMsg.ok(["errMsg": "已经投过票", "errNo": 404])
//        }
        def voteOption = voteOptionInfoRepository.findOne(body?.voteOptionInfoId)
        if (!voteOption) {
            throw new NotFoundException("id为:${body?.voteOptionInfoId}的投票不存在")
        }
        def votedUserIds = voteOption?.votedUserIds ?: []
//        votedUserIds.each {
//            if (user.id == it) {
//                return ResponseMsg.ok(["errMsg":"已经投过票","errNo":404])
//            }
//        }
        for (int i = 0; i < votedUserIds.size(); i++) {
            def votedUserId = votedUserIds[i]
            if (user.id == votedUserId) {
                return ResponseMsg.ok(["errMsg": "已经投过票", "errNo": 404])
            }
        }
        votedUserIds.add(user?.id)
        voteOption.votedUserIds = votedUserIds
        voteOptionInfoRepository.save(voteOption)
//        vote.isVoted = true
        voteRepository.save(vote)
        return ResponseMsg.ok(generateResponseVoteByPersistentVote(vote,user))
    }


    @ResponseBody
    @RequestMapping(value = "/vote/voters", method = RequestMethod.GET)
    @ApiOperation(value = "投票人详情", notes = "根据投票选项Id进行获取投票人详情")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def getVotersInfo(@RequestParam(required = true)
                                  String voteId) {
        def currentVote = !!voteId ? voteRepository.findOne(voteId) : null
        def options = []
        def dic = [:]
        currentVote.options.each {
            println("id:"+it)
            def currentOption = voteOptionInfoRepository.findOne(it)
            def d = [:]
            println("id:"+currentOption?.id)
            d["id"] = currentOption?.id
            d["optionName"] = currentOption.option
            def users = []
            currentOption.votedUserIds.each {
              def user = userRepository.findOne(it)
                dic["id"] = user?.id
                dic["imgUrl"] = user?.avatar
                users.add(dic)
            }
            d["users"] = users
            options.add(d)
        }
        return options as JSON
    }

    /**
     * 发布话题
     * @param body
     * @param files
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/topic/publish", method = RequestMethod.POST)
    @ApiOperation(value = "发布话题", notes = "根据请求体发布话题 bodyexample {\"content\":\"这是话题内容\",\"title\":\"这是一个话题的标题\",\"teamId\":\"ABC\"}")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    ResTopic publishTopic(
            @RequestPart
                    String body,

            @RequestPart
                    MultipartFile[] files,
            HttpServletRequest req
    ) {
        def currentUser = req.getAttribute("user") as User
        def topicBody = ValidationUtil.validateModelAttribute(TopicPublishRequestBody.class, body) as TopicPublishRequestBody
        def saveTopic = topicRepository.save(new Topic(
                title: topicBody?.title,
                content: topicBody?.content,
                companyId: currentUser?.companyId,
                teamId: topicBody?.teamId,
                comments: [],
                authorId: currentUser?.id,
                createdAt: new Date(),
                updatedAt: new Date(),
                image: files.size() == 0 ? null : ossService.uploadFileToOSS(files.first())
        ))
        trendItemRepository.save(new TrendItem(
                typeEnum: Constants.TypeEnum.Topic,
                trendId: saveTopic.id,
                companyId: currentUser.companyId,
                createdAt: saveTopic.createdAt,
                updatedAt: saveTopic.updatedAt
        ))
        def topicsList = []
        !!currentUser?.topics ? topicsList = currentUser?.topics : null
        topicsList.add(saveTopic?.id)
        currentUser?.topics = topicsList
        userRepository.save(currentUser)
        return generateResponseTopicByPersistentTopic(saveTopic)
    }

    /**
     * 删除话题
     * @param topicId
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/topic/{topicId}", method = RequestMethod.DELETE)
    @ApiOperation(response = ResponseMsg.class, value = "删除话题", notes = "根据传入的话题id删除一个话题")
    @ApiImplicitParam(value = "x-token", required = true, paramType = 'header', name = "x-token")
    def deleteTopicById(@PathVariable String topicId, HttpServletRequest req) {
        if (topicRepository.exists(topicId)) {
            def user = req.getAttribute('user') as User
            if (topicRepository.findOne(topicId).authorId != user.id) {
                throw new UnAuthException("您没有权限这么做!!!")
            }
            topicRepository.delete(topicId)
            return ResponseMsg.ok("删除成功")
        } else {
            throw new NotFoundException("id为${topicId}的话题")
        }

    }

    /**
     * 更新话题
     * @param topicId
     * @param body
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/topic/{topicId}", method = RequestMethod.PUT)
    @ApiOperation(response = ResponseMsg.class, value = "更新话题", notes = "根据传入的话题ID更新一个话题")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def updateTopicById(@PathVariable String topicId, @Valid @RequestBody TopicPublishRequestBody body) {
        def topic = topicRepository.findOne(topicId)
        if (!topic) {
            throw new NotFoundException("id为: ${topicId}的话题不存在")
        }
        topic.title = body?.title
        topic.content = body?.content
        def newTopic = topicRepository.save(topic)
        return ResponseMsg.ok(generateResponseTopicByPersistentTopic(newTopic))
    }

    /**
     * 获取单个话题
     * @param topicId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/topic/{topicId}", method = RequestMethod.GET)
    @ApiOperation(response = ResponseMsg.class, value = "获取指定话题", notes = "根据传入的话题id获取一个话题")
    def getTopicById(@PathVariable String topicId) {
        def topic = topicRepository.findOne(topicId)
        if (!topic) {
            throw new NotFoundException("id为: ${topicId}的话题不存在")
        }
        return generateResponseTopicByPersistentTopic(topic)
    }

    /**
     * 获取所有话题
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/topic/list/all", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有话题", notes = "如果传trendId获取所有话题信息")
    List<ResTopic> getAllTopics() {
        List<Topic> list = topicRepository.findAll()
        def newList = []
        list.each {
            newList << generateResponseTopicByPersistentTopic(it)
        }
        return newList
    }

    /**
      * 分页加载话题
      * @param topicId
      * @param page
      * @param limit
      * @param req
      * @return
      */
    @ResponseBody
    @RequestMapping(value = "/topic/list", method = RequestMethod.GET)
    @ApiOperation(value = "分页加载话题", notes = "如果传入topicId,则返回该Id之前的话题,否则返回的n条最新的话题")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    Page<ResTopic> getTopicByLastTopicId(
            @RequestParam(required = false)
            @ApiParam("指定投票的id")
                    String topicId,
            @RequestParam(required = false)
            @ApiParam("页数,默认第0页")
                    Integer page,
            @RequestParam(required = false)
            @ApiParam("每页条数,默认10条")
                    Integer limit, HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def perTopic = !!topicId ? topicRepository.findOne(topicId) : null
        def list
        if (!perTopic) {
            list = topicRepository.findByCompanyId(user?.companyId, new PageRequest(
                    page ?: 0,
                    limit ?: 10,
                    new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "createdAt")))))
        } else {
            list = topicRepository.findByCreatedAtBefore(perTopic.createdAt, new PageRequest(
                    page ?: 0,
                    limit ?: 10,
                    new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "createdAt")))))

        }
        return  list.map(new Converter() {
            @Override
        Object convert(Object source) {
                return generateResponseTopicByPersistentTopic(source as Topic)
            }
        })
    }
   /**
     * 获取所有动态
     * @param page
     * @param limit
     * @param req
     */
    @ResponseBody
    @RequestMapping(value = "/list/all", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有动态", notes = "如果传入trendId则为上拉刷新,不传则为下拉刷新")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def getAllTrend(

                    @RequestParam(required = false)
                    @ApiParam("")
                                String trendId,
                    @RequestParam(required = false)
                    @ApiParam("页数,默认第0页")
                                Integer page,
                    @RequestParam(required = false)
                    @ApiParam("每页条数,默认10条")
                            Integer limit,
                    HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        def perTrend = !!trendId ? trendItemRepository.findByTrendId(trendId) : null
        Page<TrendItem> list
        def newList = []
        if (!perTrend){
        // 如果id为空则返回最新的
         list = trendItemRepository.findByCompanyId(user?.companyId,
                new PageRequest(
                page ?: 0,
                limit ?: 10,
                new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "createdAt")))))

        } else{
        //如果不为空则返回指定动态的前n条记录
             list = trendItemRepository.findByCreatedAtBefore(
             perTrend?.createdAt,
            new PageRequest(
                page ?: 0,
                limit ?: 10,
                new Sort(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "createdAt")))))

        }

        list.each {
            switch (it.typeEnum) {
                case Constants.TypeEnum.Showtime:
                    def newShowtime = showtimeRepository.findOne(it.trendId)
                    newList.add(generateResponseShowtimeByPersistentShowtime(newShowtime,user))
                    break
                case Constants.TypeEnum.Activity:
                    def newActivity = activityRepository.findOne(it.trendId)
                    newList.add(generateResponseActivityByPersistentActivity(newActivity,user))
                    break
                case Constants.TypeEnum.Topic:
                    def newTopic = topicRepository.findOne(it.trendId)
                    newList.add(generateResponseTopicByPersistentTopic(newTopic))
                    break
                case Constants.TypeEnum.Vote:
                    def newVote = voteRepository.findOne(it.trendId)
                    newList.add(generateResponseVoteByPersistentVote(newVote,user))
                    break
            }
        }
        def dic = [:]
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
        return dic
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
        Company showtimeCompany = !!showtime?.companyId ? companyRepository.findOne(showtime?.companyId) : null
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
                typeEnum: Constants.TypeEnum.Showtime,
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
                    def comment = !!it ? commentArrItemRepository.findOne(it) : null
                    return !!comment ? generateResponseCommentArrItemByPersistentCommentArrItem(comment) : null
                },
        )
    }

    /**
     * 增加approved字段(判断当前用户是否点赞)
     * @param showtime
     * @param user
     * @return
     */
    ResShowtime generateResponseShowtimeByPersistentShowtime(Showtime showtime, User user) {
        def res = generateResponseShowtimeByPersistentShowtime(showtime)
        res.approved = false
        for (def approve : res.approves) {
            if (approve?.user?.id == user?.id) {
                res.approved = true
                break
            }
        }
        return res
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
                updatedAt: activity?.updatedAt,
                typeEnum: Constants.TypeEnum.Activity,
                comments: activity?.comments?.collect {
                    def comment = !!it ? commentArrItemRepository.findOne(it) : null
                    return !!comment ? generateResponseCommentArrItemByPersistentCommentArrItem(comment) : null
                }
        )
    }

    ResActivity generateResponseActivityByPersistentActivity(Activity activity, User user) {
        def res = generateResponseActivityByPersistentActivity(activity)
        res.isSignUp = false
        for (def member : res.members) {
            if (member?.id == user?.id) {
                res.isSignUp = true
                break
            }
        }
        return res
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
        def totalCount = 0
        vote?.options?.each {
            VoteOptionInfo voteOptionInfo = voteOptionInfoRepository.findOne(it)
            return totalCount += voteOptionInfo.votedUserIds.size();
        }

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
                            },
                            count: voteOptionInfo?.votedUserIds?.size(),
                            totalCount: totalCount
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
                updatedAt: vote?.updatedAt,
//                isVoted: vote?.isVoted,
                typeEnum: Constants.TypeEnum.Vote


        )
    }
/**
* 增加isVoted字段
* @param vote
* @param user
* @return
 */
    ResVote generateResponseVoteByPersistentVote(Vote vote, User user) {
        def res = generateResponseVoteByPersistentVote(vote)
        res.isVoted = false
        for (int i = 0; vote.options.size() > i; i++) {
            def currentOption = voteOptionInfoRepository.findOne(vote.options.get(i))
            for (int j = 0; currentOption.votedUserIds.size() > j; j++) {
                if (user?.id == currentOption.votedUserIds.get(j)) {
                    res.isVoted = true
                    return res
                }
            }

        }
        return res

    }

    /**
     * 根据持久化的投票选项生成res投票选项的item
     * @param voteOptionInfo
     * @return
     */
    ResVoteOptionInfo generateResponseVoteOptionInfoByPersistentVoteOptionInfo(VoteOptionInfo voteOptionInfo) {
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
                },
                count: voteOptionInfo?.votedUserIds?.size()
        )
    }

    /**
     * 根据持久化的评论item生成res评论item
     * @param commentArrItem
     * @return
     */
    ResCommentArrItem generateResponseCommentArrItemByPersistentCommentArrItem(CommentArrItem commentArrItem) {
        def user = !!commentArrItem?.userId ? userRepository.findOne(commentArrItem?.userId) : null
        def comment = !!commentArrItem?.replyToCommentId ? commentArrItemRepository.findOne(commentArrItem?.replyToCommentId) : null
        def replyToCommentAuthor = !!comment && !!comment?.userId ? userRepository.findOne(comment?.userId) : null
        return new ResCommentArrItem(
                id: commentArrItem?.id,
                comment: commentArrItem?.comment,
                user: !!user ? new SimpleUserModel(
                        id: user?.id,
                        nickname: user?.nickname,
                        avatar: user?.avatar
                ) : null,
                replyToCommentAuthor: !!replyToCommentAuthor ? new SimpleUserModel(
                        id: replyToCommentAuthor?.id,
                        nickname: replyToCommentAuthor?.nickname,
                        avatar: replyToCommentAuthor?.avatar
                ) : null,
                updatedAt: commentArrItem?.updatedAt,
                createdAt: commentArrItem?.createdAt
        )
    }

    /**
     * 根据持久化话题模型生成res话题
     * @param topic
     * @return
     */
    ResTopic generateResponseTopicByPersistentTopic(Topic topic) {
        def author = !!topic?.authorId ? userRepository.findOne(topic?.authorId) : null
        def company = !!topic?.companyId ? companyRepository.findOne(topic?.companyId) : null
        def team = !!topic?.teamId ? teamRepository.findOne(topic?.teamId) : null
        return new ResTopic(
                id: topic?.id,
                image: topic?.image,
                company: !!company ? new SimpleCompanyModel(
                        id: company?.id,
                        name: company?.name,
                        imageUrl: company?.image,
                ) : null,
                team: !!team ? new SimpleTeamModel(
                        id: team?.id,
                        name: team?.name,
                        imageUrl: team?.image,
                ) : null,
                content: topic?.content,
                title: topic?.title,
                author: !!author ? new SimpleUserModel(
                        id: author?.id,
                        nickname: author?.nickname,
                        avatar: author?.avatar,
                ) : null,
                createdAt: topic?.createdAt,
                updatedAt: topic?.updatedAt,
                typeEnum: Constants.TypeEnum.Topic,
                comments: topic?.comments?.collect {
                    def comment = commentArrItemRepository.findOne(it)
                    def commentUser = !!comment?.userId ? userRepository.findOne(comment?.userId) : null
                    return new ResCommentArrItem(
                            id: comment?.id,
                            user: !!commentUser ? new SimpleUserModel(
                                    id: commentUser?.id,
                                    nickname: commentUser?.nickname,
                                    avatar: commentUser?.avatar
                            ) : null,
                            comment: comment?.comment,
                            createdAt: comment?.createdAt,
                            updatedAt: comment?.updatedAt
                    )

                }

        )

    }


}
