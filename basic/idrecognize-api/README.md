# ZOLOZ IdRecognize API

This is an example of recognizing document image with ZOLOZ IdRecognize API

This example supports two authentication methods:

- 2way: Public/Private Key authentication (default)
- aksk: Access Key / Secret Key authentication

## Build
**Prequisites**
- JDK 1.8
- Maven >3.2.5

To build the project, simply execute following command from the root directory of the project:
```sh
mvn package
```

## Usage
**Prequisite**
- JRE 1.8

Execute following command:

2way mode
```sh
java -jar target/zoloz-idrecognize-api-example-1.0-SNAPSHOT.jar \
  -c <client_id> \
  -p <zoloz_public_key_content> \
  -k <merchant_private_key_path> \
  -f <passport_image_path>
```

aksk mode
```sh
java -jar target/zoloz-idrecognize-api-example-1.0-SNAPSHOT.jar \
  -c <client_id> \
  -n aksk \
  -a <zoloz_access_key> \
  -s <zoloz_secret_key> \
  -f <passport_image_path>
```
```
