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
package com.remediatetheflag.global.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public enum AvailableExerciseStatus {

	@SerializedName("0")
	AVAILABLE(0),
	@SerializedName("1")
	UPDATED(1),
	@SerializedName("2")
	COMING_SOON(2),
	@SerializedName("3")
	INACTIVE(3);

	public static final AvailableExerciseStatus DEFAULT_STATUS = INACTIVE;

	private AvailableExerciseStatus(Integer statusCode){
		this.statusCode = statusCode;
	}

	@SerializedName("status")
	@Expose
	private Integer statusCode;

	public Integer getName() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public static AvailableExerciseStatus getStatusFromStatusCode(Integer statusCode){
		for (AvailableExerciseStatus status :AvailableExerciseStatus.values()) {
			if (statusCode==status.getName()) {
				return status;
			}
		}
		return DEFAULT_STATUS;
	}
}
