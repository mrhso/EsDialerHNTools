package KO;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.cndatacom.campus.netcore.Requests.Auth;
import com.cndatacom.campus.netcore.Requests.Keep;
import com.cndatacom.campus.netcore.Requests.Ticket;

import KO.data.Result;
import KO.platform.PlatformAccess;
import KO.platform.android.AndroidAccess;
import KO.utils.Color;
import KO.utils.Constants;
import KO.utils.Tools;

public class WorkThread extends Thread {

	private UUID currentClientID = UUID.randomUUID();

	public WorkThread(String Ser) {
		Server = Ser;
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				destroy();
		}));
	}

	public String Server;

	private static Consumer<String> log = a -> System.out.println("[" + Color.ANSI_CYAN + Thread.currentThread().getName() + Color.ANSI_RESET + "] " + a);

	private static String KeepUrl;

	PlatformAccess ACCESS;

	public state State = new state();

	private String ticket;

	private String expire;

	/**
	 * @return the expire
	 */
	public final String getExpire() {
		return expire;
	}

	/**
	 * @param expire the expire to set
	 */
	public final void setExpire(String expire) {
		this.expire = expire;
	}

	private String keep_retry;

	private int keepInterval;

	private int keepChances;
	
	public long nextCheckMS;

	public class state {
		public int LoginThreadStage = 0;

		public long millToCheck = 0;

		Consumer<Integer> setState = stage -> {
			millToCheck = System.currentTimeMillis();
			LoginThreadStage = stage;
		};
	}

	public void init() throws Exception {

		State.setState.accept(1);

		ACCESS = new AndroidAccess();

		setCurrentClientID(UUID.randomUUID());

		log.accept(getCurrentClientID().toString() + " -> Begin ....");

		// Get ZSM
		ACCESS = ACCESS.loadZSM(Server, getCurrentClientID().toString());

		log.accept("Session -> " + ACCESS.getZSMSession() + " Server -> " + Server);
		
		Thread.currentThread().setName(Server);
	}

	public String getTicket() throws Exception {
		State.setState.accept(2);

		Result ObtainTicket = Tools.doPost(Constants.Urls.getTicketURL(Server), ACCESS.encrypt(new Ticket(getCurrentClientID().toString()).doAction()),
				ACCESS, getCurrentClientID().toString());
		String ticketResult = new String(ObtainTicket.result);

		log.accept(
				"TicketRawResult -> " + (ticketResult = ACCESS.decrypt(ticketResult)) + " \n LastError -> " + ObtainTicket.Error_Code);

		ticket = Tools.resolveXml(ticketResult, "ticket");
		expire = Tools.resolveXml(ticketResult, "expire");

		log.accept("Ticket -> " + ticket  + " expire -> " + expire + "\n LastError -> " + ObtainTicket.Error_Code);

		return ticket;
	}

	public String doAuth() throws Exception {
		State.setState.accept(3);

		String enc = ACCESS.encrypt(new Auth(ticket, getCurrentClientID().toString()).doAction());

		Result doAuth = Tools.doPost(Constants.Urls.getAuthURL(Server), enc, ACCESS, getCurrentClientID().toString());

		String authResult = ACCESS.decrypt(new String(doAuth.result));

		setKeepURL(Tools.resolveXml(authResult, "keep-url"));

		keep_retry = Tools.resolveXml(authResult, "keep-retry");

		log.accept("[" + Server + "] " + "Auth -> " + Pattern.compile("\t|\r|\n").matcher(authResult).replaceAll("") + "\n LastError -> "
				+ doAuth.Error_Code);

		if (!doAuth.Error_Code.equals("SUCCESS"))
			throw new Exception("Failed to do Authentication " + new Auth(ticket, getCurrentClientID().toString()).doAction());

		return authResult;
	}

	public void Keep() throws Exception {
		State.setState.accept(4);

		keepChances = 3;//Integer.parseInt(keep_retry);
		keepInterval = 1;

		do {
			
			checkAlive(10);
			
			log.accept(new Keep(ticket, getCurrentClientID().toString()).doAction());
			
			Result keepResult = Tools.doPost(getKeepURL(), ACCESS.encrypt(new Keep(ticket, getCurrentClientID().toString()).doAction()), ACCESS, getCurrentClientID().toString());

			if (keepResult == null || keepResult.Error_Code == null)
				if (--keepChances > 0) {
					log.accept("keep -> null ... try again + chance " + keepChances);
					continue;
				}
			String KeepDecrypted = ACCESS.decrypt(new String(keepResult.result));

			log.accept(Color.ANSI_GREEN + "Url -> " + getKeepURL() + " Keep -> " + KeepDecrypted + "\n LastError: "
					+ keepResult.Error_Code);
			if (!keepResult.Error_Code.equals("SUCCESS")) {

				if (--keepChances > 0) {

					log.accept("keep -> Not Success ... try again + chance " + keepChances);
					continue;
				}
			}
			
			int serverInter = Integer.parseInt(Tools.resolveXml(KeepDecrypted, "interval"));

			keepInterval = 41; // ( seconds ) if you got banned [AUTH DENIED] -> set it bigger value and wait for 30 minutes  serverInter - 5 - (int) (14 * Math.random());

			log.accept("Server interval: " + (serverInter / 60) + " minutes | real : " + keepInterval
					+ "s | ticketExpireTime : " + expire + " | chances : " + keepChances);
			
			checkAlive(keepInterval + 10);

			Thread.sleep(keepInterval * 1000);
		} while (true);

	}

	private void checkAlive(int seconds) {
		nextCheckMS = System.currentTimeMillis() + seconds * 1000; // wait for 10 seconds .
		log.accept("[checkAlive] Will be checked at " + Tools.timeStamp2Date(nextCheckMS));
	}

	public static URL getKeepURL() throws MalformedURLException {
		return new URL(KeepUrl);
	}

	public static void setKeepURL(String ur) throws MalformedURLException {
		KeepUrl = ur;
	}
	

	/**
	 * @return the currentClientID
	 */
	public final UUID getCurrentClientID() {
		return currentClientID;
	}

	/**
	 * @param currentClientID the currentClientID to set
	 */
	public final void setCurrentClientID(UUID currentClientid) {
		currentClientID = currentClientid;
	}

	@Override
	public void run() {
		try {
			init();
			getTicket();
			doAuth();
			Keep();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		if (ACCESS != null) {
			try {

				ACCESS.destroy();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}
}
