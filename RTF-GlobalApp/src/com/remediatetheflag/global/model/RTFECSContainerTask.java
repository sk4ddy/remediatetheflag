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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.amazonaws.regions.Regions;
import com.google.gson.annotations.Expose;
import com.remediatetheflag.global.messages.annotations.LazilySerialized;

@Entity(name = "RTFECSInstance")
@Table( name = "ecsInstances" )
public class RTFECSContainerTask {

	@Id
	@Column(name = "idInstance")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "cluster")
	private String cluster;

	@Column(name = "idContainerInstance")
	private String idContainerInstance;

	@Column(name = "taskArn")
	private String taskArn;

	@Column(name = "name")
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "region")
	private Regions region;

	@Column(name = "ipAddress")
	private String ipAddress;

	@Column(name = "rdpPort")
	private Integer rdpPort;

	@Column(name = "httpPort")
	private Integer httpPort;

	@Column(name = "createTime")
	private Date createTime;
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "shutdownTime")
	private Date actualShutdownTime;

	@Column(name = "status")
	private Integer status;

	@Expose
	@LazilySerialized
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	public Regions getRegion() {
		return region;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Regions getRegionId() {
		return region;
	}
	public void setRegion(Regions region) {
		this.region = region;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String privateIp) {
		this.ipAddress = privateIp;
	}
	public Date getShutdownTime() {
		return actualShutdownTime;
	}
	public void setShutdownTime(Date shutdownTime) {
		this.actualShutdownTime = shutdownTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public User getUser() {
		return user;

	}
	public void setUser(User user) {
		this.user = user;
	}
	public Integer getRdpPort() {
		return rdpPort;
	}
	public void setRdpPort(Integer rdpPort) {
		this.rdpPort = rdpPort;
	}
	public Integer getHttpPort() {
		return httpPort;
	}
	public void setHttpPort(Integer httpPort) {
		this.httpPort = httpPort;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getIdContainerInstance() {
		return idContainerInstance;
	}
	public void setIdContainerInstance(String idContainerInstance) {
		this.idContainerInstance = idContainerInstance;
	}
	public String getTaskArn() {
		return taskArn;
	}
	public void setTaskArn(String taskArn) {
		this.taskArn = taskArn;
	}

}
