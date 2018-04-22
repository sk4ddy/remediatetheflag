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

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.ContainerOverride;
import com.amazonaws.services.ecs.model.DescribeClustersRequest;
import com.amazonaws.services.ecs.model.DescribeClustersResult;
import com.amazonaws.services.ecs.model.DescribeContainerInstancesRequest;
import com.amazonaws.services.ecs.model.DescribeContainerInstancesResult;
import com.amazonaws.services.ecs.model.DescribeTasksRequest;
import com.amazonaws.services.ecs.model.DescribeTasksResult;
import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.ListTasksRequest;
import com.amazonaws.services.ecs.model.ListTasksResult;
import com.amazonaws.services.ecs.model.NetworkBinding;
import com.amazonaws.services.ecs.model.RunTaskRequest;
import com.amazonaws.services.ecs.model.RunTaskResult;
import com.amazonaws.services.ecs.model.StopTaskRequest;
import com.amazonaws.services.ecs.model.Task;
import com.amazonaws.services.ecs.model.TaskOverride;
import com.remediatetheflag.global.model.RTFECSContainerTask;
import com.remediatetheflag.global.model.RTFECSTaskDefinition;
import com.remediatetheflag.global.model.RTFInstanceReservation;
import com.remediatetheflag.global.model.User;

public class AWSHelper {

	private static Logger logger = LoggerFactory.getLogger(AWSHelper.class);

	public RTFInstanceReservation createRTFInstance(LaunchStrategy strategy){
		return strategy.launch();
	}

	public void terminateTask(String taskArn) {
		String regionFromArn = taskArn.split(":")[3];
		AmazonECS client = AmazonECSClientBuilder.standard().withRegion(regionFromArn).withCredentials(new DefaultAWSCredentialsProviderChain()).build();
		try {
			StopTaskRequest stopTaskRequest = new StopTaskRequest();
			stopTaskRequest.withCluster(RTFConfig.getExercisesCluster()).withTask(taskArn);
			client.stopTask(stopTaskRequest);
		} catch(Exception e){
			logger.error("Error terminating task "+e.getMessage());
		}
	}

	public void terminateTask(RTFECSContainerTask ecsInstance) {
		AmazonECS client = AmazonECSClientBuilder.standard().withRegion(ecsInstance.getRegion()).withCredentials(new DefaultAWSCredentialsProviderChain()).build();
		try {
			StopTaskRequest stopTaskRequest = new StopTaskRequest();
			stopTaskRequest.withCluster(RTFConfig.getExercisesCluster()).withTask(ecsInstance.getTaskArn());
			client.stopTask(stopTaskRequest);
		} catch(Exception e){
			logger.error("Error terminating task "+e.getMessage());
		}
	}

	public Integer getClusterContainerInstances(Region region) {
		AmazonECS client = AmazonECSClientBuilder.standard().withRegion(region.getName()).withCredentials(new DefaultAWSCredentialsProviderChain()).build();
		DescribeClustersRequest request = new DescribeClustersRequest();
		request.withClusters(RTFConfig.getExercisesCluster());
		try {
			DescribeClustersResult response = client.describeClusters(request);
			return response.getClusters().get(0).getRegisteredContainerInstancesCount();
		}
		catch(Exception e){
			logger.error("Error getClusterContainerInstances "+e.getMessage());
			return 0;
		}
	}

