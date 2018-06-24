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
package com.remediatetheflag.exercise.agent.model;

public class Constants {

	public static final String CONFIG_FILE = "config.json";
	public static final String JSON_VALUE_ERROR = "error";
	public static final String JSON_VALUE_SUCCESS = "success";
	public static final String JSON_VALUE_REDIRECT = "redirect";
	public static final String JSON_ATTRIBUTE_RESULT = "result";
	public static final String JSON_ATTRIBUTE_LOCATION = "location";
	public static final String JSON_ATTRIBUTE_ERROR_MSG = "errorMsg";
	public static final String JSON_ATTRIBUTE_ACTION = "action";
	public static final String REQUEST_ATTRIBUTE_JSON = "json";

	public static final String JSON_VALUE_ERROR_FAILED_LOGIN = "Login failed";
	public static final String JSON_VALUE_ERROR_JSON_PARSING = "Json format not valid";
	public static final String JSON_VALUE_ERROR_ACTION_NOT_FOUND = "Action not found";
	public static final String JSON_VALUE_ERROR_ACTION_EXCEPTION = "Action exception";
	public static final String JSON_VALUE_ERROR_TYPE_NOT_FOUND = "Json Attribute '"+Constants.JSON_ATTRIBUTE_ACTION+"' missing";
	public static final String JSON_VALUE_ERROR_INVALID_CONTENT_TYPE = "Invalid content type";
	public static final String JSON_CONTENT_TYPE = "application/json";

}
