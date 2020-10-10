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