	public Double getClusterMemoryReservation(Region region) {
		AmazonCloudWatch client = AmazonCloudWatchClientBuilder.standard().withRegion(region.getName()).withCredentials(new DefaultAWSCredentialsProviderChain()).build();

		Dimension dimension = new Dimension();
		dimension.setName("ClusterName");
		dimension.setValue(RTFConfig.getExercisesCluster());
		Date date= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, -5);
		GetMetricStatisticsRequest request= new GetMetricStatisticsRequest()
				.withMetricName("MemoryReservation")
				.withDimensions(dimension)
				.withPeriod(60)
				.withStartTime(cal.getTime())
				.withEndTime(date)
				.withStatistics("Average")
				.withNamespace("AWS/ECS");
		try {
			GetMetricStatisticsResult response = client.getMetricStatistics(request);
			if(response.getDatapoints().isEmpty())
				return 0.0;
			return response.getDatapoints().get(0).getAverage();
		}catch(Exception e){
			logger.error("Error getClusterContainerInstances "+e.getMessage());
			return 0.0;
		}
	}

	protected RTFECSContainerTask createInstance(String clusterName, String instanceName, String password, RTFECSTaskDefinition taskDef, Integer duration, User user){

		AmazonECS client = AmazonECSClientBuilder.standard().withRegion(taskDef.getRegion()).withCredentials(new DefaultAWSCredentialsProviderChain()).build();
		TaskOverride overrides = new TaskOverride();
		List<ContainerOverride> containerOverrides = new LinkedList<ContainerOverride>();
		ContainerOverride co = new ContainerOverride();
		List<KeyValuePair> environment  = new LinkedList<KeyValuePair>();
		KeyValuePair kv = new KeyValuePair();
		kv.setName(Constants.ENV_USR_PWD);
		kv.setValue(password);
		environment.add(kv);
		co.setEnvironment(environment);
		co.setName(taskDef.getContainerName());
		containerOverrides.add(co);
		overrides.setContainerOverrides(containerOverrides);	
		RunTaskRequest request = new RunTaskRequest().withCluster(clusterName).withTaskDefinition(taskDef.getTaskDefinitionName()).withOverrides(overrides);
		try {
			RunTaskResult response = client.runTask(request);
			Task task = response.getTasks().get(0);
			RTFECSContainerTask rtfInstance = new RTFECSContainerTask();
			rtfInstance.setCluster(task.getClusterArn());
			rtfInstance.setTaskArn(task.getTaskArn());
			rtfInstance.setIdContainerInstance(task.getContainerInstanceArn());
			rtfInstance.setTaskDefinition(taskDef);		
			rtfInstance.setName(instanceName);
			rtfInstance.setRegion(taskDef.getRegion());
			rtfInstance.setUser(user);
			rtfInstance.setCreateTime(task.getCreatedAt());
			rtfInstance.setStatus(Constants.STATUS_PENDING);
			logger.debug("# ECS Task "+instanceName+" created for user "+user.getIdUser()+" start: "+rtfInstance.getCreateTime());
			return rtfInstance;
		}catch(Exception e) {
			logger.warn("# ECS Task "+instanceName+" could not be created for user "+user.getIdUser()+" "+e.getMessage());
			return null;
		}
	}

	public List<String> getRunningECSTasks(List<Region> activeRegions){
		LinkedList<String> list = new LinkedList<String>();
		for(Region region : activeRegions) {
			AmazonECS client = AmazonECSClientBuilder.standard().withRegion(Regions.fromName(region.getName())).withCredentials(new DefaultAWSCredentialsProviderChain()).build();
			ListTasksRequest request = new ListTasksRequest().withCluster(RTFConfig.getExercisesCluster());
			try {
				ListTasksResult response = client.listTasks(request);
				list.addAll(response.getTaskArns());
			}catch(Exception e) {
				logger.error("Error getRunningECSTasks "+e.getMessage());
			}
		}
		return list;
	}
	
	public Date getRunningECSTaskStartTime(String taskArn){
		String regionFromArn = taskArn.split(":")[3];
		AmazonECS client = AmazonECSClientBuilder.standard().withRegion(regionFromArn).withCredentials(new DefaultAWSCredentialsProviderChain()).build();
		DescribeTasksRequest request = new DescribeTasksRequest().withCluster(RTFConfig.getExercisesCluster()).withTasks(taskArn);
		try {
			DescribeTasksResult response = client.describeTasks(request);
			return response.getTasks().get(0).getCreatedAt();
		}catch(Exception e) {
			logger.error("Could not get creation time for task arn "+taskArn);
			return null;
		}
	}

	public RTFInstanceReservation pollReservation(RTFInstanceReservation reservation) {

		if(null==reservation.getEcs()) {
			reservation.setError(true);
			reservation.setFulfilled(false);
			reservation.setWaitSeconds(0);
			return reservation;
		}
		AmazonECS client = AmazonECSClientBuilder.standard().withRegion(reservation.getEcs().getRegion()).withCredentials(new DefaultAWSCredentialsProviderChain()).build();

		DescribeTasksRequest request = new DescribeTasksRequest().withCluster(RTFConfig.getExercisesCluster()).withTasks(reservation.getEcs().getTaskArn());
		try {
			DescribeTasksResult response = client.describeTasks(request);

			if(response.getTasks().isEmpty()) {
				reservation.setError(true);
				reservation.setFulfilled(false);
				reservation.setWaitSeconds(0);
				return reservation;
			}
			Integer rdpPort = -1;
			Integer httpPort = -1;

			Task task =  response.getTasks().get(0);

			List<NetworkBinding> nb = task.getContainers().get(0).getNetworkBindings();
			if(nb.size()>2) {
				logger.warn("More than two port bindings, only RDP 3389 and HTTP 8080 will be mapped");
			}
			for(NetworkBinding b : nb) {
				if(b.getContainerPort().equals(3389)) {
					rdpPort = b.getHostPort();
				}
				else if(b.getContainerPort().equals(8080)) {
					httpPort = b.getHostPort();
				}
			}
			if(rdpPort== -1 || httpPort == -1) {
				reservation.setFulfilled(false);
				reservation.setError(false);
				reservation.setWaitSeconds(10);
				return reservation;
			}
			reservation.getEcs().setHttpPort(httpPort);
			reservation.getEcs().setRdpPort(rdpPort);

			String containerInstanceId = task.getContainerInstanceArn();
			DescribeContainerInstancesRequest containerRequest = new DescribeContainerInstancesRequest().withCluster(RTFConfig.getExercisesCluster()).withContainerInstances(containerInstanceId);
			DescribeContainerInstancesResult containerResponse = client.describeContainerInstances(containerRequest);

			if(containerResponse.getContainerInstances().isEmpty()) {
				reservation.setFulfilled(false);
				reservation.setError(true);
				reservation.setWaitSeconds(0);
				return reservation;
			}
			String ec2InstanceId = containerResponse.getContainerInstances().get(0).getEc2InstanceId();

			AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(reservation.getEcs().getRegion()).withCredentials(new DefaultAWSCredentialsProviderChain()).build();

			DescribeInstancesRequest instanceRequest = new DescribeInstancesRequest().withInstanceIds(ec2InstanceId);
			DescribeInstancesResult instanceInstances = ec2.describeInstances(instanceRequest);

			if(instanceInstances.getReservations().isEmpty() || instanceInstances.getReservations().get(0).getInstances().isEmpty()) {
				reservation.setFulfilled(false);
				reservation.setError(true);
				reservation.setWaitSeconds(0);
				return reservation;
			}

			String ipAddress = instanceInstances.getReservations().get(0).getInstances().get(0).getPrivateIpAddress();
			reservation.getEcs().setIpAddress(ipAddress);
			reservation.setFulfilled(true);
			reservation.setError(false);
			reservation.setWaitSeconds(0);		
			return reservation;

		}catch(Exception e) {
			logger.error("Error pollReservation "+e.getMessage());
			reservation.setError(true);
			reservation.setFulfilled(false);
			reservation.setWaitSeconds(0);
			return reservation;
		}
	}
}