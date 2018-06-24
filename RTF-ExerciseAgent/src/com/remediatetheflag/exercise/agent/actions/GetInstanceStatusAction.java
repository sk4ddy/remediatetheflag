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
package com.remediatetheflag.exercise.agent.actions;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.remediatetheflag.exercise.agent.messages.MessageGenerator;
import com.remediatetheflag.exercise.agent.utils.ConfigurationParser;
import com.remediatetheflag.exercise.agent.utils.OSCommandExecutor;

public class GetInstanceStatusAction extends IAction{
	
	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String cmd = ConfigurationParser.getConfig().getExerciseStatusCommand();
			if(null==cmd || cmd.equals("")) {
				MessageGenerator.sendErrorMessage("Error", response);
				return;
			}
			String statusJSON = OSCommandExecutor.exec(cmd);
			MessageGenerator.sendInstanceStatusMessage(statusJSON, response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(e.getMessage()+"\n"+sw.toString());
			MessageGenerator.sendErrorMessage("Error", response);
		}	
	}

}
