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
  pinMode(PD_Echo, INPUT);
  pinMode(PD_Trig, OUTPUT);
  digitalWrite(PD_5V, HIGH);
  
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
        analogWrite(EC_M1B, 150); // 0 - 255
        break;
      case 'b':
        analogWrite(EC_M1A, 150);
        analogWrite(EC_M1B, 0);
        break;
      case 'l':
        analogWrite(EC_M2A, 150);
        analogWrite(EC_M2B, 0);
        break;  
      case 'r':
        analogWrite(EC_M2A, 0);
        analogWrite(EC_M2B, 150);
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
  if(btSerial.available()) { 
    char c = btSerial.read();
    Serial.println(c);
    switch (c) {
      case 'f': goFront = true; goBack = false; break;
      case 'b': goBack = true; goFront = false; break;
      case 'l': goLeft = true; goRight = false; break;
      case 'r': goRight = true; goLeft = false; break;
      case 's': goFront = goBack = goLeft = goRight = false; stop(); break;
    }
  } else {
    goFront = goLeft = goRight = goBack = false;
  }

  if (goFront) move('f');
  if (goBack) move('b');
  if (goLeft) move('l');
  if (goRight) move('r');
  delay(100);
  digitalWrite(EC_M2A, LOW);
  digitalWrite(EC_M2B, LOW);
  goRight = goLeft = false;
//  uncomment to test distance
  btSerial.write(distance());
  Serial.println(distance());
//    delay(500);
}
