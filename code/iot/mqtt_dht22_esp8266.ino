#include <ESP8266WiFi.h>
#include <DHT.h>
#include <PubSubClient.h>

#define DHTPIN D4
#define DHTTYPE DHT22

const char* ssid = "TOPNET_86D0";
const char* password = "vgu6xd7pcj";
const char* mqttServer = "127.0.0.1";
const int mqttPort = 1883;
const char* mqttUser = "gardenuser";
const char* mqttPassword = "smartgardenirrigation";
const char* mqttTopic = "garden/sensor/data";

DHT dht(DHTPIN, DHTTYPE);
WiFiClient espClient;
PubSubClient client(espClient);

void setupWiFi() {
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
  }
}

void connectMQTT() {
  while (!client.connected()) {
    if (client.connect("ESP8266Client", mqttUser, mqttPassword)) {
    } else {
      delay(2000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  dht.begin();
  setupWiFi();
  client.setServer(mqttServer, mqttPort);
  connectMQTT();
}

void loop() {
  if (!client.connected()) {
    connectMQTT();
  }
  client.loop();

  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();

  if (isnan(temperature) || isnan(humidity)) {
    return;
  }

  String payload = "Temp: " + String(temperature) + "°C, Humidity: " + String(humidity) + "%";
  client.publish(mqttTopic, payload.c_str());

  delay(10000);
}
