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
package com.remediatetheflag.global.controllers;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.utils.Constants;

public class MonitorController  extends ActionsController {

	@SuppressWarnings({ "serial", "rawtypes" })
	public MonitorController(){
		
		type2action.put("getChallenges", com.remediatetheflag.global.actions.auth.management.monitor.GetChallengesAction.class);
		type2action.put("getChallengeDetails", com.remediatetheflag.global.actions.auth.management.monitor.GetChallengeDetailsAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.GetChallengeDetailsAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorNotNull.class,
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
		}});
		type2action.put("sendNotification", com.remediatetheflag.global.actions.auth.management.monitor.SendNotificationToUser.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.SendNotificationToUser.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_NOTIFICATION_TEXT, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("getGlobalStats", com.remediatetheflag.global.actions.auth.management.monitor.GetStatsGlobalAction.class);		
		type2action.put("getTeamDetails", com.remediatetheflag.global.actions.auth.management.monitor.GetTeamDetails.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.GetTeamDetails.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("getUserStats", com.remediatetheflag.global.actions.auth.management.monitor.GetStatsUserAction.class);		
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.GetStatsUserAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("getUserAchievements", com.remediatetheflag.global.actions.auth.management.monitor.GetUserAchievementsAction.class);		
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.GetUserAchievementsAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("getUserExercises", com.remediatetheflag.global.actions.auth.management.monitor.GetUserCompletedExercisesAction.class);		
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.GetUserCompletedExercisesAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("getTeamMembers", com.remediatetheflag.global.actions.auth.management.monitor.GetTeamMembersAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.GetTeamMembersAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("getTeams", com.remediatetheflag.global.actions.auth.management.monitor.GetTeamsAction.class);
		type2action.put("getTeamStats", com.remediatetheflag.global.actions.auth.management.monitor.GetStatsTeamAction.class);		
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.GetStatsTeamAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("getUserDetails", com.remediatetheflag.global.actions.auth.management.monitor.GetUserDetailsAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.monitor.GetUserDetailsAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});	
		type2action.put("getUsers", com.remediatetheflag.global.actions.auth.management.monitor.GetUsersAction.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected boolean isGranted(HttpServletRequest request, HttpServletResponse response, Class actionClass) {
		User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);

		if(null!=sessionUser.getEmailVerified() && !sessionUser.getEmailVerified()){
			MessageGenerator.sendErrorMessage("VerifyEmail", response);
			return false;
		}
		if(null!=sessionUser.getForceChangePassword() && sessionUser.getForceChangePassword()){
			MessageGenerator.sendErrorMessage("ChangePassword", response);
			return false;
		}
		return true;
	}
}