#!/usr/bin/env python

import serial

from subprocess import call

import signal
import sys
import urllib

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
	port='/dev/ttyUSB0',
	baudrate=9600,
	#writeTimeout = 2,
	parity=serial.PARITY_NONE,
	stopbits=serial.STOPBITS_ONE,
	bytesize=serial.EIGHTBITS
)

try: # pokud uz byl port ortevreny, zkusim ho zavrit
	if ser.isOpen():
		ser.close()
except Exception, e:
    print "error open serial port: " + str(e)
    exit()

try: 
    ser.open()
except Exception, e:
    print "error open serial port: " + str(e)
    exit()
    
minulyText = ""
stejny = 0    

if ser.isOpen():
	try:
		ser.flushInput() #flush input buffer, discarding all its contents
		ser.flushOutput() #flush output buffer, aborting current output 

		#ser.write("ABC")
			#time.sleep(0.5)  #give the serial port sometime to receive the data
		while True:
			nacteno = ser.readline()
			ser.flushInput() #flush input buffer, discarding all its contents
			if ( len(nacteno) ) < 5:
				print "Arduino poslalo neplatna data!"
			else:	
				if stejny < 2:					
					minulyText = nacteno
					cti = urllib.quote_plus(nacteno)
					userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36";
					# "wget -U \"" + userAgent + "\" -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "\" | madplay -"
					prikaz = "wget -q -U \"" + userAgent + "\" -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "&total=1&idx=0&client=t\" | madplay -q -"
					print prikaz
					call(["/bin/sh", "-c", prikaz])
					
				if (minulyText == nacteno):
					stejny = stejny + 1
				else:
					stejny = 0
					minulyText = ""
					
	except Exception, e1:
		print "error communicating...: " + str(e1)

else:
	print "cannot open serial port "

