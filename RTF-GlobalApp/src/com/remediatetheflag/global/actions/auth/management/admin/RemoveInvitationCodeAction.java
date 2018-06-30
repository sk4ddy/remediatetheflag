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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.Organization;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class RemoveInvitationCodeAction extends IAction {
	
	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);

		JsonElement orgIdElement = json.get(Constants.ACTION_PARAM_ORG_ID);
		Integer orgId = orgIdElement.getAsInt();
		JsonElement codeElement = json.get(Constants.ACTION_PARAM_ORG_CODE);
		String code = codeElement.getAsString();
				
		Organization org = hpc.getOrganizationById(orgId);
		if(!isManagingOrg(sessionUser,org)){
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
		}
		
		Boolean result = hpc.removeInvitationCode(code, orgId);
		
		if(result)
			MessageGenerator.sendSuccessMessage(response);
		else
			MessageGenerator.sendErrorMessage("Failed", response);
	}
	
	private Boolean isManagingOrg(User user, Organization org) {
		for(Organization managed : user.getManagedOrganizations()) {
			if(managed.getId().equals(org.getId())) {
				return true;
			}
		}
		return false;
	}

}
