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
package com.remediatetheflag.gateway.agent.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.remediatetheflag.gateway.agent.model.Constants;
import com.remediatetheflag.gateway.agent.model.FileResponse;

public class HTTPUtils {
	
	protected static Logger logger = LoggerFactory.getLogger(HTTPUtils.class);
	
	public static String sendPostRequest(String baseHost, String body) {
		URL obj;
		StringBuffer response = new StringBuffer();
		try {
			obj = new URL("http://"+baseHost+"/results/handler");
			logger.debug("#Sending request to URL : " + obj.toString()+" for "+body);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(Constants.POST_METHOD);
			con.setRequestProperty(Constants.CONTENT_TYPE_HEADER,Constants.CONTENT_TYPE_JSON ); 
			con.setReadTimeout(30000);
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
			logger.debug("#Received response for "+body+" - Length: "+response.length());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return response.toString();
	}
	public static FileResponse sendGetFileArchiveRequest(String baseHost, String body) {
		URL obj;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			obj = new URL("http://"+baseHost+"/results/handler");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(Constants.POST_METHOD);
			con.setRequestProperty(Constants.CONTENT_TYPE_HEADER,Constants.CONTENT_TYPE_JSON);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
			logger.debug("#Sending file results request to URL : " + obj.toString()+" for "+body);
			InputStream is = con.getInputStream();
            String disposition = con.getHeaderField(Constants.CONTENT_DISPOSITION_HEADER);
            String contentType = con.getContentType();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			FileResponse fr = new FileResponse();
			fr.setContentDisposition(disposition);
			fr.setContentType(contentType);
			fr.setFileContent(buffer.toByteArray());
			logger.debug("#Received results file from " + obj.toString()+" for "+body+" - Length: "+buffer.size());
			return fr;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}