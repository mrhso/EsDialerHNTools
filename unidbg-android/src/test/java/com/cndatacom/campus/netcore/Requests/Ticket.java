package com.cndatacom.campus.netcore.Requests;

import KO.utils.Constants;

public class Ticket extends Request {

	String clientID;

	public Ticket(String clientID_) {
		clientID = clientID_;
	}
	
	// Optional -> host-name gwip sysinfo ipv6 mac
	@Override
	public String doAction() {
		return HEAD + "<request><user-agent>" + Constants.getUseragent() + "</user-agent><client-id>"
				+ clientID + "</client-id><local-time>" + getTime() + "</local-time><host-name>OPPO R11</host-name><ipv4>"
				+ Constants.getIPV4() + "</ipv4><ipv6></ipv6><mac>" + Constants.getMAC() + "</mac><ostag>OPPO R11</ostag><gwip>" + Constants.getGateWayIP()
				+ "</gwip><sysinfo><sysname>Linux</sysname><machine>armv8l</machine><ifname>wlan0"
				+ "</ifname><gsm.network.type>LTE,Unknown</gsm.network.type><ro.build.version.sdk>27</ro.build.version.sdk></sysinfo></request>";
	}

}
