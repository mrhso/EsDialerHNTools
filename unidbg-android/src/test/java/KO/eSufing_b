/**
 * TIP:
 * unidbg-android -> jdk 1.8+
 * unidbg-api -> jdk 1.7
 * unidbg-backend -> jdk 1.7
 * 
 */

package KO;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cndatacom.campus.netcore.Requests.Auth;
import com.cndatacom.campus.netcore.Requests.Keep;
import com.cndatacom.campus.netcore.Requests.Ticket;

import KO.data.Result;
import KO.platform.PlatformAccess;
import KO.platform.android.AndroidAccess;
import KO.utils.Color;
import KO.utils.Constants;
import KO.utils.Tools;

public class eSufing {

	// Single Thread. so we don't need to care about the synchronization seriously (At least for now).

	// temp VALUE for keep action.
	private static Result keepResult;
	// temp VALUE for keep action.

	static Thread WatchDogThread;

	static Thread LoginThread;

	static int LoginThreadStage = 0;

	static long millToCheck = 0;

	private static Consumer<String> log = a -> System.out.println(a);

	static PlatformAccess ACCESS;

	static Consumer<Integer> setState = stage -> {
		millToCheck = System.currentTimeMillis();
		LoginThreadStage = stage;
	};

	private static Thread StateCheckThread;

	private static long keepMills;

