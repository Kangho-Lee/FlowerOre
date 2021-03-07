
#include <Arduino.h>

#if !defined(SERIAL_PORT_MONITOR)
  #error "Arduino version not supported. Please update your IDE to the latest version."
#endif
#if defined(SERIAL_PORT_USBVIRTUAL)
  // Shield Jumper on HW (for Leonardo and Due)
  #define port SERIAL_PORT_HARDWARE
  #define pcSerial SERIAL_PORT_USBVIRTUAL
#else
  // Shield Jumper on SW (using pins 12/13 or 8/9 as RX/TX)
  #include "SoftwareSerial.h"
  SoftwareSerial port(11, 12);
  #define pcSerial SERIAL_PORT_MONITOR
#endif

#include "EasyVR.h"
#include <Servo.h> // 서보모터를 사용하기 위한 부분
#include <SPI.h>
#include <WiFi.h>
#include <Adafruit_NeoPixel.h> // LED 사용을 위한 헤더파일선언

#ifdef __AVR__
  #include <avr/power.h>
#endif

#include<Wire.h> // 캐릭터LCD 사용을 위해 추가한 부분
#include<LiquidCrystal_I2C.h> // 캐릭터LCD 사용을 위해 추가한 부분
#include <DHT.h> // 온/습도센서를 위해 추가한 부분


#include "IoTMakers.h"
#include "Shield_Wrapper.h"

#define DHTPIN 2 // 온/습도센서를 위해 추가한 부분
#define MOTOR 3 // 모터 동작을 위해 추가한 부분
#define Trig 4 // 초음파 센서를 위한 부분
#define Echo 5 // 초음파 센서를 위한 부분
#define PIN 6 // LED 제어

#define DHTTYPE DHT22 // 온/습도센서를 위해 추가한 부분
#define NUMPIXELS 30 // LED 제어

int stage1 = 8; // 서보모터를 사용하기 위한 부분, 9번 디지털 핀을 사용

int rainbow = 11; // 스위치로 LED를 제어하기 위한 부분
int water_button = 12; // 물주기를 버튼으로 구현
/*
Arduino Shield
*/
LiquidCrystal_I2C lcd(0x3F, 2, 1, 0, 4, 5, 6, 7, 3, POSITIVE); // 캐릭터LCD 사용을 위해 추가한 부분
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800); // LED 사용

EasyVR easyvr(port);

Shield_Wrapper	g_shield;

#define SDCARD_CS	4

const int numReadings = 10; //추가한 부분
const int soilSensorPin = A0; // 추가한 부분, 토양습도센서

int cnt = 0; // 온도차이를 계산하기 위한 부분에 사용됨
int angle = 0; // 서보모터를 사용하기 위한 부분
int water_time = 3;
int water_time_ago = 1;
int water_cnt = 1;

unsigned long Temp_t; // 온도 변화 시 데이터를 전송하기 위한 시간 값을 전역변수로 설정, Send_temperature 함수에 사용

float humidity; // 추가한 부분
float temperature; // 추가한 부분
float temperature1; // 추가한 부분, 온도차이를 계산하기 위한 부분
float SoilHumi; // 추가한 부분

float water; // 추가한 부분, 물의 수위를 측정하기 위한 부분

double moisture;
double Average_Moisture;

DHT dht(DHTPIN, DHTTYPE); // Digital 2번 핀과 DHT22를 사용하겠다는 선언

// 음성인식 
enum Groups
{
  GROUP_0  = 0,
  GROUP_1  = 1,
};
enum Group0 
{
  G0_LED = 0,
};
enum Group1 
{
  G1_ON = 0,
  G1_OFF = 1,
};

int8_t group, idx;

void sdcard_deselect()
{
	pinMode(SDCARD_CS, OUTPUT);
	digitalWrite(SDCARD_CS, HIGH); //Wifi모듈과 SD Card는 SPI버스를 공유하기 때문에 한 번에 1개만 Active 될 수 있어서 SD Card는 Deselect 하는 게 적합하다. 
}
void init_shield()
{
	sdcard_deselect();
	
	const char* WIFI_SSID = "Smart";
	const char* WIFI_PASS = "tj15ehd92";
	g_shield.begin(WIFI_SSID, WIFI_PASS);

	g_shield.print();
}


