package com.donler.controller

import com.donler.config.AppConfig
import com.donler.exception.BadRequestException
import com.donler.exception.DatabaseDuplicateException
import com.donler.exception.NotFoundException
import com.donler.model.persistent.user.Token
import com.donler.model.persistent.user.User
import com.donler.model.request.user.UserLoginRequestModel
import com.donler.model.request.user.UserRegisterRequestModel
import com.donler.repository.company.CompanyRepository
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

    @Autowired
    CompanyRepository companyRepository



    @ApiOperation(value = "登录", notes = "根据传入的用户名/邮箱/手机号码和密码来进行登录")
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    def login(@Valid @RequestBody UserLoginRequestModel body) {

        def user = userRepository.findByUsernameOrPhoneOrEmail(body?.loginInfo, body?.loginInfo, body?.loginInfo)
        if (user?.password != MD5Util.md5Encode(body?.password ?: "")) {
            throw new BadRequestException("用户名或密码错误")
        } else {
            def token = tokenService.generateToken(user.id)
            def oldToken = tokenRepository.findByUserId(user.id)
            if (!!oldToken) {
                oldToken.token = token
                oldToken.expiredTime = new Date((System.currentTimeMillis() + appConfig.expiredTime.toBigInteger()).longValue())
                return tokenRepository.save(oldToken)
            }else {
                return tokenRepository.save(new Token(userId: user.id, token: token, expiredTime: new Date((System.currentTimeMillis() + appConfig.expiredTime.toBigInteger()).longValue())))
            }

        }

    }


    @ApiOperation(value = "注册", notes = "根据传入的信息来进行注册", response = User.class)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    def register(@Valid @RequestBody UserRegisterRequestModel body) {

        def company = companyRepository.findOne(body?.companyId)

        if (!company) {
            throw new NotFoundException("请选择正确的公司")
        }

        if (userRepository.findByUsername(body?.username)) {
            throw new DatabaseDuplicateException("用户名为${body?.username}的用户已经存在")
        } else if (userRepository.findByEmail(body?.email)) {
            throw new DatabaseDuplicateException("邮箱为${body?.email}的用户已经存在")
        } else if (userRepository.findByPhone(body?.phone)) {
            throw new DatabaseDuplicateException("手机号码为${body?.phone}的用户已经存在")
        } else {
            return userRepository.save(new User(
                    nickname: body.nickname ?: "匿名",
                    username: body?.username,
                    password: MD5Util.md5Encode(body?.password),
                    phone: body?.phone,
                    email: body?.email,
                    companyId: !!company ? company?.id : null
            ))
        }

    }
}
