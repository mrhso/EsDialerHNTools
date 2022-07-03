package KO;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cndatacom.campus.netcore.Requests.Ticket;

import KO.data.Result;
import KO.platform.PlatformAccess;
import KO.platform.android.AndroidAccess;
import KO.utils.Constants;
import KO.utils.Tools;

public class debug {
	
	private static Consumer<String> log = a -> System.out.println(a);


	public static void main(String[] s) throws Exception {

		PlatformAccess info;
		AndroidAccess ACCESS = new AndroidAccess();
			
	}
}
