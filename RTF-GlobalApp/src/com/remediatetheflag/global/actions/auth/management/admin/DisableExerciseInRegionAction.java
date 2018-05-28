package com.remediatetheflag.global.actions.auth.management.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class DisableExerciseInRegionAction extends IAction {
	
	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);
		
		JsonElement idTaskDefElement = json.get(Constants.ACTION_PARAM_TASK_DEFINITION_ID);
		Integer idTaskDef = idTaskDefElement.getAsInt();
		JsonElement idExerciseElement = json.get(Constants.ACTION_PARAM_EXERCISE_ID);
		Integer idExecrcise = idExerciseElement.getAsInt();
		
		Boolean result = hpc.enableDisableTaskDefinitionInRegion(idExecrcise,idTaskDef,false);
		if(!result){
			MessageGenerator.sendErrorMessage("Error", response);
			return;
		}
		MessageGenerator.sendSuccessMessage(response);
	}

}
