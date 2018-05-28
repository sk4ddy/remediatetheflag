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
package com.remediatetheflag.global.actions.auth.management.team;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.ExerciseInstance;
import com.remediatetheflag.global.model.ExerciseResult;
import com.remediatetheflag.global.model.ExerciseStatus;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class MarkExerciseAsCancelledAction extends IAction {

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);

		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		JsonElement jsonElement = json.get(Constants.ACTION_PARAM_ID);
		Integer idExercise = jsonElement.getAsInt();
		
		ExerciseInstance reviewedInstance = hpc.getManagementCompletedExerciseInstance(idExercise,sessionUser.getManagedOrganizations());
		if(null==reviewedInstance){
			logger.error("ExerciseInstance "+idExercise+" not found from reviewer "+sessionUser.getIdUser());
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
		}
		if(sessionUser.getRole().equals(Constants.ROLE_TEAM_MANAGER)){
			List<User> users = hpc.getUsersInTeamManagedBy(sessionUser);
			if(!users.contains(reviewedInstance.getUser())){
				MessageGenerator.sendErrorMessage("NotFound", response);
				return;
			}
		}
		if(reviewedInstance.getStatus().equals(ExerciseStatus.REVIEWED) || reviewedInstance.getStatus().equals(ExerciseStatus.CANCELLED)){
			logger.error("ExerciseInstance "+idExercise+" has already reviewer. Current attempt is from reviewer "+sessionUser.getIdUser());
			MessageGenerator.sendErrorMessage("NotFound", response);
			return;
		}
		Integer idFile = null;
		if(null!=reviewedInstance.getResultFile()){
			idFile = reviewedInstance.getResultFile().getId();
			reviewedInstance.setResultFile(null);
		}
		Integer idScore = reviewedInstance.getScore().getIdExerciseScore();
		List<Integer> idResults = new LinkedList<Integer>();
		for(ExerciseResult er : reviewedInstance.getResults()){
			idResults.add(er.getIdResult());
		}
		
		reviewedInstance.setStatus(ExerciseStatus.CANCELLED);
		reviewedInstance.setResultsAvailable(false);
		reviewedInstance.getResults().clear();
		reviewedInstance.setScore(null);
		reviewedInstance.setReviewer(sessionUser);
		reviewedInstance.setReviewedDate(new Date());

		hpc.updateExerciseInstance(reviewedInstance);
		hpc.managementCleanCancelledInstance(idFile,idScore,idResults);
		
		logger.debug("Exercise "+idExercise+" marked as cancelled from reviewer "+sessionUser.getIdUser());
		MessageGenerator.sendSuccessMessage(response);
	}

}
