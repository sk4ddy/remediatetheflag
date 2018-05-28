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

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Region;
import com.remediatetheflag.global.model.ExerciseInstance;
import com.remediatetheflag.global.model.ExerciseResult;
import com.remediatetheflag.global.model.ExerciseResultFile;
import com.remediatetheflag.global.model.ExerciseStatus;
import com.remediatetheflag.global.model.RTFGateway;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;

public class RTFInstancesAutoShutdown implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(RTFInstancesAutoShutdown.class);
	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();
	private GatewayHelper gwHelper = new GatewayHelper();
	private AWSHelper awsHelper = new AWSHelper();

	@Override
	public void run() {
		logger.debug("Running shutdown task");

		//get running exercise instances from db, returns ExerciseInstance
		List<ExerciseInstance> exerciseInstances = hpc.getAllRunningExerciseInstancesWithAwsGWFlag();
		logger.debug("Returned "+exerciseInstances.size()+" running exercise instances from db");

		// get active regions
		List<Region> activeRegions = new LinkedList<Region>();
		for(RTFGateway gw : hpc.getAllActiveGateways()){
			activeRegions.add(Region.getRegion(gw.getRegion()));
		}
		logger.debug("Returned "+activeRegions.size()+" active regions from db");

		// get running ecs instances from AWS for active regions
		List<String> runningTasks = awsHelper.getRunningECSTasks(activeRegions);
		logger.debug("Returned "+runningTasks.size()+" running tasks for aws active regions");		

		for(String taskArn : runningTasks){
			boolean found = false;
			for(ExerciseInstance ei : exerciseInstances){
				if(ei.getEcsInstance().getTaskArn().equals(taskArn)){
					found = true;
					if(ei.getEndTime().getTime() < System.currentTimeMillis()) {
						logger.debug("Exercise "+ei.getIdExerciseInstance()+" needs to be shutdown");
						if(!ei.getStatus().equals(ExerciseStatus.REVIEWED)){
							logger.debug("Pulling exercise result file for exercise instance "+ei.getIdExerciseInstance());
							ExerciseResultFile fr = gwHelper.getResultFile(ei);
							ei.setResultFile(fr);
							logger.debug("Pulling exercise results for exercise instance "+ei.getIdExerciseInstance());
							List<ExerciseResult> results = gwHelper.getResultStatus(ei);
							ei.setResults(results);
							ei.setStatus(ExerciseStatus.STOPPED);
							ei.getEcsInstance().setStatus(Constants.STATUS_TERMINATED);
							Calendar cal = Calendar.getInstance();
							ei.setEndTime(cal.getTime());
							ei.getEcsInstance().setShutdownTime(cal.getTime());
							hpc.updateExerciseInstance(ei);
						}
						awsHelper.terminateTask(ei.getEcsInstance());
					}
				}
			}
			if(!found){ //orphans
				Date createdTime = awsHelper.getRunningECSTaskStartTime(taskArn);
				long time = System.currentTimeMillis();
				if(time-createdTime.getTime() >= 1200000) {
					logger.debug("Exercise "+taskArn+" is orphan and needs to be shutdown");
					awsHelper.terminateTask(taskArn);		
				}
				else {
					logger.debug("Fullfilled Reservation "+(time-createdTime.getTime())+" ms old");
				}
			}
		}
		logger.debug("End of shutdown task");
	}
}