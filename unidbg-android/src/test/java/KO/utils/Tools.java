package KO.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cndatacom.campus.netcore.Requests.State;

import KO.data.Result;
import KO.platform.PlatformAccess;

public class Tools {


	/**
	 * 
	 * @param b
	 * @return
	 * 
	 *         (From CSDN)
	 */
	public static String byte2hex(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp = "";
		for (byte value : b) {
			// 为了保证二进制机器数不变，这里需要& 0XFF
			stmp = (Integer.toHexString(value & 0XFF));
			// 如果只有一位，需要在前面补上0凑足两位
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString();
	}


	/**
	 * 
	 * @param in
	 * @return
	 */
	public static String checkSum(String in) {
		byte[] digest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			digest = md5.digest(in.getBytes("utf-8"));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return byte2hex(digest);
	}
	
	
	/**
	 * 
	 * @param url
	 * @param body
	 * @param AlgoID
	 * @return
	 * @throws Exception
	 */
	public static Result doPost(URL url, String body, PlatformAccess access) throws Exception {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", Constants.getUseragent());
		con.setRequestProperty("Algo-ID", access == null ? "00000000-0000-0000-0000-000000000000" : access.getAlgoID());
		con.setRequestProperty("Client-ID",Constants.getCurrentClientID().toString());
		con.setRequestProperty("CDC-Checksum", Tools.checkSum(body));
//		con.setRequestProperty("CDC-SchoolId", "_");
//		con.setRequestProperty("CDC-Domain", "_");
//		con.setRequestProperty("CDC-Area", "_");
//		con.setRequestProperty("Host", ""+Constants.getServerlist()[0]+"");

		con.setDoOutput(true);
		con.setDoInput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();

		String LastError = State.check(con.getHeaderField("Error-Code"));

		if (access != null || con.getHeaderField("Content-Length") == null) {

			String result = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			if (in != null) {
				in.close();
			}

			return new Result(LastError, result.getBytes());
		} else {

			int len = Integer.parseInt(con.getHeaderField("Content-Length"));
			byte[] result = new byte[len];

			InputStream ip = con.getInputStream();

			while (len > 0) {
				int read = ip.read(result, result.length - len, len);

				if (read < 0)
					break;

				len -= read;

			}

			if (ip != null)
				ip.close();

			return new Result(LastError, result);

		}
	}
	
}
