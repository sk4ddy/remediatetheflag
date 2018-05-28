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
package com.remediatetheflag.global.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.amazonaws.regions.Regions;
import com.google.gson.annotations.Expose;

@Entity(name = "RTFECSTaskDefinition")
@Table( name = "ecsTaskDefinitions" )
public class RTFECSTaskDefinition {

	@Id
	@Column(name = "idImage")
	@Expose
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "taskDefinitionName")
	@Expose
	private String taskDefinitionName;
	
	@Column(name = "taskDefinitionArn")
	private String taskDefinitionArn;
	
	@Column(name = "containerName")
	@Expose
	private String containerName;
	
	@Column(name = "repositoryImageUrl")
	@Expose
	private String repositoryImageUrl;
	
	@Column(name = "softMemoryLimit")
	@Expose
	private Integer softMemoryLimit;
	
	@Column(name = "hardMemoryLimit")
	@Expose
	private Integer hardMemoryLimit;
	
    @Enumerated(EnumType.STRING)
	@Column(name = "region")
	private Regions region;

	@Column(name = "updateDate")
	@Expose
	private Date updateDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTaskDefinitionName() {
		return taskDefinitionName;
	}

	public void setTaskDefinitionName(String name) {
		this.taskDefinitionName = name;
	}

	public Regions getRegion() {
		return region;
	}

	public void setRegion(Regions region) {
		this.region = region;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getRepositoryImageUrl() {
		return repositoryImageUrl;
	}

	public void setRepositoryImageUrl(String repositoryImageUrl) {
		this.repositoryImageUrl = repositoryImageUrl;
	}

	public Integer getSoftMemoryLimit() {
		return softMemoryLimit;
	}

	public void setSoftMemoryLimit(Integer softMemoryLimit) {
		this.softMemoryLimit = softMemoryLimit;
	}

	public Integer getHardMemoryLimit() {
		return hardMemoryLimit;
	}

	public void setHardMemoryLimit(Integer hardMemoryLimit) {
		this.hardMemoryLimit = hardMemoryLimit;
	}

	public String getTaskDefinitionArn() {
		return taskDefinitionArn;
	}

	public void setTaskDefinitionArn(String taskDefinitionArn) {
		this.taskDefinitionArn = taskDefinitionArn;
	}


	
	
}