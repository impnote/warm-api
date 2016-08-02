package com.donler.service

import com.donler.config.AppConfig
import com.donler.exception.UnAuthException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by jason on 6/1/16.
 */
@Service
class TokenService {
    @Autowired
    AppConfig appConfig

    String generateToken(def userId) {
        return Jwts.builder().setSubject(String.valueOf(userId)).signWith(SignatureAlgorithm.HS512,appConfig.secretKey).compact()
    }

    String parseToken(String token) {
        try {
            Jwts.parser().setSigningKey(appConfig.secretKey).parseClaimsJws(token).getBody().getSubject()
        } catch (e) {
            throw new UnAuthException("请传入正确的x-token信息")
        }
    }
}
