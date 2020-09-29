/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.zoloz.example.webrealid.controller;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.zoloz.api.sdk.client.OpenApiClient;
import com.zoloz.example.webrealid.autoconfig.RealIdConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenzc
 * @version $Id: H5ClientModeController.java, v 0.1 2020年09月27日 15:32 chenzc Exp $
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class H5ClientModeController {

    private static final Logger logger = LoggerFactory.getLogger(H5ClientModeController.class);

    @Autowired
    private OpenApiClient openApiClient;

    @Autowired
    private RealIdConfig realIdConfig;

    @RequestMapping(value = "/initialize", method = RequestMethod.POST)
    public JSONObject h5RealIdInit(HttpServletRequest servletRequest, @RequestBody JSONObject request) throws Exception {

        logger.info("request=" + request);


        String metaInfo = "MOB_H5";

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String userId = "dummy_userid_" + System.currentTimeMillis();

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("flowType", "H5_REALIDLITE_KYC");

        if(request.getString("docType")==null){
            apiReq.put("docType", realIdConfig.getDocType());
        }else{
            apiReq.put("docType", request.getString("docType"));
        }

        StringBuilder sb=new StringBuilder(1024);
        sb.append(servletRequest.getScheme());
        sb.append("://");
        String serverName=servletRequest.getServerName();
        if(StringUtils.equals(serverName,"localhost")){
            serverName=InetAddress.getLocalHost().getHostAddress();
        }
        sb.append(serverName);
        sb.append(":");
        sb.append(servletRequest.getServerPort());
        sb.append("/result.html");

        Map<String, String> h5ModeConfig = new HashMap<>();
        h5ModeConfig.put("completeCallbackUrl", sb.toString());
        h5ModeConfig.put("interruptCallbackUrl",sb.toString());

        Map<String, String> pageConfig = new HashMap<>();
        if (request.getJSONObject("pageConfig") != null && request.getJSONObject("pageConfig").getString("urlFaceGuide") != null) {
            pageConfig.put("urlFaceGuide", request.getJSONObject("pageConfig").getString("urlFaceGuide"));
        }

        //apiReq.put("pages", "1");
        apiReq.put("metaInfo", metaInfo);
        apiReq.put("userId", userId);
        if(StringUtils.isNotBlank(realIdConfig.getServiceLevel())){
            apiReq.put("serviceLevel",realIdConfig.getServiceLevel());
        }

        apiReq.put("h5ModeConfig",h5ModeConfig);
        apiReq.put("pageConfig",pageConfig);

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.realid.initialize",
                JSON.toJSONString(apiReq)
        );
        JSONObject apiResp = JSON.parseObject(apiRespStr);
        JSONObject response = new JSONObject(apiResp);
        response.put("transactionId", apiResp.getString("transactionId"));
        response.put("clientCfg", apiResp.getString("clientCfg"));
        logger.info("response=" + apiRespStr);

        return response;
    }

    @RequestMapping(value = "/checkresult", method = RequestMethod.POST)
    public JSONObject realIdCheck(@RequestBody JSONObject request) {

        logger.info("request=" + request);

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");
        String isReturnImage = request.getString("isReturnImage");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);
        apiReq.put("isReturnImage", isReturnImage);

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.realid.checkresult",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        JSONObject response = new JSONObject(apiResp);
        return response;
    }
}