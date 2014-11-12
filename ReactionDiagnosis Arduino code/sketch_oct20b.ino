const int buttonPin = 2;
const int ledPin = 13;

int prevState = LOW;
int buttonState = LOW;
int ledState = LOW;

void setup() {
  Serial.begin(9600);
  pinMode(ledPin, OUTPUT);
  pinMode(buttonPin, INPUT);
}

void loop() {
  buttonState = digitalRead(buttonPin);
  
  if (buttonState == HIGH && prevState == LOW) {
    if (ledState == HIGH) {
      digitalWrite(ledPin, LOW);
      ledState = LOW;
    } else {
      digitalWrite(ledPin, HIGH);
      ledState = HIGH;
    }
    
    Serial.println("1");
  }
  prevState = buttonState;
}
