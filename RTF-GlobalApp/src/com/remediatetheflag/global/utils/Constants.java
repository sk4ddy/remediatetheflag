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
package com.remediatetheflag.global.utils;

public class Constants {
	
	public static final Integer ROLE_USER = 7;
	public static final Integer ROLE_STATS = 4;
	public static final Integer ROLE_TEAM_MANAGER = 3;
	public static final Integer ROLE_REVIEWER = 1;
	public static final Integer ROLE_ADMIN = 0;
	
	public static final Integer STATUS_RUNNING = 0;
	public static final Integer STATUS_TERMINATED = 1;
	public static final Integer STATUS_STOPPED = 2;
	public static final Integer STATUS_PENDING = 3;
	
	public static final String INDEX_PAGE = "/index.html";
	public static final String USER_HOME = "/user/index.html";
	public static final String MGMT_HOME = "/management/index.html";
	
	public static final String ATTRIBUTE_SECURITY_CSRF_TOKEN = "token";
	public static final String ATTRIBUTE_SECURITY_CONTEXT = "SECURITY_CONTEXT";
	public static final String ATTRIBUTE_SECURITY_ROLE = "SECURITY_ROLE";

	public static final String JSON_VALUE_ERROR = "error";
	public static final String JSON_VALUE_SUCCESS = "success";
	public static final String JSON_VALUE_REDIRECT = "redirect";
	public static final String JSON_ATTRIBUTE_RESULT = "result";
	public static final String JSON_ATTRIBUTE_LOCATION = "location";
	public static final String JSON_ATTRIBUTE_ERROR_MSG = "errorMsg";
	public static final String JSON_ATTRIBUTE_ACTION = "action";
	public static final String REQUEST_ATTRIBUTE_JSON = "json";
	
	public static final String JSON_VALUE_ERROR_ACTION_NOT_VALIDATED = "Action message not valid";
	
	public static final String JSON_VALUE_ERROR_ACTION_EXCEPTION = "Action exception";
	public static final String JSON_VALUE_ERROR_ACTION_INVALID_CSRF = "Invalid token";
	
	public static final String JSON_VALUE_ERROR_ACCOUNT_LOCKOUT = "Account lockout, please contact support.";

	public static final String CONFIG_FILE = "config.properties";
		
	public static final String REQUEST_JSON = "json";
	public static final String ENV_USR_PWD = "USR_PWD";

	public static final String ACTION_PARAM_ORG_CODE = "orgCode";
	public static final String ACTION_PARAM_ORG_ID = "orgId";
	
	public static final String ACTION_PARAM_ID = "id";
	public static final String ACTION_PARAM_REGION = "region";
	public static final String ACTION_PARAM_CSRF_TOKEN = "ctoken";
	public static final String ACTION_PARAM_USERNAME = "username";
	public static final String ACTION_PARAM_PASSWORD = "password";
	public static final String ACTION_PARAM_OLDPASSWORD = "oldPwd";
	public static final String ACTION_PARAM_NEWPASSWORD = "newPwd";
	
	public static final String ACTION_PARAM_FIRST_NAME = "firstName";
	public static final String ACTION_PARAM_LAST_NAME = "lastName";
	public static final String ACTION_PARAM_EMAIL = "email";
	public static final String ACTION_PARAM_COUNTRY = "country";
	public static final String ACTION_PARAM_TIMEZONE = "timezone";

	public static final Integer FAILED_ATTEMPTS_LOCKOUT = 5;
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String ACTION_PARAM_ID_EXERCISE_INSTANCE = "exId";
	public static final String ACTION_PARAM_FEEDBACK = "feedback";
	public static final String JSON_ATTRIBUTE_OBJ = "obj";
	public static final String ACTION_PARAM_NAME = "name";
	//add team
	public static final String ACTION_PARAM_ORG_NAME = "orgName";
	public static final String ACTION_PARAM_TEAM_NAME = "teamName";
	public static final String ACTION_PARAM_TEAM_ID = "teamId";
	public static final String ACTION_PARAM_USERNAME_LIST = "users";
	
	
	public static final String ACTION_PARAM_NOTIFICATION_TEXT = "text";
	public static final String ACTION_PARAM_FILTER = "filter";
	public static final String ACTION_PARAM_CONTACT_NAME = "contactName";
	public static final String ACTION_PARAM_MAX_USERS = "maxUsers";
	public static final String ACTION_PARAM_CONTACT_PHONE= "contactPhone";
	public static final String ACTION_PARAM_ALLOWED_DOMAINS = "allowedDomains";
	public static final String ACTION_PARAM_EXERCISE = "exercise";
	public static final String ACTION_PARAM_FQDN = "fqdn";
	public static final String ACTION_PARAM_STATUS = "status";
	public static final String ACTION_PARAM_EXERCISE_ID = "exerciseId";
	public static final String ACTION_PARAM_TASK_DEFINITION_ID = "taskDefId";
	public static final String ACTION_PARAM_TASK_DEFINITION_NAME = "taskDefinitionName";
	public static final String ACTION_PARAM_CONTAINER_NAME = "containerName";
	public static final String ACTION_PARAM_REPO_URL = "imageUrl";
	public static final String ACTION_PARAM_SOFT_MEMORY = "softMemory";
	public static final String ACTION_PARAM_HARD_MEMORY = "hardMemory";
	public static final String ACTION_PARAM_ROLE_ID = "roleId";
	public static final String ACTION_PARAM_FORCE_PASSWORD_CHANGE = "forcePasswordChange"; 
	public static final String ACTION_PARAM_CONCURRENT_EXERCISE_LIMIT = "concurrentExercisesLimit";

