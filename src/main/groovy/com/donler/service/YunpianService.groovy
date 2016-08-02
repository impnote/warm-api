package com.donler.service

import com.aliyun.oss.OSSClient
import com.donler.config.OSSConfig
import com.donler.config.YunpianConfig
import com.donler.model.ImageUrlUnit
import com.donler.model.request.ImageUploadDataUnit
import com.yunpian.sdk.service.YunpianRestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import sun.misc.BASE64Decoder

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
