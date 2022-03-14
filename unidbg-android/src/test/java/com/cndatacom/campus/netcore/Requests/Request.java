package com.cndatacom.campus.netcore.Requests;

import java.text.SimpleDateFormat;

public class Request {
	
	static final String HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

    protected ThreadLocal<SimpleDateFormat> LocalSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() { 
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public String doAction() {
		return null;
    }
}
