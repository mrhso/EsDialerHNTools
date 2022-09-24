package com.cndatacom.campus.netcore.Requests;

import KO.utils.Constants;

public class Term extends Request {

	String Ticket, clientID;

	public Term(String ticket, String clientID_) {
		Ticket = ticket;
		clientID = clientID_;
	}

	@Override
	public String doAction() {
		return HEAD + "<request><user-agent>" + Constants.getUseragent() + "</user-agent><client-id>"
				+ clientID + "</client-id><local-time>" + getTime() + "</local-time><ticket>" + Ticket + "</ticket><reason>1</reason></request>";
	}
}
