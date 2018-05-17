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
package com.remediatetheflag.global.actions.auth.management.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.regions.Regions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.RTFGateway;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class AddSatelliteGatewayAction extends IAction {
	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);

		JsonElement nameElement = json.get(Constants.ACTION_PARAM_NAME);
		JsonElement regionElement = json.get(Constants.ACTION_PARAM_REGION);
		JsonElement fqdnElement = json.get(Constants.ACTION_PARAM_FQDN);
		JsonElement statusElement = json.get(Constants.ACTION_PARAM_STATUS);
		
		String name = nameElement.getAsString();
		String region = regionElement.getAsString();
		String fqdn = fqdnElement.getAsString();
		Boolean status = statusElement.getAsBoolean();
		
		Regions awsRegion = null;
		try{
			awsRegion = Regions.valueOf(region);
		} catch(Exception e){
			MessageGenerator.sendErrorMessage("RegionNotFound", response);
			logger.error("Region "+region+" not found for user "+sessionUser.getIdUser());
			return;	
		}
		
		RTFGateway g = new RTFGateway();
		g.setName(name);
		g.setFqdn(fqdn);
		g.setRegion(awsRegion);
		g.setActive(status);
	
		Integer id = hpc.addGateway(g);
		if(null!=id)
			MessageGenerator.sendSuccessMessage(response);
		else
			MessageGenerator.sendErrorMessage("Failed", response);
	}

}
