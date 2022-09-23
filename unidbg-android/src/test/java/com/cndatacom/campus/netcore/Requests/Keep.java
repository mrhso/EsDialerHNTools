package com.cndatacom.campus.netcore.Requests;

import KO.utils.Constants;

public class Keep extends Request {

	String Ticket, clientID;

	public Keep(String ticket, String clientID_) {
		Ticket = ticket;
		clientID = clientID_;
	}

	// Optional -> host-name gwip sysinfo ipv6 mac
	@Override
	public String doAction() {
		return HEAD + "<request><user-agent>" + Constants.getUseragent() + "</user-agent><client-id>"
				+ clientID + "</client-id><local-time>" + getTime() + "</local-time><excheck-args><hello>17615</hello><conn>26365</conn></excheck-args><ticket>"
				+ Ticket + "</ticket><ipv4>" + Constants.getIPV4() + "</ipv4><ipv6></ipv6><mac>" + Constants.getMAC()
				+ "</mac><sysinfo><sysname>Linux</sysname><machine>armv8l</machine><ifname>wlan0"
				+ "</ifname><gsm.network.type>LTE,Unknown</gsm.network.type><ro.build.version.sdk>27</ro.build.version.sdk></sysinfo></request>";
	}
}
