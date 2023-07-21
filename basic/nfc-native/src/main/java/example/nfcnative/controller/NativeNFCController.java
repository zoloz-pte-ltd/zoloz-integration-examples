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

package example.nfcnative.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zoloz.api.sdk.client.OpenApiClient;
import example.nfcnative.autoconfig.ProductConfig;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * client mode controller
 *
 * @author Zhongyang MA
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api"})
public class NativeNFCController {

    private static final Logger logger = LoggerFactory.getLogger(NativeNFCController.class);

    @Autowired
    private OpenApiClient openApiClient;

    @Autowired
    private ProductConfig nfcConfig;

    @RequestMapping(value = {"/nfc/initialize", "/nfcDemoService/initialize"}, method = RequestMethod.POST)
    public JSONObject nfcInit(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("request=" + request);
        }

        String metaInfo = request.getString("metaInfo");
        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String userId = "dummy_userid_" + System.currentTimeMillis();

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("flowType", "NFCLITE");
        apiReq.put("docType", nfcConfig.getDocType());
        if (request.get("docType") != null) {
            apiReq.put("docType", request.get("docType"));
        }

        JSONObject productConfig = new JSONObject();
        ArrayList<CheckItem> pageInfoCheck = new ArrayList<>();
        JSONArray pageInfoCheckItem = request.getJSONArray("pageInfoCheck");
        if (pageInfoCheckItem != null && pageInfoCheckItem.size() > 0) {
            for (int i = 0; i < pageInfoCheckItem.size(); i++) {
                String item = (String) pageInfoCheckItem.get(i);
                CheckItem checkItem = new CheckItem();
                checkItem.setName(item);
                pageInfoCheck.add(checkItem);
            }
            productConfig.put("pageInfoCheck", pageInfoCheck);
        }
        if (request.get("preciseTamperCheck") != null) {
            productConfig.put("preciseTamperCheck", request.get("preciseTamperCheck"));
        }
        if (productConfig.containsKey("pageInfoCheck") || productConfig.containsKey("preciseTamperCheck")) {
            apiReq.put("productConfig", productConfig);
        }

        // use server side configured default pages
        // apiReq.put("pages", "1");
        apiReq.put("metaInfo", metaInfo);
        apiReq.put("userId", userId);
        if (StringUtils.isNotBlank(nfcConfig.getServiceLevel())) {
            apiReq.put("serviceLevel", nfcConfig.getServiceLevel());
        }

        if (request.get("serviceLevel") != null) {
            apiReq.put("serviceLevel", request.get("serviceLevel"));
        }

        JSONObject nfcConfig = new JSONObject();
        if (request.get("nfcConfig") == null) {
            nfcConfig.put("needClientOCR", "Y");
        } else {
            nfcConfig = JSON.parseObject(JSON.toJSONString(request.get("nfcConfig")));
        }
        if (StringUtils.isNotBlank(request.getString("docType"))){
            nfcConfig.put("docType",request.get("docType"));
        }
        apiReq.put("nfcConfig", nfcConfig);

        System.out.println("apiReq = " + apiReq);
        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.nfc.initialize",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        JSONObject response = new JSONObject(apiResp);
        response.put("rsaPubKey", openApiClient.getOpenApiPublicKey());
        response.put("transactionId", apiResp.getString("transactionId"));
        response.put("clientCfg", apiResp.getString("clientCfg"));
        if (logger.isInfoEnabled()) {
            logger.info("response=" + apiRespStr);
        }

        return response;
    }

    @RequestMapping(value = "/nfc/checkresult", method = RequestMethod.POST)
    public JSONObject nfcCheck(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");
        String isReturnImage = request.getString("isReturnImage");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);
        apiReq.put("isReturnImage", isReturnImage);

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.nfc.checkresult",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        JSONObject response = new JSONObject(apiResp);
        return response;
    }


    @RequestMapping(value = "/privacyinfo/delete", method = RequestMethod.POST)
    public JSONObject privacyInfoDelete(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);

        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.privacyinfo.delete",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        JSONObject response = new JSONObject(apiResp);
        return response;
    }

    @Data
    public static class CheckItem {
        private String name;
    }
}
