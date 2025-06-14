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

package example.nfcnative.autoconfig;

import com.zoloz.api.sdk.client.OpenApiClient;
import example.util.KeyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * api client configuraiton
 *
 * @author Zhang Fang
  */
@Configuration
public class ApiClientConfig {

    @Value("${host.url:https://sg-production-api.zoloz.com}")
    private String hostUrl = "https://sg-production-api.zoloz.com";

    @Value("${client.id}")
    private String clientId;

    @Value("${merchant.privkey.path:}")
    private String merchantPrivKeyPath;

    @Value("${zoloz.pubkey.path:}")
    private String zolozPubKeyPath = null;

    @Value("${zoloz.pubkey:}")
    private String zolozPubKey = null;

    @Value("${zoloz.protoName:2way}")
    private String protoName;

    @Value("${zoloz.accessKey:}")
    private String accessKey;

    @Value("${zoloz.secretKey:}")
    private String secretKey;

    private static final String TWOWAY_PROTO = "2way";

    private static final String AKSK_PROTO = "aksk";

    @Bean
    public OpenApiClient client() {
        OpenApiClient client = new OpenApiClient();
        client.setHostUrl(hostUrl);
        client.setClientId(clientId);
        client.setProtoName(protoName);
        if (TWOWAY_PROTO.equals(protoName)) {
            if (zolozPubKey == null || zolozPubKey.isEmpty()) {
                zolozPubKey = KeyUtil.loadKeyContent(zolozPubKeyPath);
            }
            String merchantPrivateKey = KeyUtil.loadKeyContent(merchantPrivKeyPath);
            client.setOpenApiPublicKey(zolozPubKey);
            client.setMerchantPrivateKey(merchantPrivateKey);
        } else if (AKSK_PROTO.equals(protoName)) {
            client.setAccessKey(accessKey);
            client.setSecretKey(secretKey);
        } else {
            throw new RuntimeException("protoName is not support");
        }
        return client;
    }

}
