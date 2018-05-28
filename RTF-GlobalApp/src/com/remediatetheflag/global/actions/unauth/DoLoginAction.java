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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.model.UserAuthenticationEvent;
import com.remediatetheflag.global.model.UserStatus;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.CSRFTokenUtils;
import com.remediatetheflag.global.utils.Constants;

public class DoLoginAction  extends IAction{

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		JsonElement usernameElement = json.get(Constants.ACTION_PARAM_USERNAME);
		String username = usernameElement.getAsString();
		
		JsonElement passwordElement = json.get(Constants.ACTION_PARAM_PASSWORD);
		String password = passwordElement.getAsString();
	
		if(null==username || username.equals("") || null==password || password.equals("")) {
			MessageGenerator.sendRedirectMessage(Constants.INDEX_PAGE, response);
			return;
		}
		Integer failedAttempts = hpc.getFailedLoginAttemptsForUser(username);
		if(failedAttempts>=Constants.FAILED_ATTEMPTS_LOCKOUT){
			logger.warn("Username "+username+" is locked out, login refused");
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACCOUNT_LOCKOUT, response);
			User lockedUser = hpc.getUserFromUsername(username);
			if(null!=lockedUser){
				lockedUser.setStatus(UserStatus.LOCKED);
				hpc.updateUserInfo(lockedUser);
			}
			MessageGenerator.sendRedirectMessage(Constants.INDEX_PAGE, response);
			return;
		}
		
		User user = hpc.getUser(username, password);
		
		UserAuthenticationEvent attempt = new UserAuthenticationEvent();
		attempt.setUsername(username);

		if(null != user && user.getUsername()!=null) {
			if(!user.getStatus().equals(UserStatus.ACTIVE)){
				logger.warn("Login UNSUCCESSFUL for USER: "+username+" - USER IS: "+user.getStatus());
				MessageGenerator.sendRedirectMessage(Constants.INDEX_PAGE, response);
				return;
			}
			request.getSession().invalidate();
			request.getSession(true);
			request.getSession().setAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT, user);
			request.getSession().setAttribute(Constants.ATTRIBUTE_SECURITY_ROLE, user.getRole());
			CSRFTokenUtils.setToken(request.getSession());
			if(user.getRole()<=Constants.ROLE_STATS){
				logger.debug("Login successful for ADMIN: "+user.getUsername());
				MessageGenerator.sendRedirectMessage(Constants.MGMT_HOME, response);
			}
			else{
				logger.debug("Login successful for USER: "+user.getUsername());
				MessageGenerator.sendRedirectMessage(Constants.USER_HOME, response);		
			}
			hpc.setFailedLoginAttemptsForUser(username,0);
			attempt.setSessionIdHash(DigestUtils.sha256Hex(request.getSession().getId()));
			attempt.setLoginSuccessful(true);
			attempt.setLoginDate(new Date());
			hpc.addLoginEvent(attempt);
		} else {
			failedAttempts++;
			hpc.setFailedLoginAttemptsForUser(username,failedAttempts);
			attempt.setLoginSuccessful(false);
			attempt.setLoginDate(new Date());
			hpc.addLoginEvent(attempt);
			logger.warn("Login UNSUCCESSFUL for USER: "+username);
			MessageGenerator.sendRedirectMessage(Constants.INDEX_PAGE, response);
		}	
	}
}