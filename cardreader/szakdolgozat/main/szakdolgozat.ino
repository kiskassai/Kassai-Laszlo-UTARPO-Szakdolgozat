#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266HTTPClient.h>
#include <SPI.h>
#include <MFRC522.h>
#define SS_PIN 2
#define RST_PIN 0
MFRC522 mfrc522(SS_PIN, RST_PIN);
ESP8266WiFiMulti WiFiMulti;
const String mac = "84cca8aa18e5";
const uint8_t fingerprint[20] = {0xc5, 0x81, 0xfd, 0xc9, 0xd6, 0xda, 0x39, 0xe3, 0x2d, 0xb1, 0x38, 0xff, 0x81, 0xea, 0x3e, 0xd1, 0xf3, 0x7c, 0xa9, 0x71};

void setup() {

  Serial.begin(9600);
  SPI.begin();      // Initiate  SPI bus
  mfrc522.PCD_Init();   // Initiate MFRC522
  for (uint8_t t = 4; t > 0; t--) {
    Serial.printf("[SETUP] WAIT %d...\n", t);
    Serial.flush();
    delay(1000);
  }
  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP("TOTH_KASSAILAK", "ODAVANIRVA!!!");
}

void loop() {

  WiFiMulti.run();
  if ( ! mfrc522.PICC_IsNewCardPresent() || ! mfrc522.PICC_ReadCardSerial()) {
    std::unique_ptr<BearSSL::WiFiClientSecure>client(new BearSSL::WiFiClientSecure);
    client->setFingerprint(fingerprint);
    HTTPClient https;
    if ( https.begin(*client, "https://card-reader-app-77yutcte4a-ey.a.run.app/schedule?cardReaderId=" + mac)) {
      int httpCode = https.GET();
      if (httpCode > 0) {
        if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
          String payload = https.getString();
          Serial.println(payload);
        }
      } else {
        Serial.printf("[HTTPS] GET... failed, error: %s\n", https.errorToString(httpCode).c_str());
        Serial.printf("[HTTPS] GET... failed, errorcode: %d\n", httpCode);
        String payload = https.getString();
        Serial.println(payload);
        https.end();
      }
    } else {
      Serial.printf("[HTTPS] Unable to connect\n");
    }
  }
  else {
    String content = "";
    byte letter;
    for (byte i = 0; i < mfrc522.uid.size; i++)
    {
      Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? "0" : "");
      Serial.print(mfrc522.uid.uidByte[i], HEX);
      content.concat(String(mfrc522.uid.uidByte[i] < 0x10 ? "0" : ""));
      content.concat(String(mfrc522.uid.uidByte[i], HEX));
    }
    content.toUpperCase();
    String url = "https://card-reader-app-77yutcte4a-ey.a.run.app/read?cardId=" + content.substring(1) + "&cardReaderId=" + mac;
    std::unique_ptr<BearSSL::WiFiClientSecure>client(new BearSSL::WiFiClientSecure);
    client->setFingerprint(fingerprint);
    HTTPClient https;
    if ( https.begin(*client, url)) {
      int httpCode = https.GET();
      if (httpCode > 0) {
        Serial.printf("[HTTPS] GET... : %d\n", httpCode);
        if (httpCode == 500) {
          String payload = https.getString();
          Serial.println(payload);
          Serial.printf("SIKERTELEN\n");
        }
        else {
          if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
            String payload = https.getString();
            Serial.println(payload);
            int  found = payload.indexOf("\"inClass\":false");

            if (found > 0) {

              Serial.println("Nincs Regisztrálva!");

            } else {

              Serial.print("SIKERES\n");
            }
          }

          else {
            Serial.printf("[HTTPS] GET... failed, error: %s\n", https.errorToString(httpCode).c_str());
            Serial.printf("[HTTPS] GET... failed, errorcode: %d\n", httpCode);
            String payload = https.getString();
            Serial.println(payload);
            https.end();
          }
        }
      }
    } else {
      Serial.printf("[HTTPS] Unable to connect\n");
    }
  }
}
