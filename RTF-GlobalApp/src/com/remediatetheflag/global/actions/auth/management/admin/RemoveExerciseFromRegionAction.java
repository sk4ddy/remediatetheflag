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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.RTFECSTaskDefinitionForExerciseInRegion;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.AWSHelper;
import com.remediatetheflag.global.utils.Constants;

public class RemoveExerciseFromRegionAction extends IAction {

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	// a task definition can be removed only if there are no ECSTaskDefinitionForExerciseInRegion referencing it as active
	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {

		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);

		JsonElement idTaskDefElement = json.get(Constants.ACTION_PARAM_TASK_DEFINITION_ID);
		Integer idTaskDef = idTaskDefElement.getAsInt();
		JsonElement idExerciseElement = json.get(Constants.ACTION_PARAM_EXERCISE_ID);
		Integer idExercise = idExerciseElement.getAsInt();
		
		List<RTFECSTaskDefinitionForExerciseInRegion> list = hpc.getAllTaskDefinitionForExerciseInRegion(idTaskDef);
		for(RTFECSTaskDefinitionForExerciseInRegion taskDef : list) {
			if(taskDef.getActive()) {
				MessageGenerator.sendErrorMessage("TaskDefinitionActive", response);
				return;
			}
		}
		RTFECSTaskDefinitionForExerciseInRegion taskDefinitionForDeletion = hpc.getTaskDefinitionForExerciseInRegion(idExercise, idTaskDef);
		AWSHelper awsHelper = new AWSHelper();
		Boolean awsResult = awsHelper.removeTaskDefinitionInRegion(taskDefinitionForDeletion.getTaskDefinition().getTaskDefinitionArn(), taskDefinitionForDeletion.getRegion());
		if(!awsResult) {
			logger.warn("Could not deregister task definition "+idTaskDef+" from AWS region "+taskDefinitionForDeletion.getRegion().getName());
			MessageGenerator.sendErrorMessage("TaskDefinitionDeregisterFailed", response);
			return;
		}
		Boolean result = hpc.removeTaskDefinitionInRegion(idExercise, idTaskDef);
		if(!result) {
			logger.warn("Could not remove task definition "+idTaskDef);
			MessageGenerator.sendErrorMessage("TaskDefinitionRemoveFailed", response);
		}
		MessageGenerator.sendSuccessMessage(response);
		return;	
	}
}
