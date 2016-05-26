package com.donler.controller

import com.donler.model.persistent.trend.ShowTime
import com.donler.model.request.trend.ShowTimePublishRequestBody
import com.donler.repository.trend.ShowTimeRepository
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
    ShowTimeRepository showTimeRepository

    @Autowired
    OSSService ossService

    @ResponseBody
    @RequestMapping(value = "/showtime/publish", method = RequestMethod.POST)
    @ApiOperation(response = ShowTime.class, value = "发布瞬间", notes = "根据传入信息发布一个瞬间")
    def publishShowtime(@Valid @RequestBody ShowTimePublishRequestBody body) {
        return showTimeRepository.save(new ShowTime(
                content: body.content,
                activityId: body?.activityId,
                obviousTeamIds: body?.obviousTeamIds,
                images: ossService.uploadFilesToOSS(body.imagesData)
        ))

    }

    @ResponseBody
    @RequestMapping(value = "/showtime/{showtimeId}", method = RequestMethod.DELETE)
    @ApiOperation(response = )
    def getShowTimeById(@PathVariable("showtimeId") def showtimeId) {



    }

}
