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
package com.remediatetheflag.global.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;
import com.remediatetheflag.global.model.AchievedTrophy;
import com.remediatetheflag.global.model.AvailableExercise;
import com.remediatetheflag.global.model.AvailableExerciseInfo;
import com.remediatetheflag.global.model.AvailableExerciseStatus;
import com.remediatetheflag.global.model.AvailableExercisesForOrganization;
import com.remediatetheflag.global.model.Challenge;
import com.remediatetheflag.global.model.ChallengeStatus;
import com.remediatetheflag.global.model.Country;
import com.remediatetheflag.global.model.ExerciseInstance;
import com.remediatetheflag.global.model.ExerciseResult;
import com.remediatetheflag.global.model.ExerciseResultFile;
import com.remediatetheflag.global.model.ExerciseScore;
import com.remediatetheflag.global.model.ExerciseStatus;
import com.remediatetheflag.global.model.Feedback;
import com.remediatetheflag.global.model.Flag;
import com.remediatetheflag.global.model.FlagQuestion;
import com.remediatetheflag.global.model.GuacTempUser;
import com.remediatetheflag.global.model.InvitationCodeForOrganization;
import com.remediatetheflag.global.model.Notification;
import com.remediatetheflag.global.model.Organization;
import com.remediatetheflag.global.model.RTFECSContainerTask;
import com.remediatetheflag.global.model.RTFECSTaskDefinition;
import com.remediatetheflag.global.model.RTFECSTaskDefinitionForExerciseInRegion;
import com.remediatetheflag.global.model.RTFGateway;
import com.remediatetheflag.global.model.RTFInstanceReservation;
import com.remediatetheflag.global.model.SupportedAWSRegion;
import com.remediatetheflag.global.model.Team;
import com.remediatetheflag.global.model.Trophy;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.model.UserAuthenticationEvent;
import com.remediatetheflag.global.model.UserFailedLogins;
import com.remediatetheflag.global.model.UserStatus;
import com.remediatetheflag.global.utils.Constants;
import com.remediatetheflag.global.utils.RandomGenerator;

public class HibernatePersistenceFacade {
	private static Logger logger = LoggerFactory.getLogger(HibernatePersistenceFacade.class);

	private synchronized static HibernateSessionTransactionWrapper openSessionTransaction() {
		HibernatePersistenceFacade pf = new HibernatePersistenceFacade();
		HibernateSessionTransactionWrapper hb = pf.new HibernateSessionTransactionWrapper();
		hb.localSession = getHibernateSession();
		hb.localTransaction = hb.localSession.getTransaction();
		hb.localTransaction.begin();
		return hb;
	}
	private synchronized static void closeSessionTransaction(HibernateSessionTransactionWrapper hb) {
		hb.localTransaction.commit();
		hb.localSession.close();	
	}
	private class HibernateSessionTransactionWrapper {
		Session localSession;
		Transaction localTransaction;
	}
	private static synchronized EntityManager getHibernateEntityManager() {
		return HibernatePersistenceSingleton.getSessionFactory().createEntityManager();
	}
	private static synchronized Session getHibernateSession() {
		Session localSession = HibernatePersistenceSingleton.getSessionFactory().openSession();
		return localSession;
	}

