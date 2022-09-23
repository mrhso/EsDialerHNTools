package KO.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Constants {

	private static final List<?> serverList = Arrays
			.asList(new String[] { "218.77.121.110" }); //index.cgi?wlanuserip ....

	private static final String UserAgent = "CCTP/Android4_vpn/2020";// CCTP/Android8/2075 android8Huizhou_vpn

	private static String MAC = "";

	private static String IPV4 = ""; // Current IPV4 (wlanuserip)

	private static String GateWayIP = ""; // Optional

	private static String Account = "";

	private static String Password = "";

	public static class Urls {
		
		static String KeepUrl;
		
		public static URL getTicketURL(String ser) throws MalformedURLException {
			return new URL("http://" + ser + "/ticket.cgi?wlanuserip="
					+ Constants.getIPV4() + "&mscgip=218.75.255.6&wlanusermac=" + Pattern.compile(":").matcher(Constants.getMAC()).replaceAll("-"));
		}
		
		public static URL getAuthURL(String ser) throws MalformedURLException {
			return new URL("http://" + ser + "/auth.cgi");
		}
		
		public static URL getStateURL(String ser) throws MalformedURLException {
			return new URL("http://" + ser + "/state.cgi");
		}
		
		public static URL getTermURL(String ser) throws MalformedURLException {
			return new URL("http://" + ser + "/term.cgi");
		}
	}

	/**
	 * @return the MAC
	 */
	public static final String getMAC() {
		return MAC;
	}

	/**
	 * @param MAC the MAC to set
	 */
	public static final void setMAC(String mac) {
		MAC = mac;
	}

	/**
	 * @return the iPV4
	 */
	public static final String getIPV4() {
		return IPV4;
	}

	/**
	 * @param iPV4 the iPV4 to set
	 */
	public static final void setIPV4(String iPV4) {
		IPV4 = iPV4;
	}

	/**
	 * @return the gateWayIP
	 */
	public static final String getGateWayIP() {
		return GateWayIP;
	}

	/**
	 * @param gateWayIP the gateWayIP to set
	 */
	public static final void setGateWayIP(String gateWayIP) {
		GateWayIP = gateWayIP;
	}

	/**
	 * @return the account
	 */
	public static final String getAccount() {
		return Account;
	}

	/**
	 * @param account the account to set
	 */
	public static final void setAccount(String account) {
		Account = account;
	}

	/**
	 * @return the password
	 */
	public static final String getPassword() {
		return Password;
	}

	/**
	 * @param password the password to set
	 */
	public static final void setPassword(String password) {
		Password = password;
	}

	/**
	 * @return the serverlist
	 */
	public static final List<?> getServerlist() {
		return serverList;
	}

	/**
	 * @return the useragent
	 */
	public static final String getUseragent() {
		return UserAgent;
	}
}
