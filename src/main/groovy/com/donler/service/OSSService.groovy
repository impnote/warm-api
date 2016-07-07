package com.donler.service

import com.aliyun.oss.OSSClient
import com.donler.config.OSSConfig
import com.donler.model.ImageUrlUnit
import com.donler.model.request.ImageUploadDataUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sun.misc.BASE64Decoder

/**
 * Created by jason on 5/25/16.
 */
@Service
class OSSService {

    @Autowired
    OSSConfig ossConfig

    /**
     * 上传单张图片
     * @param code
     * @return file url path
     */
    String uploadFileToOSS(String code) {
        if (code.isEmpty()) {
            return null
        }
        if (code.indexOf("data") == -1) {
            throw new RuntimeException("base64字符串异常,必须包含data参数")
        }
        def ossClient = new OSSClient(ossConfig.endpoint, ossConfig.accessKeyId, ossConfig.accessKeySecret)
        def codeArr = code.tokenize(",")
        def suffix = codeArr.get(0).split(":")[1].split(";")[0].split("/")[1]
        def name = UUID.randomUUID().toString()
        def urlPrefix = ossConfig.urlPrefix
        ossClient.putObject(ossConfig.bucketName, "$name.$suffix", new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(codeArr.get(1))))
        ossClient.shutdown()
        return "$urlPrefix$name.$suffix"
    }

    /**
     * 上传数组上传到OSS
     * @param images
     * @return url数组
     */
    List<ImageUrlUnit> uploadFilesToOSS(List<ImageUploadDataUnit> images) {
        if (images.isEmpty()) {
            return null
        }
        def list = []
        images.eachWithIndex { it, i ->
            list << new ImageUrlUnit(index: i, name: it?.name ?: "image-$i", imageUrl: uploadFileToOSS(it.imageData))
        }
        return list
    }

}
