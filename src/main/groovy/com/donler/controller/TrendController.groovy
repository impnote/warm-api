package com.donler.controller

import com.donler.exception.NotFoundException
import com.donler.model.persistent.trend.Activity
import com.donler.model.persistent.trend.Showtime
import com.donler.model.request.trend.ActivityPublishRequestBody
import com.donler.model.request.trend.ShowtimePublishRequestBody
import com.donler.model.response.ResponseMsg
import com.donler.repository.trend.ShowtimeRepository
import com.donler.service.OSSService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

/**
 * Created by jason on 5/25/16.
 */
@RestController
@RequestMapping("/trend")
@Api(value = "trend", tags = ["动态"])
class TrendController {

    @Autowired
    ShowtimeRepository showtimeRepository

    @Autowired
    OSSService ossService

    @ResponseBody
    @RequestMapping(value = "/showtime/publish", method = RequestMethod.POST)
    @ApiOperation(response = Showtime.class, value = "发布瞬间", notes = "根据传入信息发布瞬间")
    def publishShowtime(@Valid @RequestBody ShowtimePublishRequestBody body) {
        Showtime inline = showtimeRepository.save(new Showtime(
                content: body.content,
                activity: body?.activityId,
                obviousTeams: body?.obviousTeamIds,
                images: ossService.uploadFilesToOSS(body.imagesData)
        ))

        return inline
    }

    @ResponseBody
    @RequestMapping(value = "/showtime/{showtimeId}", method = RequestMethod.DELETE)
    @ApiOperation(response = ResponseMsg.class, value = "删除瞬间", notes = "根据传入的瞬间id删除一个瞬间")
    def deleteShowtimeById(@PathVariable("showtimeId") String showtimeId) {
        showtimeRepository.exists(showtimeId) ? {
            showtimeRepository.delete(showtimeId)
            return ResponseMsg.ok("删除成功")
        } : {
            throw new NotFoundException("id为[$showtimeId]的瞬间未找到")
        }
    }

//TODO
    @ResponseBody
    @RequestMapping(value = "/activity/publish", method = RequestMethod.POST)
    @ApiOperation(response = Activity.class, value = "发布活动", notes = "根据传入的信息发布活动")
    def publishActivity(@Valid @RequestBody ActivityPublishRequestBody body) {

        return null
    }


}
