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
package com.remediatetheflag.global.actions.validators;

import com.google.gson.JsonObject;
import org.apache.commons.validator.routines.EmailValidator;

public class ValidatorEmail  implements IFieldValidator{

	@Override
	public boolean isValid(JsonObject json, String attribute) {
		return null != json.get(attribute) && EmailValidator.getInstance().isValid(json.get(attribute).getAsString());
	}

}
