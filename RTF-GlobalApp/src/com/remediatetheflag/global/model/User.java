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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.remediatetheflag.global.messages.annotations.LeaderboardUser;
import com.remediatetheflag.global.messages.annotations.MemberUser;
import com.remediatetheflag.global.messages.annotations.TeamManager;
import com.remediatetheflag.global.messages.annotations.UserStatusList;;

@Entity(name = "User")
@Table( name = "users" )
public class User implements HttpSessionBindingListener {

	@Transient
	private static Logger logger = LoggerFactory.getLogger(User.class);

	@Id
	@TeamManager
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idUser")
	@MemberUser
	private Integer idUser;

	@Column(name="password", nullable = false)
	private String password;

	@Column(name="salt", nullable = false)
	private String salt;

	@Expose
	@SerializedName("r")
	@Column(name="role", nullable = false)
	private Integer role;

	@Enumerated(EnumType.STRING)
	@Expose
	@UserStatusList
	@Column(name="status", nullable = false)
	private UserStatus status;

	@LeaderboardUser
	@SerializedName("user")
	@Expose
	@Column(name="username", nullable = false, unique = true)
	private String username;

	@LeaderboardUser
	@SerializedName("score")
	@Column(name="score", nullable = false)
	private Integer score;
	
	@LeaderboardUser
	@SerializedName("exercisesRun")
	@Column(name="exercisesRun", nullable = false)
	private Integer exercisesRun;

	@SerializedName("firstName")
	@Expose
	@Column(name="firstName", nullable = false)
	private String firstName;

	@SerializedName("lastName")
	@Expose
	@Column(name="lastName", nullable = false)
	private String lastName;

	@SerializedName("email")
	@Expose
	@Column(name="email", nullable = false)
	private String email;

	@LeaderboardUser
	@SerializedName("country")
	@Expose
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "countryId" )
	private Country country;

	@SerializedName("team")
	@Expose
	@LeaderboardUser
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "teamId")
	private Team team;

	@SerializedName("instanceLimit")
	@Expose
	@Column(name="instanceLimit", nullable = false)
	private Integer instanceLimit;
	
	@Column(name="emailVerified")
	@Expose
	private Boolean emailVerified;
	
	@Column(name="forceChangePassword")
	@Expose
	private Boolean forceChangePassword;
	
	@Column(name="joinDateTime")
	@Expose
	private Date joinedDateTime;
	
	@Column(name="personalDataUpdateDateTime")
	@Expose
	private Date personalDataUpdateDateTime;
	
	@Column(name="personalDataAnonymisedDateTime")
	@Expose
	private Date personalDataAnonymisedDateTime;
	
	@SerializedName("organizations")
	@UserStatusList	
	@Expose
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Set<Organization> managedOrganizations = new HashSet<Organization>();

	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "defaultOrganizationId")
	@Expose
	@UserStatusList
	private Organization defaultOrganization;
	
	@Transient
	private static Map<User, HttpSession> logins = new HashMap<User, HttpSession>(); 

	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	public UserStatus getStatus() {
		return status;
	}
	public void setStatus(UserStatus status) {
		this.status = status;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((idUser == null) ? 0 : idUser.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (idUser == null) {
			if (other.idUser != null)
				return false;
		} else if (!idUser.equals(other.idUser))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		HttpSession session = logins.remove(this);
	
		if (session != null) {
			logger.debug("Security: double login detected for user: "+this.getUsername());
			session.invalidate();
		}
		logins.put(this, event.getSession());
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		logins.remove(this);
	}
	public Integer getInstanceLimit() {
		return instanceLimit;
	}
	public void setInstanceLimit(Integer limit) {
		this.instanceLimit = limit;
	}
	public Boolean getEmailVerified() {
		return emailVerified;
	}
	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}
	public Boolean getForceChangePassword() {
		return forceChangePassword;
	}
	public void setForceChangePassword(Boolean forceChangePassword) {
		this.forceChangePassword = forceChangePassword;
	}
	public Date getJoinedDateTime() {
		return joinedDateTime;
	}
	public void setJoinedDateTime(Date joinedDateTime) {
		this.joinedDateTime = joinedDateTime;
	}
	public Set<Organization> getManagedOrganizations() {
		return managedOrganizations;
	}
	public void setManagedOrganizations(Set<Organization> managedOrganizations) {
		this.managedOrganizations = managedOrganizations;
	}
	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", password=" + password + ", salt=" + salt + ", role=" + role + ", status="
				+ status + ", username=" + username + ", score=" + score + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", country=" + country + ", team=" + team
				+ ", instanceLimit=" + instanceLimit + ", emailVerified=" + emailVerified + ", forceChangePassword="
				+ forceChangePassword + ", joinedDateTime=" + joinedDateTime + ", organizations=" + managedOrganizations + "]";
	}
	public static Logger getLogger() {
		return logger;
	}
	public static void setLogger(Logger logger) {
		User.logger = logger;
	}
	public Organization getDefaultOrganization() {
		return defaultOrganization;
	}
	public void setDefaultOrganization(Organization defaultOrganization) {
		this.defaultOrganization = defaultOrganization;
	}
	public static Map<User, HttpSession> getLogins() {
		return logins;
	}
	public static void setLogins(Map<User, HttpSession> logins) {
		User.logins = logins;
	}
	public Integer getExercisesRun() {
		return exercisesRun;
	}
	public void setExercisesRun(Integer exercisesRun) {
		this.exercisesRun = exercisesRun;
	}
	public Date getPersonalDataUpdateDateTime() {
		return personalDataUpdateDateTime;
	}
	public void setPersonalDataUpdateDateTime(Date personalDataUpdateDateTime) {
		this.personalDataUpdateDateTime = personalDataUpdateDateTime;
	}
	public Date getPersonalDataAnonymisedDateTime() {
		return personalDataAnonymisedDateTime;
	}
	public void setPersonalDataAnonymisedDateTime(Date personalDataAnonymisedDateTime) {
		this.personalDataAnonymisedDateTime = personalDataAnonymisedDateTime;
	}

}
