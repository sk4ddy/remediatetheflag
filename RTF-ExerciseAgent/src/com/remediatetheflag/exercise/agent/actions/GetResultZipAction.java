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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.remediatetheflag.exercise.agent.messages.MessageGenerator;
import com.remediatetheflag.exercise.agent.utils.ConfigurationParser;
import com.remediatetheflag.exercise.agent.utils.OSCommandExecutor;
import com.remediatetheflag.exercise.agent.utils.ZipHelper;

public class GetResultZipAction extends IAction{

	private ZipHelper zipArchive;
	private static Logger logger = LoggerFactory.getLogger(GetResultZipAction.class);

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		zipArchive = new ZipHelper();

		// automated check
		automatedChecker();

		// retrieve logs
		retrieveLogs();
		
		// do code diff
		performSourceDiff();

		//prepare archive
		String zipName = "RTF"+UUID.randomUUID();

		zipArchive.zipIt("/tmp/"+zipName);

		//send archive
		File zippedFile = new File("/tmp/"+zipName);

		response.setContentType("application/octet-stream");
		response.setContentLength((int) zippedFile.length());
		response.setHeader( "Content-Disposition", String.format("attachment; filename=\"%s\"", zippedFile.getName()+".zip"));

		OutputStream out = response.getOutputStream();
		try (FileInputStream in = new FileInputStream(zippedFile)) {
			byte[] buffer = new byte[4096];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			out.flush();
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(e.getMessage()+"\n"+sw.toString());
			out.close();
			MessageGenerator.sendErrorMessage("Error", response);
		}
	}
	
	private void automatedChecker() {
		try {
			String cmd = ConfigurationParser.getConfig().getTestCommandFull();
			String checkerResultJSON = OSCommandExecutor.exec(cmd);
			File checkerResults = new File("/tmp/selfcheck.json");
			if (!checkerResults.exists()) {
				checkerResults.createNewFile();
			}
			FileWriter fw = new FileWriter(checkerResults.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(checkerResultJSON);
			bw.close();
			zipArchive.addToFileList(new File("/tmp/selfcheck.json"));
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(e.getMessage()+"\n"+sw.toString());
		}
	}

	private void retrieveLogs() {	
		for(String path : ConfigurationParser.getConfig().getLogFiles()){
			zipArchive.addToFileList(new File(path));
		}
	}

	private void performSourceDiff(){
		
		try {
			String cmd = "diff -r -u "+ConfigurationParser.getConfig().getOriginalSourceDirectory()+" "+ConfigurationParser.getConfig().getExerciseSourceDirectory()+" | egrep -vE '(^Only in|^Binary files)'";
			String diff = OSCommandExecutor.exec(cmd);
			File sourceDiff = new File("/tmp/sourceDiff.txt");
			if (!sourceDiff.exists()) {
				sourceDiff.createNewFile();
			}
			FileWriter fw = new FileWriter(sourceDiff.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(diff);
			bw.close();
			zipArchive.addToFileList(new File("/tmp/sourceDiff.txt"));
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(e.getMessage()+"\n"+sw.toString());
		}
	}		
}
