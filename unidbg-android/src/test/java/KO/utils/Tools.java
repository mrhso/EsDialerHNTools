package KO.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.cndatacom.campus.netcore.Requests.State;

import KO.data.Result;
import KO.platform.PlatformAccess;

public class Tools {
	
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

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

    public static String randomString(int len){
       StringBuilder sb = new StringBuilder(len);
       for(int i = 0; i < len; i++)
          sb.append(AB.charAt(rnd.nextInt(AB.length())));
       return sb.toString();
    }
    
    //https://stackoverflow.com/questions/3316674/how-to-shuffle-characters-in-a-string-without-using-collections-shuffle
    public static String shuffle(String input){
        List<Character> characters = new ArrayList<Character>();
        for(char c:input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while(characters.size()!=0){
            int randPicker = (int)(Math.random()*characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
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
	public static Result doPost(URL url, String body, PlatformAccess access, String ID) throws Exception {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("POST");
		con.setConnectTimeout(1500);
		con.setReadTimeout(1500);
		con.setRequestProperty("User-Agent", Constants.getUseragent());
		con.setRequestProperty("Algo-ID", access == null ? "00000000-0000-0000-0000-000000000000" : access.getAlgoID());
		con.setRequestProperty("Client-ID", ID);
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

	public static String resolveXml(String in, String target)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(in.getBytes()));
		NodeList nl = doc.getElementsByTagName(target);

		return nl.item(0).getFirstChild().getNodeValue();
	}

	public static <T extends Number>String timeStamp2Date(T seconds) {
		if (seconds == (Number)0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(Long.valueOf(seconds + "")));
	}

}
