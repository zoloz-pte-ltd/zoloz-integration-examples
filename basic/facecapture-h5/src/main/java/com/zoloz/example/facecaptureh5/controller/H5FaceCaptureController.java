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

package com.zoloz.example.facecaptureh5.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.zoloz.api.sdk.client.OpenApiClient;
import com.zoloz.example.facecaptureh5.autoconfig.ProductConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller providing APIs for front-end
 *
 * @author Zhang Fang
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class H5FaceCaptureController {

    private static final Logger logger = LoggerFactory.getLogger(H5FaceCaptureController.class);

    @Autowired
    private OpenApiClient openApiClient;
    @Autowired
    private ProductConfig productConfig;

    @RequestMapping(value = "/facecapture/initialize", method = RequestMethod.POST)
    public JSONObject faceCaptureInit(HttpServletRequest servletRequest, @RequestBody JSONObject request) throws Exception {

        if (logger.isInfoEnabled()) {
            logger.info("faceCaptureInit request=" + request);
        }

        String metaInfo = "MOB_H5";

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String userId = "dummy_userid_" + System.currentTimeMillis();

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);

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

        if (request.getJSONObject("faceProductConfig") != null) {
            Map<String, Object> faceProductConfig = new HashMap<>();
            if (request.getJSONObject("faceProductConfig").getString("livenessMode") != null) {
                faceProductConfig.put("livenessMode", request.getJSONObject("faceProductConfig").getString("livenessMode"));
            }
            if (request.getJSONObject("faceProductConfig").getString("antiInjectionMode") != null) {
                faceProductConfig.put("antiInjectionMode", request.getJSONObject("faceProductConfig").getString("antiInjectionMode"));
            }
            if (request.getJSONObject("faceProductConfig").getJSONArray("actionCheckItems") != null) {
                faceProductConfig.put("actionCheckItems", request.getJSONObject("faceProductConfig").getJSONArray("actionCheckItems"));
            }
            if (request.getJSONObject("faceProductConfig").getString("actionRandom") != null) {
                faceProductConfig.put("actionRandom", request.getJSONObject("faceProductConfig").getString("actionRandom"));
            }
            if (request.getJSONObject("faceProductConfig").getJSONArray("actionFrame") != null) {
                faceProductConfig.put("actionFrame", request.getJSONObject("faceProductConfig").getJSONArray("actionFrame"));
            }
            apiReq.put("productConfig", faceProductConfig);
        }

        //serviceLevel
        if (request.getString("serviceLevel") == null) {
            apiReq.put("serviceLevel", productConfig.getServiceLevel());
        } else {
            apiReq.put("serviceLevel", request.getString("serviceLevel"));
        }
        if (request.getString("metaInfo") == null) {
            apiReq.put("metaInfo", metaInfo);
        } else {
            apiReq.put("metaInfo", request.getString("metaInfo"));
        }
        apiReq.put("userId", userId);

        apiReq.put("h5ModeConfig", h5ModeConfig);


        if (logger.isInfoEnabled()) {
            logger.info("facecapture initialize request=" + apiReq);
        }

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.facecapture.initialize",
                JSON.toJSONString(apiReq)
        );
        JSONObject apiResp = JSON.parseObject(apiRespStr);
        JSONObject response = new JSONObject(apiResp);
        response.put("transactionId", apiResp.getString("transactionId"));
        response.put("clientCfg", apiResp.getString("clientCfg"));
        if (logger.isInfoEnabled()) {
            logger.info("facecapture initialize response=" + apiRespStr);
        }

        return response;
    }

    @RequestMapping(value = "/facecapture/checkresult", method = RequestMethod.POST)
    public JSONObject faceCaptureCheck(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("faceCaptureCheck request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");
        String isReturnImage = request.getString("isReturnImage");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);
        apiReq.put("isReturnImage", isReturnImage);
        logger.info("facecapture checkresult request=" + apiReq);

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.facecapture.checkresult",
                JSON.toJSONString(apiReq)
        );

        if (logger.isInfoEnabled()) {
            logger.info("facecapture checkresult response: " + apiRespStr);
        }

        return JSON.parseObject(apiRespStr);
    }
}