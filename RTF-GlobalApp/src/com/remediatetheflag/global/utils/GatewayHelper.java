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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.remediatetheflag.global.model.ExerciseInstance;
import com.remediatetheflag.global.model.ExerciseInstanceLaunchType;
import com.remediatetheflag.global.model.ExerciseResult;
import com.remediatetheflag.global.model.ExerciseResultFile;
import com.remediatetheflag.global.model.ExerciseResultStatus;
import com.remediatetheflag.global.model.ExerciseSelfCheckResult;
import com.remediatetheflag.global.model.Flag;
import com.remediatetheflag.global.model.FlagQuestion;
import com.remediatetheflag.global.model.SelfCheckResult;

public class GatewayHelper {

	private static Logger logger = LoggerFactory.getLogger(GatewayHelper.class);

	public ExerciseResultFile getResultFile(ExerciseInstance instance){
		String ip = "";
		Integer httpPort = -1;
		if(null==instance || null==instance.getLaunchType()) {
			return null;
		}
		if(instance.getLaunchType().equals(ExerciseInstanceLaunchType.ECS)) {
			ip = instance.getEcsInstance().getIpAddress();
			httpPort = instance.getEcsInstance().getHttpPort();
		}
		String proxyAction = "{\"action\":\"getResultArchive\",\"ip\":\""+ip+":"+httpPort+"\"}";

		logger.debug("Sending getResultFile request to "+ip+":"+httpPort);

		byte[] resultFile = sendFilePost(instance.getGuac().getGateway().getFqdn(),proxyAction);

		ExerciseResultFile file = new ExerciseResultFile();

		if(null==resultFile || resultFile.length==0) {
			logger.warn("ExerciseResultFile from exerciseInstance "+instance.getIdExerciseInstance()+" is null");
			return null;
		}
		logger.debug("Returning result file for "+ip+":"+httpPort+" length:"+resultFile.length);

		file.setFile(resultFile);
		if(instance.getLaunchType().equals(ExerciseInstanceLaunchType.ECS))
			file.setFilename(instance.getEcsInstance().getName());

		return file;	
	}

	public ExerciseSelfCheckResult getSelfcheckResult(ExerciseInstance instance){
		String ip = "";
		Integer httpPort = -1;
		ExerciseSelfCheckResult selfcheck = new ExerciseSelfCheckResult();

		if(null==instance || null==instance.getLaunchType()) {
			return selfcheck;
		}
		if(instance.getLaunchType().equals(ExerciseInstanceLaunchType.ECS)) {
			ip = instance.getEcsInstance().getIpAddress();
			httpPort = instance.getEcsInstance().getHttpPort();
		}
		String proxyAction = "{\"action\":\"getResultStatus\",\"ip\":\""+ip+":"+httpPort+"\"}";

		logger.debug("Sending getSelfcheckResult request to "+ip+":"+httpPort);

		List<SelfCheckResult> sList = new LinkedList<SelfCheckResult>();
		String jsonResponse = sendPost(instance.getGuac().getGateway().getFqdn(),proxyAction);

		if(null==jsonResponse || jsonResponse.equals("")){
			logger.warn("Received null results response from exerciseInstance "+instance.getIdExerciseInstance());
			return selfcheck;
		}
		JsonParser parser = new JsonParser();
		try {
			JsonObject obj = parser.parse(jsonResponse).getAsJsonObject();
			if(obj.get("results") == null || (obj.get("result") != null && obj.get("result").getAsString().equals("error"))) {
				return selfcheck;
			}
			
			Set<Map.Entry<String, JsonElement>> entries = obj.get("results").getAsJsonObject().entrySet();
			for (Map.Entry<String, JsonElement> entry: entries) {
				SelfCheckResult r = new SelfCheckResult();
				r.setName(entry.getKey());
				r.setIsVulnerable(entry.getValue().getAsBoolean());
				sList.add(r);
			}		
			selfcheck.setFlagList(sList);
			logger.debug("Returning result list for "+ip+":"+httpPort+" length:"+sList.size());
			return selfcheck;
		}catch(Exception e) {
			logger.warn("Received non-JSON results response from exerciseInstance "+instance.getIdExerciseInstance());
			return selfcheck;
		}
	}



