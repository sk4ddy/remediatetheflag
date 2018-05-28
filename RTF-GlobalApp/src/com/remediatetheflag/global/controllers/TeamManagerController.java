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

public class TeamManagerController  extends ActionsController {

	@SuppressWarnings({ "serial", "rawtypes" })
	public TeamManagerController() {		

		HashMap<String, Class[]> idNotNullInteger = new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorNotNull.class,
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
		}};
		
		type2action.put("getAllRunningExercises", com.remediatetheflag.global.actions.auth.management.team.GetRunningExercisesAction.class);

		type2action.put("getExerciseDetails", com.remediatetheflag.global.actions.auth.management.team.GetAvailableExerciseDetailsAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.GetAvailableExerciseDetailsAction.class, idNotNullInteger);
		type2action.put("getRegionsForExercise", com.remediatetheflag.global.actions.auth.management.team.GetAvailableRegionsForExerciseAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.GetAvailableRegionsForExerciseAction.class, idNotNullInteger);
		type2action.put("getExercises", com.remediatetheflag.global.actions.auth.management.team.GetAllAvailableExercisesAction.class);
		type2action.put("getReferenceFile", com.remediatetheflag.global.actions.auth.management.team.GetAvailableExerciseReferenceFileAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.user.GetAvailableExerciseReferenceFileAction.class, idNotNullInteger);
		type2action.put("getSolutionFile", com.remediatetheflag.global.actions.auth.management.team.GetAvailableExerciseSolutionFileAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.user.GetAvailableExerciseSolutionFileAction.class, idNotNullInteger);
		type2action.put("addUsersToTeam", com.remediatetheflag.global.actions.auth.management.team.AddUsersToTeamAction.class);
		
		type2action.put("getPendingReviews", com.remediatetheflag.global.actions.auth.management.team.GetPendingReviewsAction.class);
		type2action.put("getCompletedReviews", com.remediatetheflag.global.actions.auth.management.team.GetCompletedExercisesAction.class);
		type2action.put("getAvailableUsersForTeam", com.remediatetheflag.global.actions.auth.management.team.GetUsersWithoutTeamAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.GetUsersWithoutTeamAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_TEAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("renameTeam", com.remediatetheflag.global.actions.auth.management.team.RenameTeamAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.RenameTeamAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_TEAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_NAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("addTeamManager", com.remediatetheflag.global.actions.auth.management.team.MakeUserTeamManagerAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.MakeUserTeamManagerAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_TEAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("removeFromTeam", com.remediatetheflag.global.actions.auth.management.team.RemoveUserFromTeamAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.RemoveUserFromTeamAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_TEAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
			put(Constants.ACTION_PARAM_USERNAME, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorStringNotEmpty.class
			}
					);
		}});
		type2action.put("getReviewDetails", com.remediatetheflag.global.actions.auth.management.team.GetReviewDetailsAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.GetReviewDetailsAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorNotNull.class,
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
		}});
		type2action.put("getUserFeedback", com.remediatetheflag.global.actions.auth.management.team.GetUserFeedbackForExerciseAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.GetUserFeedbackForExerciseAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorNotNull.class,
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
		}});
		type2action.put("getReviewFile", com.remediatetheflag.global.actions.auth.management.team.GetReviewFileAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.GetReviewFileAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorNotNull.class,
					com.remediatetheflag.global.actions.validators.ValidatorInteger.class
			}
					);
		}});
		type2action.put("postReview", com.remediatetheflag.global.actions.auth.management.team.PostReviewAction.class);
		type2action.put("markAsCancelled", com.remediatetheflag.global.actions.auth.management.team.MarkExerciseAsCancelledAction.class);
		type2fieldValidator.put(com.remediatetheflag.global.actions.auth.management.team.MarkExerciseAsCancelledAction.class, new HashMap<String, Class[]>() {{
			put(Constants.ACTION_PARAM_ID, new Class[]{
					com.remediatetheflag.global.actions.validators.ValidatorNotNull.class,
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