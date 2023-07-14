/*
 * Copyright (c) 2020 ZOLOZ PTE.LTD.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.zoloz.example.connecth5.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.zoloz.api.sdk.client.OpenApiClient;
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
 * @author He Yujie
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class H5ConnectController {

    private static final Logger logger = LoggerFactory.getLogger(H5ConnectController.class);

    @Autowired
    private OpenApiClient openApiClient;

    @RequestMapping(value = "/connect/enroll", method = RequestMethod.POST)
    public JSONObject connectEnroll(HttpServletRequest servletRequest, @RequestBody JSONObject request) throws Exception {

        if (logger.isInfoEnabled()) {
            logger.info("request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String userId = "dummy_userid_" + System.currentTimeMillis();

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("userId", userId);
        apiReq.put("base64ImageContent",request.get("base64ImageContent"));

        if (logger.isInfoEnabled()) {
            logger.info("request=" + apiReq);
        }

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.connect.enroll",
                JSON.toJSONString(apiReq)
        );
        JSONObject apiResp = JSON.parseObject(apiRespStr);
        JSONObject response = new JSONObject(apiResp);
        response.put("transactionId", apiResp.getString("transactionId"));
        if (logger.isInfoEnabled()) {
            logger.info("response=" + apiRespStr);
        }

        return response;
    }

    @RequestMapping(value = "/connect/initialize", method = RequestMethod.POST)
    public JSONObject connectInit(HttpServletRequest servletRequest, @RequestBody JSONObject request) throws Exception {

        if (logger.isInfoEnabled()) {
            logger.info("request=" + request);
        }

        String metaInfo = "MOB_H5";

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String userId = "dummy_userid_" + System.currentTimeMillis();

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("metaInfo", metaInfo);
        apiReq.put("userId", userId);
        apiReq.put("sceneCode",request.getOrDefault("sceneCode","test scene"));

        String resultUrl = String.format(
                "%s://%s:%d/result.html",
                servletRequest.getScheme(),
                servletRequest.getServerName(),
                servletRequest.getServerPort()
        );

        /**
         * with isIframe：transparent transmits isIframe
         * without isIframe： do nothing
         * with completeCallbackUrl  or interruptCallbackUrl：transparent transmits this value
         * without completeCallbackUrl or interruptCallbackUrl：server do hard code
         */
        Map<String, String> h5ModeConfig = new HashMap<>();
        if (request.getJSONObject("h5ModeConfig") != null && request.getJSONObject("h5ModeConfig").getString("isIframe") != null) {
            h5ModeConfig.put("isIframe", request.getJSONObject("h5ModeConfig").getString("isIframe"));
        }
        if (request.getJSONObject("h5ModeConfig") != null && request.getJSONObject("h5ModeConfig").getString("completeCallbackUrl")
                != null) {
            h5ModeConfig.put("completeCallbackUrl", request.getJSONObject("h5ModeConfig").getString("completeCallbackUrl"));
        }
        if (request.getJSONObject("h5ModeConfig") != null && request.getJSONObject("h5ModeConfig").getString("interruptCallbackUrl")
                != null) {
            h5ModeConfig.put("interruptCallbackUrl", request.getJSONObject("h5ModeConfig").getString("interruptCallbackUrl"));
        } else {
            h5ModeConfig.put("completeCallbackUrl", resultUrl);
            h5ModeConfig.put("interruptCallbackUrl", resultUrl);
        }
        apiReq.put("h5ModeConfig", h5ModeConfig);

        // 参数开放
        if (request.getJSONObject("productConfig") != null) {
            Map<String, Object> faceProductConfig = new HashMap<>();
            if (request.getJSONObject("productConfig").getString("livenessMode") != null) {
                faceProductConfig.put("livenessMode", request.getJSONObject("productConfig").getString("livenessMode"));
            }
            if (request.getJSONObject("productConfig").getString("antiInjectionMode") != null) {
                faceProductConfig.put("antiInjectionMode", request.getJSONObject("productConfig").getString("antiInjectionMode"));
            }
            if (request.getJSONObject("productConfig").getJSONArray("actionCheckItems") != null) {
                faceProductConfig.put("actionCheckItems", request.getJSONObject("productConfig").getJSONArray("actionCheckItems"));
            }
            if (request.getJSONObject("productConfig").getString("actionRandom") != null) {
                faceProductConfig.put("actionRandom", request.getJSONObject("productConfig").getString("actionRandom"));
            }
            if (request.getJSONObject("productConfig").getJSONArray("actionFrame") != null) {
                faceProductConfig.put("actionFrame", request.getJSONObject("productConfig").getJSONArray("actionFrame"));
            }
            apiReq.put("productConfig", faceProductConfig);
        }

        if (logger.isInfoEnabled()) {
            logger.info("request=" + apiReq);
        }

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.connect.initialize",
                JSON.toJSONString(apiReq)
        );
        JSONObject apiResp = JSON.parseObject(apiRespStr);
        JSONObject response = new JSONObject(apiResp);
        response.put("transactionId", apiResp.getString("transactionId"));
        response.put("clientCfg", apiResp.getString("clientCfg"));
        if (logger.isInfoEnabled()) {
            logger.info("response=" + apiRespStr);
        }

        return response;
    }

    @RequestMapping(value = "/connect/checkresult", method = RequestMethod.POST)
    public JSONObject connectCheck(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.connect.checkresult",
                JSON.toJSONString(apiReq)
        );

        if(logger.isInfoEnabled()){
            logger.info("checkresult response: "+apiRespStr);
        }

        return JSON.parseObject(apiRespStr);
    }
}