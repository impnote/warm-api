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
        /**
         * 需要验证的url数组
         */
        def needAuths = [
                ~/\/trend.*/, // 动态中的所有路由
                ~/\/team.*/, // 群组中的所有路由
                ~/\/easemob.*/, // 环信中的所有路由
                ~/\/user\/profile.*/,// 获取个人资料
                ~/\/user\/choose\/company.*/,
                ~/\/team\/team\/detail/, // 群组中的所有的搜索的get请求

        ]

        /**
         * get方法中需要验证的url,比如某些search
         */
        def needAuthsOfGetMethod = [
                ~/\/easemob.*/, // 环信中的所有路由
                ~/\/team(.*)search/, // 群组中的所有的搜索的get请求
                ~/\/team\/team\/detail/, // 群组中的所有的搜索的get请求
                ~/\/trend(.*)search/, // 动态的中的所有搜索的get请求
                ~/\/user\/profile/, // 获取个人资料的get请求
                ~/\/trend\/showtime\/list/, //根据指定id分页获取瞬间之前的瞬间
                ~/\/trend\/vote\/list/, //根据指定id分页获取瞬间之前的瞬间
                ~/\/trend\/list\/all/, //获取所有的动态
                ~/\/trend\/activity\/list/,
                ~/\/trend\/topic\/list/,
                ~/\/user\/profile\/votes/, //获取当前登录用户发布过的投票
                ~/\/user\/profile.*/,
                ~/\/trend\/activity\/detail/,
                ~/\/user\/choose\/company.*/

        ]


        /**
         *  校验规则:
         *  1.get方法中需要认证的url需要认证,
         *  2.非get方法中不需要认证的url不需要认证
         *  3.非get请求中所有需要认证的url需要认证
         */
        if (( req.method.toUpperCase() != 'GET' || needAuthsOfGetMethod.any { req.requestURI.matches(it)} )
                && needAuths.any { req.requestURI.matches(it)}) {


            def tokenStr = req.getHeader("x-token")
            if (!tokenStr) {
                throw new UnAuthException("请传入x-token请求头")
            }
            def userId = tokenService.parseToken(tokenStr) as String
            if (!userId) {
                throw new UnAuthException("请传入正确的x-token信息")
            }
            def token = tokenRepository.findByUserId(userId) as Token
            println(token)
            if (!!token && new Date().before(token.expiredTime)) {
                if (!token?.userId) {
                    throw new UnAuthException("token不正确")
                }
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
