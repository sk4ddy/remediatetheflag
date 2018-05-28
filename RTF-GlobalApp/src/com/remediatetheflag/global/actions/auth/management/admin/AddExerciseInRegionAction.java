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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.regions.Regions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.AvailableExercise;
import com.remediatetheflag.global.model.RTFECSTaskDefinition;
import com.remediatetheflag.global.model.RTFECSTaskDefinitionForExerciseInRegion;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.AWSHelper;
import com.remediatetheflag.global.utils.Constants;

public class AddExerciseInRegionAction extends IAction {

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {

		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);	

		JsonElement idExerciseElement = json.get(Constants.ACTION_PARAM_EXERCISE_ID);

		JsonElement taskDefinitionNameElement = json.get(Constants.ACTION_PARAM_TASK_DEFINITION_NAME);
		JsonElement containerNameElement = json.get(Constants.ACTION_PARAM_CONTAINER_NAME);
		JsonElement repositoryImageUrlElement = json.get(Constants.ACTION_PARAM_REPO_URL);
		JsonElement softMemoryLimitElement= json.get(Constants.ACTION_PARAM_SOFT_MEMORY);
		JsonElement regionElement = json.get(Constants.ACTION_PARAM_REGION);
		JsonElement hardMemoryLimitElement = json.get(Constants.ACTION_PARAM_HARD_MEMORY);
		JsonElement statusElement = json.get(Constants.ACTION_PARAM_STATUS);

		String taskDefinitionName = taskDefinitionNameElement.getAsString();
		String containerName = containerNameElement.getAsString();
		
		String repositoryImageUrl = repositoryImageUrlElement.getAsString();
		String region = regionElement.getAsString();
		Integer softMemoryLimit = softMemoryLimitElement.getAsInt(); 
		Integer hardMemoryLimit = hardMemoryLimitElement.getAsInt();

		Boolean active = statusElement.getAsBoolean();
		Integer idExercise = idExerciseElement.getAsInt();

		AvailableExercise exercise = hpc.getAvailableExercise(idExercise);
		if(null==exercise) {
			MessageGenerator.sendErrorMessage("ExerciseNotFound", response);
			logger.error("Exercise "+idExercise+" not found for user "+sessionUser.getIdUser());
			return;	
		}

		Regions awsRegion = null;
		try{
			awsRegion = Regions.valueOf(region);
		} catch(Exception e){
			MessageGenerator.sendErrorMessage("RegionNotFound", response);
			logger.error("Region "+region+" not found for user "+sessionUser.getIdUser());
			return;	
		}

		RTFECSTaskDefinition ecsTask = new RTFECSTaskDefinition();

		ecsTask.setRegion(awsRegion);
		ecsTask.setContainerName(containerName);
		ecsTask.setTaskDefinitionName(taskDefinitionName);
		ecsTask.setHardMemoryLimit(hardMemoryLimit);
		ecsTask.setSoftMemoryLimit(softMemoryLimit);
		ecsTask.setRepositoryImageUrl(repositoryImageUrl);
		ecsTask.setUpdateDate(new Date());

		AWSHelper awsHelper = new AWSHelper();
		String arn = awsHelper.createECSTaskDefinition(ecsTask, sessionUser);

		if(null!=arn) {
			ecsTask.setTaskDefinitionArn(arn);
			String nameWithRevision = arn.split("/")[(arn.split("/").length)-1];
			if(null!=nameWithRevision && !nameWithRevision.equals(""))
				ecsTask.setTaskDefinitionName(nameWithRevision);
			RTFECSTaskDefinitionForExerciseInRegion ecsTaskForExercise = new RTFECSTaskDefinitionForExerciseInRegion();
			ecsTaskForExercise.setExercise(exercise);
			ecsTaskForExercise.setRegion(awsRegion);
			ecsTaskForExercise.setTaskDefinition(ecsTask);
			ecsTaskForExercise.setActive(active);
			Integer id = hpc.addECSTaskDefinitionForExerciseInRegion(ecsTaskForExercise);
			if(null==id) {
				MessageGenerator.sendErrorMessage("Error", response);
				logger.error("New ECSTaskDefinitionForExerciseInRegion could not be saved for user "+sessionUser.getIdUser());
				return;	
			}
			MessageGenerator.sendSuccessMessage(response);
			return;
		}
		MessageGenerator.sendErrorMessage("Error", response);
		logger.error("New ECSTaskDefinitionForExerciseInRegion could not be saved for user "+sessionUser.getIdUser());
	}
}