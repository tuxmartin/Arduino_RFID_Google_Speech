/*
 * Dump block 0 of a MIFARE RFID card using a RFID-RC522 reader
 * Uses MFRC522 - Library to use ARDUINO RFID MODULE KIT 13.56 MHZ WITH TAGS SPI W AND R BY COOQROBOT.
 -----------------------------------------------------------------------------
 * Pin layout should be as follows:
 * Signal     Pin              Pin               Pin
 *            Arduino Uno      Arduino Mega      MFRC522 board
 * ------------------------------------------------------------
 * Reset      9                5                 RST
 * SPI SS     10               53                SDA
 * SPI MOSI   11               52                MOSI
 * SPI MISO   12               51                MISO
 * SPI SCK    13               50                SCK
 *
 * Hardware required:
 * Arduino
 * PCD (Proximity Coupling Device): NXP MFRC522 Contactless Reader IC
 * PICC (Proximity Integrated Circuit Card): A card or tag using the ISO 14443A interface, eg Mifare or NTAG203.
 * The reader can be found on eBay for around 5 dollars. Search for "mf-rc522" on ebay.com.
 */

#include <EEPROM.h>
#include <SPI.h>
#include "MFRC522.h"  // Library from https://github.com/miguelbalboa/rfid

#define SS_PIN 10    //Arduino Uno
#define RST_PIN 9
MFRC522 mfrc522(SS_PIN, RST_PIN);        // Create MFRC522 instance.

#define NUMBER_OF_RECORDS 30


// SETTINGS
#define RELAY_PIN 5
#define RELAY_INTERVAL 5
// SETTINGS

struct zaznam {
  byte id[4];
  char name[8];
};

struct zaznam z[NUMBER_OF_RECORDS] {
  // ukazka rucniho naplneni daty
  //{ { 0x64, 0x24, 0x95, 0x5C }, "CIP"},
  //{ { 0x05, 0x88, 0x72, 0x45 }, "KARTA"},
  //{ { 0x6A, 0x19, 0x1E, 0x95 }, "ISIC"}
  // ukazka rucniho naplneni daty
};

long previousMillis = 0;

boolean relayOn = false;

void saveToEEPROM() {
  EEPROM.put(0, z);
}

void readFromEEPROM() {
  EEPROM.get(0, z);
}

void printDataToUART() {
  for (byte i = 0; i < NUMBER_OF_RECORDS; i++) {
    Serial.print("COUNT=");
    Serial.print(i);
    Serial.print(";");
    Serial.print("ID=");
    for (byte j = 0; j < 4; j++) {
      Serial.print("0x");
      Serial.print(z[i].id[j], HEX);
      if (j < 3) {
        Serial.print(",");
      }
    }
    Serial.print(";NAME=");
    Serial.print(z[i].name);
    Serial.print("\n");
  }
}

void setup() {
  Serial.begin(9600); // Initialize serial communications with the PC

  //saveToEEPROM();
  readFromEEPROM();

  //printDataToUART();

  // ukazka rucniho naplneni daty
  /*
  byte test_id[] = { 0x11, 0x22, 0x33, 0x44 };
  char test_name[] = "_TEST";

  memcpy(z[3].id, test_id, sizeof(test_id) / sizeof(byte));
  memcpy(z[3].name, test_name, sizeof(test_name) / sizeof(char));
  */
  // ukazka rucniho naplneni daty

  pinMode(RELAY_PIN, OUTPUT);
  digitalWrite(RELAY_PIN, HIGH);

  SPI.begin();               // Init SPI bus
  mfrc522.PCD_Init();        // Init MFRC522 card
  mfrc522.PCD_SetAntennaGain(mfrc522.RxGain_max); // set maximum antenna gain
  //Serial.println("Print block 0 of a MIFARE PICC ");
  Serial.println();
}

void loop() {
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis > (RELAY_INTERVAL * 1000)) {
    previousMillis = currentMillis;
    if (relayOn = true) {
      currentMillis = previousMillis; // reset counter
      digitalWrite(RELAY_PIN, HIGH);
      relayOn = false;
    }
  }

  // Look for new cards
  if ( ! mfrc522.PICC_IsNewCardPresent() ) {
    return;
  }

  // Select one of the cards
  if ( ! mfrc522.PICC_ReadCardSerial() ) {
    return;
  }

  // --------- VYHLEDANI V "DB" ---------
  byte i = 0;
  bool nalezeno = false;
  for (i = 0; i < NUMBER_OF_RECORDS; i++) {
    byte ok = 0;
    for (byte j = 0; j < mfrc522.uid.size; j++) { // mfrc522.uid.size = pocet bajtu id = 4
      if (mfrc522.uid.uidByte[j] == z[i].id[j]) {
        ok++;
      }
    }
    if (ok == 4) {
      nalezeno = true;
      break;
    }
  }  
  if (nalezeno) {
    //Serial.println("___ID_FOUND___ACCESS_ACCEPT");
    relayOn = true;
    digitalWrite(RELAY_PIN, LOW);
    //Serial.print("NAME: ");
    //Serial.println(z[i].name);
  } else {
    //Serial.println("___ID_NOT_FOUND___ACCESS_REJECT");
  }
  // --------- VYHLEDANI V "DB" ---------

  Serial.print("Přístup ");
  Serial.print(nalezeno ? "povolen. " : "zamítnut. ");
  if (nalezeno) {    
    Serial.print("Jméno uživatele: ");
    Serial.print(z[i].name);
    Serial.print(". ");    
  }

  Serial.print("ID vaší karty je: ");

  //Serial.print("ID:");    //Dump UID
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
    Serial.print(mfrc522.uid.uidByte[i], HEX);
  }
  Serial.println(".");

  //Serial.print("\nPICC type: ");   // Dump PICC type
  //byte piccType = mfrc522.PICC_GetType(mfrc522.uid.sak);
  //Serial.println(mfrc522.PICC_GetTypeName(piccType));


  mfrc522.PICC_HaltA(); // Halt PICC
  mfrc522.PCD_StopCrypto1();  // Stop encryption on PCD    - platebni (NFC) karty bez tohoto radku spusobi vytuhnuti

  Serial.println();
  delay(50);
}
