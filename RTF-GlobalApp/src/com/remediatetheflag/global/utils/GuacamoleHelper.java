/*
 *  
 * REMEDIATE THE FLAG
 * Copyright 2018 - Andrea Scaduto 
 * remediatetheflag@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.remediatetheflag.global.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.remediatetheflag.global.model.GuacTempUser;
import com.remediatetheflag.global.model.RTFECSContainerTask;
import com.remediatetheflag.global.model.RTFGateway;
import com.remediatetheflag.global.model.User;

public class GuacamoleHelper {
	
	public boolean isGuacOnline(RTFGateway gw){
		String cookie;
		try {
			cookie = doLogin(gw.getFqdn(),RTFConfig.getGuacAdminUsername(),RTFConfig.getGuacAdminPassword());
		} catch (Exception e) {
			return false;
		}
		JsonObject cookieObj = (JsonObject) new JsonParser().parse(cookie);
		String adminToken = (String) cookieObj.get("authToken").getAsString();
		return null!=adminToken && !adminToken.equals("");
	}
	
	public Integer getUserExerciseDuration(GuacTempUser guacUser) throws Exception{
		
		String cookie = doLogin(guacUser.getGateway().getFqdn(),RTFConfig.getGuacAdminUsername(),RTFConfig.getGuacAdminPassword());
		JsonObject cookieObj = (JsonObject) new JsonParser().parse(cookie);
		String adminToken = (String) cookieObj.get("authToken").getAsString();
		String encodedCookie = "GUAC_AUTH="+URLEncoder.encode(cookie, "UTF-8");
		long duration = 0;

		String history = sendGet(guacUser.getGateway().getFqdn(), "/rtf/api/session/data/mysql/connections/"+guacUser.getConnectionId()+"/history?token="+adminToken, encodedCookie);
		JsonArray historyObj = new JsonParser().parse(history).getAsJsonArray();
		for (JsonElement h : historyObj) {
		    JsonObject hObj = h.getAsJsonObject();
		    long start = hObj.get("startDate").getAsLong();
		    long end = 0;
		    if(hObj.get("endDate").isJsonNull()){
		    	end = System.currentTimeMillis();
		    }
		    else{
		    	end = hObj.get("endDate").getAsLong();
		    }
		    duration += (end - start);
		}
		return (int) TimeUnit.MILLISECONDS.toMinutes(duration);
	}
	
	public GuacTempUser setupUser(User user, RTFECSContainerTask instance, RTFGateway gw, String password) throws Exception{
		Integer userId = user.getIdUser();
		String instanceHostname = instance.getIpAddress();
		Integer rdpPort = instance.getRdpPort();

		String cookie = doLogin(gw.getFqdn(),RTFConfig.getGuacAdminUsername(),RTFConfig.getGuacAdminPassword());
		JsonObject cookieObj = (JsonObject) new JsonParser().parse(cookie);
		String adminToken = (String) cookieObj.get("authToken").getAsString();

		String jsonContentType = "application/json";
		String encodedCookie = "GUAC_AUTH="+URLEncoder.encode(cookie, "UTF-8");

		String instanceName = userId+"-E-"+System.currentTimeMillis();

		String newInstance = "{\"parentIdentifier\":\"ROOT\",\"name\":\""+instanceName+"\",\"protocol\":\"rdp\",\"parameters\":{\"port\":\""+rdpPort+"\",\"read-only\":\"\",\"swap-red-blue\":\"\",\"cursor\":\"\",\"color-depth\":\"\",\"create-recording-path\":\"\",\"resize-method\":\"reconnect\",\"enable-audio-input\":\"\",\"clipboard-encoding\":\"\",\"dest-port\":\"\",\"enable-sftp\":\"\",\"sftp-port\":\"\",\"enable-audio\":\"\",\"security\":\"any\",\"disable-auth\":\"\",\"ignore-cert\":\"true\",\"server-layout\":\"\",\"console\":\"\",\"width\":\"\",\"height\":\"\",\"dpi\":\"\",\"console-audio\":\"\",\"disable-audio\":\"true\",\"enable-printing\":\"\",\"enable-drive\":\"\",\"create-drive-path\":\"\",\"enable-wallpaper\":\"\",\"enable-theming\":\"\",\"enable-font-smoothing\":\"\",\"enable-full-window-drag\":\"\",\"enable-desktop-composition\":\"\",\"enable-menu-animations\":\"\",\"preconnection-id\":\"\",\"hostname\":\""+instanceHostname+"\",\"username\":\"rtf\",\"password\":\""+password+"\"},\"attributes\":{\"max-connections\":\"5\",\"max-connections-per-user\":\"5\"}}";
		String newUsername = instanceName;
		String createdConnection = sendPost(gw.getFqdn(), "/rtf/api/session/data/mysql/connections?token="+adminToken, newInstance, encodedCookie, jsonContentType);
		JsonObject connectionObj = (JsonObject) new JsonParser().parse(createdConnection);
		String connectionId = (String) connectionObj.get("identifier").getAsString();

		String readWrite = "{\"primaryConnectionIdentifier\":\""+connectionId+"\",\"name\":\""+newUsername+"-RW\",\"parameters\":{\"read-only\":\"\"},\"attributes\":{}}"; 
		String readOnly = "{\"primaryConnectionIdentifier\":\""+connectionId+"\",\"name\":\""+newUsername+"-R\",\"parameters\":{\"read-only\":\"true\"},\"attributes\":{}}";
		
		String sharingReadWrite = sendPost(gw.getFqdn(), "/rtf/api/session/data/mysql/sharingProfiles?token="+adminToken, readWrite, encodedCookie, jsonContentType);
		JsonObject sharingReadWriteObj = (JsonObject) new JsonParser().parse(sharingReadWrite);

		String readWriteConnectionId = (String) sharingReadWriteObj.get("identifier").getAsString();

		String sharingReadOnly = sendPost(gw.getFqdn(), "/rtf/api/session/data/mysql/sharingProfiles?token="+adminToken, readOnly, encodedCookie, jsonContentType);
		JsonObject sharingReadObj = (JsonObject) new JsonParser().parse(sharingReadOnly);

		String readConnectionId = (String) sharingReadObj.get("identifier").getAsString();

		String newUser = "{\"username\":\""+newUsername+"\",\"password\":\""+password+"\",\"attributes\":{\"disabled\":\"\",\"expired\":\"\",\"access-window-start\":\"\",\"access-window-end\":\"\",\"valid-from\":\"\",\"valid-until\":\"\",\"timezone\":null}}";

		sendPost(gw.getFqdn(), "/rtf/api/session/data/mysql/users?token="+adminToken, newUser, encodedCookie, jsonContentType);

		String user2connection = "[{\"op\":\"add\",\"path\":\"/connectionPermissions/"+connectionId+"\",\"value\":\"READ\"},"+
		  "{\"op\":\"add\",\"path\":\"/sharingProfilePermissions/"+readWriteConnectionId+"\",\"value\":\"READ\"},"+
		  "{\"op\":\"add\",\"path\":\"/sharingProfilePermissions/"+readConnectionId+"\",\"value\":\"READ\"}]";
		sendPatch(gw.getFqdn() + "/rtf/api/session/data/mysql/users/"+newUsername+"/permissions?token="+adminToken, user2connection); 
		
		String userCookie = doLogin(gw.getFqdn(),newUsername,password);
		JsonObject userCookieObj = (JsonObject) new JsonParser().parse(userCookie);
		String userToken = (String) userCookieObj.get("authToken").getAsString();

		GuacTempUser guacTemp = new GuacTempUser();
		guacTemp.setGateway(gw);
		guacTemp.setUser(user);
		guacTemp.setUsername(newUsername);	
		guacTemp.setLastValidToken(userToken);
		guacTemp.setPassword(password);	
		guacTemp.setConnectionId(connectionId);
		return guacTemp;
		
	}
	
	public String getFreshToken(RTFGateway gw, String guacTempUsername, String guacTempPpassword){
		try {
			String userCookie = doLogin(gw.getFqdn(),guacTempUsername,guacTempPpassword);
			JsonObject userCookieObj = (JsonObject) new JsonParser().parse(userCookie);
			String userToken = (String) userCookieObj.get("authToken").getAsString();
			return userToken;
		} catch (Exception e) {
			return null;
		}
	}

	private String doLogin(String hostname, String username, String password) throws Exception{
		String payload = "username="+username+"&password="+password;		
		URL obj = new URL("https://"+hostname+"/rtf/api/tokens ");
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setConnectTimeout(4000);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	private String sendPost(String baseHost, String url, String body, String cookie, String contentType) throws Exception {
		URL obj = new URL("https://"+baseHost+url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Cookie", cookie);
		con.setRequestProperty("Content-Type",contentType); 
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
	
	private String sendGet(String baseHost, String url, String cookie) throws Exception {
		URL obj = new URL("https://"+baseHost+url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Cookie", cookie);
		con.setDoOutput(true);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	private String sendPatch(String url, String body) throws Exception, IOException {
		HttpResponse response = null;
		HttpClient httpclient = HttpClients.createDefault();
		HttpPatch httpPatch = new HttpPatch("https://"+url);
		StringEntity params = new StringEntity(body, ContentType.APPLICATION_JSON);
		httpPatch.setEntity(params);
		response = httpclient.execute(httpPatch);
		return response.getStatusLine().getStatusCode()+"";
	}
}
