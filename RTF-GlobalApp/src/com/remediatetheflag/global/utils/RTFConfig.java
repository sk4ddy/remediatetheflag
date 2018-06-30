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

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTFConfig {
	
	private static Properties config = new Properties();
	private static String guacAdminUser = "";
	private static String guacAdminPassword = "";
	private static String exercisesCluster = "";
	private static String agentUser = "";
	private static String agentPassword = "";
	
	private static Logger logger = LoggerFactory.getLogger(RTFConfig.class);

	static{
		try {
			config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.CONFIG_FILE));
			RTFConfig.guacAdminUser = config.getProperty("guacAdminUser");
			RTFConfig.guacAdminPassword = config.getProperty("guacAdminPassword");
			RTFConfig.exercisesCluster = config.getProperty("exercisesCluster");
			RTFConfig.agentUser = config.getProperty("agentUser");
			RTFConfig.agentPassword = config.getProperty("agentPassword");
		
			logger.info("RTFGlobalConfiguration parsed");
		} catch (IOException e) {
			logger.error(e.toString());
		}			
	}
	
	public static String getGuacAdminUsername() {
		return guacAdminUser;
	}
	public static String getGuacAdminPassword() {
		return guacAdminPassword;
	}
	public static String getExercisesCluster() {
		return exercisesCluster;
	}
	public static String getAgentUser() {
		return agentUser;
	}
	public static String getAgentPassword() {
		return agentPassword;
	}
}
