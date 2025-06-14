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

package com.zoloz.example.idrecognizeapi;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import com.alibaba.fastjson.parser.ParserConfig;

import com.zoloz.api.sdk.api.DocRecognitionAPI;
import com.zoloz.api.sdk.client.OpenApiClient;
import com.zoloz.api.sdk.model.DocRecognitionRequest;
import com.zoloz.api.sdk.model.DocRecognitionResponse;
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
 * Example of ID Recognize
 *
 * @author Zhang Fang
 */
public class IdRecognizeExample {

    private static final Logger logger = LoggerFactory.getLogger(IdRecognizeExample.class);
    // Protocol type constants
    private static final String TWOWAY_PROTO = "2way";
    private static final String AKSK_PROTO = "aksk";

    public static void main(String[] args) {
        ParserConfig.getGlobalInstance().setSafeMode(true);

        // Create command line options
        Options options = new Options();
        options.addOption("c", true, "Client ID");
        options.addOption("n", true, "Protocol type (twoway/aksk), default: twoway");
        options.addOption("p", true, "Zoloz public key (Base64-encoded, required for twoway)");
        options.addOption("k", true, "Merchant private key path (required for twoway)");
        options.addOption("a", true, "Access Key (required for aksk)");
        options.addOption("s", true, "Secret Key (required for aksk)");
        options.addOption("f", true, "Passport image path (required)");
        options.addOption(new Option("e", true, "Service endpoint (optional)") {{
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
        if (!cmd.hasOption("c") || !cmd.hasOption("f")) {
            logger.error("Missing required parameters: -c <client_id> or -f <passport_image_path>");
            printHelp(options);
            System.exit(-1);
            return;
        }

        // Determine protocol type (default to twoway)
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
        String endpointUrl = cmd.getOptionValue("e","https://sg-sandbox-api.zoloz.com/");

        OpenApiClient client = new OpenApiClient();
        client.setHostUrl(endpointUrl);
        client.setClientId(clientId);
        client.setProtoName(protoName);
        client.setSigned(true); // Signature validation enabled by default

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
            // Initialize document recognition API
            DocRecognitionAPI docRecognitionAPI = new DocRecognitionAPI(client);

            // Prepare request
            String imagePath = cmd.getOptionValue("f");
            DocRecognitionRequest request = new DocRecognitionRequest();
            request.setBizId(UUID.randomUUID().toString()); // Use unique business ID
            request.setDocType("00000001003"); // Fixed document type (passport)
            request.setFrontPageImage(getBase64ImageContent(imagePath));

            // Call API
            DocRecognitionResponse response = docRecognitionAPI.recognition(request);

            // Handle response
            if ("S".equals(response.getResult().getResultStatus())) {
                if ("Y".equals(response.getRecognitionResult())) {
                    logger.info("Document recognition successful, valid ID document detected");

                    // Output OCR results
                    StringBuilder ocrResult = new StringBuilder("OCR Result: ");
                    response.getOcrResult().forEach((key, value) ->
                            ocrResult.append(String.format("%s: %s; ", key, value)));
                    logger.info(ocrResult.toString());

                    // Output anti-spoofing detection results (if any)
                    if (response.getSpoofResult() != null && !response.getSpoofResult().isEmpty()) {
                        StringBuilder spoofResult = new StringBuilder("Anti-Spoofing Detection: ");
                        response.getSpoofResult().forEach((key, value) ->
                                spoofResult.append(String.format("%s: %s; ", key, value)));
                        logger.info(spoofResult.toString());
                    }

                    logger.info("Operation succeeded [ResultCode: {}, Message: {}]",
                            response.getResult().getResultCode(),
                            response.getResult().getResultMessage());
                } else {
                    logger.warn("Failed to recognize document content [Error Code: {}]",
                            response.getRecognitionErrorCode());
                }
            } else {
                logger.error("Operation failed [ResultCode: {}, Message: {}]",
                        response.getResult().getResultCode(),
                        response.getResult().getResultMessage());
            }
        } catch (Exception e) {
            logger.error("API call failed: {}", e.getMessage(), e);
        }
    }

    // Print help information
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                "java IdRecognizeExample\n" +
                        "Required Parameters:\n" +
                        "  -c <client_id>         Unique merchant identifier\n" +
                        "  -f <passport_image_path> Path to identity document image\n" +
                        "Protocol Selection (optional, default=twoway):\n" +
                        "  -n twoway|aksk         Authentication protocol\n\n" +
                        "Twoway Protocol Parameters:\n" +
                        "  -p <zoloz_public_key>  Zoloz public key (Base64-encoded)\n" +
                        "  -k <private_key_path>  Merchant private key path\n\n" +
                        "AKSK Protocol Parameters:\n" +
                        "  -a <access_key>        Access Key\n" +
                        "  -s <secret_key>        Secret Key\n\n" +
                        "Optional Parameters:\n" +
                        "  -e <endpoint_url>      Service endpoint",
                options
        );
    }

    /**
     * Get Base64 content of an image file
     * @param imagePath Image file path
     * @return Base64 encoded content
     * @throws IOException If file read fails
     */
    @SneakyThrows(IOException.class)
    protected static String getBase64ImageContent(String imagePath) {
        byte[] content = FileUtils.readFileToByteArray(new File(imagePath));
        return Base64.getEncoder().encodeToString(content);
    }
}

