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

package com.zoloz.example.facecompareapi;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import com.alibaba.fastjson.parser.ParserConfig;

import com.zoloz.api.sdk.api.FaceCompareAPI;
import com.zoloz.api.sdk.client.OpenApiClient;
import com.zoloz.api.sdk.model.FaceCompareRequest;
import com.zoloz.api.sdk.model.FaceCompareResponse;
import com.zoloz.example.util.KeyUtil;
import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example of Face Compare
 *
 * @author Zhang Fang
 */
public class FaceCompareExample {

    private static final Logger logger = LoggerFactory.getLogger(FaceCompareExample.class);
    // Protocol type constants
    private static final String TWOWAY_PROTO = "2way";
    private static final String AKSK_PROTO = "aksk";

    public static void main(String[] args) {
        ParserConfig.getGlobalInstance().setSafeMode(true);

        // Create command line options
        Options options = new Options();
        options.addOption("c", true, "Client ID");
        options.addOption("n", true, "Protocol type (twoway/aksk), default: twoway");
        options.addOption("p", true, "Base64-encoded Zoloz public key (required for twoway)");
        options.addOption("k", true, "Path to merchant private key file (required for twoway)");
        options.addOption("a", true, "Access key (required for aksk)");
        options.addOption("s", true, "Secret key (required for aksk)");
        options.addOption("f1", true, "Path of 1st face image to be compared");
        options.addOption("f2", true, "Path of 2nd face image to be compared");
        options.addOption(new Option("e", true, "Zoloz service endpoint (optional)") {{
            setRequired(false);
        }});

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException ex) {
            printHelp(options);
            System.exit(-1);
            return;
        }

        // Validate required parameters
        if (!cmd.hasOption("c") || !cmd.hasOption("f1") || !cmd.hasOption("f2")) {
            logger.error("Missing required parameters!");
            printHelp(options);
            System.exit(-1);
            return;
        }

        // Get protocol type (default to twoway)
        String protoName = cmd.getOptionValue("n", TWOWAY_PROTO);

        // Validate protocol-specific parameters
        if (TWOWAY_PROTO.equalsIgnoreCase(protoName)) {
            if (!cmd.hasOption("p") || !cmd.hasOption("k")) {
                logger.error("For twoway protocol, both -p (public key) and -k (private key path) are required!");
                printHelp(options);
                System.exit(-1);
                return;
            }
        } else if (AKSK_PROTO.equalsIgnoreCase(protoName)) {
            if (!cmd.hasOption("a") || !cmd.hasOption("s")) {
                logger.error("For aksk protocol, both -a (access key) and -s (secret key) are required!");
                printHelp(options);
                System.exit(-1);
                return;
            }
        } else {
            logger.error("Unsupported protocol: {}", protoName);
            System.exit(-1);
            return;
        }

        // Initialize OpenApiClient
        String clientId = cmd.getOptionValue("c");
        String endpointUrl = cmd.getOptionValue("e", "https://sg-sandbox-api.zoloz.com/");

        OpenApiClient client = new OpenApiClient();
        client.setHostUrl(endpointUrl);
        client.setClientId(clientId);
        client.setProtoName(protoName);
        client.setSigned(true);  // Maintain signature validation by default

        // Configure credentials based on protocol type
        if (TWOWAY_PROTO.equalsIgnoreCase(protoName)) {
            String zolozPublicKey = cmd.getOptionValue("p");
            String merchantPrivateKeyPath = cmd.getOptionValue("k");
            String merchantPrivateKey = KeyUtil.loadKeyContent(merchantPrivateKeyPath);
            client.setOpenApiPublicKey(zolozPublicKey);
            client.setMerchantPrivateKey(merchantPrivateKey);
        } else if (AKSK_PROTO.equalsIgnoreCase(protoName)) {
            String accessKey = cmd.getOptionValue("a");
            String secretKey = cmd.getOptionValue("s");
            client.setAccessKey(accessKey);
            client.setSecretKey(secretKey);
        }

        try {
            // Initialize FaceCompare API
            FaceCompareAPI faceCompareApi = new FaceCompareAPI(client);

            // Prepare API request
            String face1ImgPath = cmd.getOptionValue("f1");
            String face2ImgPath = cmd.getOptionValue("f2");

            FaceCompareRequest request = new FaceCompareRequest();
            request.setBizId(UUID.randomUUID().toString());  // Generate unique biz ID
            request.getFace1().setContent(getBase64ImageContent(face1ImgPath));
            request.getFace2().setContent(getBase64ImageContent(face2ImgPath));

            // Execute API call
            FaceCompareResponse response = faceCompareApi.compare(request);

            // Handle response
            if ("S".equals(response.getResult().getResultStatus())) {
                logger.info("Face comparison successful: {}",
                        String.format("Two faces are from %s, similarity score: %.2f",
                                response.getSamePerson() ? "same person" : "different persons",
                                response.getScore()));
            } else {
                logger.error("Operation failed! Code: {}, Message: {}",
                        response.getResult().getResultCode(),
                        response.getResult().getResultMessage());
            }
        } catch (Exception e) {
            logger.error("API call failed: {}", e.getMessage(), e);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                "java FaceCompareExample \n" +
                        "Required Parameters:\n" +
                        "  -c <client_id>         Client identifier\n" +
                        "  -f1 <face1_image_path> Path to first face image\n" +
                        "  -f2 <face2_image_path> Path to second face image\n" +
                        "Protocol Selection (optional, default=twoway):\n" +
                        "  -n twoway|aksk         Authentication protocol\n\n" +
                        "Parameters for twoway protocol:\n" +
                        "  -p <zoloz_public_key>  Base64-encoded Zoloz public key\n" +
                        "  -k <private_key_path>  Path to merchant's private key file\n\n" +
                        "Parameters for aksk protocol:\n" +
                        "  -a <access_key>        Access key ID\n" +
                        "  -s <secret_key>        Secret access key\n\n" +
                        "Optional Parameters:\n" +
                        "  -e <endpoint_url>      Service endpoint",
                options
        );
    }

    /**
     * Get content of the image file
     * @param imagePath path of the image file
     * @return base64 encoded content of the image file
     * @throws IOException if file read fails
     */
    @SneakyThrows(IOException.class)
    protected static String getBase64ImageContent(String imagePath) {
        byte[] content = FileUtils.readFileToByteArray(new File(imagePath));
        return Base64.getEncoder().encodeToString(content);
    }
}

