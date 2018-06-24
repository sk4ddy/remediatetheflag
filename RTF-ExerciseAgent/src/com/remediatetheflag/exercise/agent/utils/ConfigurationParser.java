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
package com.remediatetheflag.exercise.agent.utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.remediatetheflag.exercise.agent.model.AgentConfiguration;
import com.remediatetheflag.exercise.agent.model.Constants;

public class ConfigurationParser {

	private static Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);
	private static AgentConfiguration config = null;

	public static void parseConfiguration(){
		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.CONFIG_FILE);
			config = new Gson().fromJson( new InputStreamReader(is), AgentConfiguration.class);
			logger.debug("Configuration file parsed");
			if(null==config.getExerciseSourceDirectory() || config.getExerciseSourceDirectory().equals("") || 
					null==config.getTestCommandFull() || config.getTestCommandFull().equals("") ||
					null==config.getTestCommandLite() || config.getTestCommandLite().equals("") ||
					null==config.getOriginalSourceDirectory() || config.getOriginalSourceDirectory().equals("") ||
					null==config.getLogFiles() || config.getLogFiles().isEmpty()) {
				logger.error("Configuration file contains missing required fields\n"+printConfiguration(config));
			}
			logger.debug("RTFAgent configuration completed\n"+printConfiguration(config));

		} catch (JsonSyntaxException | JsonIOException e1) {
			StringWriter sw = new StringWriter();
			e1.printStackTrace(new PrintWriter(sw));
			logger.error("Configuration Parsing error: "+e1.getMessage()+"\n"+sw.toString());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				logger.error("Configuration Parsing error (finally block): "+e.getMessage()+"\n"+sw.toString());			}
		}
	}

	public static String printConfiguration(AgentConfiguration config){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(config);
		return json;
	}

	public static AgentConfiguration getConfig() {
		return config;
	}
}