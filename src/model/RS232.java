package model;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;

public class RS232 {
	private SerialPort serialPort;
	private String portName;
	private int baudRate;
	private static InputStream in;
	private OutputStream out;
	
	public static String nacteno;
	
	public RS232(String portName, int baudRate) {
		this.portName = portName;
		this.baudRate = baudRate;
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void connect() throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				//SerialPort serialPort = (SerialPort) commPort;
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				// http://mattiaslife.blogspot.cz/2010/03/rxtx-and-blocking.html
				
				serialPort.disableReceiveTimeout();
				serialPort.disableReceiveThreshold();
								
				/*InputStream*/ in = serialPort.getInputStream();
				/*OutputStream*/ out = serialPort.getOutputStream();
				out.flush();
	
			//	new SerialWriter(out, 'd').start(); // resetne LEDky
			//	new SerialWriter(out, 'a').start(); // rozsviti A
			//	new SerialWriter(out, 'c', 2000).start(); // vsechno vypne	
			//	new SerialReader(in, 5000).start();	// precte zpravu od 'c'
				
				// TODO poresit zavirani portu, neceka na dokonceni predchozich operaci => furt to pada :-(
				//serialPort.close(); // zavre port

			} else {
				System.out
						.println("Error: Only serial ports are handled by this example.");
			}
		}
	}
	
	public static String cti(int timeout) throws InterruptedException {
		SerialReader sr = new SerialReader(in, timeout);
		Thread vlaknoSR = new Thread(sr);
		vlaknoSR.start();
		vlaknoSR.join(); // ceka na ukonceni vlakna
		return nacteno;	
	}
}