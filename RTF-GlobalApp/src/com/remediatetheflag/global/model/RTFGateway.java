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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.amazonaws.regions.Regions;

@Entity(name = "RTFGateway")
@Table( name = "rtfGateways" )
public class RTFGateway {
	
	@Id
	@Column(name = "idGateway")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
    @Enumerated(EnumType.STRING)
	@Column(name = "region")
	private Regions region;
	
	@Column(name = "fqdn")
	private String fqdn;
	
	@Column(name = "active")
	private Boolean active;
	
	public Boolean isActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
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
	public String getFqdn() {
		return fqdn;
	}
	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}
	public Regions getRegion() {
		return region;
	}
	public void setRegion(Regions region) {
		this.region = region;
	}
}