/*
IoTMakers
*/
IoTMakers g_im;

const char deviceID[]   = "HwilyrD1471591659898";
const char authnRqtNo[] = "s20ck5wxh";
const char extrSysID[]  = "OPEN_TCP_001PTL001_1000002603";

void init_iotmakers()
{
	Client* client = g_shield.getClient();
	if ( client == NULL )	{
		Serial.println(F("No client from shield."));
		while(1);
	}

	g_im.init(deviceID, authnRqtNo, extrSysID, *client);
	g_im.set_numdata_handler(mycb_numdata_handler);
	g_im.set_strdata_handler(mycb_strdata_handler);
	g_im.set_dataresp_handler(mycb_resp_handler);

	// IoTMakers 서버 연결
	Serial.println(F("connect()..."));
	while ( g_im.connect() < 0 ){
		Serial.println(F("retrying."));
		delay(3000);
	}

	// Auth

	Serial.println(F("auth."));
	while ( g_im.auth_device() < 0 ) {
		Serial.println(F("fail"));
    lcd.clear();
    lcd.setCursor(3,1);
    lcd.print("Connection Fail");
    lcd.setCursor(3,2);
    lcd.print("Press Restart");
		while(1);
	}

	Serial.print(F("FreeRAM="));Serial.println(g_im.getFreeRAM());
}

//#define PIN_LED		9

void setup() 
{
 
  Temp_t = millis(); // TempAbove에 적용하기 위한 시간 측정
  lcd.begin(20,4); // LCD 4X20 을 사용한다는 의미

  
	#if defined (__AVR_ATtiny85__)
  if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif
  
	pixels.begin();
	Serial.begin(115200);
  
  	while ( !Serial )  {
	  ;
	}
 
  dht.begin();//온습도센서 작동시작
  
  pinMode(MOTOR, OUTPUT);
  pinMode(Trig, OUTPUT); //초음파센서 사용
  pinMode(Echo, INPUT); //초음파센서 사용
  pinMode(rainbow, INPUT_PULLUP);
  pinMode(water_button, INPUT);
  pinMode(stage1, OUTPUT);
  
  LCD_Boot();
  init_shield();
	init_iotmakers();




   // 시작하자마자 시간과 관계 없이 처음 한번은 센서값 전송
   send_temperature();
   send_humidity();
   send_soilhumidity();
   send_water(); 
   send_CDS();
   LCD_Display();

    pcSerial.begin(115200);
  int mode = easyvr.bridgeRequested(pcSerial);
  switch (mode)
  {
  case EasyVR::BRIDGE_NONE:
    // setup EasyVR serial port
    port.begin(115200);
    // run normally
    pcSerial.println(F("---"));
    pcSerial.println(F("Bridge not started!"));
    break;
    
  case EasyVR::BRIDGE_NORMAL:
    // setup EasyVR serial port (low speed)
    port.begin(115200);
    // soft-connect the two serial ports (PC and EasyVR)
    easyvr.bridgeLoop(pcSerial);
    // resume normally if aborted
    pcSerial.println(F("---"));
    pcSerial.println(F("Bridge connection aborted!"));
    break;
    
  case EasyVR::BRIDGE_BOOT:
    // setup EasyVR serial port (high speed)
    port.begin(115200);
    // soft-connect the two serial ports (PC and EasyVR)
    easyvr.bridgeLoop(pcSerial);
    // resume normally if aborted
    pcSerial.println(F("---"));
    pcSerial.println(F("Bridge connection aborted!"));
    break;
  }

  while (!easyvr.detect())
  {
    Serial.println("EasyVR not detected!");
    delay(1000);
  }

  easyvr.setPinOutput(EasyVR::IO1, LOW);
  Serial.println("EasyVR detected!");
  easyvr.setTimeout(5);
  easyvr.setLanguage(0);

  group = EasyVR::TRIGGER; //<-- start group (customize)
}

