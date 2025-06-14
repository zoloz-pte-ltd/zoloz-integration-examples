# ZOLOZ IdRecognize (H5 Mode) 

This is an example of minimum server allowing user to capture valid document (such as Id card and passport) in mobile browser.

This example supports two authentication methods:

- 2way: Public/Private Key authentication (default)
- aksk: Access Key / Secret Key authentication

Please note that ZOLOZ IdRecognize (H5 Mode) is currently in beta.

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
  -jar target/zoloz-idrecognize-h5-bizserver-1.0-SNAPSHOT.jar
```

or specify the public key content directly instead of specify the file path of the public key:
```shell
java \
  -Dclient.id=<client_id> \
  -Dmerchant.privkey.path=<merchant_private_key_path> \
  -Dzoloz.pubkey=<zoloz_public_key_base64_content> \
  -jar target/zoloz-idrecognize-h5-bizserver-1.0-SNAPSHOT.jar
```

aksk mode
```shell
java \
  -Dclient.id=<client_id> \
  -Dzoloz.protoName=aksk \
  -Dzoloz.accessKey=<zoloz_accessKey> \
  -Dzoloz.secretKey=<zoloz_secretKey> \
  -jar target/zoloz-idrecognize-h5-bizserver-1.0-SNAPSHOT.jar
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
2020-01-01 00:08:20.099 [] [main] INFO  com.zoloz.example.idrecognize.ServerInfoListener - Server started on 192.168.1.5:8080
2020-01-01 00:08:20.102 [] [main] INFO  com.zoloz.example.idrecognize.Application - Started Application in 7.766 seconds (JVM running for 10.232)
```

### 3. Start the H5 IdRecognize demo from the mobile phone
Make sure your mobile phone is in the same network with the server, visit http://\<ip\>:\<port\>/index.html (e.g. http://192.168.1.5:8080/index.html) with supported browser in your mobile phone.

| **OS** | **Supported Browser** |
| :---- | :---- |
| iOS | Safari |
| Android | Chrome |