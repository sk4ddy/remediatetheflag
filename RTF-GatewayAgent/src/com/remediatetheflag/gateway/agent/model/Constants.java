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
package com.remediatetheflag.gateway.agent.model;

public class Constants {

	public static final String CONFIG_FILE = "config.properties";
	public static final String JSON_VALUE_ERROR = "error";
	public static final String JSON_VALUE_SUCCESS = "success";
	public static final String JSON_VALUE_REDIRECT = "redirect";
	public static final String JSON_ATTRIBUTE_RESULT = "result";
	public static final String JSON_ATTRIBUTE_LOCATION = "location";
	public static final String JSON_ATTRIBUTE_ERROR_MSG = "errorMsg";
	public static final String JSON_ATTRIBUTE_ACTION = "action";
	public static final String REQUEST_ATTRIBUTE_JSON = "json";
	
	public static final String ERROR_COULD_NOT_FETCH = "CouldNotFetch";
	
	public static final String JSON_VALUE_ERROR_FAILED_LOGIN = "Login failed";
	public static final String JSON_VALUE_ERROR_JSON_PARSING = "Json format not valid";
	public static final String JSON_VALUE_ERROR_ACTION_NOT_FOUND = "Action not found";
	public static final String JSON_VALUE_ERROR_ACTION_NOT_VALIDATED = "Action message not valid";
	public static final String JSON_VALUE_ERROR_ACTION_NOT_AUTHORIZED = "Action not authorised";
	public static final String JSON_VALUE_ERROR_ACTION_EXCEPTION = "Action exception";
	public static final String JSON_VALUE_ERROR_ACTION_INVALID_CSRF = "Invalid token";
	public static final String JSON_VALUE_ERROR_TYPE_NOT_FOUND = "Json Attribute '"+Constants.JSON_ATTRIBUTE_ACTION+"' missing";
	public static final String JSON_VALUE_ERROR_ACCOUNT_LOCKOUT = "Account lockout, please contact support.";
	public static final String JSON_VALUE_ERROR_INVALID_CONTENT_TYPE = "Invalid content type";

	public static final String REQUEST_JSON = "json";
	
	public static final String CONTENT_TYPE_HEADER = "Content-Type";
	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String ACTION_PARAM_ID = "id";
	public static final String INSTANCE_IP = "ip";

	public static final String ACTION_GET_RESULT_STATUS = "getResultStatus";
	public static final String ACTION_GET_INSTANCE_STATUS = "getInstanceStatus";
	public static final String ACTION_GET_RESULT_ARCHIVE = "getResultArchive";
	
	public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BASIC_AUTH_HEADER = "Basic";

	public static final String CHARSET = "UTF-8";
	
	public static final String AGENT_USER = "agentUser";
	public static final String AGENT_PASSWORD = "agentPassword";
	public static final String POST_METHOD = "POST";
	


}
