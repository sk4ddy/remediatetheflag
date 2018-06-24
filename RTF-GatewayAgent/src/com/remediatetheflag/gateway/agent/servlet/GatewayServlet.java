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
package com.remediatetheflag.gateway.agent.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.remediatetheflag.gateway.agent.actions.IAction;
import com.remediatetheflag.gateway.agent.messages.MessageGenerator;
import com.remediatetheflag.gateway.agent.model.Constants;

@SuppressWarnings("rawtypes")
public class GatewayServlet  extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected static org.slf4j.Logger logger = LoggerFactory.getLogger(GatewayServlet.class);

	protected static Map<String, Class> type2action = new HashMap<>();
	
	static {
		type2action.put(Constants.ACTION_GET_INSTANCE_STATUS, com.remediatetheflag.gateway.agent.actions.GetInstanceStatusAction.class);
		type2action.put(Constants.ACTION_GET_RESULT_STATUS, com.remediatetheflag.gateway.agent.actions.GetResultStatusAction.class);
		type2action.put(Constants.ACTION_GET_RESULT_ARCHIVE, com.remediatetheflag.gateway.agent.actions.GetResultZipAction.class);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		executeAction(request,response);
	}

	public void executeAction(HttpServletRequest request, HttpServletResponse response) {

		
		JsonObject jsonObject;
		String actionType;
		try {
			StringBuffer allLines = new StringBuffer();
			String output;
			while ((output = request.getReader().readLine()) != null) {
				allLines.append(output);
			}  
			jsonObject = (JsonObject) new JsonParser().parse(allLines.toString());
			JsonElement jActionType = jsonObject.get(Constants.JSON_ATTRIBUTE_ACTION);
			if (jActionType==null) {
				MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_TYPE_NOT_FOUND, response);
				return;
			}
			actionType = jActionType.getAsString();
		} catch (Exception e) {
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_JSON_PARSING, response);
			logger.error(e.getMessage());
			return;
		}

		Class actionClass = type2action.get(actionType);
		if (null == actionClass) {
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_NOT_FOUND, response);
			logger.error("Requested action "+actionType+" is not mapped");
			return;
		}

		try {
			IAction action = ((IAction) actionClass.newInstance());
			request.setAttribute(Constants.REQUEST_ATTRIBUTE_JSON, jsonObject);
			action.doAction(request,response);	
		} catch ( Exception  e) {
			logger.error(e.getMessage());
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_EXCEPTION, response);
		}
	}
}