package test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpTmepTest {

	public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException {
				
		new URL("http://localhost/martin/TMEP-6.1/app/index.php?tempV=+21.2&humV=50.1").openStream();
		/*
		 * Nefunguje ulozeni v TEMPu. TMEP vidi program jako mobilni telefon a nedovoli ulozeni teploty.
		 * Log z Apache webserveru:
		 * $ tail -n 1 /var/log/apache2/access.log
		 * 127.0.0.1 - - [24/Feb/2013:12:23:04 +0100] "GET /martin/TMEP-6.1/app/index.php?tempV=+21.2&humV=50.1 HTTP/1.1" 200 247 "-" "Java/1.7.0_13"
		 * User-Agent je tedy "Java/1.7.0_13"
		 */
		
		URLConnection con = new URL("http://localhost/martin/TMEP-6.1/app/index.php?tempV=+17.89&humV=41.53").openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.70 Safari/537.17");
        InputStream response = con.getInputStream();
        /*
         * Funguje - posila se user-agent desktopoveho Google Chrome.
         */
        
	}
}