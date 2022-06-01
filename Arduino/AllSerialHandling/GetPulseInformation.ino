
#include<Wire.h>
#define PROCESSING_VISUALIZER 1
#define SERIAL_PLOTTER  2
#include <SoftwareSerial.h>
//  Variables
int pulsePin = 0;                 // Pulse Sensor purple wire connected to
int blinkPin = 13;                // pin to blink led at each beat
int fadePin = 5;                  // pin to do fancy classy fading blink at each beat
int fadeRate = 0;                 // used to fade LED on with PWM on fadePin

// Volatile Variables, used in the interrupt service routine!
volatile int BPM;                   // int that holds raw Analog in 0. updated every 2mS
volatile int Signal;                // holds the incoming raw data
volatile int IBI = 600;             // int that holds the time interval between beats! Must be seeded!
volatile boolean Pulse = false;     // "True" when User's live heartbeat is detected. "False" when not a "live beat".
volatile boolean QS = false;        // becomes true when Arduoino finds a beat.

boolean connectStart = false;

static int outputType = SERIAL_PLOTTER;
SoftwareSerial BTSerial(8, 9);

void setup() {
  pinMode(blinkPin, OUTPUT);      
  pinMode(fadePin, OUTPUT);     
  Serial.begin(9600);         
  BTSerial.begin(9600);
  interruptSetup();               
  }

void loop() {
    if (QS == true) {  
      fadeRate = 255;      
      serialOutputWhenBeatHappens();   
      serialOutput();
      QS = false;            
    }
    ledFadeToBeat();           
  delay(50);                           
}
