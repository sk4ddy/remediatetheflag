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
package com.remediatetheflag.global.actions.auth.user;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.Country;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class SetUserInfoAction extends IAction{

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {

		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);

		JsonElement firstNameElement = json.get(Constants.ACTION_PARAM_FIRST_NAME);
		String firstName = firstNameElement.getAsString();

		JsonElement lastNameElement = json.get(Constants.ACTION_PARAM_LAST_NAME);
		String lastName = lastNameElement.getAsString();

		JsonElement emailElement = json.get(Constants.ACTION_PARAM_EMAIL);
		String email = emailElement.getAsString();

		JsonElement countryElement = json.get(Constants.ACTION_PARAM_COUNTRY);
		String country = countryElement.getAsString();
		Country c = hpc.getCountryFromCode(country);
		
		if(null!=firstName && null!=lastName && null!=email && null!=c){
			User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);
			sessionUser.setCountry(c);
			sessionUser.setEmail(email);
			sessionUser.setPersonalDataUpdateDateTime(new Date());
			sessionUser.setFirstName(firstName);
			sessionUser.setLastName(lastName);
			hpc.updateUserInfo(sessionUser);
			MessageGenerator.sendSuccessMessage(response);
		}
		else{
			User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);
			logger.error("Could not update profile details for user "+sessionUser.getIdUser());
			MessageGenerator.sendErrorMessage("InvalidData", response);
		}
	}
}