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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.remediatetheflag.global.actions.IAction;
import com.remediatetheflag.global.actions.validators.IClassValidator;
import com.remediatetheflag.global.actions.validators.IFieldValidator;
import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.model.ValidatedData;
import com.remediatetheflag.global.utils.CSRFTokenUtils;
import com.remediatetheflag.global.utils.Constants;

public abstract class ActionsController {

	@SuppressWarnings("rawtypes")
	protected Map<String, Class> type2action = new HashMap<>();
	@SuppressWarnings("rawtypes")
	protected Map<Class, Map<String, Class[]>> type2fieldValidator= new HashMap<>();
	private Map<String, String> type2classValidator= new HashMap<>();
	@SuppressWarnings("rawtypes")
	protected List<Class> csrfExclusion = new LinkedList<Class>();

	private static Logger logger = LoggerFactory.getLogger(ActionsController.class);

	/**
	 * Execute Action
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void executeAction(HttpServletRequest request, HttpServletResponse response) {
		
		User user = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);	

		//supports logging messages for unauthenticated requests
		String usr = "";
		if(user!=null && null!=user.getIdUser()){
			usr = String.valueOf(user.getIdUser());
		}
		
		if(!isJSONContentType(request)){
			logger.error("INVALID content type received from user: "+usr);
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_NOT_VALIDATED, response);
			return;	
		}	
		
		JsonObject jsonObject;
		String actionType;
		try {
			StringBuffer allLines = new StringBuffer();
			String output;
			while ((output = request.getReader().readLine()) != null) {
				allLines.append(output);
			}  
			jsonObject = (JsonObject) new JsonParser().parse(allLines.toString());
			JsonElement jActionType = jsonObject.get(Constants.JSON_ATTRIBUTE_ACTION);
			if (jActionType==null) {
				logger.error("Missing value for '"+Constants.JSON_ATTRIBUTE_ACTION+ "' - JSON is " + jsonObject.toString() + " - from user: "+usr);
				MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_NOT_VALIDATED, response);
				return;
			}
			actionType = jActionType.getAsString();
		} catch (JsonSyntaxException | IOException e) {
			logger.error("Not valid Json format from user: "+usr+" - Exception: " + e.getMessage());
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_NOT_VALIDATED, response);
			return;
		}

		// Find Action
		@SuppressWarnings("rawtypes")
		Class actionClass = type2action.get(actionType);
		if (null == actionClass) {
			logger.error("Action NOT found for actionType: "+actionType+ " for user: "+usr);
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_NOT_VALIDATED, response);
			return;
		}

		// Authorize Request
		if (!isGranted(request, response, actionClass)) {
			logger.error("Action NOT authorized. actionClass: " + actionClass+ " for user: "+usr);
			return;
		}

		// CSRF 
		if(!csrfExclusion.contains(actionClass)){
			if(!CSRFTokenUtils.isValid(request.getSession(), jsonObject)){
				logger.error("Action csrf token INVALID. actionClass: " + actionClass+ " for user: "+usr);
				MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_INVALID_CSRF, response);
				return;
			}
		}

		// Validate Request
		@SuppressWarnings("rawtypes")
		Map<String, Class[]> validators = type2fieldValidator.get(actionClass);
		ValidatedData validatedData = new ValidatedData(jsonObject, false, new ArrayList<String>());
		if(null!=validators){
			validatedData = performValidation(jsonObject, validators, validatedData);
		}

		// Validate Request, class-wide
		String validatorClass = type2classValidator.get(actionType);
		if (null!=validatorClass && !validatedData.isWithErrors()) {
			try {
				IClassValidator validator = ((IClassValidator) Class.forName(validatorClass).newInstance());
				logger.debug("Class Wide Validator - ExecuteAction: " + validatorClass);
				validatedData = validator.doValidation(jsonObject, validatedData);

			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				logger.error("Class Wide Validator - Action Exception: " + e.getMessage() + " for user: "+usr);
				MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_NOT_VALIDATED, response);
			}
		}
		if (validatedData.isWithErrors()) {
			logger.error("Action NOT validated. actionClass: " + actionClass + " validation: " + new Gson().toJson(validatedData.getErrors())+ " for user: "+usr);
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_NOT_VALIDATED, response);
			return;
		}

		// Execute request
		try {
			IAction action = ((IAction) actionClass.newInstance());
			logger.debug("ExecuteAction: " + actionClass);
			request.setAttribute(Constants.REQUEST_ATTRIBUTE_JSON, jsonObject);
			action.doAction(request,response);	
		} catch ( Exception  e) {
			logger.error("Action Exception: " + e.getMessage()+ " for user: "+usr);
			MessageGenerator.sendErrorMessage(Constants.JSON_VALUE_ERROR_ACTION_EXCEPTION, response);
		}

	}

	private Boolean isJSONContentType(HttpServletRequest request) {
		String cType = request.getHeader("Content-Type");
		if(null==cType || cType.equals("")){
			return false;
		}
		return cType.indexOf(Constants.JSON_CONTENT_TYPE)==0;
	}

	/**
	 * Check if Request Message is valid
	 * @param json
	 * @param validators
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private ValidatedData performValidation(JsonObject json, Map<String, Class[]> validators, ValidatedData validatedData) {
		for (String attribute : validators.keySet()) {
			for (Class validatorClass : validators.get(attribute)) {
				try {
					IFieldValidator validator = ((IFieldValidator) validatorClass.newInstance());
					if (!validator.isValid(json, attribute) ){
						validatedData.setWithErrors(true);
						validatedData.addError(attribute + ":" + json.get(attribute) + " NOT pass " + validatorClass);
					}
				} catch (InstantiationException | IllegalAccessException e) {
					logger.error("Validator Exception: " + e.getMessage());
					validatedData.setWithErrors(true);
				}
			}
		}	
		return validatedData;
	}

	@SuppressWarnings("rawtypes")
	protected abstract boolean isGranted(HttpServletRequest request, HttpServletResponse response, Class actionClass);

	@SuppressWarnings("rawtypes")
	public Map<String, Class> getType2action() {
		return type2action;
	}
}