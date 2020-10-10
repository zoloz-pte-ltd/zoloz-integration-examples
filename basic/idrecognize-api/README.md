# ZOLOZ IdRecognize API

This is an example of recognizing document image with ZOLOZ IdRecognize API

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

```sh
java -jar target/zoloz-idrecognize-api-example-1.0-SNAPSHOT.jar \
  -c <client_id> \
  -p <zoloz_public_key_content> \
  -k <merchant_private_key_path> \
  -f <passport_image_path>
```
