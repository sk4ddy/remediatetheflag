/*
 *  
w * REMEDIATE THE FLAG
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.remediatetheflag.global.messages.annotations.BriefDetails;
import com.remediatetheflag.global.messages.annotations.UserStatusList;

@Entity( name = "Organization" )
@Table( name = "organizations" )
public class Organization {

	@Id
	@Column(name = "id")
	@Expose
	@UserStatusList
	@BriefDetails
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "name", unique = true)
	@SerializedName("name")
	@Expose
	@BriefDetails
	@UserStatusList
	private String name;
	
	@Column(name = "dateJoined")
	@SerializedName("dateJoined")
	private Date dateJoined;
	
	@Column(name = "status")
	@SerializedName("status")
	private OrganizationStatus status;
	
	@Column(name = "maxUsers")
	@SerializedName("maxUsers")
	private Integer maxUsers;
	
	@Column(name = "email")
	@SerializedName("email")
	private String email;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDateJoined() {
		return dateJoined;
	}
	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}
	public Integer getMaxUsers() {
		return maxUsers;
	}
	public void setMaxUsers(Integer maxUsers) {
		this.maxUsers = maxUsers;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public OrganizationStatus getStatus() {
		return status;
	}
	public void setStatus(OrganizationStatus status) {
		this.status = status;
	}
}
