package com.zoloz.example.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zoloz.api.sdk.client.OpenApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

/**
 * @author hans.zy@antfin.com
 * @date 2022/6/10  8:58 pm
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = {"/webapi"})
public class ConnectController {
    private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);

    @Autowired
    private OpenApiClient openApiClient;
    /**
     * this api supply a method to enroll your image
     * @param imageFile : upload selfie image content
     * @param userId    : specify user ID
     */
    @RequestMapping(value = {"/connectDemoService/enroll"}, method = RequestMethod.POST)
    public JSONObject connectEnroll(@RequestParam(value = "imagePath")MultipartFile imageFile,String userId) {

        if (logger.isInfoEnabled()) {
            logger.info("imageFile="+imageFile.getName()+" imageSize"+imageFile.getSize()+" userId:"+userId);
        }

        JSONObject apiReq = new JSONObject();
        apiReq.put("userId", userId);
        apiReq.put("bizId", "dummy_bizid_" + System.currentTimeMillis());
        String base64string = "";
        try {
             base64string = Base64.getEncoder().encodeToString(imageFile.getBytes());
        }
         catch (IOException e) {
           logger.error(e.getMessage());
           logger.error(e.toString());
           JSONObject errorReturn =   new JSONObject();
           errorReturn.put("fileError",e.getMessage());
           return errorReturn;
        }
        apiReq.put("base64ImageContent",base64string);
        if (logger.isInfoEnabled()) {
            logger.info("openApiClient request=" + JSON.toJSONString(apiReq));
        }
        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.connect.enroll",
                JSON.toJSONString(apiReq)
         );
        if (logger.isInfoEnabled()) {
            logger.info("response=" + apiRespStr);
        }
        return JSON.parseObject(apiRespStr);
    }

    @RequestMapping(value = {"/connectDemoService/initialize"}, method = RequestMethod.POST)
    public JSONObject connectInitialize(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();

        JSONObject apiReq = new JSONObject();
/*
       face enrollment must be done before the verify action.
       That is to say, user A's face must be enrolled before you can verify his face.
 */
        String userId = request.getString("userId");
        apiReq.put("userId", userId);
        apiReq.put("bizId", businessId);
        apiReq.put("uiType", request.getOrDefault("uiType","Cherry"));
        String metaInfo = request.getString("metaInfo");
        apiReq.put("metaInfo", metaInfo);
        apiReq.put("sceneCode",request.getOrDefault("sceneCode","test scene"));
        apiReq.put("serviceLevel",request.getOrDefault("serviceLevel","CONNECT0002"));
        if (logger.isInfoEnabled()) {
            logger.info("openApiClient request=" + JSON.toJSONString(apiReq));
        }
        String apiRespStr = openApiClient.callOpenApi(
                        "v1.zoloz.connect.initialize",
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

    @RequestMapping(value = "/connectDemoService/checkresult", method = RequestMethod.POST)
    public JSONObject connectCheckresult(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("request=" + request);
        }

        String businessId = "dummy_bizid_" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);
        if (logger.isInfoEnabled()) {
            logger.info("openApiClient request=" + request);
        }
        String apiRespStr = openApiClient.callOpenApi(
                "v1.zoloz.connect.checkresult",
                JSON.toJSONString(apiReq)
        );
        if(logger.isInfoEnabled()){
            logger.info("response="+apiRespStr);
        }

        return JSON.parseObject(apiRespStr);
    }


}
