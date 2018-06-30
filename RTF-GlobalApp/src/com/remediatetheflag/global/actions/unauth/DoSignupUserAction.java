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
package com.remediatetheflag.global.actions.unauth;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.Country;
import com.remediatetheflag.global.model.Organization;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.model.UserStatus;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;
import com.remediatetheflag.global.utils.NotificationsHelper;
import com.remediatetheflag.global.utils.PasswordComplexityUtil;
import com.remediatetheflag.global.utils.RandomGenerator;

public class DoSignupUserAction  extends IAction{

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {

		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		JsonElement usernameElement = json.get(Constants.ACTION_PARAM_USERNAME);
		String username	 = usernameElement.getAsString();

		JsonElement firstNameElement = json.get(Constants.ACTION_PARAM_FIRST_NAME);
		JsonElement lastNameElement = json.get(Constants.ACTION_PARAM_LAST_NAME);
		JsonElement emailElement = json.get(Constants.ACTION_PARAM_EMAIL);
		JsonElement countryElement = json.get(Constants.ACTION_PARAM_COUNTRY);
		JsonElement passwordElement = json.get(Constants.ACTION_PARAM_PASSWORD);
		JsonElement orgInvitationCodeElement = json.get(Constants.ACTION_PARAM_ORG_CODE);

		String firstName = firstNameElement.getAsString();
		String lastName = lastNameElement.getAsString();
		String email = emailElement.getAsString();
		String country = countryElement.getAsString();
		String password = passwordElement.getAsString();
		String orgInvitationCode = orgInvitationCodeElement.getAsString();

		Organization o = hpc.getOrganizationFromInvitationCode(orgInvitationCode);
		if(null==o){
			MessageGenerator.sendErrorMessage("OrgCodeWrong", response);
			return;
		}
		@SuppressWarnings("serial")
		List<User> organizationUsers = hpc.getManagementAllUsers(new HashSet<Organization>(){{add(o);}});
		if (organizationUsers.size()>=o.getMaxUsers()) {
			MessageGenerator.sendErrorMessage("MaxUserLimit", response);
			return;
		}


		User existingUser = hpc.getUserFromUsername(username);
		if (existingUser != null) {
			MessageGenerator.sendErrorMessage("UserExists", response);
			return;
		}
		if(!PasswordComplexityUtil.isPasswordComplex(password)){
			MessageGenerator.sendErrorMessage("WeakPassword", response);
			return;
		}
		Country c = hpc.getCountryFromCode(country);
		if(null==c){
			logger.error("Invalid data entered for signup for email: "+email);
			MessageGenerator.sendErrorMessage("InvalidData", response);
			return;
		}
		User user = new User();
		user.setEmail(email);
		user.setInvitationCodeRedeemed(orgInvitationCode);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setFirstName(firstName);
		user.setRole(Constants.ROLE_USER);		
		String salt = RandomGenerator.getNextSalt();
		String pwd = DigestUtils.sha512Hex(password.concat(salt)); 
		user.setSalt(salt);
		user.setPassword(pwd);
		user.setStatus(UserStatus.INACTIVE);
		user.setCountry(c);
		user.setScore(0);	
		user.setExercisesRun(0);
		user.setEmailVerified(true);
		user.setForceChangePassword(false);
		user.setInstanceLimit(1);
		user.setJoinedDateTime(new Date());
		user.setTeam(null);
		user.setDefaultOrganization(o);
		Integer id = hpc.addUser(user);
		if(null!=id && id>0){
			hpc.decreseOrganizationCodeRedeem(o,orgInvitationCode);
			NotificationsHelper helper = new NotificationsHelper();
			helper.addNewUserAdded(user);
			helper.addWelcomeToRTFNotification(user);
			MessageGenerator.sendSuccessMessage(response);
		}
		else{
			logger.error("Signup failed at DB-end for email: "+email);
			MessageGenerator.sendErrorMessage("SignupFailed", response);
		}
	}
}