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
package com.remediatetheflag.gateway.agent.utils;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.remediatetheflag.gateway.agent.model.Constants;

public class GatewayAgentConfig {
	
	private static Properties config = new Properties();
	private static String agentUser = "";
	private static String agentPassword = "";

	private static Logger logger = LoggerFactory.getLogger(GatewayAgentConfig.class);

	static{
		try {
			config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.CONFIG_FILE));
			GatewayAgentConfig.agentUser = config.getProperty(Constants.AGENT_USER);
			GatewayAgentConfig.agentPassword = config.getProperty(Constants.AGENT_PASSWORD);
			logger.info("RTFGatewayAgent configuration parsed");
		} catch (IOException e) {
			logger.error(e.toString());
		}			
	}
	
	public static String getAgentUsername() {
		return agentUser;
	}
	public static String getAgentPassword() {
		return agentPassword;
	}
}