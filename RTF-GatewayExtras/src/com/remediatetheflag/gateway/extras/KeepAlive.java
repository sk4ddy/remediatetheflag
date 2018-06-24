package com.remediatetheflag.gateway.extras;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAlive implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(KeepAlive.class);
	private final String url = "http://localhost:8080/guacamole/api/tokens";

	@Override
	public void run() {
		sendPost();
	}
	
	private String sendPost() {
		logger.info("Start of KeepAlive");
		URL obj;
		StringBuffer response = new StringBuffer();
		try {
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setConnectTimeout(5000);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes("{}");
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
			return "";
		}
		logger.info("End of KeepAlive");
		return response.toString();
	}
}