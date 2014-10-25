package model;
import java.io.IOException;
import java.io.InputStream;

public class SerialReader extends Thread {
		InputStream in;
		private int timeout;

		public SerialReader(InputStream in, int timeout) {
			this.in = in;
			this.timeout = timeout;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int len = 0;
			String nacteno = "";

			long start = System.currentTimeMillis();
			
			do {
				try {
					len = this.in.read(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (len > 0) {
					nacteno += new String(buffer, 0, len);
				}
				
				try { // Kdyz vyprsi timeout, vyhodi vyjimku. Stacil by jen break, ale vyjimka vypada dulezite ;-)
					if ((System.currentTimeMillis() - start) > timeout) throw new VyprselCasPriCteniPortu();
				} catch (VyprselCasPriCteniPortu e) {
					e.printStackTrace();
					//System.out.println(e);
					break;
				}

			} while (!nacteno.contains("\r\n")); // dokud nenarazi na konec radku, tak cte
			
			//System.out.println("Nacteno: " + nacteno); // vypise precteny text
			RS232.nacteno = nacteno;
		}
		
		// TODO ta vyjimka je nejaka divna - upravit!
		@SuppressWarnings("serial")
		private class VyprselCasPriCteniPortu extends Exception {
			VyprselCasPriCteniPortu() {
				new String("Vyprsel cas pri cteni ze serioveho portu!");
			}			
		}
		
	}