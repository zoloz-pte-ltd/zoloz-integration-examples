/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.zoloz.example.realidh5.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.zoloz.api.sdk.client.OpenApiClient;
import com.zoloz.example.realidh5.autoconfig.ProductConfig;
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
 * Controller providing APIs for front-end
 *
 * @author chenzc
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class H5RealIdController {

    private static final Logger logger = LoggerFactory.getLogger(H5RealIdController.class);

    @Autowired
    private OpenApiClient openApiClient;

    @Autowired
    private ProductConfig realIdConfig;

    @RequestMapping(value = "/initialize", method = RequestMethod.POST)
    public JSONObject realIdInit(HttpServletRequest servletRequest, @RequestBody JSONObject request) throws Exception {

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

        String resultUrl = String.format(
                "%s://%s:%d/result.html",
                servletRequest.getScheme(),
                servletRequest.getServerName(),
                servletRequest.getServerPort()
        );

        Map<String, String> h5ModeConfig = new HashMap<>();
        h5ModeConfig.put("completeCallbackUrl", resultUrl);
        h5ModeConfig.put("interruptCallbackUrl",resultUrl);

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