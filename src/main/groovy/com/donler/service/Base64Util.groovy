package com.donler.service

import sun.misc.BASE64Decoder

/**
 * Created by jason on 5/25/16.
 */
class Base64Util {
     static void decoderBase64File(String base64Code) throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream("")
        out.write(buffer);
        out.close();
    }
}
