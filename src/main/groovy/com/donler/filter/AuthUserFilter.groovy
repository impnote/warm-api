package com.donler.filter

import com.donler.exception.UnAuthException
import com.donler.model.persistent.trend.Token
import com.donler.repository.user.TokenRepository
import com.donler.repository.user.UserRepository
import com.donler.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.servlet.*
import javax.servlet.http.HttpServletRequest

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
        println(req.getRequestURI())
        def needAuths = [~/\/trend.*/]
        if(needAuths.any {req.getRequestURI().matches(it)}) {
            def tokenStr = req.getHeader("x-token")
            if (!tokenStr) {
                throw new UnAuthException("请传入x-token请求头")
            }
            def userId = tokenService.parseToken(tokenStr) as String
            if (!userId) {
                throw new UnAuthException("请传入正确的x-token信息")
            }
            def token = tokenRepository.findByUserId(userId) as Token
            new Date().before(token.expiredTime) ? {
                req.setAttribute("user", userRepository.findOne(token?.userId))
            } : {
                throw new UnAuthException("token已过期,请重新登录")
            }
        }
        chain.doFilter(request, response)
    }

    @Override
    void destroy() {

    }
}
