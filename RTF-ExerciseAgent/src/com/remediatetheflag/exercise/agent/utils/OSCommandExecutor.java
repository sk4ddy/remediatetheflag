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

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSCommandExecutor {

	private static Logger logger = LoggerFactory.getLogger(OSCommandExecutor.class);

	@SuppressWarnings("resource")
	public static String exec(String cmd){
		String [] command = new String[]{"sh", "-c", cmd};
		StringBuilder strOut = new StringBuilder();

		Runtime rt = Runtime.getRuntime();
		Process proc;
		try {
			proc = rt.exec(command);
			int result = proc.waitFor();
			InputStream in = (result == 0) ? proc.getInputStream() : proc.getErrorStream();
			int c;
			while ((c = in.read()) != -1) {
				strOut.append((char) c);
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(e.getMessage()+"\n"+sw.toString());
			return strOut.toString();
		}
		return strOut.toString();
	}
	
	public static void execAndReturn(String cmd){
		String [] command = new String[]{"sh", "-c", cmd};
		Runtime rt = Runtime.getRuntime();
		try {
			 rt.exec(command);
		} catch (Exception  e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(e.getMessage()+"\n"+sw.toString());
		}
	}
}
