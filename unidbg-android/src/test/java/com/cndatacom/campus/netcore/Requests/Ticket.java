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
				+ clientID + "</client-id><local-time>" + getTime() + "</local-time><ipv4>"
				+ Constants.getIPV4() + "</ipv4><gwip>" + Constants.getGateWayIP() + "</gwip><mac>" + getMacAddrWithUUID(clientID)
				+ "</mac><ipv6>null</ipv6><sysinfo><sysname>Linux</sysname><ifname>wlan0," + Constants.getIPV4()
				+ "</ifname></sysinfo><ostag>Linux</ostag><host-name>Honor" + clientID.substring(4, 8) + "</host-name></request>";
	}

}
