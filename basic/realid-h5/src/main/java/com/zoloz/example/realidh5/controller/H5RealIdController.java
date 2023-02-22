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

package com.zoloz.example.realidh5.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zoloz.api.sdk.client.OpenApiClient;
import com.zoloz.example.realidh5.autoconfig.ProductConfig;
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

        if (logger.isInfoEnabled()) {
            logger.info("realIdInit request=" + request);
        }

        String metaInfo = "MOB_H5";

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String userId = "dummy_userid_" + System.currentTimeMillis();

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("flowType", "H5_REALIDLITE_KYC");
        apiReq.put("operationMode", "STRICT");

        if (request.getString("docType") == null) {
            apiReq.put("docType", realIdConfig.getDocType());
        } else {
            apiReq.put("docType", request.getString("docType"));
        }

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

        Map<String, String> pageConfig = new HashMap<>();
        if (request.getJSONObject("pageConfig") != null && request.getJSONObject("pageConfig").getString("urlFaceGuide") != null) {
            pageConfig.put("urlFaceGuide", request.getJSONObject("pageConfig").getString("urlFaceGuide"));
        }

        //apiReq.put("pages", "1");
        apiReq.put("metaInfo", metaInfo);
        apiReq.put("userId", userId);

        if (request.getString("serviceLevel") == null) {
            apiReq.put("serviceLevel", realIdConfig.getServiceLevel());
        } else {
            apiReq.put("serviceLevel", request.getString("serviceLevel"));
        }

        apiReq.put("h5ModeConfig", h5ModeConfig);
        apiReq.put("pageConfig", pageConfig);

        if (logger.isInfoEnabled()) {
            logger.info("realid initialize request=" + apiReq);
        }

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.realid.initialize",
                JSON.toJSONString(apiReq)
        );
        JSONObject apiResp = JSON.parseObject(apiRespStr);
        JSONObject response = new JSONObject(apiResp);
        response.put("transactionId", apiResp.getString("transactionId"));
        response.put("clientCfg", apiResp.getString("clientCfg"));
        if (logger.isInfoEnabled()) {
            logger.info("realid initialize response=" + apiRespStr);
        }

        return response;
    }

    @RequestMapping(value = "/checkresult", method = RequestMethod.POST)
    public JSONObject realIdCheck(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("realIdCheck request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");
        String isReturnImage = request.getString("isReturnImage");
        JSONArray extraImageControlList = request.getJSONArray("extraImageControlList");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);
        apiReq.put("isReturnImage", isReturnImage);
        apiReq.put("extraImageControlList", extraImageControlList);

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.realid.checkresult",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        return new JSONObject(apiResp);
    }

    @RequestMapping(value = "/idrecognition/initialize", method = RequestMethod.POST)
    public JSONObject idRecognizeInit(HttpServletRequest servletRequest, @RequestBody JSONObject request) throws Exception {

        if (logger.isInfoEnabled()) {
            logger.info("idRecognizeInit request=" + request);
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
         * 有isIframe：透传isIframe
         * 无isIframe：不做处理，走原有逻辑
         * 有completeCallbackUrl或interruptCallbackUrl：透传
         * 无completeCallbackUrl或interruptCallbackUrl：服务端兜底写死
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
        // h5ModeConfig.put("uiCfg", "{\"captureMode\":\"landscape\"}");


        if (request.getString("docType") == null) {
            apiReq.put("docType", realIdConfig.getDocType());
        } else {
            apiReq.put("docType", request.getString("docType"));
        }

        // if (request.getString("pages") == null) {
        //     apiReq.put("pages", "1");
        // } else {
        //     apiReq.put("pages", request.getString("pages"));
        // }

        if (request.getString("metaInfo") == null) {
            apiReq.put("metaInfo", metaInfo);
        } else {
            apiReq.put("metaInfo", request.getString("metaInfo"));
        }
        apiReq.put("userId", userId);
        //serviceLevel
        if (request.getString("serviceLevel") != null) {
            apiReq.put("serviceLevel", request.getString("serviceLevel"));
        }        
        //增加isIframe入参
        apiReq.put("h5ModeConfig", h5ModeConfig);

        if (logger.isInfoEnabled()) {
            logger.info("idrecognition initialize request=" + apiReq);
        }

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.idrecognition.initialize",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        JSONObject response = new JSONObject(apiResp);
        response.put("transactionId", apiResp.getString("transactionId"));
        response.put("clientCfg", apiResp.getString("clientCfg"));
        if (logger.isInfoEnabled()) {
            logger.info("idrecognition initialize response=" + apiRespStr);
        }

        return response;
    }

    @RequestMapping(value = "/idrecognition/checkresult", method = RequestMethod.POST)
    public JSONObject idRecognizeCheck(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("idRecognizeCheck request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.idrecognition.checkresult",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        JSONObject response = new JSONObject(apiResp);
        return response;
    }

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
         * 有isIframe：透传isIframe
         * 无isIframe：不做处理，走原有逻辑
         * 有completeCallbackUrl或interruptCallbackUrl：透传
         * 无completeCallbackUrl或interruptCallbackUrl：服务端兜底写死
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
        h5ModeConfig.put("locale", "id");

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
            apiReq.put("serviceLevel", "FACECAPTURE0002");
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
        JSONArray extraImageControlList = request.getJSONArray("extraImageControlList");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);
        apiReq.put("isReturnImage", isReturnImage);
        apiReq.put("extraImageControlList", extraImageControlList);

        String apiRespStr = openApiClient.callOpenApi(
                "v1a.zoloz.facecapture.checkresult",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        JSONObject response = new JSONObject(apiResp);
        return response;
    }
}