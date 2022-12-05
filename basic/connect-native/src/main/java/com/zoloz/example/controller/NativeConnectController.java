package com.zoloz.example.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zoloz.api.sdk.client.OpenApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hans.zy@antfin.com
 * @date 2022/6/10   8:58 pm
 */
@RestController
@RequestMapping(value = {"/webapi"})
public class NativeConnectController {
    private static final Logger logger = LoggerFactory.getLogger(NativeConnectController.class);

    @Autowired
    private OpenApiClient openApiClient;

    /**
     * this api supply a method to enroll your image
     * @param request e.g.
     *    {
     *     "bizId":"2017839040588699",
     *     "userId":"merchant side user id",
     *   "base64ImageContent":"xxxxxxxxxxxxxxxxx"
     *    }
     */
    @RequestMapping(value = {"/connectDemoService/enroll"}, method = RequestMethod.POST)
    public JSONObject connectEnroll(@RequestBody JSONObject request) {

        if (logger.isInfoEnabled()) {
            logger.info("enroll request ="+JSONObject.toJSONString(request));
        }

        JSONObject apiReq = new JSONObject();
        apiReq.put("userId", request.get("userId"));
        apiReq.put("bizId", request.getOrDefault("bizId","dummy_bizid_" + System.currentTimeMillis()));
        apiReq.put("base64ImageContent",apiReq.get("base64ImageContent"));

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
        apiReq.put("wirelessConfigGroup",request.getOrDefault("wirelessConfigGroup","sit"));
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
