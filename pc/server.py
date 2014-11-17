#!/usr/bin/env python

import serial

from subprocess import call

import signal
import sys

def signal_handler(signal, frame): # otestovat!!!!
	print('You pressed Ctrl+C!')
	if ser.isOpen():
		ser.close()
	sys.exit(0)
signal.signal(signal.SIGINT, signal_handler)
#print('Press Ctrl+C')
#signal.pause()

# configure the serial connections (the parameters differs on the device you are connecting to)
ser = serial.Serial(
	#port='/dev/ttyUSB0',
	port='/dev/ttyS0',
	baudrate=9600,
	#writeTimeout = 2,
	parity=serial.PARITY_NONE,
	stopbits=serial.STOPBITS_ONE,
	bytesize=serial.EIGHTBITS
)

try: 
    ser.open()
except Exception, e:
    print "error open serial port: " + str(e)
    exit()


if ser.isOpen():
	try:
		ser.flushInput() #flush input buffer, discarding all its contents
		ser.flushOutput() #flush output buffer, aborting current output 

    #ser.write("ABC")
		#time.sleep(0.5)  #give the serial port sometime to receive the data
	while True:
		nacteno = ser.readline()
		if ( len(nacteno) ) < 5:
			print "Arduino poslalo neplatna data!"
		else:
			userAgent = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
			uvod = "ID+va%C5%A1%C3%AD+karty+je"
			cti = uvod + nacteno
			# "wget -U \"" + userAgent + "\" -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "\" | madplay -"
			prikaz = "wget -U \"" + userAgent + "\" -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "\" | madplay -"
			print prikaz
			call(["/bin/sh", "-c", prikaz])
			
"""
			String[] cmd = {
							"/bin/sh",
							"-c",
							"wget -U \"" + userAgent + "\" -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "\" | madplay -"
							//"wget -U Mozilla -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "\" | madplay -"
							};
"""
    

    except Exception, e1:

    print "error communicating...: " + str(e1)

else:

    print "cannot open serial port "

