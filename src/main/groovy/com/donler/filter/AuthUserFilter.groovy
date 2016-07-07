package com.donler.filter

import com.donler.exception.UnAuthException
import com.donler.model.persistent.user.Token
import com.donler.repository.user.TokenRepository
import com.donler.repository.user.UserRepository
import com.donler.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by jason on 6/1/16.
 */
@Component
class AuthUserFilter implements Filter {
    @Autowired
    TokenService tokenService
    @Autowired
    TokenRepository tokenRepository
    @Autowired
    UserRepository userRepository

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        def req = request as HttpServletRequest
        def res = response as HttpServletResponse
        println(req.method)
        println(req.getRequestURI())
        def needAuths = [~/\/trend.*/]
        def notNeedAuths = [~/\/trend(.*)list/]

        /**
         * 根据白名单 黑名单 和请求方式来校验用户身份
         */
        if (!notNeedAuths.any { req.getRequestURI().matches(it) } && needAuths.any { req.getRequestURI().matches(it) } && req.method.toUpperCase() != "GET") {
            def tokenStr = req.getHeader("x-token")
            if (!tokenStr) {
                throw new UnAuthException("请传入x-token请求头")
            }
            def userId = tokenService.parseToken(tokenStr) as String
            if (!userId) {
                throw new UnAuthException("请传入正确的x-token信息")
            }
            def token = tokenRepository.findByUserId(userId) as Token
            if (new Date().before(token.expiredTime)) {
                req.setAttribute("user", userRepository.findOne(token?.userId))
            } else {
                throw new UnAuthException("token已过期,请重新登录")
            }
        }
        chain.doFilter(req, res)
    }

    @Override
    void destroy() {

    }
}
