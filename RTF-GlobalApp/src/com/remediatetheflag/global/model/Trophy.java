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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
@Table( name = "trophies" )
public class Trophy {

    @SerializedName("id")
    @Expose
    @Id
    @Column(name = "idTrophy")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idTrophy;
    
    @SerializedName("name")
    @Expose
    @Column(name="name", nullable = false)
    private String name;
   
    @SerializedName("description")
    @Expose
    @Column(name="description", nullable = false)
    private String description;
    
    @SerializedName("technology")
    @Expose
    @Column(name="technology", nullable = false)
    private String technology;
   
    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

	public Integer getIdTrophy() {
		return idTrophy;
	}

	public void setIdTrophy(Integer idTrophy) {
		this.idTrophy = idTrophy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}
}
