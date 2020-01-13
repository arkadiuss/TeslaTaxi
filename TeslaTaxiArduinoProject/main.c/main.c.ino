#include <SoftwareSerial.h>

//EC - engine connector
#define EC_GND 2
#define EC_M1A 6
#define EC_M1B 9
#define EC_M2A 10
#define EC_M2B 11
//PD - proximity detector
#define PD_5V 3
#define PD_Echo 7
#define PD_Trig 8
#define PD_GND 4

SoftwareSerial btSerial(0,1);

void setup() {
  //setup pins
  //EC
  pinMode(EC_GND, OUTPUT);
  pinMode(EC_M1A, OUTPUT);
  pinMode(EC_M1B, OUTPUT);
  pinMode(EC_M2A, OUTPUT);
  pinMode(EC_M2B, OUTPUT);
  digitalWrite(EC_GND, LOW);
  digitalWrite(EC_M1A, LOW);
  digitalWrite(EC_M1B, LOW);
  digitalWrite(EC_M2A, LOW);
  digitalWrite(EC_M2B, LOW);
  //PD
  pinMode(PD_5V, OUTPUT);
  pinMode(PD_GND, OUTPUT);
  pinMode(PD_Echo, INPUT);
  pinMode(PD_Trig, OUTPUT);
  digitalWrite(PD_5V, HIGH);
  digitalWrite(PD_GND, LOW);
  
  //setup communication
  Serial.begin(9600);
  btSerial.begin(9600);
  Serial.println("### Tesla Taxi ###");
}

bool goFront = false;
bool goBack = false;
bool goLeft = false;
bool goRight = false;

void move(char c) {
  switch(c) {
      case 'f':
        analogWrite(EC_M1A, 0);
        analogWrite(EC_M1B, 100); // 0 - 255
        break;
      case 'b':
        analogWrite(EC_M1A, 100);
        analogWrite(EC_M1B, 0);
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

void stopFrontBack() {
  digitalWrite(EC_M1A, LOW);
  digitalWrite(EC_M1B, LOW);
  goFront = goBack = false;
}

void stopLeftRight() {
  digitalWrite(EC_M2A, LOW);
  digitalWrite(EC_M2B, LOW);
  if(goLeft) {
    move('r');
    delay(10);
  } else if(goRight) {
    move('l');
    delay(10);
  }
  digitalWrite(EC_M2A, LOW);
  digitalWrite(EC_M2B, LOW);
  goRight = goLeft = false;
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
  if(btSerial.available()) { 
    char c = btSerial.read();
    switch (c) {
      case 'f': goFront = true; goBack = false; break;
      case 'b': goBack = true; goFront = false; break;
      case 'l': goLeft = true; goRight = false; move('l'); break;
      case 'r': goRight = true; goLeft = false; move('r'); break;
      case 's': stopFrontBack(); break;
      case 'w': stopLeftRight(); break;
    }
  }

  if (goFront) move('f');
  if (goBack) move('b');
  delay(50);
  digitalWrite(EC_M2A, LOW);
  digitalWrite(EC_M2B, LOW);
//  uncomment to test distance
  btSerial.println(distance());
  Serial.println(distance());
}
