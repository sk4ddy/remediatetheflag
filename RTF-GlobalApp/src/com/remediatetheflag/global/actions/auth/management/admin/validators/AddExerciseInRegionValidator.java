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
package com.remediatetheflag.global.actions.auth.management.admin.validators;

import org.owasp.esapi.ESAPI;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.validators.IClassValidator;
import com.remediatetheflag.global.model.ValidatedData;
import com.remediatetheflag.global.utils.Constants;

public class AddExerciseInRegionValidator implements IClassValidator {

	@Override
	public ValidatedData doValidation(JsonObject json, ValidatedData validatedData) {

		JsonElement taskDefinitionNameElement = json.get(Constants.ACTION_PARAM_TASK_DEFINITION_NAME);
		JsonElement containerNameElement = json.get(Constants.ACTION_PARAM_CONTAINER_NAME);
		JsonElement softMemoryLimitElement= json.get(Constants.ACTION_PARAM_SOFT_MEMORY);
		JsonElement hardMemoryLimitElement = json.get(Constants.ACTION_PARAM_HARD_MEMORY);

		String taskDefinitionName = taskDefinitionNameElement.getAsString();
		try {
			if(!ESAPI.validator().isValidInput("AddExerciseInRegionValidator", taskDefinitionName, "ECSSafe", 255, false)){
				validatedData.setWithErrors(true);
				validatedData.addError("taskDefinitionName" + ":" + taskDefinitionName + " NOT pass AddExerciseInRegionValidator");
				return validatedData;
			}
			String containerName = containerNameElement.getAsString();
			if(!ESAPI.validator().isValidInput("AddExerciseInRegionValidator", containerName, "ECSSafe", 255, false)){
				validatedData.setWithErrors(true);
				validatedData.addError("containerName" + ":" + containerName + " NOT pass AddExerciseInRegionValidator");
				return validatedData;
			}
			if(!ESAPI.validator().isValidInteger("AddExerciseInRegionValidator", softMemoryLimitElement.getAsString(), Constants.MIN_SOFT_MEMORY_LIMIT, Constants.MAX_SOFT_MEMORY_LIMIT, false)){
				validatedData.setWithErrors(true);
				validatedData.addError("softMemoryLimit" + ":" + softMemoryLimitElement.getAsString() + " NOT pass AddExerciseInRegionValidator");
				return validatedData;
			}
			if(!ESAPI.validator().isValidInteger("AddExerciseInRegionValidator", hardMemoryLimitElement.getAsString(), Constants.MIN_HARD_MEMORY_LIMIT, Constants.MAX_HARD_MEMORY_LIMIT, false)){
				validatedData.setWithErrors(true);
				validatedData.addError("hardMemoryLimit" + ":" + hardMemoryLimitElement.getAsString() + " NOT pass AddExerciseInRegionValidator");
				return validatedData;
			}
			return validatedData;
		}catch(Exception e) {
			return validatedData;
		}
	}
}