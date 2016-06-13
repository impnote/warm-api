package com.donler.controller

import com.donler.exception.NotFoundException
import com.donler.model.CreateAndModifyTimestamp
import com.donler.model.SimpleCompanyModel
import com.donler.model.SimpleShowtimeModel
import com.donler.model.SimpleTeamModel
import com.donler.model.SimpleUserModel
import com.donler.model.persistent.trend.Activity
import com.donler.model.persistent.trend.Company
import com.donler.model.persistent.trend.Showtime
import com.donler.model.persistent.trend.User
import com.donler.model.request.trend.ActivityPublishRequestBody
import com.donler.model.request.trend.ShowtimePublishRequestBody
import com.donler.model.response.Activity as ResActivity
import com.donler.model.response.ResponseMsg
import com.donler.repository.company.CompanyRepository
import com.donler.repository.company.TeamRepository
import com.donler.repository.trend.ActivityRepository
import com.donler.repository.trend.ShowtimeRepository
import com.donler.repository.user.UserRepository
import com.donler.service.OSSService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

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

    @ResponseBody
    @RequestMapping(value = "/showtime/publish", method = RequestMethod.POST)
    @ApiOperation(response = Showtime.class, value = "发布瞬间", notes = "根据传入信息发布瞬间")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def publishShowtime(@Valid @RequestBody ShowtimePublishRequestBody body, HttpServletRequest req) {
        def currentUser = req.getAttribute("user") as User
        Showtime inline = showtimeRepository.save(new Showtime(
                content: body.content,
                activityId: body?.activityId,
                teamId: body?.teamId,
                companyId: currentUser.companyId,
                images: ossService.uploadFilesToOSS(body.imagesData),
                authorId: currentUser.id,
                timestamp: new CreateAndModifyTimestamp(createdAt: new Date(), updatedAt: new Date())
        ))

        return inline
    }

    @ResponseBody
    @RequestMapping(value = "/showtime/{showtimeId}", method = RequestMethod.DELETE)
    @ApiOperation(response = ResponseMsg.class, value = "删除瞬间", notes = "根据传入的瞬间id删除一个瞬间")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def deleteShowtimeById(@PathVariable("showtimeId") String showtimeId, HttpServletRequest req) {
        print(req.getAttribute('user'))

        if (showtimeRepository.exists(showtimeId)) {
            showtimeRepository.delete(showtimeId)
            return ResponseMsg.ok("删除成功")
        } else {
            throw new NotFoundException("id为${showtimeId}的瞬间")
        }
    }

    @ResponseBody
    @RequestMapping(value = "/showtime/{showtimeId}", method = RequestMethod.PUT)
    @ApiOperation(response = ResponseMsg.class, value = "更新瞬间", notes = "根据传入的瞬间的id更新一个瞬间")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def updateShowtimeById(@PathVariable("showtimeId") String showtimeId) {
        return null
    }

    @ResponseBody
    @RequestMapping(value = "/showtime/{showtimeId}", method = RequestMethod.GET)
    @ApiOperation(response = ResponseMsg.class, value = "更新瞬间", notes = "根据传入的瞬间的id获取一个瞬间")
    def getShowtimeById(String showtimeId) {
        return null
    }

    @ResponseBody
    @RequestMapping(value = "/showtime/by/activity/{activityId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取活动瞬间", notes = "根据活动的id获取该活动的所有瞬间")
    def getShowtimeByActivityId(String activityId) {
        return null
    }

    /**
     * 发布活动
     * @param body
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/activity/publish", method = RequestMethod.POST)
    @ApiOperation(value = "发布活动", notes = "根据传入的信息发布活动")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    ResActivity publishActivity(@Valid @RequestBody ActivityPublishRequestBody body, HttpServletRequest req) {
        def currentUser = req.getAttribute("user") as User

        Activity activity = activityRepository.save(new Activity(
                name: body?.name,
                image: ossService.uploadFileToOSS(body?.image?.imageData),
                teamId: body?.teamId,
                authorId: currentUser.id,
                companyId: currentUser.companyId,
                startTime: body?.startTime,
                endTime: body?.endTime,
                deadline: body?.deadline,
                memberMax: body?.memberMax,
                memberMin: body?.memberMin,
                description: body?.description,
                timestamp: new CreateAndModifyTimestamp(updatedAt: new Date(), createdAt: new Date())
        ))

        def author = userRepository.findOne(activity.authorId) as User
        def team = !!activity?.teamId ? teamRepository.findOne(activity?.teamId) : null
        def company = companyRepository.findOne(activity?.companyId) as Company
        return new ResActivity(
                id: activity.id,
                name: activity.name,
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
                        id: author.id,
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
                    def showtimeAuthor = userRepository.findOne(showtime.authorId)
                    return new SimpleShowtimeModel(
                            id : showtime.id,
                            content: showtime.content,
                            images: showtime.images,
                            author: new SimpleUserModel(
                                    id: showtimeAuthor.id,
                                    nickname: showtimeAuthor.nickname,
                                    avatar: showtimeAuthor.avatar
                            )
                    )
                },
                startTime: activity?.startTime,
                endTime: activity?.endTime,
                deadline: activity.deadline,
                memberMax: activity.memberMax,
                memberMin: activity.memberMin,
                description: activity.description,
                timestamp: activity.timestamp
        )
    }

    /**
     *
     * @param activity
     * @return
     */
    ResActivity generateResponseActivityByPresistentActivity(Activity activity) {
        return null
    }

    @ResponseBody
    @RequestMapping(value = "/activity/{activityId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取活动详情", notes = "根据活动的id获取活动的详情")
    ResActivity getActivityById(@PathVariable("activityId")String activityId) {
        return new ResActivity()
    }





}
