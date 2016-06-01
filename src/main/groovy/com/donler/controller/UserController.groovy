package com.donler.controller

import com.donler.config.AppConfig
import com.donler.exception.BadRequestException
import com.donler.exception.DatabaseDuplicateException
import com.donler.model.persistent.trend.Token
import com.donler.model.persistent.trend.User
import com.donler.model.request.user.UserLoginRequestModel
import com.donler.model.request.user.UserRegisterRequestModel
import com.donler.repository.user.TokenRepository
import com.donler.repository.user.UserRepository
import com.donler.service.MD5Util
import com.donler.service.TokenService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
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

    @Autowired
    TokenService tokenService

    @Autowired
    AppConfig appConfig

    @Autowired
    TokenRepository tokenRepository



    @ApiOperation(value = "登录", notes = "根据传入的用户名/邮箱/手机号码和密码来进行登录")
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    def login(@Valid @RequestBody UserLoginRequestModel body, HttpServletRequest req) {


        def user = userRepository.findByUsernameOrPhoneOrEmail(body?.loginInfo, body?.loginInfo, body?.loginInfo)
        user?.password != MD5Util.md5Encode(body?.password ?: "") ? {
            throw new BadRequestException("用户名或密码错误")
        }: {
            def token = tokenService.generateToken(user.id)
            return tokenRepository.save(new Token(userId: user.id, token: token, expiredTime: new Date((System.currentTimeMillis() + appConfig.expiredTime.toBigInteger()).longValue())))

        }


        // 生成token

    }


    @ApiOperation(value = "注册", notes = "根据传入的信息来进行注册", response = User.class)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    def register(@Valid @RequestBody UserRegisterRequestModel body) {
        userRepository.findByUsername(body?.username) ? {
            throw new DatabaseDuplicateException("用户名为${body?.username}的用户已经存在")
        }: {
            userRepository.findByEmail(body?.email) ? {
                throw new DatabaseDuplicateException("邮箱为${body?.email}的用户已经存在")
            }: {
                userRepository.findByPhone(body?.phone) ? {
                    throw new DatabaseDuplicateException("手机号码为${body?.phone}的用户已经存在")
                } : {
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
        }

    }
}
