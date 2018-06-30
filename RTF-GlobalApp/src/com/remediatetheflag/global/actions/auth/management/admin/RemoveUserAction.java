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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.model.UserStatus;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class RemoveUserAction extends IAction {

	private  HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);
		
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		JsonElement usernameElement = json.get(Constants.ACTION_PARAM_USERNAME);
		String username = usernameElement.getAsString();

		User user = hpc.getUserFromUsername(username,sessionUser.getManagedOrganizations());
		if(null==user) {
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
		}
		long time = System.currentTimeMillis();
		Date date = new Date();
		user.setEmail("removed"+time+"@remediatetheflag.com");
		user.setEmailVerified(false);
		user.setForceChangePassword(true);
		user.setFirstName("Removed");
		user.setLastName("User");
		user.setInstanceLimit(0);
		user.setTeam(null);
		user.setPersonalDataAnonymisedDateTime(date);
		user.setUsername("removed"+time);
		user.setStatus(UserStatus.REMOVED);
		
		Boolean result = hpc.updateUserInfo(user);
		if(!result) {
			logger.error("Could not update info for removal of user: "+user.getIdUser() );
			MessageGenerator.sendErrorMessage("UpdateFailed", response);
			return;
		}
		MessageGenerator.sendSuccessMessage(response);
	}
}