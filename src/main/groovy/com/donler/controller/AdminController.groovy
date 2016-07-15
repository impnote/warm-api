package com.donler.controller

import com.donler.repository.company.CompanyRepository
import com.donler.repository.team.TeamRepository
import com.donler.repository.trend.ActivityRepository
import com.donler.repository.trend.ShowtimeRepository
import com.donler.repository.user.TokenRepository
import com.donler.repository.user.UserRepository
import com.donler.service.OSSService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
/**
 * Created by jason on 5/27/16.
 */
@RestController
@RequestMapping("/admin")
@Api(value = "admin", tags = ["维护"], consumes = "application/json", produces = "application/json")
class AdminController {

    @Autowired
    CompanyRepository companyRepository
    @Autowired
    TokenRepository tokenRepository
    @Autowired
    ActivityRepository activityRepository
    @Autowired
    TeamRepository teamRepository
    @Autowired
    ShowtimeRepository showtimeRepository
    @Autowired
    UserRepository userRepository

    @Autowired
    OSSService ossService


    @ApiOperation(value = "清空数据库", notes = "清空所有数据库内容")
    @RequestMapping(path = "/deleteAll", method = RequestMethod.GET)
    String deleteDb() {
        // TODO 唯一性检查
        companyRepository.deleteAll()
        tokenRepository.deleteAll()
        activityRepository.deleteAll()
        teamRepository.deleteAll()
        showtimeRepository.deleteAll()
        userRepository.deleteAll()
        return "success"
    }


}
