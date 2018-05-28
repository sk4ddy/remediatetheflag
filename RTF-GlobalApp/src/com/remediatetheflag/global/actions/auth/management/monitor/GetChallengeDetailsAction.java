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
package com.remediatetheflag.global.actions.auth.management.monitor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.Challenge;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class GetChallengeDetailsAction extends IAction {

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		JsonElement jsonElement = json.get(Constants.ACTION_PARAM_ID);
		Integer idChallenge = jsonElement.getAsInt();
		
		User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);
		Challenge challenge =  hpc.getChallengeWithDetails(idChallenge,sessionUser.getManagedOrganizations());	
		
		boolean granted = true;
		if(sessionUser.getRole().equals(Constants.ROLE_TEAM_MANAGER)){
			List<User> users = hpc.getUsersInTeamManagedBy(sessionUser);
			granted = isTeamManagerGranted(users,challenge);
		}
		if(!granted) {
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
		}
		MessageGenerator.sendChallengeDetailsMessage(challenge,response);	
		
	}
	
	private boolean isTeamManagerGranted(List<User> users, Challenge challenge) {
		for(User user : challenge.getUsers()) {
			for(User managed : users) {
				if(user.getIdUser().equals(managed.getIdUser())){
					return true;
				}
			}
		}
		return false;
	}
}