# ZOLOZ FaceCapture (Native Mode) 

This is an example of minimum server allowing user to capture alive faces in mobile app.

This example supports two authentication methods:

- 2way: Public/Private Key authentication (default)
- aksk: Access Key / Secret Key authentication

> NOTE: This server needs to cooperate with a mobile app integrating ZOLOZ client SDK, check the sample code of minimum demo apps below:
> - [Minimum demo app of iOS](https://github.com/zoloz-pte-ltd/zoloz-demo-ios)
> - [Minimum demo app of Android](https://github.com/zoloz-pte-ltd/zoloz-demo-android)


## Build
**Prequisites**

- JDK 1.8
- Maven >3.2.5

<br />Execute following command from the root directory of the project:
```shell
mvn package
```

## Usage
**Prequisites**
- JRE 1.8

### 1. Launch the server
Execute following command (on local machine typically):

2way mode
```shell
java \
  -Dclient.id=<client_id> \
  -Dmerchant.privkey.path=<merchant_private_key_path> \
  -Dzoloz.pubkey.path=<zoloz_public_key_path> \
  -jar target/zoloz-facecapture-native-bizserver-1.0-SNAPSHOT.jar
```

or specify the public key content directly instead of specify the file path of the public key:
```shell
java \
  -Dclient.id=<client_id> \
  -Dmerchant.privkey.path=<merchant_private_key_path> \
  -Dzoloz.pubkey=<zoloz_public_key_base64_content> \
  -jar target/zoloz-facecapture-native-bizserver-1.0-SNAPSHOT.jar
```
aksk mode
```shell
java \
  -Dclient.id=<client_id> \
  -Dzoloz.protoName=aksk \
  -Dzoloz.accessKey=<zoloz_accessKey> \
  -Dzoloz.secretKey=<zoloz_secretKey> \
  -jar target/zoloz-facecapture-native-bizserver-1.0-SNAPSHOT.jar
```


### 2. Find the endpoint of the server

The endpoint consists of the ip (in local network) and the port (8080 by default). 

It is printed in the server log with the pattern "Server started on $ip:$port", following is a sample of the server log, and the endpoint is 192.168.1.5:8080 in this sample.
```plain
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

... (some log lines are ignored.)
2020-01-01 00:08:19.922 [] [main] INFO  org.apache.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8080"]
2020-01-01 00:08:19.949 [] [main] INFO  org.apache.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8080"]
2020-01-01 00:08:19.970 [] [main] INFO  org.apache.tomcat.util.net.NioSelectorPool - Using a shared selector for servlet write/read
2020-01-01 00:08:20.085 [] [main] INFO  org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer - Tomcat started on port(s): 8080 (http)
2020-01-01 00:08:20.099 [] [main] INFO  com.zoloz.example.facecapturenative.ServerInfoListener - Server started on 192.168.1.5:8080
2020-01-01 00:08:20.102 [] [main] INFO  com.zoloz.example.facecapturenative.Application - Started Application in 7.766 seconds (JVM running for 10.232)
```

### 3. Launch the demo app
> Ignored

### 4. Start face-capture flow
1. Fill the inbox of "HOST" with "http://\<ip\>:\<port\>", the `ip` and the `port` are values get from step 2.
2. Fill the inbox of "API" with "/api/facecapture/initialize".
3. Fill the inbox of "LEVEL" with "FACECAPTURE002".
4. Click "START ZOLOZ" button to start the flow.