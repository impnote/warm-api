package com.donler.controller

import com.donler.model.persistent.trend.ShowTime
import com.donler.model.request.trend.ShowTimePublishRequestBody
import com.donler.repository.trend.ShowTimeRepository
import com.donler.service.OSSService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
/**
 * Created by jason on 5/25/16.
 */
@RestController
@RequestMapping("/trend")
class TrendController {

    @Autowired
    ShowTimeRepository showTimeRepository

    @Autowired
    OSSService ossService

    @ResponseBody
    @RequestMapping(value = "/showtime/publish", method = RequestMethod.POST)
    def publishShowtime(@Valid @RequestBody ShowTimePublishRequestBody body) {
        return showTimeRepository.save(new ShowTime(
                content: body.content,
                activityId: body?.activityId,
                obviousTeamIds: body?.obviousTeamIds,
                images: ossService.uploadFilesToOSS(body.imagesData)
        ))

    }
}