void action();

void loop()
{
	static unsigned long tick = millis();

	// 3초 주기로 센서 정보 송신
	if ( ( millis() - tick) > 60000 )
	{
    LCD_Sending();
		send_temperature();
    send_humidity();
		send_soilhumidity();
		send_water(); // 물의 수위를 보내는 함수
    send_CDS();//조도값을 보내는 함수, 추가한 부분
		
		tick = millis();
    LCD_Display();
   
 	}
   
   
   
  if(digitalRead(rainbow) == 0)
  {
    rainbowCycle(10);
  }
  
  water_time_control();

  if(digitalRead(water_button) == 0) // 이부분 함수로 구현해서 간단하게 하기!!!
  {
    lcd.noBacklight();// 서보모터를 2개 작동시 LCD에 전력이 부족한 문제를 해결하기 위한 방법
    lcd.noDisplay();
    unsigned long T = millis();
    while(millis() - T < (water_time * 1000)){
      digitalWrite(MOTOR, HIGH);
   }
   digitalWrite(MOTOR, LOW);
   lcd.backlight();
   lcd.display();
  }
	// IoTMakers 서버 수신처리 및 keepalive 송신
	voice();
	g_im.loop();
}

void voice()
{
  if (easyvr.getID() < EasyVR::EASYVR3)
    easyvr.setPinOutput(EasyVR::IO1, HIGH); // LED on (listening)

  Serial.print("Say a command in Group ");
  Serial.println(group);
  easyvr.recognizeCommand(group);

  do
  {
    // can do some processing while waiting for a spoken command
  }
  while (!easyvr.hasFinished());
  
  if (easyvr.getID() < EasyVR::EASYVR3)
    easyvr.setPinOutput(EasyVR::IO1, LOW); // LED off

  idx = easyvr.getWord();
  if (idx >= 0)
  {
    // built-in trigger (ROBOT)
    // group = GROUP_X; <-- jump to another group X
    return;
  }
  idx = easyvr.getCommand();
  if (idx >= 0)
  {
    // print debug message
    uint8_t train = 0;
    char name[32];
    Serial.print("Command: ");
    Serial.print(idx);
    if (easyvr.dumpCommand(group, idx, name, train))
    {
      Serial.print(" = ");
      Serial.println(name);
    }
    else
      Serial.println();
  // beep
    easyvr.playSound(0, EasyVR::VOL_FULL);
    // perform some action
    action();
  }
  else // errors or timeout
  {
    if (easyvr.isTimeout())
      Serial.println("Timed out, try again...");
    int16_t err = easyvr.getError();
    if (err >= 0)
    {
      Serial.print("Error ");
      Serial.println(err, HEX);
    }
  }
}


void water_time_control(){
  
  water_time = analogRead(A1);
  water_time = map(water_time, 0, 1023, 1, 11);
  water_time = constrain(water_time, 1, 11);
  if(water_time_ago != water_time && water_cnt == 0){
    lcd.clear();
    lcd.setCursor(1,1);
    lcd.print(" Duration : ");
    lcd.print(water_time);
    lcd.print(" sec");
    delay(1000);
    LCD_Display();
    
  }
  water_time_ago = water_time;
  water_cnt = 0;
}

void LCD_Sending(){
  lcd.clear();
  lcd.setCursor(2,0);
  lcd.print("Sending Data ...");
  lcd.setCursor(7,2);
  lcd.print("Buttons");
  lcd.setCursor(3,3);
  lcd.print("Not responding");
}

void LCD_Boot()
{
  lcd.clear();
  lcd.setCursor(4,1);
  lcd.print("Booting....");
}

