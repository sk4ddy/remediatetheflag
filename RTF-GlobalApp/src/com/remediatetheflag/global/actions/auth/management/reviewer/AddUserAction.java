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
package com.remediatetheflag.global.actions.auth.management.reviewer;

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

public class AddUserAction extends IAction {

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonObject json = (JsonObject)request.getAttribute("json");

		User sessionUser = (User)request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);

		JsonElement usernameElement = json.get(Constants.ACTION_PARAM_USERNAME);
		JsonElement firstNameElement = json.get(Constants.ACTION_PARAM_FIRST_NAME);
		JsonElement lastNameElement = json.get(Constants.ACTION_PARAM_LAST_NAME);
		JsonElement emailElement = json.get(Constants.ACTION_PARAM_EMAIL);
		JsonElement countryElement = json.get(Constants.ACTION_PARAM_COUNTRY);
		JsonElement passwordElement = json.get(Constants.ACTION_PARAM_PASSWORD);
		JsonElement orgElement = json.get(Constants.ACTION_PARAM_ORG_ID);

		JsonElement roleElement = json.get(Constants.ACTION_PARAM_ROLE_ID);
		JsonElement concurrentExerciseLimitElement = json.get(Constants.ACTION_PARAM_CONCURRENT_EXERCISE_LIMIT);
		//JsonElement emailVerifiedElement = json.get("emailVerified");
		JsonElement passwordChangeElement = json.get(Constants.ACTION_PARAM_FORCE_PASSWORD_CHANGE);

		String username = usernameElement.getAsString();
		String firstName = firstNameElement.getAsString();
		String lastName = lastNameElement.getAsString();
		String email = emailElement.getAsString();
		String country = countryElement.getAsString();
		String password = passwordElement.getAsString();
		Integer orgId = orgElement.getAsInt();

		Integer roleId = roleElement.getAsInt();
		Integer concurrentExercisesLimit = concurrentExerciseLimitElement.getAsInt();
		Boolean emailVerified = true; //emailVerifiedElement.getAsBoolean();
		Boolean forcePasswordChange = passwordChangeElement.getAsBoolean();

		Integer usrRole = -1;
		switch (roleId){
		case 0: 
			usrRole = Constants.ROLE_ADMIN;
			break;
		case 1: 
			usrRole = Constants.ROLE_REVIEWER;
			break;
		case 3: 
			usrRole = Constants.ROLE_TEAM_MANAGER;
			break;
		case 4: 
			usrRole = Constants.ROLE_STATS;
			break;
		case 7: 
			usrRole = Constants.ROLE_USER;
			break;

		default: {
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
			}
		}
		if (usrRole.intValue() < sessionUser.getRole().intValue()){
			MessageGenerator.sendErrorMessage("NotAuthorized", response);
			return;
		}
		Organization o = hpc.getOrganizationById(orgId);
		if(null==o){
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
		}
		boolean isManager = false;
		for(Organization organization : sessionUser.getManagedOrganizations()){
			if(o.getId().equals(organization.getId())){
				isManager = true;
				break;
			}
		}
		if(!isManager){
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
		}
		List<User> organizationUsers = hpc.getManagementAllUsers(new HashSet<Organization>(){{add(o);}});
		if (organizationUsers.size()>=o.getMaxUsers()) {
			MessageGenerator.sendErrorMessage("MaxUserLimit", response);
			return;
		}
		if(!PasswordComplexityUtil.isPasswordComplex(password)){
			MessageGenerator.sendErrorMessage("WeakPassword", response);
			return;
		}
		User existingUser = hpc.getUserFromUsername(username);
		if (existingUser != null) {
			MessageGenerator.sendErrorMessage("UserExists", response);
			return;
		}
		Country c = hpc.getCountryFromCode(country);
		if(null==c){
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
		}
		User user = new User();
		user.setEmail(email);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setFirstName(firstName);
		user.setRole(usrRole);
		String salt = RandomGenerator.getNextSalt();
		String pwd = DigestUtils.sha512Hex(password.concat(salt));
		user.setSalt(salt);
		user.setPassword(pwd);
		user.setStatus(UserStatus.ACTIVE);
		user.setCountry(c);
		user.setScore(0);
		user.setExercisesRun(0);
		user.setEmailVerified(emailVerified);
		user.setForceChangePassword(forcePasswordChange);
		user.setInstanceLimit(concurrentExercisesLimit);
		user.setJoinedDateTime(new Date());
		user.setTeam(null);
		user.setDefaultOrganization(o);
		if (user.getRole().intValue() < 7) {
			user.setManagedOrganizations(new HashSet() {{ add(o); }});
		} else {
			user.setManagedOrganizations(null);
		}
		Integer id = hpc.addUser(user);
		if(null!=id && id>0){
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
