package com.donler.service

/**
 * Created by apple on 16/8/15.
 */
class CommonUtil {
    static checkNULL(Objects a, Object b) {
        return  a!=null?a:b

    }
}


//import com.donler.model.persistent.user.User
//
//import java.lang.reflect.Modifier
//
//def checkNull(String a , String b ) {
//    return a!=null? a : b
//
//}
//
//static Map asMap(def obj) {
//    obj.getClass().declaredFields.findAll{
//        it.modifiers == Modifier.PRIVATE
//    }.collectEntries{ [it.name, obj[it.name]]}
//}
//
//def user = new User(
//        nickname: "sdads"
//
//)
//def userMap = asMap(user)
//userMap["nickname"] = null
//userMap.asType(User.class)
//User newUser = userMap.asType(User.class)
//println(userMap)
