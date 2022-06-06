package KO.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Constants {

	private static final List<?> serverList = Arrays
			.asList(new String[] { "14.146.227.141:7001", "121.8.177.212:7001" }); //index.cgi?wlanuserip ....

	private static final String UserAgent = "CCTP/Android8/2075";// CCTP/Android8/2075 android8Huizhou_vpn

	private static String Wlanacip = "";

	private static String IPV4 = ""; // Current IPV4 (wlanuserip)

	private static String GateWayIP = ""; // Optional

	private static String Account = "";

	private static String Password = "";

	public static class Urls {
		
		static String KeepUrl;
		
		public static URL getTicketURL(String ser) throws MalformedURLException {
			return new URL("http://" + ser + "/ticket.cgi?wlanacip="
					+ Constants.getWlanacip() + "&wlanuserip=" + Constants.getIPV4());
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
	 * @return the wlanacip
	 */
	public static final String getWlanacip() {
		return Wlanacip;
	}

	/**
	 * @param wlanacip the wlanacip to set
	 */
	public static final void setWlanacip(String wlanacip) {
		Wlanacip = wlanacip;
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
