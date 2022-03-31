package com.cndatacom.campus.netcore.Requests;

import KO.utils.Constants;

public class Keep extends Request {

	String Ticket;

	public Keep(String ticket) {
		Ticket = ticket;
	}

	// Optional -> host-name gwip sysinfo ipv6 mac
	@Override
	public String doAction() {
		return HEAD + "<request><user-agent>" + Constants.getUseragent() + "</user-agent><client-id>"
				+ Constants.getCurrentClientID().toString() + "</client-id><local-time>" + getTime() + "</local-time><ipv4>"
				+ Constants.getIPV4() + "</ipv4><ticket>" + Ticket + "</ticket>" + "<gwip>" + Constants.getGateWayIP() + "</gwip><mac>"
				+ getMacAddrWithFormat(":") + "</mac><ipv6></ipv6><host-name>Vivo</host-name>" + "</request>"; 

	}
}
