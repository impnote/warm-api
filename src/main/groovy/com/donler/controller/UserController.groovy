package com.donler.controller

import com.donler.model.persistent.trend.User
import com.donler.model.request.user.UserLoginRequestModel
import com.donler.model.request.user.UserRegisterRequestModel
import com.donler.repository.user.UserRepository
import com.donler.service.MD5Util
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid
/**
 * Created by jason on 5/27/16.
 */
@RestController
@RequestMapping("/user")
@Api(value = "user", tags = ["用户"], consumes = "application/json", produces = "application/json")
class UserController {

    @Autowired
    UserRepository userRepository


    @ApiOperation(value = "登录", notes = "根据传入的用户名/邮箱/手机号码和密码来进行登录")
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    def login(@Valid @RequestBody UserLoginRequestModel body) {
//        TODO
//        body.username
    }


    @ApiOperation(value = "注册", notes = "根据传入的信息来进行注册", response = User.class)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    def register(@Valid @RequestBody UserRegisterRequestModel body) {
        return userRepository.save(new User(
                nickname: body.nickname ?: "匿名",
                username: body?.username,
                password: MD5Util.md5Encode(body?.password),
                phone: body?.phone,
                email: body?.email,
                company: body?.companyId
        ))
    }
}
