package KO.platform;

import java.io.IOException;

import KO.platform.android.AndroidAccess;

public interface PlatformAccess {

	AndroidAccess loadZSM() throws Exception;

	String decrypt(String in) throws Exception;

	String encrypt(String in) throws Exception;

	void destroy() throws IOException;

	long getZSMSession();

	String getAlgoID();
}