	public AvailableExerciseInfo getAvailableExerciseInfo(Integer id) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			AvailableExerciseInfo info = hb.localSession.get( AvailableExerciseInfo.class, id );
			closeSessionTransaction(hb);
			return info;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}

	public Boolean updateAvailableExerciseInfo(AvailableExerciseInfo updated) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			AvailableExerciseInfo info = hb.localSession.get( AvailableExerciseInfo.class, updated.getId() );
			if(null!=updated.getDescription())
				info.setDescription(updated.getDescription());
			if(null!=updated.getImage())
				info.setImage(updated.getImage());
			if(null!=updated.getInfoOrder())
				info.setInfoOrder(updated.getInfoOrder());
			if(null!=updated.getTitle())
				info.setTitle(updated.getTitle());
			hb.localSession.update(info);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}

	public Integer addUser(User user){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Integer id = (Integer) hb.localSession.save( user );
			closeSessionTransaction(hb);
			return id;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<User> getManagementAllUsers(Set<Organization> organizations){
		List<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();	
		try {
			List<User> users = hb.localSession.createQuery("select distinct(u) from User u "
					+ "where u.defaultOrganization.id in (:names) and u.status != :removed")
					.setParameterList("names", ids)
					.setParameter("removed", UserStatus.REMOVED)
					.getResultList();
			closeSessionTransaction(hb);
			return users;
		}catch(Exception e){	
			logger.error(e.getMessage());
			closeSessionTransaction(hb);
			return new LinkedList<User>();
		}
	}

	public User getUser(String username, String password){		
		EntityManager em =  getHibernateEntityManager();
		User user;
		try{
			user = (User) em.createNativeQuery(
					"SELECT * FROM users WHERE username = :usr AND password = SHA2(CONCAT(:pwd,(SELECT salt FROM users WHERE username = :usr)),512)", User.class )
					.setParameter("usr", username)
					.setParameter("pwd", password)
					.getSingleResult();
			em.close();
			return user;
		}catch(Exception e){
			em.close();
			logger.error("Login Failed for "+username+": "+e.getMessage());
			return null;
		}
	}
	public User getUserFromUserId(Integer userId){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			User user = (User) hb.localSession.createQuery( "from User where idUser = :usrId")
					.setParameter( "usrId", userId )
					.getSingleResult();
			closeSessionTransaction(hb);
			return user;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public User getUserFromUsername(String username) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			User user = (User) hb.localSession.createQuery( "from User where username = :username")
					.setParameter( "username", username )
					.getSingleResult();
			closeSessionTransaction(hb);
			return user;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public User getUserFromUsername(String username, Set<Organization> organizations){
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			User user = (User) hb.localSession.createQuery( "select u from User u "
					+ "where u.username = :username "
					+ "and u.defaultOrganization.id in (:names)")
					.setParameter( "username", username)
					.setParameterList("names", ids)
					.getSingleResult();
			closeSessionTransaction(hb);
			return user;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}

	public boolean updateGateway(Integer id, RTFGateway g) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			RTFGateway o = hb.localSession.get( RTFGateway.class, id );
			o.setActive(g.isActive());
			o.setFqdn(g.getFqdn());
			o.setName(g.getName());
			hb.localSession.update(o);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}

	public boolean addManagedOrganization(User usr, Integer id) {
		User user = getUserFromUserId(usr.getIdUser());
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Organization o = hb.localSession.get( Organization.class, id );
			user.getManagedOrganizations().add(o);
			hb.localSession.update(user);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}


	public Boolean addUserToTeam(User usr, Team team) {
		User user = getUserFromUserId(usr.getIdUser());
		user.setTeam(team);
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			hb.localSession.update(user);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Boolean renameTeam(Integer teamId, String name) {
		Team oldTeam = getTeam(teamId);
		oldTeam.setName(name);
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			hb.localSession.update(oldTeam);
			closeSessionTransaction(hb);
			return true;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Boolean addToTeamManager(Integer teamId, User user) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		Team team = hb.localSession.get( Team.class, teamId );
		User uDb = hb.localSession.get( User.class, user.getIdUser() );
		List<User> managers = new LinkedList<User>();
		managers.addAll(team.getManagers());
		managers.add(uDb);
		team.getManagers().clear();
		team.getManagers().addAll(managers);
		try {
			hb.localSession.update(team);
			closeSessionTransaction(hb);
			return true;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Boolean removeFromTeamManager(Integer teamId, User user) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		Team team = hb.localSession.get( Team.class, teamId );
		List<User> managers = new LinkedList<User>();
		for(User u : team.getManagers()){
			if(!u.getIdUser().equals(user.getIdUser())){
				managers.add(u);
			}
		}
		team.getManagers().clear();
		team.getManagers().addAll(managers);
		try {
			hb.localSession.update(team);
			closeSessionTransaction(hb);
			return true;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Boolean updateUserInfo(User user){
		User oldUser = getUserFromUserId(user.getIdUser());
		oldUser.setCountry(user.getCountry());
		oldUser.setEmail(user.getEmail());
		oldUser.setFirstName(user.getFirstName());
		oldUser.setLastName(user.getLastName());
		oldUser.setEmailVerified(user.getEmailVerified());
		oldUser.setInstanceLimit(user.getInstanceLimit());
		oldUser.setForceChangePassword(user.getForceChangePassword());
		oldUser.setScore(user.getScore());
		oldUser.setPersonalDataAnonymisedDateTime(user.getPersonalDataAnonymisedDateTime());
		oldUser.setPersonalDataUpdateDateTime(user.getPersonalDataUpdateDateTime());
		oldUser.setTeam(user.getTeam());
		oldUser.setUsername(user.getUsername());
		oldUser.setStatus(user.getStatus());
		oldUser.setExercisesRun(user.getExercisesRun());
		oldUser.setRole(user.getRole());
		oldUser.setManagedOrganizations(user.getManagedOrganizations());
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			hb.localSession.update(oldUser);
			closeSessionTransaction(hb);
			return true;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Integer updateAvailableExercise(AvailableExercise ei) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			hb.localSession.update( ei );
			closeSessionTransaction(hb);
			return ei.getId();
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Boolean updateUserPassword(Integer idUser, String newPwd) {
		String salt = RandomGenerator.getNextSalt();
		String pwd = DigestUtils.sha512Hex(newPwd.concat(salt)); 
		User user = getUserFromUserId(idUser);
		user.setSalt(salt);
		user.setPassword(pwd);
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			hb.localSession.update(user);
			closeSessionTransaction(hb);
			return true;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Integer addAvailableExercise(AvailableExercise exercise) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Integer id = (Integer) hb.localSession.save( exercise );
			closeSessionTransaction(hb);
			return id;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<AvailableExercise> getAllAvailableExercises(){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<AvailableExercise> exercises = hb.localSession.createQuery("from AvailableExercise").getResultList();
			closeSessionTransaction(hb);
			return exercises;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<AvailableExercisesForOrganization> getAllAvailableExercisesForOrganization(Set<Organization> organizations){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		LinkedList<Integer> orgList = new LinkedList<Integer>();
		for(Organization o : organizations){
			orgList.add(o.getId());
		}
		try {
			List<AvailableExercisesForOrganization> exercises = hb.localSession.createQuery("from AvailableExercisesForOrganization a "
					+ "where a.organization.id in :org")
					.setParameterList( "org", orgList )
					.getResultList();
			closeSessionTransaction(hb);
			return exercises;
		}catch(Exception e ) {
			logger.error(e.getMessage());
			return new LinkedList<AvailableExercisesForOrganization>();
		}
	}
	public AvailableExercise getExerciseByName(String name) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			AvailableExercise exercise = (AvailableExercise) hb.localSession.createQuery("from AvailableExercise where title = :title")
					.setParameter("title", name)
					.getSingleResult();
			closeSessionTransaction(hb);
			return exercise;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<AvailableExercise> getAllAvailableExercises(Set<Organization> organizations){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		LinkedList<Integer> orgList = new LinkedList<Integer>();
		for(Organization o : organizations){
			orgList.add(o.getId());
		}
		try {
			List<AvailableExercise> exercises = hb.localSession.createQuery("select a.exercises from AvailableExercisesForOrganization a "
					+ "where a.organization.id in :org")
					.setParameterList( "org", orgList )
					.getResultList();
			closeSessionTransaction(hb);
			return exercises;
		}catch(Exception e ) {
			logger.error(e.getMessage());
			return new LinkedList<AvailableExercise>();
		}
	}
	public AvailableExercise getAvailableExercise(Integer exerciseId) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			AvailableExercise exercise = hb.localSession.get( AvailableExercise.class, exerciseId );
			closeSessionTransaction(hb);
			return exercise;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	public AvailableExercise getAvailableExercise(Integer exerciseId, Organization org) {
		if(isExerciseEnabledForOrganization(org.getId(),exerciseId)) {
			return getAvailableExercise(exerciseId);
		}
		return null;
	}
	public AvailableExercise getAvailableExerciseWithSolution(Integer idExercise) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			AvailableExercise exercise = hb.localSession.get( AvailableExercise.class, idExercise );
			Hibernate.initialize(exercise.getSolutionFile());
			closeSessionTransaction(hb);
			return exercise;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	public AvailableExercise getAvailableExerciseWithReferenceFile(Integer idExercise,Organization org) {
		if(isExerciseEnabledForOrganization(org.getId(),idExercise)) {
			return getAvailableExerciseWithReferenceFile(idExercise);
		}
		return null;
	}
	public AvailableExercise getAvailableExerciseWithReferenceFile(Integer idExercise) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			AvailableExercise exercise = hb.localSession.get( AvailableExercise.class, idExercise );
			Hibernate.initialize(exercise.getReferenceFile());
			closeSessionTransaction(hb);
			return exercise;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<AvailableExercise> getAllAvailableExercisesWithFlags(Set<Organization> orgs) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<AvailableExercise> exercises = hb.localSession.createQuery("from AvailableExercise").getResultList();
			for(AvailableExercise exercise : exercises){
				for(Flag f : exercise.getFlags()){
					Hibernate.initialize(f);
				}	
			}
			closeSessionTransaction(hb);
			return exercises;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return new LinkedList<AvailableExercise>();
		}
	}
	public AvailableExercise getAvailableExerciseDetails(Integer idExercise, Organization org) {
		if(isExerciseEnabledForOrganization(org.getId(),idExercise)) {
			return getAvailableExerciseDetails(idExercise);
		}
		return null;
	}
	public AvailableExercise getAvailableExerciseDetails(Integer exerciseId) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		AvailableExercise exercise = hb.localSession.get( AvailableExercise.class, exerciseId );
		if(null==exercise || (!exercise.getStatus().equals(AvailableExerciseStatus.AVAILABLE) && !exercise.getStatus().equals(AvailableExerciseStatus.UPDATED))){
			return null;
		}
		for(AvailableExerciseInfo i : exercise.getInfoList()){
			Hibernate.initialize(i);
		}	
		for(Flag f : exercise.getFlags()){
			Hibernate.initialize(f);
		}	
		closeSessionTransaction(hb);
		return exercise;
	}

	@SuppressWarnings("unchecked")
	public List<Country> getAllCountries(){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<Country> countries = hb.localSession.createQuery("from Country").getResultList();
			closeSessionTransaction(hb);
			return countries;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}

	public Country getCountryFromCode(String code) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Country country =  (Country) hb.localSession.createQuery("from Country where shortName = :code")
					.setParameter("code", code)
					.getSingleResult();
			closeSessionTransaction(hb);
			return country;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<SupportedAWSRegion> getAllSupportedAWSRegions(){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<SupportedAWSRegion> gws = hb.localSession.createQuery("from SupportedAWSRegion").getResultList();
			closeSessionTransaction(hb);
			return gws;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<RTFGateway> getAllGateways(){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<RTFGateway> gws = hb.localSession.createQuery("from RTFGateway").getResultList();
			closeSessionTransaction(hb);
			return gws;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<RTFGateway> getAllActiveGateways(){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<RTFGateway> gws = hb.localSession.createQuery("from RTFGateway g where g.active is true").getResultList();
			closeSessionTransaction(hb);
			return gws;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	public Integer addGateway(RTFGateway gw){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Integer id = (Integer) hb.localSession.save( gw );
			closeSessionTransaction(hb);
			return id;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	public RTFGateway getGatewayForRegion(Regions region) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			RTFGateway gw = (RTFGateway) hb.localSession.createQuery(
					"from RTFGateway g " +
					"where g.region = :reg and g.active is true")
					.setParameter( "reg", region )
					.getSingleResult();
			closeSessionTransaction(hb);
			return gw;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	public Integer managementAddAchievedTrophy(AchievedTrophy achievedTrophy){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Integer id = (Integer) hb.localSession.save( achievedTrophy );
			closeSessionTransaction(hb);
			return id;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<AchievedTrophy> getAllAchievedTropies(){
		EntityManager em = getHibernateEntityManager();
		try {
			List<AchievedTrophy> achievedTrophies = em.createQuery(
					"select at "+
							"from AchievedTrophy at "+
					"left join fetch at.user")
					.getResultList();
			em.close();
			return achievedTrophies;
		}catch(Exception e){	
			em.close();
			logger.error(e.getMessage());		
			return new LinkedList<AchievedTrophy>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<AchievedTrophy> getAllAchievedTropiesForUser(Integer idUser){
		EntityManager em = getHibernateEntityManager();
		try {
			List<AchievedTrophy> achievedTrophies = em.createQuery(
					"select at "+
							"from AchievedTrophy at "+
							"left join fetch at.user " +
					"where at.user.idUser = :idUsr")
					.setParameter( "idUsr", idUser )
					.getResultList();
			em.close();
			return achievedTrophies;
		}catch(Exception e){	
			em.close();
			logger.error(e.getMessage());		
			return new LinkedList<AchievedTrophy>();
		}
	}
	public Integer addTrophy(Trophy trophy){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Integer id = (Integer) hb.localSession.save( trophy );
			closeSessionTransaction(hb);
			return id;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<Trophy> getAllTrophies(){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<Trophy> trophies = hb.localSession.createQuery("from Trophy" ).getResultList();
			closeSessionTransaction(hb);
			return trophies;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return new LinkedList<Trophy>();
		}
	}
	public Trophy getTrophy(Integer trophyId) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Trophy trophy = hb.localSession.get( Trophy.class, trophyId );
			closeSessionTransaction(hb);
			return trophy;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	public Flag getFlagWithHints(Integer idFlag) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Flag flag = hb.localSession.get( Flag.class, idFlag );
			for(FlagQuestion fq : flag.getFlagQuestionList()){
				Hibernate.initialize(fq.getHint());
			}	
			closeSessionTransaction(hb);
			return flag;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public Boolean deleteGateway(Integer id) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			RTFGateway gw = (RTFGateway) hb.localSession.load(RTFGateway.class,id);
			List<RTFECSTaskDefinition> ecsTasks = hb.localSession.createQuery("from RTFECSTaskDefinition where region = :reg")
					.setParameter("reg", gw.getRegion())
					.getResultList();
			if(ecsTasks.size()>0)
				return false;
			hb.localSession.delete(gw);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return false;
		}
	}
	public Boolean removeAvailableExercise(Integer idExercise) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			AvailableExercise ex = (AvailableExercise) hb.localSession.load(AvailableExercise.class,idExercise);
			hb.localSession.delete(ex);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public Boolean deleteOrganization(Integer idOrg) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Organization o = (Organization) hb.localSession.load(Organization.class,idOrg);
			hb.localSession.delete(o);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			logger.error("Trying to remove org from users managed organizations due to:\n" +e.getMessage());		
			List<User> users = new LinkedList<User>();
			hb = openSessionTransaction();
			try {
				Organization o = (Organization) hb.localSession.load(Organization.class,idOrg);
				users = hb.localSession.createQuery("select u from User u inner join u.managedOrganizations managedOrgs "
						+ "where managedOrgs.id = :orgId ")
						.setParameter( "orgId", o.getId() )
						.getResultList();
				for(User u : users) {
					for(Organization org : u.getManagedOrganizations()) {
						if(org.getId().equals(idOrg)) {
							u.getManagedOrganizations().remove(org);
							break;
						}
					}
					hb.localSession.update(u);
				}
				logger.error("Managing users removed, trying to remove org again...");		
				hb.localSession.delete(o);
				closeSessionTransaction(hb);
				logger.error("Removal was successful...");		
				return true;
			}catch(Exception innerException){	
				logger.error("Removal unsuccessful, rolling back users managed organizations due to:\n" +innerException.getMessage());		
				hb = openSessionTransaction();
				try {
					Organization dbOrg = (Organization) hb.localSession.load(Organization.class,idOrg);
					for(User u : users) {
						u.getManagedOrganizations().add(dbOrg);
						hb.localSession.update(u);
					}			
					closeSessionTransaction(hb);
					return false;
				}catch(Exception rollExp) {
					closeSessionTransaction(hb);
					return false;
				}
			}
		}
	}

	public Boolean deleteFlag(Integer idFlag) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Flag myObject = (Flag) hb.localSession.load(Flag.class,idFlag);
			hb.localSession.delete(myObject);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return false;
		}
	}
	public Boolean deleteTeam(Team team) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		Team myObject = (Team) hb.localSession.load(Team.class,team.getIdTeam());
		myObject.setManagers(null);
		try {
			hb.localSession.update(myObject);
			Team updatedTeam = (Team) hb.localSession.load(Team.class,team.getIdTeam());
			hb.localSession.delete(updatedTeam);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());		
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public List<Challenge> getAllExpiredChallenges() {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<Challenge> ei = hb.localSession.createQuery("from Challenge where status != :expired and endDate < :now")
					.setParameter("expired", ChallengeStatus.FINISHED)
					.setParameter("now", new Date())
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		}
		catch(Exception e) {
			logger.error(e.getMessage());
			closeSessionTransaction(hb);
			return new LinkedList<Challenge>();
		}
	}
	@SuppressWarnings("unchecked")
	public boolean isExerciseInChallengeForUser(Integer idExercise, Integer idUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		List<Challenge> cList;
		try{
			cList = hb.localSession.createQuery("select c from Challenge c inner join c.users usrs where usrs.idUser = :usrId ")
					.setParameter( "usrId", idUser )
					.getResultList();
			for(Challenge ei : cList){
				for(AvailableExercise a : ei.getExercises()){
					if(a.getId().equals(idExercise)) {
						return true;
					}
				}
			}
			return false;
		}catch(Exception e){	
			logger.error(e.getMessage());
			closeSessionTransaction(hb);
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public List<Challenge> getAllChallenges(Set<Organization> organizations) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<Challenge> ei = hb.localSession.createQuery("from Challenge where organization in (:names)")
					.setParameterList("names", organizations)
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<Challenge>();
		}
	}
	public Challenge getChallengeWithDetailsForUser(Integer idChallenge, Integer idUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		Challenge ei;
		try{
			ei = (Challenge) hb.localSession.createQuery("select c from Challenge c inner join c.users usrs where usrs.idUser = :usrId ")
					.setParameter( "usrId", idUser )
					.getSingleResult();
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
		Hibernate.initialize(ei.getRunExercises());
		Hibernate.initialize(ei.getExercises());
		for(ExerciseInstance i : ei.getRunExercises()){
			Hibernate.initialize(i.getResults());
		}
		for(AvailableExercise a : ei.getExercises()){
			Hibernate.initialize(a.getQuestionsList());
		}
		closeSessionTransaction(hb);
		return ei;
	}
	public Challenge getChallengeWithDetails(Integer idChallenge, Set<Organization> org) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		LinkedList<Integer> orgList = new LinkedList<Integer>();
		for(Organization o : org){
			orgList.add(o.getId());
		}
		Challenge ei;
		try{
			ei = (Challenge) hb.localSession.createQuery("select c from Challenge c where c.idChallenge =:id and c.organization.id in :org ")
					.setParameter( "id", idChallenge )
					.setParameterList( "org", orgList )
					.getSingleResult();
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
		Hibernate.initialize(ei.getRunExercises());
		Hibernate.initialize(ei.getExercises());
		for(ExerciseInstance i : ei.getRunExercises()){
			Hibernate.initialize(i.getResults());
		}
		for(AvailableExercise a : ei.getExercises()){
			Hibernate.initialize(a.getQuestionsList());
		}
		closeSessionTransaction(hb);
		return ei;
	}
	@SuppressWarnings("unchecked")
	public List<Challenge> getAllChallengesForUser(Integer idUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		List<Challenge> cList;
		try{
			cList = hb.localSession.createQuery("select c from Challenge c inner join c.users usrs where usrs.idUser = :usrId ")
					.setParameter( "usrId", idUser )
					.getResultList();
			for(Challenge ei : cList){
				Hibernate.initialize(ei.getRunExercises());
				Hibernate.initialize(ei.getExercises());
				for(ExerciseInstance i : ei.getRunExercises()){
					Hibernate.initialize(i.getResults());
				}
				for(AvailableExercise a : ei.getExercises()){
					Hibernate.initialize(a.getQuestionsList());
				}
			}
			return cList;

		}catch(Exception e){	
			cList = new LinkedList<Challenge>();
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<Challenge>();
		}
	}
	public Boolean updateChallenge(Challenge challenge) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			hb.localSession.update( challenge );
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Integer addTeam(Team team) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Integer id = (Integer) hb.localSession.save( team );
			closeSessionTransaction(hb);
			return id;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<Team> getManagementAllTeams(Set<Organization> organizations) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<Team> teams = hb.localSession.createQuery("from Team where organization in (:names)")
					.setParameterList("names", organizations)
					.getResultList();
			closeSessionTransaction(hb);
			return teams;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<Team>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<Team> getManagementAllTeams(Integer idUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<Team> teams = hb.localSession.createQuery("select t from Team t inner join t.managers man where man.idUser =:idUsr")
					.setParameter("idUsr", idUser)
					.getResultList();
			closeSessionTransaction(hb);
			return teams;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<Team>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<User> getUsersInTeamManagedBy(User sessionUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{	
			List<User> ei = hb.localSession.createQuery("select distinct(u) from User u inner join u.team.managers man where man.idUser =:idUsr")
					.setParameter("idUsr", sessionUser.getIdUser())
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<User>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<Team> geTeamsManagedBy(User sessionUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{	
			List<Team> ei = hb.localSession.createQuery("select distinct(t) from Team t inner join t.managers man where man.idUser =:idUsr")
					.setParameter("idUsr", sessionUser.getIdUser())
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<Team>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<User> getUsersForTeamName(String team, Organization organization){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{	
			List<User> ei = hb.localSession.createQuery("from User as u where u.team.name = :teamName "
					+ "and u.defaultOrganization.id in (:names)")
					.setParameter( "teamName", team )
					.setParameter("names", organization.getId())
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		}
		catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<User> getUsersForTeamName(String team, Set<Organization> organizations){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		List<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		try{	
			List<User> ei = hb.localSession.createQuery("select distinct(u) from User u "
					+ "where u.team.name = :teamName and u.defaultOrganization.id in (:names)")
					.setParameter( "teamName", team )
					.setParameterList("names", ids)
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<User>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<User> getUsersForTeamName(String name) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{	
			List<User> ei = hb.localSession.createQuery("from User as u where u.team.name = :teamName")
					.setParameter( "teamName", name )
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<User>();
		}
	}
	public Team getTeam(Integer idTeam) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Team aei = hb.localSession.get( Team.class, idTeam );
			closeSessionTransaction(hb);
			return aei;
		} catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Team getTeam(Integer idTeam, Set<Organization> organizations){
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Team team = (Team) hb.localSession.createQuery("from Team as t where t.idTeam = :idTeam and t.organization.id in (:idOrg)")
					.setParameter( "idTeam", idTeam )
					.setParameterList( "idOrg", ids )
					.getSingleResult();
			closeSessionTransaction(hb);
			return team;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Team getTeamFromName(String name) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Team team = (Team) hb.localSession.createQuery("from Team as t where t.name = :teamName")
					.setParameter( "teamName", name )
					.getSingleResult();
			closeSessionTransaction(hb);
			return team;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Team getTeamFromName(String teamName, Organization organization) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Team team = (Team) hb.localSession.createQuery("from Team as t where t.name = :teamName and organization.id = :idOrg")

					.setParameter( "teamName", teamName )
					.setParameter( "idOrg", organization.getId() )
					.getSingleResult();
			closeSessionTransaction(hb);
			return team;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Team getTeamFromName(String teamName, Set<Organization> organizations) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Team team = (Team) hb.localSession.createQuery("from Team as t where t.name = :teamName and organization.id in (:ids)")

					.setParameter( "teamName", teamName )
					.setParameterList( "ids", ids )
					.getSingleResult();
			closeSessionTransaction(hb);
			return team;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Integer addExerciseInstance(ExerciseInstance ei) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Integer id = (Integer) hb.localSession.save( ei );
			closeSessionTransaction(hb);
			return id;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Integer updateExerciseInstance(ExerciseInstance ei) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			hb.localSession.update( ei );
			closeSessionTransaction(hb);
			return ei.getIdExerciseInstance();
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getAllExercisesInstances(){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> teams = hb.localSession.createQuery("from ExerciseInstance").getResultList();
			closeSessionTransaction(hb);
			return teams;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getActiveExerciseInstanceForUser(Integer id){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.user.idUser = :userId "
					+ "and "
					+ "ei.status < 3")
					.setParameter( "userId",id )
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getActiveExerciseInstanceForUserWithAWSInstanceAndGW(Integer id){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.user.idUser = :userId "
					+ "and "
					+ "ei.status < 3")
					.setParameter( "userId",id )
					.getResultList();
			for(ExerciseInstance instance : ei){
				Hibernate.initialize(instance.getAvailableExercise().getQuestionsList());
				Hibernate.initialize(instance.getGuac().getGateway());
				Hibernate.initialize(instance.getEcsInstance());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getActiveExerciseInstanceForUserWithAvailableExercise(Integer idUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.user.idUser = :userId "
					+ "and "
					+ "ei.status < 3")
					.setParameter( "userId",idUser )
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<RTFECSContainerTask> getRunningECSInstances() {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<RTFECSContainerTask> ei = hb.localSession.createQuery("from RTFECSInstance ei "
					+ "where ei.status = :running or ei.status = :pending")
					.setParameter("running", Constants.STATUS_RUNNING)
					.setParameter("pending", Constants.STATUS_PENDING)
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<RTFECSContainerTask>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getAllRunningExerciseInstances(Set<Organization> filteredOrg) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : filteredOrg){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance instance "
					+ "where instance.status < 3 "
					+ "and instance.organization.id in (:names)")
					.setParameterList("names", ids)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getAllRunningExerciseInstancesWithAwsGWFlag() {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where ei.status < 3")
					.getResultList();
			for(ExerciseInstance instance : ei){
				Hibernate.initialize(instance.getGuac().getGateway());
				Hibernate.initialize(instance.getEcsInstance());
				for(Flag f : instance.getAvailableExercise().getQuestionsList()){
					Hibernate.initialize(f);
				}	
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getManagegementPendingExerciseInstances(Set<Organization> organizations){
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance instance "
					+ "where instance.status = 4 "
					+ "and instance.organization.id in (:names)")
					.setParameterList("names", ids)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getManagementCancelledExerciseInstances(Set<Organization> organizations){
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance instance "
					+ "where instance.status = :cancelled "
					+ "and instance.organization.id in (:names)")
					.setParameter("cancelled",ExerciseStatus.CANCELLED)
					.setParameterList("names", ids)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getManagegementPendingExerciseInstances(List<User> users){
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(User u : users){
			ids.add(u.getIdUser());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance instance "
					+ "where instance.status = :pending "
					+ "and instance.user.idUser in (:names)")
					.setParameter("pending", ExerciseStatus.STOPPED)
					.setParameterList("names", ids)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	public ExerciseInstance getManagementExerciseInstance(Integer idExerciseInstance, Set<Organization> organizations) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			ExerciseInstance ei = (ExerciseInstance) hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.idExerciseInstance = :instanceId"
					+ " and ei.organization.id in (:names)")
					.setParameter( "instanceId", idExerciseInstance)
					.setParameterList("names", ids)
					.getSingleResult();

			Hibernate.initialize(ei.getUsedHints());
			Hibernate.initialize(ei.getUser());
			for(AvailableExerciseInfo i : ei.getAvailableExercise().getInfoList()){
				Hibernate.initialize(i);
			}	
			for(Flag f : ei.getAvailableExercise().getQuestionsList()){
				Hibernate.initialize(f);
			}	
			for(ExerciseResult er : ei.getResults()){
				Hibernate.initialize(er);
			}
			closeSessionTransaction(hb);
			return ei;

		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public ExerciseInstance getManagementExerciseInstanceWithFile(Integer idExerciseInstance, Set<Organization> organizations) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}		
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			ExerciseInstance ei = (ExerciseInstance) hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.idExerciseInstance = :instanceId "
					+ "and ei.organization.id in (:ids)")
					.setParameter( "instanceId", idExerciseInstance)
					.setParameterList("ids", ids)
					.getSingleResult();
			Hibernate.initialize(ei.getResultFile());
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public ExerciseInstance getCompletedExerciseInstanceForUser(Integer idUser, Integer idExerciseInstance) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			ExerciseInstance ei = (ExerciseInstance) hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.user.idUser = :userId "
					+ "and ei.idExerciseInstance = :instanceId "
					+ "and "
					+ "ei.status >= 3")
					.setParameter( "userId", idUser)
					.setParameter( "instanceId", idExerciseInstance)
					.getSingleResult();

			for(ExerciseResult er : ei.getResults()){
				Hibernate.initialize(er);
			}
			Hibernate.initialize(ei.getUsedHints());
			for(AvailableExerciseInfo i : ei.getAvailableExercise().getInfoList()){
				Hibernate.initialize(i);
			}	
			for(Flag f : ei.getAvailableExercise().getQuestionsList()){
				Hibernate.initialize(f);
			}	
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getManagementReviewedExerciseInstances(List<User> users) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(User u : users){
			ids.add(u.getIdUser());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where ei.user.idUser in (:names) and (ei.status =:reviewed or ei.status =:cancelled)")
					.setParameter( "reviewed", ExerciseStatus.REVIEWED)
					.setParameter( "cancelled", ExerciseStatus.CANCELLED)
					.setParameterList("names", ids)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getManagementReviewedExerciseInstances(Set<Organization> organizations) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where ei.status =:reviewed or ei.status =:cancelled and ei.organization.id in (:names)")
					.setParameter( "reviewed", ExerciseStatus.REVIEWED)
					.setParameter( "cancelled", ExerciseStatus.CANCELLED)
					.setParameterList("names", ids)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getReviewedExerciseInstancesWithResultsFlagsUserForStats(Set<Organization> organizations) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where ei.status =:reviewed and organization.id in (:names)")
					.setParameter( "reviewed", ExerciseStatus.REVIEWED)
					.setParameterList("names", ids)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getResults());
				Hibernate.initialize(ex.getExercise().getFlags().get(0).getCategory());
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getReviewedExerciseInstancesWithResultsFlagsUserForStats(List<User> users) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		List<Integer> userIds = new LinkedList<Integer>();
		for(User u : users){
			userIds.add(u.getIdUser());
		}
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where ei.user.idUser in (:names) and ei.status = 5")
					.setParameterList("names", userIds)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getResults());
				Hibernate.initialize(ex.getExercise().getFlags().get(0).getCategory());
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getReviewedExerciseInstancesWithUsersForStats() {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where ei.status =:reviewed")
					.setParameter( "reviewed", ExerciseStatus.REVIEWED)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getUser());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getReviewedExerciseInstancesWithFlagsForStats() {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where ei.status =:reviewed")
					.setParameter( "reviewed", ExerciseStatus.REVIEWED)
					.getResultList();
			for(ExerciseInstance ex : ei){
				Hibernate.initialize(ex.getExercise().getFlags().get(0).getCategory());
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	public ExerciseInstance getManagementCompletedExerciseInstance(Integer idExerciseInstance, Set<Organization> organizations) {
		List<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			ExerciseInstance ei = (ExerciseInstance) hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.idExerciseInstance = :instanceId "
					+ "and ei.organization.id in (:ids) "
					+ "and ei.status >= 3")
					.setParameter( "instanceId", idExerciseInstance)
					.setParameterList("ids", ids)
					.getSingleResult();
			Hibernate.initialize(ei.getResultFile());
			Hibernate.initialize(ei.getUser());
			for(ExerciseResult er : ei.getResults()){
				Hibernate.initialize(er);
			}
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getCompletedExerciseInstancesForUser(Integer idUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.user.idUser = :userId "
					+ "and "
					+ "ei.status > 3")
					.setParameter( "userId", idUser )
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ExerciseInstance> getCompletedExerciseInstancesPerUserForStats(Integer idUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<ExerciseInstance> ei = hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.user.idUser = :userId "
					+ "and "
					+ "ei.status = 5")
					.setParameter( "userId", idUser )
					.getResultList();
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseInstance>();
		}
	}
	public ExerciseInstance getCompletedExerciseInstanceWithFileForUser(Integer idUser, Integer idExerciseInstance) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			ExerciseInstance ei = (ExerciseInstance) hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.user.idUser = :userId "
					+ "and ei.idExerciseInstance = :instanceId "
					+ "and "
					+ "ei.status >= 3")
					.setParameter( "userId", idUser)
					.setParameter( "instanceId", idExerciseInstance)
					.getSingleResult();
			Hibernate.initialize(ei.getResultFile());
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public ExerciseInstance getCompletedExerciseInstanceWithSolution(Integer idUser, Integer idExerciseInstance) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			ExerciseInstance ei = (ExerciseInstance) hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.user.idUser = :userId "
					+ "and ei.idExerciseInstance = :instanceId "
					+ "and "
					+ "ei.status >= 3")
					.setParameter( "userId", idUser)
					.setParameter( "instanceId", idExerciseInstance)
					.getSingleResult();
			Hibernate.initialize(ei.getAvailableExercise().getSolutionFile());
			closeSessionTransaction(hb);
			return ei;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public ExerciseInstance getExerciseInstanceWithHints(Integer idExerciseInstance) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			ExerciseInstance ei = (ExerciseInstance) hb.localSession.createQuery("from ExerciseInstance ei "
					+ "where "
					+ "ei.idExerciseInstance = :instanceId ")
					.setParameter( "instanceId", idExerciseInstance)
					.getSingleResult();
			Hibernate.initialize(ei.getUsedHints());
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Integer getFailedLoginAttemptsForUser(String username) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			UserFailedLogins ei = (UserFailedLogins) hb.localSession.createQuery("from UserFailedLogins ei "
					+ "where "
					+ "ei.username = :username ")
					.setParameter( "username", username )
					.getSingleResult();
			closeSessionTransaction(hb);
			return ei.getCounter();
		}catch(Exception e){
			UserFailedLogins failedLogins = new UserFailedLogins();
			failedLogins.setUser(username);
			failedLogins.setCounter(0);
			failedLogins.setDate(new Date());
			hb.localSession.save(failedLogins);
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return 0;
		}
	}
	public Boolean setFailedLoginAttemptsForUser(String username, Integer counter) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			hb.localSession.createQuery("update UserFailedLogins ei set ei.counter = :counter" +
					" where ei.username = :username")
			.setParameter("counter", counter)
			.setParameter("username", username)
			.executeUpdate();
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Integer addLoginEvent(UserAuthenticationEvent attempt){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Integer id = (Integer) hb.localSession.save( attempt );
			closeSessionTransaction(hb);
			return id;
		}catch(Exception e) {
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	private UserAuthenticationEvent getAuthenticationEvent(String sessionHash){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			UserAuthenticationEvent ei = (UserAuthenticationEvent) hb.localSession.createQuery("from UserAuthenticationEvent ei "
					+ "where ei.sessionIdHash = :sessionHash ")
					.setParameter( "sessionHash", sessionHash)
					.getSingleResult();
			closeSessionTransaction(hb);
			return ei;
		}catch(Exception e) {
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}

	public Boolean addLogoutEvent(UserAuthenticationEvent logoutAttempt) {
		UserAuthenticationEvent loginAttempt = getAuthenticationEvent(logoutAttempt.getSessionIdHash());
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			if(null == loginAttempt){
				hb.localSession.save(logoutAttempt);
				closeSessionTransaction(hb);
				return false;
			}
			Integer minutes = getElapsedMinutes(logoutAttempt.getLogoutDate(),loginAttempt.getLoginDate());
			loginAttempt.setLogoutDate(logoutAttempt.getLogoutDate());
			loginAttempt.setSessionTimeMinutes(minutes);
			hb.localSession.update(loginAttempt);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e) {
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	private static Integer getElapsedMinutes(Date date1, Date date2) {
		long diffInMillies = date1.getTime() - date2.getTime();
		long diffMinutes = diffInMillies / (60 * 1000) % 60;
		return (int) diffMinutes;
	}

	@SuppressWarnings("unchecked")
	private List<RTFECSTaskDefinitionForExerciseInRegion> getAllTaskDefinitionForExercise(Integer idExercise) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<RTFECSTaskDefinitionForExerciseInRegion> list = (List<RTFECSTaskDefinitionForExerciseInRegion>) hb.localSession.createQuery(
					"from ECSTaskDefinitionForExerciseInRegion where exerciseId = :exId")
					.setParameter( "exId", idExercise )
					.getResultList();
			closeSessionTransaction(hb);
			return list;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<RTFECSTaskDefinitionForExerciseInRegion>();
		}
	}

	public RTFECSTaskDefinition getTaskDefinitionForExerciseInRegion(Integer exerciseId, Regions region) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			RTFECSTaskDefinitionForExerciseInRegion td = (RTFECSTaskDefinitionForExerciseInRegion) hb.localSession.createQuery("from ECSTaskDefinitionForExerciseInRegion where "
					+ " region = :regId and exerciseId = :exId and active is true")
					.setParameter( "regId", region  )
					.setParameter( "exId", exerciseId )
					.setMaxResults(1)
					.setFirstResult(0)
					.getSingleResult();
			closeSessionTransaction(hb);
			return td.getTaskDefinition();
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.warn("Exercise id "+exerciseId+" does not have task definition in region "+region.getName()+" : "+e.getMessage());
			return null;
		}
	}

	public Integer getFlagIdFromQuestionId(Integer idQuestion) {
		EntityManager em =  getHibernateEntityManager();
		Integer id = null;
		try{
			id = (Integer) em.createNativeQuery(
					"SELECT Flag_idFlag FROM flags_flagQuestions WHERE flagQuestionList_idFlagQuestion = :qst")
					.setParameter("qst", idQuestion)
					.getSingleResult();
			em.close();
			return id;
		}catch(Exception e){
			em.close();
			logger.error(e.getMessage());
			return id;
		}
	}
	public Integer addGuacTempUser(GuacTempUser guacUser){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Integer id = (Integer) hb.localSession.save( guacUser );
			closeSessionTransaction(hb);
			return id;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Feedback getUserFeedbackForExercise(Integer id, Set<Organization> organizations) {
		List<Integer> ids = new LinkedList<Integer>();
		for(Organization o : organizations){
			ids.add(o.getId());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			Feedback fb = (Feedback) hb.localSession.createQuery("from Feedback f "
					+ "where f.instance.idExerciseInstance = :exId and f.instance.organization.id in (:names) ")
					.setParameter( "exId", id)
					.setParameterList("names", ids)
					.getSingleResult();
			closeSessionTransaction(hb);
			return fb;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Integer addUserFeedback(Feedback feedback) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Integer id = (Integer) hb.localSession.save( feedback );
			closeSessionTransaction(hb);
			return id;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Boolean managementCleanCancelledInstance(Integer idFile, Integer idScore, List<Integer> idResults) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			ExerciseResultFile file = (ExerciseResultFile) hb.localSession.load( ExerciseResultFile.class, idFile );
			hb.localSession.delete(file);
			ExerciseScore score  = (ExerciseScore) hb.localSession.load( ExerciseScore.class, idScore );
			hb.localSession.delete(score);
			for(Integer idRes : idResults){
				ExerciseResult result =   (ExerciseResult) hb.localSession.load( ExerciseResult.class, idRes );
				hb.localSession.delete(result);
			}
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	@SuppressWarnings({ "unchecked"})
	public List<ExerciseResult> getAllVerifiedExerciseResultsForStats() {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<ExerciseResult> results = hb.localSession.createQuery("from ExerciseResult r where r.verified is true").getResultList();
			closeSessionTransaction(hb);
			return results;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<ExerciseResult>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<Organization> getOrganizations(User user) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<Organization> results = hb.localSession.createQuery("select managedOrganizations from User u where u.idUser = :usr")
					.setParameter( "usr", user.getIdUser())
					.getResultList();
			closeSessionTransaction(hb);
			return results;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<Organization>();
		}
	}
	public Organization getOrganizationByName(String name) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Organization result = (Organization) hb.localSession.createQuery("from Organization o "
					+ "where o.name = :name")
					.setParameter( "name", name)
					.getSingleResult();
			closeSessionTransaction(hb);
			return result;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Organization getOrganizationById(Integer teamOrg) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Organization result = (Organization) hb.localSession.createQuery("from Organization o "
					+ "where o.id = :id")
					.setParameter( "id", teamOrg)
					.getSingleResult();
			closeSessionTransaction(hb);
			return result;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<Notification> getUnreadNotificationsForUser(User user) {
		Date date = new java.util.Date();
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<Notification> results = hb.localSession.createQuery("from Notification n "
					+ "where n.idUser = :usr "
					+ "and n.userRead is false "
					+ "and n.dateStart <= :start and dateEnd >= :end")
					.setParameter( "usr", user.getIdUser())
					.setParameter("start", date)
					.setParameter("end",date)
					.getResultList();
			closeSessionTransaction(hb);
			return results;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<Notification>();
		}
	}
	public boolean markNotificationAsRead(Integer idNotification, User user) {
		Notification n = getNotificationFromId(idNotification);
		if(null!=n && user.getIdUser().equals(n.getIdUser())){
			n.setUserRead(true);
			n.setDateRead(new Date());
			HibernateSessionTransactionWrapper hb = openSessionTransaction();
			try {
				hb.localSession.update(n);
				closeSessionTransaction(hb);
				return true;
			}catch(Exception e){
				closeSessionTransaction(hb);
				logger.error(e.getMessage());
				return false;
			}
		}
		return false;
	}
	private Notification getNotificationFromId(Integer idNotification){
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Notification result = (Notification) hb.localSession.createQuery("from Notification n "
					+ "where n.idNotification = :id ")
					.setParameter( "id", idNotification)
					.getSingleResult();
			closeSessionTransaction(hb);
			return result;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<User> getManagementUsersForOrganization(Organization defaultOrganization) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<User> result =  hb.localSession.createQuery("from User u "
					+ "where u.defaultOrganization.id =:id and u.role <= 2 and u.status = :active")
					.setParameter( "id", defaultOrganization.getId())
					.setParameter("active", UserStatus.ACTIVE)
					.getResultList();
			closeSessionTransaction(hb);
			return result;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<User>();
		}
	}
	@SuppressWarnings("unchecked")
	public List<User> getUsersWithoutTeam(Organization organization) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<User> result =  hb.localSession.createQuery("from User u "
					+ "where u.defaultOrganization.id =:id and u.team is null and u.status = :active")
					.setParameter( "id", organization.getId())
					.setParameter("active", UserStatus.ACTIVE)
					.getResultList();
			closeSessionTransaction(hb);
			return result;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<User>();
		}
	}
	public boolean addNotification(Notification n) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Integer id = (Integer) hb.localSession.save( n );
			closeSessionTransaction(hb);
			if(null!=id)
				return true;
			return false;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public boolean addNotificationList(List<Notification> notifications) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		boolean successful = true;
		for(Notification n : notifications){
			try{
				Integer id = (Integer) hb.localSession.save( n );
				if(null!=id)
					successful = false;
			} catch(Exception e){
				logger.error(e.getMessage());
				successful = false;
				continue;
			}
		}
		closeSessionTransaction(hb);
		return successful;
	}
	
	public Integer addChallenge(Challenge c) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		Integer id;
		try{
			id = (Integer) hb.localSession.save( c );
			closeSessionTransaction(hb);
			return id;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Integer addOrganization(Organization o) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		Integer id;
		try{
			id = (Integer) hb.localSession.save( o );
			closeSessionTransaction(hb);
			return id;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Integer addReservation(RTFInstanceReservation reservation) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		Integer id;
		try{
			id = (Integer) hb.localSession.save( reservation );
			closeSessionTransaction(hb);
			return id;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}

	public RTFInstanceReservation getReservation(Integer reservationId) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			RTFInstanceReservation aei = hb.localSession.get( RTFInstanceReservation.class, reservationId );
			closeSessionTransaction(hb);
			return aei;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Boolean updateReservation(RTFInstanceReservation updatedReservation) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			hb.localSession.update( updatedReservation );
			closeSessionTransaction(hb);
			return true;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<RTFInstanceReservation> getUnfulfilledReservationsForUser(User sessionUser) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<RTFInstanceReservation> reservations = hb.localSession.createQuery("select r from RTFInstanceReservation r "
					+ "where r.user.idUser = :userId and r.fulfilled is false and r.error is false")
					.setParameter("userId", sessionUser.getIdUser())
					.getResultList();
			closeSessionTransaction(hb);
			return reservations;
		}catch(Exception e) {
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<RTFInstanceReservation>();
		}
	}
	@SuppressWarnings("unchecked")
	public boolean isExerciseEnabledForOrganization(Integer orgId, Integer exerciseId) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			List<AvailableExercise> exercises = hb.localSession.createQuery("select a.exercises from AvailableExercisesForOrganization a "
					+ "where a.organization.id = :org")
					.setParameter("org", orgId)
					.getResultList();
			closeSessionTransaction(hb);
			for(AvailableExercise e : exercises) {
				if(e.getId().equals(exerciseId)) {
					return true;
				}
			}
			return false;
		}catch(Exception e) {
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public Boolean addAvailableExerciseForOrganization(Organization org, AvailableExercise ex) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			AvailableExercisesForOrganization exercises = (AvailableExercisesForOrganization) hb.localSession.createQuery("select a from AvailableExercisesForOrganization a "
					+ "where a.organization.id = :org")
					.setParameter("org", org.getId())
					.getSingleResult();
			for(AvailableExercise avEx : exercises.getExercises()) {
				if(avEx.getId().equals(ex.getId())) {
					return false;
				}
			}
			exercises.getExercises().add(ex);
			hb.localSession.update(exercises);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e) {
			AvailableExercisesForOrganization exercises = new AvailableExercisesForOrganization();
			exercises.setOrganization(org);
			List<AvailableExercise> list = new ArrayList<AvailableExercise>();
			list.add(ex);
			exercises.setExercises(list);
			try {
				hb.localSession.save(exercises);
				closeSessionTransaction(hb);
			}catch(Exception e2) {
				closeSessionTransaction(hb);
				logger.error(e2.getMessage());
				return false;
			}
			logger.debug("AvailableExercisesForOrganization not present for organization "+org.getId()+" "+e.getMessage());
			return true;
		}
	}
	public Boolean removeAvailableExerciseForOrganization(Organization org, AvailableExercise ex) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			AvailableExercisesForOrganization exercises = (AvailableExercisesForOrganization) hb.localSession.createQuery("select a from AvailableExercisesForOrganization a "
					+ "where a.organization.id = :org")
					.setParameter("org", org.getId())
					.getSingleResult();
			for(AvailableExercise e : exercises.getExercises()) {
				if(ex.getId().equals(e.getId())) {
					exercises.getExercises().remove(e);
					break;
				}
			}
			if(exercises.getExercises().isEmpty())
				hb.localSession.delete(exercises);
			else
				hb.localSession.update(exercises);
			closeSessionTransaction(hb);
			return true;
		} catch(Exception e) {
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	public RTFECSTaskDefinition getTaskDefinitionForExerciseInRegion(Integer idExercise, Regions region,Organization org) {
		if(isExerciseEnabledForOrganization(org.getId(),idExercise)) {
			return getTaskDefinitionForExerciseInRegion(idExercise, region);
		}
		return null;
	}
	public List<RTFECSTaskDefinitionForExerciseInRegion> getAllTaskDefinitionsForExercise(Integer idExercise, Organization org) {
		if(isExerciseEnabledForOrganization(org.getId(),idExercise)) {
			return getAllTaskDefinitionForExercise(idExercise);
		}
		return new LinkedList<RTFECSTaskDefinitionForExerciseInRegion>();
	}
	@SuppressWarnings("unchecked")
	public List<Challenge> getAllChallengesForUsers(List<User> users) {
		HashSet<Integer> usrs = new HashSet<Integer>();
		for(User user : users) {
			usrs.add(user.getIdUser());
		}
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		List<Challenge> cList;
		try{
			cList = hb.localSession.createQuery("select distinct c from Challenge c inner join c.users usrs where usrs.idUser in :usrIds ")
					.setParameterList( "usrIds", usrs )
					.getResultList();
			for(Challenge ei : cList){
				Hibernate.initialize(ei.getRunExercises());
				Hibernate.initialize(ei.getExercises());
				for(ExerciseInstance i : ei.getRunExercises()){
					Hibernate.initialize(i.getResults());
				}
				for(AvailableExercise a : ei.getExercises()){
					Hibernate.initialize(a.getQuestionsList());
				}
				Hibernate.initialize(ei.getUsers());
			}
			closeSessionTransaction(hb);
			return cList;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<Challenge>();
		}
	}
	public Integer addECSTaskDefinitionForExerciseInRegion(RTFECSTaskDefinitionForExerciseInRegion ecsTaskForExercise) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		Integer id;
		try{
			id = (Integer) hb.localSession.save( ecsTaskForExercise );
			closeSessionTransaction(hb);
			if(ecsTaskForExercise.getActive()) {
				disableAllOtherTaskDefinitionsForExerciseInRegion(id);
			}
			return id;
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked" )
	public Boolean disableAllOtherTaskDefinitionsForExerciseInRegion(Integer id) {

		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		RTFECSTaskDefinitionForExerciseInRegion tdEnabled;
		try {
			tdEnabled = hb.localSession.get( RTFECSTaskDefinitionForExerciseInRegion.class, id );
		} catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
		try {
			List<RTFECSTaskDefinitionForExerciseInRegion> tds =  hb.localSession.createQuery("select td from ECSTaskDefinitionForExerciseInRegion td where "
					+ "td.active is true and td.exercise.id = :exId and td.region = :tdRegion")
					.setParameter( "exId", tdEnabled.getExercise().getId() )
					.setParameter( "tdRegion", tdEnabled.getRegion() )
					.getResultList();		
			for(RTFECSTaskDefinitionForExerciseInRegion dbTd : tds ) {
				if(!dbTd.getId().equals(id)) {
					dbTd.setActive(false);
					hb.localSession.update(dbTd);
				}
			}
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}

	public Boolean removeTaskDefinitionInRegion(Integer idExercise, Integer idTaskDef) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			RTFECSTaskDefinitionForExerciseInRegion td = (RTFECSTaskDefinitionForExerciseInRegion) hb.localSession.createQuery("select td from ECSTaskDefinitionForExerciseInRegion td where "
					+ "td.exercise.id = :exId and td.taskDefinition.id = :tdId")
					.setParameter( "exId", idExercise )
					.setParameter( "tdId", idTaskDef )
					.getSingleResult();
			if(null!=td) {
				hb.localSession.delete(td);
				closeSessionTransaction(hb);
				return true;
			}
			closeSessionTransaction(hb);
			return false;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}

	public Boolean enableDisableTaskDefinitionInRegion(Integer exerciseId, Integer taskDefId, Boolean active) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			RTFECSTaskDefinitionForExerciseInRegion td = (RTFECSTaskDefinitionForExerciseInRegion) hb.localSession.createQuery("select td from ECSTaskDefinitionForExerciseInRegion td where "
					+ "td.exercise.id = :exId and td.taskDefinition.id = :tdId")
					.setParameter( "exId", exerciseId )
					.setParameter( "tdId", taskDefId )
					.getSingleResult();
			if(null==td) {
				closeSessionTransaction(hb);
				return false;
			}
			td.setActive(active);
			hb.localSession.update(td);
			closeSessionTransaction(hb);
			if(active) {
				disableAllOtherTaskDefinitionsForExerciseInRegion(td.getId());
			}
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}


	@SuppressWarnings("unchecked")
	public List<RTFECSTaskDefinitionForExerciseInRegion> getAllTaskDefinitionForExerciseInRegion(Integer taskDefId) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<RTFECSTaskDefinitionForExerciseInRegion> taskDef = hb.localSession.createQuery("select t from ECSTaskDefinitionForExerciseInRegion t "
					+ "where t.taskDefinition.id = :taskId ")
					.setParameter( "taskId", taskDefId )
					.getResultList();
			closeSessionTransaction(hb);
			return taskDef;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<RTFECSTaskDefinitionForExerciseInRegion>();
		}
	}
	public RTFECSTaskDefinitionForExerciseInRegion getTaskDefinitionForExerciseInRegion(Integer idExercise,
			Integer idTaskDef) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			RTFECSTaskDefinitionForExerciseInRegion taskDef = (RTFECSTaskDefinitionForExerciseInRegion) hb.localSession.createQuery("select t from ECSTaskDefinitionForExerciseInRegion t "
					+ "where t.exercise.id = :exerciseId and t.taskDefinition.id = :taskId ")
					.setParameter( "exerciseId", idExercise )
					.setParameter( "taskId", idTaskDef )
					.getSingleResult();
			closeSessionTransaction(hb);
			return taskDef;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	public Boolean addInvitationCode(InvitationCodeForOrganization invite) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try {
			hb.localSession.save( invite );
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){	
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}
	
	public Boolean removeInvitationCode(String code, Integer orgId) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			InvitationCodeForOrganization result = (InvitationCodeForOrganization) hb.localSession.createQuery("select u from InvitationCodeForOrganization u "
					+ "where u.code = :orgInvitationCode and u.organization.id = :orgId")
					.setParameter( "orgInvitationCode", code)
					.setParameter( "orgId", orgId)
					.getSingleResult();
			hb.localSession.delete(result);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}

	public Boolean decreseOrganizationCodeRedeem(Organization o, String orgInvitationCode) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			InvitationCodeForOrganization result = (InvitationCodeForOrganization) hb.localSession.createQuery("select u from InvitationCodeForOrganization u "
					+ "where u.code = :orgInvitationCode")
					.setParameter( "orgInvitationCode", orgInvitationCode)
					.getSingleResult();
			result.setLeftToRedeem((result.getLeftToRedeem()-1));
			hb.localSession.update(result);
			closeSessionTransaction(hb);
			return true;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return false;
		}
	}

	public Organization getOrganizationFromInvitationCode(String orgInvitationCode) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			Organization result = (Organization) hb.localSession.createQuery("select u.organization from InvitationCodeForOrganization u "
					+ "where u.code =:code and u.leftToRedeem>0")
					.setParameter( "code", orgInvitationCode)
					.getSingleResult();
			closeSessionTransaction(hb);
			return result;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<InvitationCodeForOrganization> getInvitationCodesForOrganization(Integer orgId) {
		HibernateSessionTransactionWrapper hb = openSessionTransaction();
		try{
			List<InvitationCodeForOrganization> result = hb.localSession.createQuery("select u from InvitationCodeForOrganization u "
					+ "where u.organization.id = :orgId")
					.setParameter( "orgId", orgId)
					.getResultList();
			closeSessionTransaction(hb);
			return result;
		}catch(Exception e){
			closeSessionTransaction(hb);
			logger.error(e.getMessage());
			return new LinkedList<InvitationCodeForOrganization>();
		}
	}



}