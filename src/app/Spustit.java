package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import model.RS232;

public class Spustit {
	private static RS232 port232;
	
	public static final String rs232PortName;
	public static final int rs232BaudRate;
	public static final int rs232Timeout;
	public static final int rs232FrekvenceCteni;	
	
	static {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("nastaveni.ini"));						
		} catch (FileNotFoundException e) {
			System.out.println("Soubor s nastavenim nenalezen!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
		
		rs232PortName = p.getProperty("rs232PortName");
		rs232BaudRate = Integer.parseInt(p.getProperty("rs232BaudRate"));
		rs232Timeout = Integer.parseInt(p.getProperty("rs232Timeout"));
		rs232FrekvenceCteni = Integer.parseInt(p.getProperty("rs232FrekvenceCteni"));							
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		File lock = new File("/var/lock/LCK..ttyUSB0"); // FIXME: to je mega prasarna...
		lock.delete();
		
		port232 = new RS232(rs232PortName, rs232BaudRate);
		
		System.out.println("Prilozte cip...............");
				
		class SeriovyPort implements Runnable {	

			@SuppressWarnings("static-access")
			@Override
			public void run() {
				while (true) {
					String nacteno = "";
					Date date = new Date();
					try {
						nacteno = port232.cti(rs232Timeout * 1000);
						nacteno = nacteno.trim(); // odstrani konec radku
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
					
					String userAgent = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
					
					String uvod = "ID+va%C5%A1%C3%AD+karty+je";					
					String cti = uvod + nacteno;
					System.out.println("CTU: " + cti);
					
					System.out.println("wget -U \"" + userAgent + "\" -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "\" | madplay -");
					String[] cmd = {
							"/bin/sh",
							"-c",
							"wget -U \"" + userAgent + "\" -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "\" | madplay -"
							//"wget -U Mozilla -O - \"http://translate.google.com/translate_tts?ie=UTF-8&tl=cs&q=" + cti + "\" | madplay -"
							};
							try {
								Process abc = Runtime.getRuntime().exec(cmd);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
					
					
					try {
						Thread.sleep(rs232FrekvenceCteni*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				

			}
			
		}
		
		SeriovyPort sp = new SeriovyPort();
		Thread vlaknoSP = new Thread(sp);
		vlaknoSP.start();
		vlaknoSP.join(); // ceka na ukonceni vlakna

		
	}

}
