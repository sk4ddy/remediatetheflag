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

public class AdminController extends ActionsController {

	@SuppressWarnings({ "rawtypes", "serial" })
	public AdminController(){
		
		type2action.put("removeUser", com.remediatetheflag.global.actions.auth.management.admin.RemoveUserAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.ResetUserPasswordAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("addTaskDefinition", com.remediatetheflag.global.actions.auth.management.admin.AddExerciseInRegionAction.class);
		type2classValidator.put("addTaskDefinition", com.remediatetheflag.global.actions.auth.management.admin.validators.AddExerciseInRegionValidator.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.AddExerciseInRegionAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_EXERCISE_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_REGION, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_TASK_DEFINITION_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_CONTAINER_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_SOFT_MEMORY, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_HARD_MEMORY, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_REPO_URL, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorURL.class
			}
					);
			put(Constants.ACTION_PARAM_STATUS, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorBoolean.class
			}
					);
		}});
		type2action.put("getAWSRegions", com.remediatetheflag.global.actions.auth.management.admin.GetAWSRegionsAction.class);
		type2action.put("deleteGateway", com.remediatetheflag.global.actions.auth.management.admin.RemoveSatelliteGatewayAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.RemoveSatelliteGatewayAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
		}});
		type2action.put("getGateways", com.remediatetheflag.global.actions.auth.management.admin.GetSatelliteGatewaysAction.class);
		type2action.put("addGateway", com.remediatetheflag.global.actions.auth.management.admin.AddSatelliteGatewayAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.AddSatelliteGatewayAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_REGION, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_FQDN, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_STATUS, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorBoolean.class
			}
					);
		}});
		type2action.put("updateGateway", com.remediatetheflag.global.actions.auth.management.admin.UpdateSatelliteGatewayAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.UpdateSatelliteGatewayAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_FQDN, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_STATUS, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorBoolean.class
			}
					);
		}});
		type2action.put("getOrganizations", com.remediatetheflag.global.actions.auth.management.admin.GetOrganizationsAction.class);
		type2action.put("checkOrganizationNameAvailable", com.remediatetheflag.global.actions.auth.management.admin.CheckOrganizationNameAvailable.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.CheckOrganizationNameAvailable.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		
		type2action.put("removeExerciseInRegion", com.remediatetheflag.global.actions.auth.management.admin.RemoveExerciseFromRegionAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.RemoveExerciseFromRegionAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_EXERCISE_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
			put(Constants.ACTION_PARAM_TASK_DEFINITION_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
		}});
		type2action.put("enableExerciseInRegion", com.remediatetheflag.global.actions.auth.management.admin.EnableExerciseInRegionAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.EnableExerciseInRegionAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_EXERCISE_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
			put(Constants.ACTION_PARAM_TASK_DEFINITION_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
		}});
		type2action.put("disableExerciseInRegion", com.remediatetheflag.global.actions.auth.management.admin.DisableExerciseInRegionAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.DisableExerciseInRegionAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_EXERCISE_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
			put(Constants.ACTION_PARAM_TASK_DEFINITION_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
		}});
		
		type2action.put("enableExerciseForOrg", com.remediatetheflag.global.actions.auth.management.admin.EnableAvailableExerciseForOrganizationAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.EnableAvailableExerciseForOrganizationAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ORG_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_EXERCISE, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
		}});
		type2action.put("disableExerciseForOrg", com.remediatetheflag.global.actions.auth.management.admin.DisableAvailableExerciseForOrganizationAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.DisableAvailableExerciseForOrganizationAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ORG_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_EXERCISE, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			});
		}});
		type2action.put("addOrganization", com.remediatetheflag.global.actions.auth.management.admin.AddOrganizationAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.AddOrganizationAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_MAX_USERS, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_CONTACT_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_CONTACT_PHONE, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_CONTACT_EMAIL, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);

		}});
		type2action.put("removeTeamManager", com.remediatetheflag.global.actions.auth.management.admin.RemoveTeamManagerAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.RemoveTeamManagerAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_TEAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("resetUserPassword", com.remediatetheflag.global.actions.auth.management.admin.ResetUserPasswordAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.ResetUserPasswordAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
			put(Constants.ACTION_PARAM_PASSWORD, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("deleteTeam", com.remediatetheflag.global.actions.auth.management.admin.RemoveTeamAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.admin.RemoveTeamAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_TEAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
		}});
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
