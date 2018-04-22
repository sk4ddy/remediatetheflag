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

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.Organization;
import com.remediatetheflag.global.model.OrganizationStatus;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class AddOrganizationAction extends IAction {

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);

		JsonElement nameElement = json.get(Constants.ACTION_PARAM_NAME);
		JsonElement contactNameElement = json.get(Constants.ACTION_PARAM_CONTACT_NAME);
		JsonElement contactPhoneElement = json.get(Constants.ACTION_PARAM_CONTACT_PHONE);
		JsonElement contactEmailElement = json.get(Constants.ACTION_PARAM_CONTACT_EMAIL);
		JsonElement maxUsersElement = json.get(Constants.ACTION_PARAM_MAX_USERS);
		JsonElement allowedDomainsElement = json.get(Constants.ACTION_PARAM_ALLOWED_DOMAINS);
		
		String name = nameElement.getAsString();
		String contactName = contactNameElement.getAsString();
		String contactPhone = contactPhoneElement.getAsString();
		String contactEmail = contactEmailElement.getAsString();
		Integer maxUsers = maxUsersElement.getAsInt();
		
		List<String> allowedDomains = new LinkedList<String>();

		for(JsonElement domainElement : allowedDomainsElement.getAsJsonArray()){
			allowedDomains.add(domainElement.getAsString());
		}
		
		Organization o = new Organization();
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		o.setDateJoined(c.getTime());
		c.add(Calendar.YEAR, 2); 
		o.setDateExpiration(c.getTime());
		o.setName(name);
		o.setContactName(contactName);
		o.setContactEmail(contactEmail);
		o.setContactTelephone(contactPhone);
		o.setMaxUsers(maxUsers);
		o.setStatus(OrganizationStatus.ACTIVE);
		o.setAllowedEmailDomains(allowedDomains);
		
		
		Integer id = hpc.addOrganization(o);
		if(null!=id){
			User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);
			Organization oDB = hpc.getOrganizationById(id);
			sessionUser.getManagedOrganizations().add(oDB);
			request.getSession().setAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT, sessionUser);
			hpc.addManagedOrganization(sessionUser,id);
			MessageGenerator.sendSuccessMessage(response);
		}else{
			MessageGenerator.sendErrorMessage("Failed", response);
		}
	}
}
