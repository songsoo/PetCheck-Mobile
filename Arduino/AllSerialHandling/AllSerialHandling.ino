
int preIBI = 0;
int nextIBI = 0;
double IBIDiffArr[10];
int IBICount = 10;
double RMSSD1 = 0;
double RMSSD2 = 0;
int n = 0;
int offlineCount = 0;
String beatData;
String RMSSDData;

boolean isPreConnected = false;

String origArray[100];


void serialOutput() { 
  switch (outputType) {
    case PROCESSING_VISUALIZER:
      sendDataToSerial('S', Signal);     
      break;
    case SERIAL_PLOTTER:  
      beatData = "";
      RMSSDData = "";
      nextIBI = IBI;
      int btRead = BTSerial.read();
      
      if (preIBI != 0 && nextIBI != 0 && nextIBI < 800) {
        IBIDiffArr[n%IBICount] = pow((nextIBI - preIBI), 2);
        beatData += "#beat/";
        beatData += BPM;
        beatData += "/";
        n += 1;
        BTSerial.println(beatData);
        printRMSSD();
        Serial.println(getRMSSD());
      }
      preIBI = nextIBI;
      if (n > IBICount) {
        RMSSD1 = getRMSSD();
        RMSSDData = "#RMSSD/";
        RMSSDData += RMSSD1;
        RMSSDData += "/";
        RMSSD2 = RMSSD1;
        BTSerial.println(RMSSDData);
      }
      break;
    default:
      break;
  }

}

double getRMSSD(){
  double difference = 0;
  for(int i=0;i<IBICount;i++){
      difference += IBIDiffArr[i];
  }
  difference = sqrt(difference/IBICount);
  
  return difference;
}

void printRMSSD(){
  for(int i=0;i<IBICount;i++){
      Serial.print(IBIDiffArr[i]);
      Serial.print("/");
  }
  Serial.println("");
  }

void serialOutputWhenBeatHappens() {
  switch (outputType) {
    case PROCESSING_VISUALIZER:    
      sendDataToSerial('B', BPM);  
      sendDataToSerial('Q', IBI);
      break;

    default:
      break;
  }
}

void sendDataToSerial(char symbol, int data ) {
  Serial.print(symbol);
  Serial.println(data);
}
