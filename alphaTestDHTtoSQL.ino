#include "DHT.h"
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>

#define DHTPIN 14
#define DHTTYPE DHT22

const int moistPin =A0;
int moisture = 0;
float valueToSendTemp, valueToSendHumidity ;
int iD = 0;
String request = "addValue";

DHT dht(DHTPIN, DHTTYPE);

ESP8266WiFiMulti WiFiMulti;

void setup() {
  Serial.begin(115200);
  Serial.setTimeout(2000);
  pinMode(moistPin, INPUT);
  dht.begin();

  

  //while(!Serial){}
  

  //pinMode(14, INPUT);
  

  
  
  connectToWifi();
  readDHT();
  readSoil();
  sendToServer();
  delay(1500);

  ESP.deepSleep(15000*1000);
}

void loop() {}

void connectToWifi(){

  // anslut till wifi
    WiFiMulti.addAP("Tele2Internet-5AFF2", "kattpiss");
    //WiFiMulti.addAP("AndroidAP", "petc9982");
    
    Serial.println();
    Serial.println();
    Serial.print("Wait for WiFi... ");

    while(WiFiMulti.run() != WL_CONNECTED) {
        Serial.print(".");
        delay(500);
    }
    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
    delay(500);
  }

void sendToServer(){

    const uint16_t port = 8000;
    const char * host = "192.168.1.5";
    //const char * host = "192.168.43.100";

    Serial.print("connecting to ");
    Serial.println(host);

    // Use WiFiClient class to create TCP connections
    WiFiClient client;

    if (!client.connect(host, port)) {
        Serial.println("connection failed");
        Serial.println("wait 5 sec...");
        delay(5000);
        return;
    }

    // This will send the request to the server
    client.print(request+"\r\n" + iD + "\r\n" + valueToSendTemp +
    "\r\n" + valueToSendHumidity + "\r\n" + moisture + "\r\n");
    Serial.print("Value sent to server");

    Serial.println("closing connection");
    client.stop();
    delay(5000);
  }

void readDHT(){
  
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  delay(1000);
  
  if (isnan(t) || isnan(h)) {
  Serial.println("Failed to read from DHT");
  return;
  } else {
  Serial.print("Humidity = ");
  Serial.println(h);
  valueToSendHumidity = h;
  Serial.print("Temperature = ");
  Serial.println(t);
  valueToSendTemp = t;
    }
  }

  void readSoil(){

  moisture = analogRead(moistPin);
  Serial.print(moisture);
  Serial.print("   ");
  moisture = map(moisture, 100, 381, 100, 0);
  Serial.print(moisture);
  Serial.println("  %");
  delay(1000);
    
    }


    //för att läsa från server
    //String line = client.readStringUntil('\r');
    //client.println(line);
