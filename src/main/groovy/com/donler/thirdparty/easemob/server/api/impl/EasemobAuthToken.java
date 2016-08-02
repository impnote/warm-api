package com.donler.thirdparty.easemob.server.api.impl;

import com.donler.thirdparty.easemob.server.api.AuthTokenAPI;
import com.donler.thirdparty.easemob.server.comm.helper.HeaderHelper;
import com.donler.thirdparty.easemob.server.comm.body.AuthTokenBody;
import com.donler.thirdparty.easemob.server.comm.wrapper.BodyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donler.thirdparty.easemob.server.api.EasemobRestAPI;
import com.donler.thirdparty.easemob.server.comm.constant.HTTPMethod;
import com.donler.thirdparty.easemob.server.comm.wrapper.HeaderWrapper;

public class EasemobAuthToken extends EasemobRestAPI implements AuthTokenAPI {
	
	public static final String ROOT_URI = "/token";
	
	private static final Logger log = LoggerFactory.getLogger(EasemobAuthToken.class);
	
	@Override
	public String getResourceRootURI() {
		return ROOT_URI;
	}

	public Object getAuthToken(String clientId, String clientSecret) {
		String url = getContext().getSeriveURL() + getResourceRootURI();
		BodyWrapper body = new AuthTokenBody(clientId, clientSecret);
		HeaderWrapper header = HeaderHelper.getDefaultHeader();
		
		return getInvoker().sendRequest(HTTPMethod.METHOD_POST, url, header, body, null);
	}
}
