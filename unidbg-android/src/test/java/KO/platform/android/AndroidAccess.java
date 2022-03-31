package KO.platform.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import com.github.unidbg.linux.android.dvm.DvmObject;

import KO.data.Result;
import KO.platform.PlatformAccess;
import KO.utils.Constants;
import KO.utils.Tools;

@SuppressWarnings("unused")
public class AndroidAccess implements PlatformAccess{
	
	AndroidMock mock;
	
	private long ZSMSession;
	private String AlgoID;

	public AndroidAccess() {
		mock = new AndroidMock();
	}

	/**
	 * 
	 * @param in
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String in) throws Exception {
		DvmObject<?> r2 = mock.getAPI().callStaticJniMethodObject(mock.getEmulator(), "dec(J[B)[B", ZSMSession,
				in.getBytes("UTF-8"));
		return new String((byte[]) r2.getValue());
	}

	public void destroy() throws IOException {
		mock.getEmulator().close();

		mock.clear();
	}



	/**
	 * 
	 * @param in   String to encrypt
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String in) throws Exception {
		DvmObject<?> r = mock.getAPI().callStaticJniMethodObject(mock.getEmulator(), "enc(J[B)[B", ZSMSession,
				in.getBytes("UTF-8"));
		return new String((byte[]) r.getValue());
	}

	/**
	 * 
	 * @param info
	 * @throws Exception
	 */
	private void free() throws Exception {
		mock.getAPI().callStaticJniMethodObject(mock.getEmulator(), "free(J)V", ZSMSession);
	}

	/**
	 * @return the algoID
	 */
	public final String getAlgoID() {
		return AlgoID;
	}

	/**
	 * @return the zSMSession
	 */
	public final long getZSMSession() {
		return ZSMSession;
	}

	public long load(byte[] zsmBytes) throws Exception {
		return mock.getAPI().callStaticJniMethodLong(mock.getEmulator(), "load([B)J", zsmBytes);
	}

	/**
	 * 
	 * Get initial DaMod
	 * 
	 * @throws Exception
	 * 
	 */
	public AndroidAccess loadZSM() throws Exception {
		try {

			String Body = "tarce log algo auto compstr null value";

			URL url = new URL("http://" + Constants.getServerlist().get(0) + "/ticket.cgi");

			Result result = Tools.doPost(url, Body, null);
			
			byte[] zsmBytes = result.result;

			final BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(zsmBytes));
			byte[] bArr = new byte[3]; // 001
			bufferedInputStream.read(bArr, 0, 3);
			String KeyID = new String(bArr);

			byte[] bArr2 = new byte[1]; // length
			bufferedInputStream.read(bArr2);

			int i2 = bArr2[0] & 0xff;
			byte[] bArr3 = new byte[i2];
			bufferedInputStream.read(bArr3, 0, i2);

			bufferedInputStream.read(bArr2);
			int i3 = bArr2[0] & 0xff;
			byte[] bArr4 = new byte[i3];
			bufferedInputStream.read(bArr4, 0, i3);
			AlgoID = new String(bArr4);

			bufferedInputStream.close();

			if (zsmBytes != null && zsmBytes.length > 10) {
				setZSMSession(load(zsmBytes));
				
				if(getZSMSession() > 0)
					return this;
			}

			

		} catch (Throwable c) {
			c.printStackTrace();
		}

		throw new Exception();
	}

	/**
	 * @param algoID the algoID to set
	 */
	protected final void setAlgoID(String algoID) {
		AlgoID = algoID;
	}
	


	/**
	 * @param zSMSession the zSMSession to set
	 */
	protected final void setZSMSession(long zSMSession) {
		ZSMSession = zSMSession;
	}
}