	public static final Integer MIN_HARD_MEMORY_LIMIT = 300;
	public static final Integer MAX_HARD_MEMORY_LIMIT = 5000;

	public static final Integer MIN_SOFT_MEMORY_LIMIT = 50;
	public static final Integer MAX_SOFT_MEMORY_LIMIT = 3000;
	public static final String AWS_ECS_STATUS_STOPPED = "stopped";
	public static final String IMAGES_AVAILABLE_EXERCISES_INFO = "exerciseInfo";

	//notifications
		public static final String NOTIFICATION_LINK_COMPLETED_REVIEW = "#/history";
		public static final String NOTIFICATION_TEXT_COMPLETED_REVIEW = "The exercise {EXERCISE} has been reviewed, check your results and leave a feedback.";
		public static final String NOTIFICATION_LINK_WELCOME_TO_RTF = "#/exercises";
		public static final String NOTIFICATION_TEXT_WELCOME_TO_RTF = "Welcome to RTF, browse available exercises and start hacking!";
		public static final String NOTIFICATION_LINK_NEW_EXERCISE_AVAILABLE = "#/exercises/details/{ID}";
		public static final String NOTIFICATION_TEXT_NEW_EXERCISE_AVAILABLE = "A new exercise \"{EXERCISE}\" is availabe on RTF!";
		public static final String NOTIFICATION_LINK_NEW_USER_ADDED = "#/users/details/{USERNAME}";
		public static final String NOTIFICATION_TEXT_NEW_USER_ADDED = "User {USERNAME} is now enrolled on RTF!";
	
		
		public static final String ACTION_PARAM_TOPICS = "topics";
		public static final String ACTION_PARAM_DIFFICULTY= "difficulty";
		public static final String ACTION_PARAM_TITLE= "title";
		public static final String ACTION_PARAM_DESCRIPTION= "description";
		public static final String ACTION_PARAM_TECHNOLOGY= "technology";
		public static final String ACTION_PARAM_DURATION= "duration";
		public static final String ACTION_PARAM_SCORE= "score";
		public static final String ACTION_PARAM_TROPHY_TITLE= "trophyTitle";
		public static final String ACTION_PARAM_TROPHY_DESCRIPTION= "trophyDescription";
		
		public static final String ACTION_PARAM_REFERENCE_FILE = "referenceFile";
		public static final String ACTION_PARAM_SOLUTION_FILE = "solutionFile";
		public static final String ACTION_PARAM_FLAGS_LIST = "flags";
		public static final String ACTION_PARAM_INFO_LIST = "infoList";
		public static final String ACTION_PARAM_RESOURCE_LIST = "resources";
		public static final String ACTION_PARAM_CATEGORY = "category";
		public static final String ACTION_PARAM_FLAG_QUESTIONS = "questions";
	
		public static final String ACTION_PARAM_TYPE = "type";
		public static final String ACTION_PARAM_SELF_CHECK_AVAILABLE = "selfCheckAvailable";
		public static final String ACTION_PARAM_SELF_CHECK = "selfCheck";
		public static final String ACTION_PARAM_INSTRUCTIONS = "instructions";
		public static final String ACTION_PARAM_HINT_AVAILABLE = "hintAvailable";
		public static final String ACTION_PARAM_HINT = "hint";
		public static final String ACTION_PARAM_IMAGE = "image";
		public static final String ACTION_PARAM_DATA = "data";
		public static final String ACTION_PARAM_URL = "url";
		
		public static final String CHARSET = "UTF-8";

}
