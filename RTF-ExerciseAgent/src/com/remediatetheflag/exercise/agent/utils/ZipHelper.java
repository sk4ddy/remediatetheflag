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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipHelper{ 

	List<String> fileList;
	private static Logger logger = LoggerFactory.getLogger(ZipHelper.class);

	public ZipHelper(){
		fileList = new ArrayList<String>();
	}

	public void zipIt(String zipFile){

		byte[] buffer = new byte[1024];

		try{

			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for(String file : this.fileList){

				ZipEntry ze= new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in =
						new FileInputStream(file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			zos.close();

		}catch(Exception e){
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(e.getMessage()+"\n"+sw.toString());
		}
	}

	public void addToFileList(File node){
		if(node.isFile()){
			fileList.add(node.getAbsoluteFile().toString());
		}
		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				addToFileList(new File(node, filename));
			}
		}
	}
}