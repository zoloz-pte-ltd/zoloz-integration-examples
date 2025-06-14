# ZOLOZ FaceCompare API

This is an example of comparing two faces with ZOLOZ FaceCompare API

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
java -jar target/zoloz-facecompare-api-example-1.0-SNAPSHOT.jar \
  -c <client_id> \
  -p <zoloz_public_key_content> \
  -k <merchant_private_key_path> \
  -f1 <face1_image_path> \
  -f2 <face2_image_path>
```

aksk mode

```sh
java -jar target/zoloz-facecompare-api-example-1.0-SNAPSHOT.jar \
  -c <client_id> \
  -n aksk \
  -a <access_key> \
  -s <secret_key> \
  -f1 <face1_image_path> \
  -f2 <face2_image_path>
```