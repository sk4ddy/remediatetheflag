package com.remediatetheflag.gateway.extras;

import com.remediatetheflag.gateway.extras.Constants;

public class Constants {

	public static final String JSON_VALUE_ERROR = "error";
	public static final String JSON_VALUE_SUCCESS = "success";
	public static final String JSON_VALUE_REDIRECT = "redirect";
	public static final String JSON_ATTRIBUTE_RESULT = "result";
	public static final String JSON_ATTRIBUTE_LOCATION = "location";
	public static final String JSON_ATTRIBUTE_ERROR_MSG = "errorMsg";
	public static final String JSON_ATTRIBUTE_ACTION = "action";
	public static final String REQUEST_ATTRIBUTE_JSON = "json";
	public static final String JSON_VALUE_OK = "ok";
	public static final String JSON_VALUE_KO = "ko";

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
	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String ACTION_PARAM_ID = "id";
	public static final String INSTANCE_IP = "ip";

	public static final String CHARSET = "UTF-8";
	
	public static final String USER = "u";
	public static final String TOKEN = "t";
	
	public static final String GUAC_AUTH_COOKIE  = "GUAC_AUTH";
	public static final String RTF_PATH  = "/rtf/";
	public static final String CONTENT_TYPE_PLAIN = "text/plain";

	
}
