#include <SoftwareSerial.h>

//EC - engine connector
#define EC_5V 6
#define EC_GND 7
#define EC_M1A 2
#define EC_M1B 3
#define EC_M2A 4
#define EC_M2B 5
//PD - proximity detector
#define PD_5V 9
#define PD_Echo 10
#define PD_Trig 11

SoftwareSerial btSerial(0,1);

void setup() {
  //setup pins
  //EC
  pinMode(EC_5V, OUTPUT);
  pinMode(EC_GND, OUTPUT);
  pinMode(EC_M1A, OUTPUT);
  pinMode(EC_M1B, OUTPUT);
  pinMode(EC_M2A, OUTPUT);
  pinMode(EC_M2B, OUTPUT);
  digitalWrite(EC_5V, HIGH);
  digitalWrite(EC_GND, LOW);
  digitalWrite(EC_M1A, LOW);
  digitalWrite(EC_M1B, LOW);
  digitalWrite(EC_M2A, LOW);
  digitalWrite(EC_M2B, LOW);
  //PD
  pinMode(PD_5V, OUTPUT);
  pinMode(PD_Echo, INPUT);
  pinMode(PD_Trig, OUTPUT);
  digitalWrite(PD_5V, HIGH);
  
  //setup communication
  Serial.begin(9600);
  btSerial.begin(9600);
  Serial.println("### Tesla Taxi ###");
}

void move(char c) {
  switch(c) {
      case 'f':
        digitalWrite(EC_M1A, HIGH);
        digitalWrite(EC_M1B, LOW);
        break;
      case 'b':
        digitalWrite(EC_M1A, LOW);
        digitalWrite(EC_M1B, HIGH);
        break;
      case 'l':
        digitalWrite(EC_M2A, HIGH);
        digitalWrite(EC_M2B, LOW);
        break;  
      case 'r':
        digitalWrite(EC_M2A, LOW);
        digitalWrite(EC_M2B, HIGH);
        break;  
    }
}

void stop() {
  digitalWrite(EC_M1A, LOW);
  digitalWrite(EC_M1B, LOW);
  digitalWrite(EC_M2A, LOW);
  digitalWrite(EC_M2B, LOW);
}

int distance() {
  //init sequence
  digitalWrite(PD_Trig, LOW);
  delayMicroseconds(2);
  digitalWrite(PD_Trig, HIGH);
  delayMicroseconds(15);  
  digitalWrite(PD_Trig, LOW);
  //read data
  digitalWrite(PD_Echo, HIGH); 
  int t = pulseIn(PD_Echo, HIGH);
  return t / 58;
}

void loop() {
//  uncomment to test car movement over bt
//  if(btSerial.available()) { 
//    char c = btSerial.read();
//    Serial.println(c);
//    move(c);
//    delay(300);
//    stop();
//  }

//  uncomment to test distance
//    Serial.println(distance());
//    delay(500);
}
