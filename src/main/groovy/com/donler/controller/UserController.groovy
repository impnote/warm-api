package com.donler.controller

import com.donler.config.AppConfig
import com.donler.exception.BadRequestException
import com.donler.exception.DatabaseDuplicateException
import com.donler.exception.NotFoundException
import com.donler.model.SimpleCompanyModel
import com.donler.model.persistent.user.Token
import com.donler.model.persistent.user.User
import com.donler.model.request.user.UserLoginRequestModel
import com.donler.model.request.user.UserRegisterRequestModel
import com.donler.model.response.ResponseMsg
import com.donler.model.response.User as ResUser
import com.donler.repository.company.CompanyRepository
import com.donler.repository.user.TokenRepository
import com.donler.repository.user.UserRepository
import com.donler.service.MD5Util
import com.donler.service.OSSService
import com.donler.service.TokenService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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

    @Autowired
    CompanyRepository companyRepository

    @Autowired
    OSSService ossService



    @ApiOperation(value = "登录", notes = "根据传入的用户名/邮箱/手机号码和密码来进行登录", response = Token.class)
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


    @ApiOperation(value = "注册", notes = "根据传入的信息来进行注册,companyId非必填项,注册时可以先不指定" , response = User.class)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    def register(@Valid @RequestBody UserRegisterRequestModel body) {

        def company = companyRepository.findOne(body?.companyId)

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

    /**
     * 获取个人资料
     * @param req
     * @return
     */
    @ApiOperation(value = "个人资料", notes = "获取当前登录用户的个人资料" , response = ResUser.class)
    @RequestMapping(path = "/profile", method = RequestMethod.GET)
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def profile(HttpServletRequest req) {
        def user = req.getAttribute("user") as User
        return generateResponseUserByPersistentUser(user)

    }

    /**
     * 选择公司
     * @param userId
     * @param companyId
     * @return
     */
    @ApiOperation(value = "选择公司", notes = "用户选择公司", response = ResponseMsg.class)
    @RequestMapping(path = "/{userId}/choose/company/{companyId}", method = RequestMethod.GET)
    def chooseCompany(@PathVariable(value = "userId") String userId,@PathVariable(value = "companyId") String companyId) {
        def company = !!companyId ? companyRepository.findOne(companyId) : null
        def user = !!userId ? userRepository.findOne(userId) : null
        if (!user) {
            throw new NotFoundException("id为 ${userId} 的用户不存在")
        }
        if (!company) {
            throw new NotFoundException("id为 ${companyId} 的公司不存在")
        }
        user.companyId = company?.id
        return ResponseMsg.ok(generateResponseUserByPersistentUser(userRepository.save(user)))
    }


    @ApiOperation(value = "选择头像上传", notes = "用户更换头像", response = ResponseMsg.class)
    @RequestMapping(path = "/{userId}/avatar", method = RequestMethod.POST)
    def chooseAvatar(@RequestPart MultipartFile[] files, @PathVariable(value = "userId") String userId) {
        def currentAvatar = ossService.uploadFileToOSS(files?.first())
        def user = !!userId ? userRepository.findOne(userId) : null
        if (!user) {
            throw new NotFoundException("id为 ${userId} 的用户不存在")
        }
        user.avatar = currentAvatar
        return ResponseMsg.ok(generateResponseUserByPersistentUser(userRepository.save(user)))


    }

    /**
     * 根据持久化User生成响应的User
     * @param user
     * @return
     */
    def generateResponseUserByPersistentUser(User user) {
        def company = !!user?.companyId ? companyRepository.findOne(user?.companyId) : null
        return new ResUser(
                id: user?.id,
                nickname: user?.nickname,
                avatar: user?.avatar,
                job: user?.job,
                username: user?.username,
                phone: user?.phone,
                email: user?.email,
                company: !!company ? new SimpleCompanyModel(
                        id: company?.id,
                        name: company?.name,
                        imageUrl: company?.image
                ) : null
        )
    }
}
