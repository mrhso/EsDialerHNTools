package com.cndatacom.campus.netcore.Requests;

import KO.utils.Constants;

public class Auth extends Request {

	String Ticket;

	public Auth(String ticket) {
		Ticket = ticket;
	}

	// Optional -> host-name gwip sysinfo ipv6 mac verify(?)
	@Override
	public String doAction() {
		return HEAD + "<request><user-agent>" + Constants.getUseragent() + "</user-agent><client-id>"
				+ Constants.getCurrentClientID().toString() + "</client-id><local-time>" + getTime() + "</local-time><ipv4>"
				+ Constants.getIPV4() + "</ipv4><userid>" + Constants.getAccount() + "</userid><passwd>" + Constants.getPassword()
				+ "</passwd><ticket>" + Ticket + "</ticket>" + "<gwip>" + Constants.getGateWayIP() + "</gwip><mac>"
				+ getMacAddrWithFormat(":") + "</mac><ipv6></ipv6><host-name>Vivo</host-name>" + "</request>";
	}
}