	public List<ExerciseResult> getResultStatus(ExerciseInstance instance) {
		String ip = "";
		Integer httpPort = -1;
		List<ExerciseResult> sList = new LinkedList<ExerciseResult>();

		if(null==instance || null==instance.getLaunchType()) {
			return sList;
		}
		if(instance.getLaunchType().equals(ExerciseInstanceLaunchType.ECS)) {
			ip = instance.getEcsInstance().getIpAddress();
			httpPort = instance.getEcsInstance().getHttpPort();
		}
		String proxyAction = "{\"action\":\"getResultStatus\",\"ip\":\""+ip+":"+httpPort+"\"}";

		logger.debug("Sending getResultStatus request to "+ip+":"+httpPort);

		String jsonResponse = sendPost(instance.getGuac().getGateway().getFqdn(),proxyAction);

		if(null==jsonResponse || jsonResponse.equals("")){
			logger.warn("Received null results response from exerciseInstance "+instance.getIdExerciseInstance());
			return sList;
		}
		JsonParser parser = new JsonParser();
		try {
			JsonObject obj = parser.parse(jsonResponse).getAsJsonObject();
			if(obj.get("results") == null || (obj.get("result") != null && obj.get("result").getAsString().equals("error"))) {
				return sList;
			}
			
			Set<Map.Entry<String, JsonElement>> entries = obj.get("results").getAsJsonObject().entrySet();
			//complexity is O(n3), but we're looking at max 2-3 flags/questions per exercise
			for (Map.Entry<String, JsonElement> entry: entries) {
				for(Flag flag : instance.getAvailableExercise().getFlags()){
					for(FlagQuestion fq : flag.getFlagQuestionList()){
						if(null!=fq.getSelfCheckName() && fq.getSelfCheckName().equals(entry.getKey())){
							ExerciseResult r = new ExerciseResult();
							r.setName(entry.getKey());
							r.setCategory(flag.getCategory());
							r.setVerified(false);
							Boolean b = entry.getValue().getAsBoolean();
							if(b.equals(true)){
								r.setStatus(ExerciseResultStatus.VULNERABLE);
							}
							else{
								r.setStatus(ExerciseResultStatus.NOT_VULNERABLE);
							}
							sList.add(r);
						}
					}
				}
			}	
			logger.debug("Returning result list for "+ip+":"+httpPort+" length:"+sList.size());
			return sList;
		}catch(Exception e) {
			logger.warn("Received non-JSON results response from exerciseInstance "+instance.getIdExerciseInstance());
			return sList;
		}
	}

	private String getBasicAuthString() {
		//server-to-server interaction with the Gateway Agent
		//too expensive at this stage to setup Mutual TLS
		//password is configured from the cloudformation template
		String credsString = RTFConfig.getAgentUser()+":"+RTFConfig.getAgentPassword();
		String credentials = Base64.getEncoder().encodeToString(credsString.getBytes());
		return "Basic "+credentials;
	}

	private String sendPost(String gateway, String body) {
		URL obj;
		StringBuffer response = new StringBuffer();
		try {
			obj = new URL("https://"+gateway+"/helper/handler");
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type","application/json"); 
			con.setRequestProperty("Authorization", getBasicAuthString());
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.error("Failed sending POST request to gateway: "+gateway+" for body:\n"+body+"\n due to: "+e.getMessage());
			return null;
		}
		return response.toString();
	}
	private byte[] sendFilePost(String gateway, String body) {
		URL obj;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			obj = new URL("https://"+gateway+"/helper/handler");
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");			
			con.setRequestProperty("Content-Type","application/json"); 
			con.setRequestProperty("Authorization", getBasicAuthString());
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
			InputStream is = con.getInputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			return buffer.toByteArray();
		} catch (Exception e) {
			logger.error("Failed sending File POST request to gateway: "+gateway+" for body:\n"+body+"\n due to: "+e.getMessage());
			return null;
		}
	}
}