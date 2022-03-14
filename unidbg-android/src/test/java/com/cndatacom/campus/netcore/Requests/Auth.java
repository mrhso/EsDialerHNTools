package com.cndatacom.campus.netcore.Requests;

import com.anjuke.mobile.sign.eSufing;

public class Auth extends Request{
	
	final eSufing pa;
	
	public Auth(eSufing par) {
		pa = par;
	}
	
	@Override
	public String doAction() {
		return HEAD + 
		"<request><user-agent>" +
		   pa.UserAgent
		+ "</user-agent><client-id>"+
		   pa.currentClientID.toString()
		   +"</client-id><local-time>"+
		   LocalSimpleDateFormat
		   +"</local-time><ipv4>"+
		   pa.IPV4
		   +"</ipv4></request>";  //ipv6 mac
		
		//<ticket> </ticket>
		//userid(account)
		//passwd
		//verify (If needed)

	}
}
