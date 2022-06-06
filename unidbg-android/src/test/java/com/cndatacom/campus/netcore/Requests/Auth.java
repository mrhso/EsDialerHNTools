package com.cndatacom.campus.netcore.Requests;

import KO.utils.Constants;

public class Auth extends Request {

	String Ticket, clientID;

	public Auth(String ticket, String clientID_) {
		Ticket = ticket;
		clientID = clientID_;
	}

	// Optional -> host-name gwip sysinfo ipv6 mac verify(?)
	@Override
	public String doAction() {
		return HEAD + "<request><user-agent>" + Constants.getUseragent() + "</user-agent><client-id>"
				+ clientID + "</client-id><local-time>" + getTime() + "</local-time><ipv4>"
				+ Constants.getIPV4() + "</ipv4><userid>" + Constants.getAccount() + "</userid><passwd>" + Constants.getPassword()
				+ "</passwd><ticket>" + Ticket + "</ticket>" + "<gwip>" + Constants.getGateWayIP() + "</gwip><mac>"
				+ getMacAddrWithUUID(clientID) + "</mac><ipv6></ipv6><host-name>Honor" + clientID.substring(4, 8) + "</host-name><ostag>Linux</ostag>" + "</request>";
	}
}