void LCD_Display()
{
   lcd.clear();
   lcd.setCursor(0,0); //Start at character 4 on line 0
   lcd.print("Temp:");
   lcd.print(temperature);
   lcd.print(" Hum:");
   lcd.print(humidity);
   lcd.setCursor(0,1);
   lcd.print("Soil: ");
   lcd.print(SoilHumi);
   lcd.setCursor(0,2);
   lcd.print("Water: ");
   lcd.print((int)water);
   lcd.print("%");
   lcd.setCursor(0,3); 
   lcd.print("Status : 13 days");
  
}
int send_water()
{
  long duration;
  digitalWrite(Trig, HIGH);
  delayMicroseconds(10);
  digitalWrite(Trig,LOW);

  duration = pulseIn(Echo, HIGH);
  water = microsecondsToCentimeters(duration);
  water = water + 2;
  
  if(isnan(water))
  {
    Serial.println("Failed to read water depth from HC-SR04 sensor!");
    return;
  }
  Serial.print(F("water_depth : ")); Serial.println(water);
  if ( g_im.send_numdata("water", (double)water) < 0 ) {
      Serial.println(F("fail"));  
      return -1;
  }
  water = (11 - water) / 11 * 100; //LCD 에 나타내기 위한 조치
  return 0; 
}

long microsecondsToCentimeters(long microseconds)
 {
   return microseconds / 29 / 2;
 } 

int send_soilhumidity() // 토양센서의 수분을 측정하여 서버로 보내는 함수
{
  SoilHumi = analogRead(soilSensorPin); // 수분에 따른 저항값을 측정( 0  ~ 1023 )
 
  if(isnan(SoilHumi) )
  {
    Serial.println("Failed to read humidity from DHT sensor!");
    return;
  }
  Serial.print(F("Soil_Humidity : ")); Serial.println(SoilHumi);
  if ( g_im.send_numdata("SoilHumidity", (double)SoilHumi) < 0 ) {
      Serial.println(F("fail"));  
    return -1;
  }
  return 0; 

}
int send_humidity()
{
  if(isnan(humidity))
  {
    Serial.println("Failed to read humidity from DHT sensor!");
    return;
  }
  
  Serial.print(F("Humidity : ")); Serial.println(humidity);
  if ( g_im.send_numdata("humidity", (double)humidity) < 0 ) {
      Serial.println(F("fail"));  
    return -1;
  }
  return 0; 
  
}


int send_temperature()
{
	
	//int tmpVal = analogRead(A0);
	//float voltage = (tmpVal/1024.0) * 5.0;
  
	temperature = dht.readTemperature();
	
  
  if(cnt == 0 || millis() - Temp_t > 60000)
  {
    if(temperature > 15 )
    {
      g_im.send_numdata("tempAbove", (double)1);
      cnt = 1;
      Temp_t = millis();
    }
  }
 
  humidity = dht.readHumidity(); // 습도값 읽는 부분

  
  if(isnan(temperature))
  {  
    Serial.println("Failed to read temperature from DHT sensor!");
    return;
  }

	Serial.print(F("Temperature (c): ")); Serial.println(temperature);
	if ( g_im.send_numdata("temperature", (double)temperature) < 0) {
	    Serial.println(F("fail"));  
		  return -1;
	}
	return 0;   
}

/*
int send_light()
{
	int tmpVal = analogRead(A1);
	int light = tmpVal/4;

	Serial.print(F("Light : ")); Serial.println(light);
	if ( g_im.send_numdata("light", (double)light) < 0 ){
  		Serial.println(F("fail"));  
		return -1;
	}
	return 0;   
}
*/

int CDS() //조도센서값을 측정하기 위한 함수
{
  int illuminance[numReadings] = {0};
  int total = 0;
  int average;
  for(int i = 0; i<numReadings; i++)
  {
    illuminance[i] = analogRead(A3);
    total = total + illuminance[i];
  }
  average = total/numReadings;
  return average;
}

int send_CDS()
{
  int illuminance;
  illuminance = CDS(); // 조도값을 Return 해주는 함수
  Serial.print(F("illuminance : ")); Serial.println(illuminance);
  if ( g_im.send_numdata("illuminance", (double)illuminance) < 0 ){
      Serial.println(F("fail"));  
    return -1;
  }
  return 0;
  
}


