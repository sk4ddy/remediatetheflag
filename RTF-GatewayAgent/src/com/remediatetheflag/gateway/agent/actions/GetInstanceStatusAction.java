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
package com.remediatetheflag.gateway.agent.actions;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.gateway.agent.messages.MessageGenerator;
import com.remediatetheflag.gateway.agent.model.Constants;
import com.remediatetheflag.gateway.agent.utils.HTTPUtils;

public class GetInstanceStatusAction extends IAction{

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {

		JsonObject actionObject = new JsonObject();
		actionObject.addProperty(Constants.JSON_ATTRIBUTE_ACTION, Constants.ACTION_GET_INSTANCE_STATUS);
		String jsonAction = actionObject.toString();
		
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		JsonElement instanceIpElement = json.get(Constants.INSTANCE_IP);
		if(null==instanceIpElement){
			MessageGenerator.sendErrorMessage("Missing IP", response);
			logger.warn("Received null string when requesting status");
			return;
		}
		String responseString = HTTPUtils.sendPostRequest(instanceIpElement.getAsString(), jsonAction);
		if(null==responseString){
			MessageGenerator.sendErrorMessage(Constants.ERROR_COULD_NOT_FETCH, response);
			logger.warn("Received null string when requesting status from "+instanceIpElement.getAsString());
			return;
		}
		try {
			response.setContentType(Constants.CONTENT_TYPE_JSON);
			PrintWriter out = response.getWriter();
			out.print(responseString);
			out.flush();
		} catch (Exception e) {
			logger.error(e.getMessage());
			MessageGenerator.sendErrorMessage(Constants.ERROR_COULD_NOT_FETCH, response);
		}
	}
}