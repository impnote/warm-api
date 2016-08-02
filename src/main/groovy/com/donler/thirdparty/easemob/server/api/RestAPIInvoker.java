package com.donler.thirdparty.easemob.server.api;

import com.donler.thirdparty.easemob.server.comm.wrapper.QueryWrapper;
import com.donler.thirdparty.easemob.server.comm.wrapper.BodyWrapper;
import com.donler.thirdparty.easemob.server.comm.wrapper.HeaderWrapper;
import com.donler.thirdparty.easemob.server.comm.wrapper.ResponseWrapper;

import java.io.File;

public interface RestAPIInvoker {
	ResponseWrapper sendRequest(String method, String url, HeaderWrapper header, BodyWrapper body, QueryWrapper query);
	ResponseWrapper uploadFile(String url, HeaderWrapper header, File file);
    ResponseWrapper downloadFile(String url, HeaderWrapper header, QueryWrapper query);
}
