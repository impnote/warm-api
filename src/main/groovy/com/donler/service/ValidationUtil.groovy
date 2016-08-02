package com.donler.service

import com.donler.exception.AttrNotValidException
import com.fasterxml.jackson.databind.ObjectMapper

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
/**
 * Created by jason on 7/11/16.
 */
class ValidationUtil {
    /**
     * 验证对应类型的model字符串是否符合规定约束
     * @param clazz
     * @param model
     */
    static def validateModelAttribute(Class clazz, String model){
        def instance
        try {
            instance = new ObjectMapper().readValue(model, clazz)
        } catch (Exception ex) {
            throw new AttrNotValidException(ex.localizedMessage)
        }

        ValidatorFactory vf = Validation.buildDefaultValidatorFactory()
        Validator vd = vf.getValidator()
        def set = vd.validate(instance)
        if (set.size() > 0) {
            def errorMsg = []
            set.forEach {
                errorMsg << "[${it.propertyPath}] ${it.message}"
            }
            def errMsg = errorMsg.join(',')
            throw new AttrNotValidException(errMsg)
        }
       return instance
    }
}
