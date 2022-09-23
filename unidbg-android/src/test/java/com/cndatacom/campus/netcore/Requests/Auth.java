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
				+ clientID + "</client-id><local-time>" + getTime() + "</local-time><ticket>" + Ticket + "</ticket><userid>"
				+ Constants.getAccount() + "</userid><passwd>" + Constants.getPassword()
				+ "</passwd></request>";
	}
}
