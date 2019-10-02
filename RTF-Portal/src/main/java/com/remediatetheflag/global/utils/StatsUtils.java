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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.remediatetheflag.global.model.ExerciseInstance;
import com.remediatetheflag.global.model.ExerciseResult;
import com.remediatetheflag.global.model.ExerciseResultStatus;
import com.remediatetheflag.global.model.Stats;

public class StatsUtils {
	
	private static long getDateDiff(Date date1, Date date2) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return TimeUnit.MILLISECONDS.toMinutes(diffInMillies); 
	}
	
	private HashMap<String,Integer> initializeMapWithExerciseResultStatus(){
		HashMap<String,Integer> tmpMap = new HashMap<String,Integer>();
		for(ExerciseResultStatus ers: ExerciseResultStatus.values()) {
			tmpMap.put(ers.toString(),0);
		}
		return tmpMap;
	}
	
	public void setTimePerCategory(List<ExerciseInstance> exerciseInstances, Stats stats){
		
		Map<String, Long> totalTimeEach = new HashMap<String, Long>();
		Map<String, Integer> totalInstancesEach = new HashMap<String, Integer>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for(ExerciseInstance ei : exerciseInstances){
			String category = ei.getExercise().getFlags().get(0).getCategory();
			Integer dur = ei.getDuration();
			if(null==dur){
				dur = (int) getDateDiff(ei.getStartTime(),ei.getEndTime());
			}
			if(!totalTimeEach.containsKey(category)){
				totalTimeEach.put(category, (long) 0);
			}
			if(!totalInstancesEach.containsKey(category)){
				totalInstancesEach.put(category, 0);
			}
			totalTimeEach.put(category, (totalTimeEach.get(category)+dur));
			totalInstancesEach.put(category, (totalInstancesEach.get(category)+1));
		}
		for(String key : totalTimeEach.keySet()){
			map.put(key,(int) Math.floor(totalTimeEach.get(key)/totalInstancesEach.get(key)));
		}
		stats.setAverageMinutesPerIssueCategory(map);
		stats.setTotalMinutesPerIssueCategory(totalTimeEach);
	}
	public void setTimePerRegion(List<ExerciseInstance> exerciseInstances, Stats stats){
		Map<String, Long> totalTimeEach = new HashMap<String, Long>();
		Map<String, Integer> totalInstancesEach = new HashMap<String, Integer>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(ExerciseInstance ei : exerciseInstances){
			String region = ei.getRegion().toString();
			Integer dur = ei.getDuration();
			if(null==dur){
				dur = (int) getDateDiff(ei.getStartTime(),ei.getEndTime());
			}
			if(!totalTimeEach.containsKey(region)){
				totalTimeEach.put(region, (long) 0);
			}
			if(!totalInstancesEach.containsKey(region)){
				totalInstancesEach.put(region, 0);
			}
			totalTimeEach.put(region, (totalTimeEach.get(region)+dur));
			totalInstancesEach.put(region, (totalInstancesEach.get(region)+1));
		}
		for(String key : totalTimeEach.keySet()){

			map.put(key,(int) Math.floor(totalTimeEach.get(key)/totalInstancesEach.get(key)));
		}
		stats.setAverageMinutesPerRegion(map);
		stats.setTotalMinutesPerRegion(totalTimeEach);
	}
	public void setTimePerTeam(List<ExerciseInstance> exerciseInstances, Stats stats){
		Map<String, Long> totalTimeEach = new HashMap<String, Long>();
		Map<String, Integer> totalInstancesEach = new HashMap<String, Integer>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(ExerciseInstance ei : exerciseInstances){
			if(null==ei.getUser().getTeam())
				continue;
			String team = ei.getUser().getTeam().getName();
			Integer dur = ei.getDuration();
			if(null==dur){
				dur = (int) getDateDiff(ei.getStartTime(),ei.getEndTime());
			}
			if(!totalTimeEach.containsKey(team)){
				totalTimeEach.put(team, (long) 0);
			}
			if(!totalInstancesEach.containsKey(team)){
				totalInstancesEach.put(team, 0);
			}
			totalTimeEach.put(team, (totalTimeEach.get(team)+dur));
			totalInstancesEach.put(team, (totalInstancesEach.get(team)+1));
		}
		for(String key : totalTimeEach.keySet()){
			map.put(key,(int) Math.floor(totalTimeEach.get(key)/totalInstancesEach.get(key)));
		}
		stats.setAverageMinutesPerTeam(map);
		stats.setTotalMinutesPerTeam(totalTimeEach);
	}

	public Map<String,Map<String,Integer>> getRemediationRatePerRegion(List<ExerciseInstance> exerciseInstances){ 

		Map<String,Map<String,Integer>> irr = new HashMap<String,Map<String,Integer>>();

		for(ExerciseInstance ei : exerciseInstances){
			if(!irr.containsKey(ei.getRegion().toString())){
				Map<String,Integer> tmpMap = initializeMapWithExerciseResultStatus();
				irr.put(ei.getRegion().toString(), tmpMap);
			}
			Map<String,Integer> tmpStatus = irr.get(ei.getRegion().toString());
			for(ExerciseResult er : ei.getResults()){
				tmpStatus.put(er.getStatus().toString(), (tmpStatus.get(er.getStatus().toString())+1));
			}
		}
		return irr;
	}
	public Map<String,Map<String,Integer>> getRemediationRatePerTeam(List<ExerciseInstance> exerciseInstances){ 

		Map<String,Map<String,Integer>> irr = new HashMap<String,Map<String,Integer>>();

		for(ExerciseInstance ei : exerciseInstances){
			if(null==ei.getUser().getTeam())
				continue;
			if(!irr.containsKey(ei.getUser().getTeam().getName())){
				Map<String,Integer> tmpMap = initializeMapWithExerciseResultStatus();
				irr.put(ei.getUser().getTeam().getName(), tmpMap);
			}
			Map<String,Integer> tmpStatus = irr.get(ei.getUser().getTeam().getName());
			for(ExerciseResult er : ei.getResults()){
				tmpStatus.put(er.getStatus().toString(), (tmpStatus.get(er.getStatus().toString())+1));
			}
		}
		return irr;
	}
	public Map<String,Map<String,Integer>> getRemediationRatePerIssueCategory(List<ExerciseInstance> exerciseInstances){ 

		Map<String,Map<String,Integer>> irr = new HashMap<String,Map<String,Integer>>();

		for(ExerciseInstance ei : exerciseInstances){
			for(ExerciseResult er : ei.getResults()){
				if(!irr.containsKey(er.getCategory())){
					Map<String,Integer> tmpMap = initializeMapWithExerciseResultStatus();
					irr.put(er.getCategory(), tmpMap);
				}
				Map<String,Integer> tmpStatus = irr.get(er.getCategory());
				tmpStatus.put(er.getStatus().toString(), (tmpStatus.get(er.getStatus().toString())+1));
			}
		}
		return irr;
	}
	public Map<String,Map<String,Integer>> getRemediationRatePerIssue(List<ExerciseInstance> exerciseInstances){ 
		
		Map<String,Map<String,Integer>> irr = new HashMap<String,Map<String,Integer>>();
		
		for(ExerciseInstance ei : exerciseInstances){
			for(ExerciseResult er : ei.getResults()){
				if(!irr.containsKey(er.getName())){
					Map<String,Integer> tmpMap = initializeMapWithExerciseResultStatus();
					irr.put(er.getName(), tmpMap);
				}
				Map<String,Integer> tmpStatus = irr.get(er.getName());
				tmpStatus.put(er.getStatus().toString(), (tmpStatus.get(er.getStatus().toString())+1));
			}
		}
		return irr;
	}	
}