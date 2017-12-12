#define F_CPU 16000000UL
#include <avr/io.h>
#include <string.h>
#include "util/delay.h"


void delay(int t)
{
while(t--);
}
 
unsigned char get_data(void)
{
while(!(UCSR0A&0x80));
    return UDR0;
}
 
void send_data(unsigned char data)
{   
    while(!(UCSR0A&0x20));
    UDR0 = data;
}
 
int get_value(void)
{
//    ADMUX = 0x04;  //use AREF, adc channel 4 00000100
//    ADCSRA = 0xe6; //adc enable, start, prescaler 128 11000110
     
ADMUX  = 0b11000010; // 내부 2.56전압, ADC 결과값 오른쪽 정렬, 싱글엔드 입력 ADC2
ADCSRA = 0b11100111; // ADC Enable, free running, 프리스케일 최대

    while(ADCSRA&&(1 << ADIF) == 0); // 변환완료 될 때까지 대기
     
    return ADC; // ADC값 반환
}
 
void main()
{
    int res = 10;
    int i=0;
int ii=0;
int iii=0;
    unsigned char data;
    unsigned char input;
    UCSR0A = 0x00; // ready flag clear
    UCSR0B = 0x18; // rx, tx enable
    UCSR0C = 0x06; // tx data len : 8bit
     
    UBRR0H = 0;
    UBRR0L = 2; // boudrate 115200

    DDRD = 0xFF;
    DDRA = 0x00;
iii=0;  
ii=1;     
i=1;     
while(1)
{
	input = PINA & 0b00000001;
	if (input==0b00000001) {
	data=data>>1;
	} 
	else
	{
	data=data>>1;
	data=data | 0b10000000;
	}
	i++;
	if (i==8) {
	i=1;
	send_data(data);
	}


    _delay_us(5);

    PORTD++;
    
    }
}
