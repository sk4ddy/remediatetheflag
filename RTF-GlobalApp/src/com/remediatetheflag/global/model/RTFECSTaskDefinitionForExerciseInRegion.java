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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.amazonaws.regions.Regions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity( name = "ECSTaskDefinitionForExerciseInRegion" )
@Table( name = "taskDefinitionsForExerciseInRegion" )
public class RTFECSTaskDefinitionForExerciseInRegion {

	@Id
	@Column(name = "idTaskDefinitionForExerciseInRegion")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@SerializedName("taskDefinition")
	@Expose
	@Cascade({CascadeType.ALL})
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "taskDefinition")
	private RTFECSTaskDefinition taskDefinition;

    @Enumerated(EnumType.STRING)
	@Column(name="region")
	@Expose
	private Regions region;

	@SerializedName("exercise")
	@Cascade({CascadeType.SAVE_UPDATE})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exerciseId")
	private AvailableExercise exercise;
	
	@Column(name = "active")
	@Expose
	private Boolean active;

	public RTFECSTaskDefinition getTaskDefinition() {
		return taskDefinition;
	}
	public void setTaskDefinition(RTFECSTaskDefinition image) {
		this.taskDefinition = image;
	}
	public Regions getRegion() {
		return region;
	}
	public void setRegion(Regions region) {
		this.region = region;
	}
	public AvailableExercise getExercise() {
		return exercise;
	}
	public void setExercise(AvailableExercise exercise) {
		this.exercise = exercise;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
}