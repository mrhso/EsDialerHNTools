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

		Constants.setIPV4(Objects.requireNonNull("0.0.0.0", "IPV4 is null"));
		Constants.setAccount(Objects.requireNonNull("ss", "Account is null"));
		Constants.setPassword(Objects.requireNonNull("ss", "Password is null"));
		Constants.setWlanacip(Objects.requireNonNull("ss", "wlanacip is null"));


			step1: {
	//			Constants.setCurrentClientID(UUID.randomUUID());
				
	//			info = ACCESS.loadZSM();
			}


			String ticketResult = "null";

			step2: {

//				log.accept("Session: " + info.getZSMSession() + " ServerIp: " + Constants.getServerlist().get(0));
//
//				// Obtain Ticket
//				Result ObtainTicket = Tools.doPost(Constants.Urls.getTicketURL(),
//						ACCESS.encrypt(new Ticket().doAction()), info);
//				ticketResult = new String(ObtainTicket.result);
//
//				log.accept(ObtainTicket.Error_Code + " TicketRawResult -> " + (ticketResult = info.decrypt(ticketResult)));
//
//				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//				DocumentBuilder builder = factory.newDocumentBuilder();
//				Document doc = builder.parse(new ByteArrayInputStream(ticketResult.getBytes()));
//				NodeList nl = doc.getElementsByTagName("ticket");
//
//				ticketResult = nl.item(0).getFirstChild().getNodeValue();
//
//				log.accept("Ticket -> " + ticketResult + "\n LastError -> " + ObtainTicket.Error_Code);

			}
			
	}
}