void rainbowCycle(uint8_t wait){
   uint16_t i, j;
  for(j=0; j<256*2; j++) { // 5 cycles of all colors on wheel
    for(i=0; i< pixels.numPixels(); i++) {
      pixels.setPixelColor(i, Wheel(((i * 256 / pixels.numPixels()) + j) & 255));
    }
    pixels.show();
    delay(wait);
  }
  for(int i=0;i<NUMPIXELS;i++){
    // pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
    pixels.setPixelColor(i, pixels.Color(255,0,0));
    if(i % 2 == 0)
     {
      pixels.setPixelColor(i, pixels.Color(0,0,255));
     }
    }
    pixels.show();
}


uint32_t Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if(WheelPos < 85) {
    return pixels.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  }
  if(WheelPos < 170) {
    WheelPos -= 85;
    return pixels.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
  WheelPos -= 170;
  return pixels.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
}


void mycb_numdata_handler(char *tagid, double numval)
{
  if(( strcmp(tagid, "T5") == 0) && (int)numval == 1)
	{
    digitalWrite(stage1, HIGH);
	}
  if(( strcmp(tagid, "T5") == 0) && (int)numval == 2)
  {
 
  }
  if(( strcmp(tagid, "T5") == 0) && (int)numval == 3)
  {
   
  }
  if(( strcmp(tagid, "T5") == 0) && (int)numval == 0)
  {
    digitalWrite(stage1, LOW);
   
  }
  
}
void mycb_strdata_handler(char *tagid, char *strval)
{
	// !!! USER CODE HERE
	//Serial.print(tagid);Serial.print(F("="));Serial.println(strval);
 
	if ( strcmp(tagid, "led")==0 && strcmp(strval, "on")==0 )  	
	{
    
    //digitalWrite(LED, HIGH);
    for(int i=0;i<NUMPIXELS;i++){
    // pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
      pixels.setPixelColor(i, pixels.Color(255,0,0));
      if(i % 2 == 0)
      {
       pixels.setPixelColor(i, pixels.Color(0,0,255));
      }

    }
    pixels.show();
	}
  else if ( strcmp(tagid, "led")==0 && strcmp(strval, "message")==0 )
  {
    rainbowCycle(20);
  }
	else if ( strcmp(tagid, "led")==0 && strcmp(strval, "off")==0 )  	
	{
   for(int i=0;i<NUMPIXELS;i++){
    pixels.setPixelColor(i, pixels.Color(0,0,0));
   }
    pixels.show();
	}
  
  else if ( strcmp(tagid, "motor")==0 && strcmp(strval, "on")==0 )   
  {
     unsigned long T = millis();
     lcd.noBacklight();
     lcd.noDisplay();
     while(millis() - T < (water_time * 1000)){
      digitalWrite(MOTOR, HIGH);
     }
     digitalWrite(MOTOR, LOW);
     lcd.backlight();
     lcd.display();
  }
  else if ( strcmp(tagid, "motor")==0 && strcmp(strval, "off")==0 )   
  {
    digitalWrite(MOTOR, LOW);
  }
  else if( strcmp(tagid, "motor")==0 && strcmp(strval, "try")==0)
  {
    
  }
}
void mycb_resp_handler(long long trxid, char *respCode)
{
	if ( strcmp(respCode, "100")==0 )
		Serial.println("resp:OK");
	else
		Serial.println("resp:Not OK");
}

void action()
{
    switch (group)
    {
    case GROUP_0:
      switch (idx)
      {
      case G0_LED:
        // write your action code here
        // group = GROUP_X; <-- or jump to another group X for composite commands
        group = GROUP_1;
        break;
      }
      break;
    case GROUP_1:
      switch (idx)
      {
      case G1_ON:
        // write your action code here
        easyvr.playSound(0, EasyVR::VOL_FULL);
        // group = GROUP_X; <-- or jump to another group X for composite commands
        break;
      case G1_OFF:
        // write your action code here
        easyvr.playSound(0, EasyVR::VOL_FULL);
        // group = GROUP_X; <-- or jump to another group X for composite commands
        break;
      }
      break;
    }
}