	@SuppressWarnings("unused")
	static void executeSingleSession() {

		while (StateCheckThread != null && StateCheckThread.isAlive())
			StateCheckThread.interrupt();

		PlatformAccess info;
		ACCESS = new AndroidAccess();

		try {

			setState.accept(1);

			step1: {
				Constants.setCurrentClientID(UUID.randomUUID());

				log.accept(Constants.getCurrentClientID().toString() + " -> Begin");

				// Get ZSM
				info = ACCESS.loadZSM();
			}

			setState.accept(2);
			String ticketResult = "null";

			step2: {

				log.accept("Session: " + info.getZSMSession() + " ServerIp: " + Constants.getServerlist().get(0));

				// Obtain Ticket
				Result ObtainTicket = Tools.doPost(Constants.Urls.getTicketURL(),
						ACCESS.encrypt(new Ticket().doAction()), info);
				ticketResult = new String(ObtainTicket.result);

				log.accept("TicketRawResult -> " + (ticketResult = info.decrypt(ticketResult)));

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new ByteArrayInputStream(ticketResult.getBytes()));
				NodeList nl = doc.getElementsByTagName("ticket");

				ticketResult = nl.item(0).getFirstChild().getNodeValue();

				log.accept("Ticket -> " + ticketResult + "\n LastError -> " + ObtainTicket.Error_Code);

			}

			setState.accept(3);

			step: {

				// Auth phase
				Result doAuth = Tools.doPost(Constants.Urls.getAuthURL(),
						info.encrypt(new Auth(ticketResult).doAction()), info);
				String authResult = info.decrypt(new String(doAuth.result));

				log.accept("Auth -> " + Pattern.compile("\t|\r|\n").matcher(authResult).replaceAll("")
						+ "\n LastError -> " + doAuth.Error_Code);

				if (!doAuth.Error_Code.equals("SUCCESS"))
					throw new Exception("Failed to do Authentication " + new Auth(ticketResult).doAction());
			}

			setState.accept(4);

			int ticketExpireCount = 0;

			final String ticketResult_final = ticketResult;

			StateCheckThread = getStateCheckThread(info, ticketResult_final);

			// Keep Phase
			do {
				keepResult = null;

				Thread temp = new Thread(() -> {
					try {
						keepResult = Tools.doPost(Constants.Urls.getKeepURL(),
								info.encrypt(new Keep(ticketResult_final).doAction()), info);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				temp.start();
				try {
					temp.join(6 * 1000L);
				} catch (InterruptedException ignored) {
					log.accept("keep -> time out ... breaks");
					try {
						temp.interrupt();
					} catch (Exception c) {
						c.printStackTrace();
					}

					break;
				}

				if (temp.isAlive()) {
					temp.interrupt();
				}

				if (keepResult == null) {
					if (StateCheckThread != null && StateCheckThread.isAlive()) {
						StateCheckThread.interrupt();
					}

					throw new Exception();
				}

				log.accept(Color.ANSI_GREEN + "Keep -> " + info.decrypt(new String(keepResult.result)) + " LastError: "
						+ keepResult.Error_Code);
				log.accept(Color.ANSI_GREEN + "WatchDog Thread: Alive -> " + WatchDogThread.isAlive()
						+ " | Interrupted -> " + WatchDogThread.isInterrupted());
				System.out.println(Color.ANSI_RESET);

				if (!WatchDogThread.isAlive())
					WatchDogThread.start();

				if (!keepResult.Error_Code.equals("SUCCESS")) {
					if (StateCheckThread.isAlive()) {
						StateCheckThread.interrupt();
					}

					throw new Exception("Failed to Keep Online");
				}

				if (StateCheckThread != null && !StateCheckThread.isAlive()) {
					StateCheckThread.start();
				}

				// keep per 30 second (-1s)
				keepMills = 1 * 29 + ThreadLocalRandom.current().nextInt(0, 2);

				try {
					while (--keepMills > 0)
						Thread.sleep(1000L);
				} catch (InterruptedException ignored) {

				}

			} while (ticketExpireCount++ < 3 + Math.random() * 1);

			if (StateCheckThread != null && StateCheckThread.isAlive()) {
				StateCheckThread.interrupt();
			}

		} catch (Throwable c) {
			c.printStackTrace();
		} finally {
			Collections.shuffle(Constants.getServerlist(), ThreadLocalRandom.current());

			try {
				ACCESS.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (StateCheckThread != null && StateCheckThread.isAlive())
				StateCheckThread.interrupt();

			LoginThreadStage = 0;
		}
	}

	private static Thread getStateCheckThread(PlatformAccess info, String ticket) {
		return new Thread(new Runnable() {

			Result doState;
			String CurrentState;

			@Override
			public void run() {
				try {
					while (LoginThread.isAlive() && LoginThreadStage >= 4) {

						if (keepMills <= 5)
							continue;

						Thread temp = new Thread(() -> {
							try {
								doState = Tools.doPost(Constants.Urls.getStateURL(),
										info.encrypt("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<request><ticket>"
												+ ticket + "</ticket></request>"),
										info);

								CurrentState = info.decrypt(new String(doState.result));
							} catch (Exception e) {
								e.printStackTrace();
							}

						});

						temp.start();

						try {
							temp.join(2 * 1000L);
						} catch (InterruptedException ignored) {
							log.accept("keep -> time out ... breaks");
							try {
								temp.interrupt();
							} catch (Exception c) {
								c.printStackTrace();
							}

							continue;
						}

						System.out.print(Color.ANSI_YELLOW + "[KeepAlive] State -> " + CurrentState + " LastError: "
								+ doState.Error_Code);

						if (doState.Error_Code.equals("WAIT_CLIENT_AUTH")
								|| doState.Error_Code.equals("WAIT_SERVER_AUTH")) {
							System.out.println(" do relog");
							LoginThread.interrupt();
							break;
						}

						System.out.println(" fine");

						System.out.print(Color.ANSI_RESET);

						Thread.sleep(500);

						int count = 1;
						System.out.print(String.format("\033[%dA", count)); // Move up
						System.out.print("\033[2K"); // Erase line content

						Thread.sleep(250);

						System.out.println(Color.ANSI_YELLOW + " .......");
						System.out.print(Color.ANSI_RESET);

						Thread.sleep(250);

						System.out.print(String.format("\033[%dA", count)); // Move up
						System.out.print("\033[2K"); // Erase line content

					}

				} catch (Throwable cx) {
					cx.printStackTrace();
				}
			}
		});

	}

}
