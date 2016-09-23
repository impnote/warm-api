package com.donler.service

import com.donler.config.YunpianConfig
import com.yunpian.sdk.service.YunpianRestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by jason on 5/25/16.
 */
@Service
class YunpianService {

    @Autowired
    YunpianConfig yunpianConfig


    def sendSms() {
        YunpianRestClient client = new YunpianRestClient(yunpianConfig.apiKey)
    }

}
