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
package com.remediatetheflag.global.messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.remediatetheflag.global.messages.annotations.BriefDetails;
import com.remediatetheflag.global.messages.annotations.ChallengeDetails;
import com.remediatetheflag.global.messages.annotations.HistoryDetails;
import com.remediatetheflag.global.messages.annotations.LazilyHint;
import com.remediatetheflag.global.messages.annotations.LazilySerialized;
import com.remediatetheflag.global.messages.annotations.LeaderboardUser;
import com.remediatetheflag.global.messages.annotations.MemberUser;
import com.remediatetheflag.global.messages.annotations.TeamManager;
import com.remediatetheflag.global.messages.annotations.UserStatusList;
import com.remediatetheflag.global.model.Achievements;
import com.remediatetheflag.global.model.AvailableExercise;
import com.remediatetheflag.global.model.AvailableExerciseReferenceFile;
import com.remediatetheflag.global.model.AvailableExerciseSolutionFile;
import com.remediatetheflag.global.model.AvailableExercisesForOrganization;
import com.remediatetheflag.global.model.Challenge;
import com.remediatetheflag.global.model.CompletedReview;
import com.remediatetheflag.global.model.Country;
import com.remediatetheflag.global.model.ExerciseInstance;
import com.remediatetheflag.global.model.ExerciseResultFile;
import com.remediatetheflag.global.model.ExerciseSelfCheckResult;
import com.remediatetheflag.global.model.ExerciseStatus;
import com.remediatetheflag.global.model.Feedback;
import com.remediatetheflag.global.model.FlagQuestionHint;
import com.remediatetheflag.global.model.InvitationCodeForOrganization;
import com.remediatetheflag.global.model.Notification;
import com.remediatetheflag.global.model.Organization;
import com.remediatetheflag.global.model.PendingReview;
import com.remediatetheflag.global.model.RTFECSTaskDefinitionForExerciseInRegion;
import com.remediatetheflag.global.model.RTFGateway;
import com.remediatetheflag.global.model.RTFInstanceReservation;
import com.remediatetheflag.global.model.Stats;
import com.remediatetheflag.global.model.SupportedAWSRegion;
import com.remediatetheflag.global.model.Team;
import com.remediatetheflag.global.model.TeamLeaderboard;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.utils.Constants;

public class MessageGenerator {

	private static Logger logger = LoggerFactory.getLogger(MessageGenerator.class);

	private static ExclusionStrategy excludedLazyObjects = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(LazilySerialized.class) != null;
		}
	};
	private static ExclusionStrategy excludedHints = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(LazilyHint.class) != null;
		}
	};
	private static ExclusionStrategy briefDetails = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(BriefDetails.class) == null;
		}
	};
	private static ExclusionStrategy teamManagers = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(TeamManager.class) == null;
		}
	};
	private static ExclusionStrategy historyDetails = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(LazilySerialized.class) != null && f.getAnnotation(HistoryDetails.class) == null;
		}
	};
	private static ExclusionStrategy challengeDetails = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(LazilySerialized.class) != null && f.getAnnotation(ChallengeDetails.class) == null;
		}
	};
	private static ExclusionStrategy leadearboardUsers = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(LeaderboardUser.class) == null;
		}
	};
	private static ExclusionStrategy memberUsers = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(MemberUser.class) == null && f.getAnnotation(LeaderboardUser.class) == null;
		}
	};

	private static ExclusionStrategy usersList = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(LeaderboardUser.class) == null && f.getAnnotation(UserStatusList.class) == null;
		}
	};
	public static void sendErrorMessage(String error, HttpServletResponse response) {
		JsonObject msg = new JsonObject();
		msg.addProperty(Constants.JSON_ATTRIBUTE_RESULT, Constants.JSON_VALUE_ERROR);
		msg.addProperty(Constants.JSON_ATTRIBUTE_ERROR_MSG, error);
		send(msg.toString(),response);
	}

	public static void sendSuccessMessage(HttpServletResponse response) {
		JsonObject msg = new JsonObject();
		msg.addProperty(Constants.JSON_ATTRIBUTE_RESULT, Constants.JSON_VALUE_SUCCESS);
		send(msg.toString(),response);
	}

	public static void sendRedirectMessage(String destination, HttpServletResponse response) {
		JsonObject msg = new JsonObject();
		msg.addProperty(Constants.JSON_ATTRIBUTE_RESULT, Constants.JSON_VALUE_REDIRECT);
		msg.addProperty(Constants.JSON_ATTRIBUTE_LOCATION, destination);
		send(msg.toString(),response);
	}

	private static void send(String msg, HttpServletResponse response){
		try {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(msg.toString());
			out.close();
		} catch (IOException e) {
			logger.error("HTTP Response. Exception: " + e.getMessage());
		}
	}

	public static void sendAllExercisesForOrgMessage(List<AvailableExercise> exercises, List<AvailableExercisesForOrganization> exercisesForOrg,
			HttpServletResponse response) {
		Gson exercisesGson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String exercisesJson = exercisesGson.toJson(exercises);
		Gson orgsGson = new GsonBuilder().addSerializationExclusionStrategy(briefDetails).create();
		String orgsJson = orgsGson.toJson(exercisesForOrg);
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("exercises", parser.parse(exercisesJson));
		jsonObject.add("orgs", parser.parse(orgsJson));
		send(jsonObject.toString(),response);
	}

	public static void sendTokenMessage(Integer eiId, String fqdn, String guacUser, String lastValidToken, Integer countdown, HttpServletResponse response) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("exInstanceId", eiId);
		jsonObject.addProperty("fqdn", fqdn);
		jsonObject.addProperty("token", lastValidToken);
		jsonObject.addProperty("user", guacUser);
		jsonObject.addProperty("countdown", countdown);
		send(jsonObject.toString(),response);

	}
	public static void sendReservationMessage(RTFInstanceReservation reservation, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(reservation);
		send(json,response);
	}


	public static void sendUserInfoMessage(User user, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(user);
		send(json,response);
	}

	public static void sendUserAchievementsMessage(Achievements achievements, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(achievements);
		send(json,response);
	}


	public static void sendAllChallengesMessage(List<Challenge> challenges, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(challenges);
		send(json,response);
	}

	public static void sendAllExercisesMessage(List<AvailableExercise> exercises, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(exercises);
		send(json,response);
	}

	public static void sendCSRFTokenMessage(String token, HttpServletResponse response) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("ctoken", token);
		send(jsonObject.toString(),response);
	}

	public static void sendUserHistoryMessage(List<ExerciseInstance> history, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(history);
		send(json,response);
	}

	public static void sendExerciseInfoMessage(AvailableExercise exercise, HttpServletResponse response) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.addSerializationExclusionStrategy(excludedHints).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(exercise);
		send(json,response);
	}

	public static void sendAllCountriesMessage(List<Country> countries, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(countries);
		send(json,response);
	}

	public static void sendUserHistoryDetailMessage(ExerciseInstance instance, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(historyDetails).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(instance);
		send(json,response);
	}

	public static void sendFileMessage(ExerciseInstance instance, HttpServletResponse response) {
		ExerciseResultFile resultFile = instance.getResultFile();
		if(null==resultFile){
			MessageGenerator.sendErrorMessage("CouldNotRetrieve", response);
			logger.error("Failed to send zip file");
			return;
		}
		byte[] file = resultFile.getFile();
		response.setContentType("application/octet-stream");
		response.setContentLength((int) file.length);
		response.setHeader( "Content-Disposition", String.format("attachment; filename=\"%s\"", resultFile.getFilename()+".zip"));
		try {
			response.getOutputStream().write(file);
		} catch (IOException e) {
			logger.error("Failed to send zip file: "+e.getMessage());
		}
	}

	public static void sendExerciseRegionsMessage(List<Regions> regions,HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(regions);
		send(json,response);

	}

	public static void sendHintMessage(FlagQuestionHint hint, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(hint);
		send(json,response);

	}
	
	
	public static void sendUserReservationsMessage(List<RTFInstanceReservation> reservations, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(reservations);
		send(json,response);

	}
	public static void sendUserRunningExercisesMessage(List<ExerciseInstance> exercises, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(exercises);
		send(json,response);

	}

	public static void sendExerciseSelfCheckStatus(ExerciseSelfCheckResult res, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(res);
		send(json,response);
	}
	
	public static void sendImageFileMessage(byte[] image, HttpServletResponse response) {
		response.setContentType("image/png");
		response.setContentLength((int) image.length);
		try {
			response.getOutputStream().write(image);
		} catch (IOException e) {
			logger.error("Failed to send image file: "+e.getMessage());
		}
	}

	public static void sendExerciseSolutionFileMessage(ExerciseInstance instance, HttpServletResponse response) {
		AvailableExerciseSolutionFile resultFile = instance.getAvailableExercise().getSolutionFile();
		byte[] file = resultFile.getFile();
		response.setContentType("application/pdf");
		response.setContentLength((int) file.length);
		response.setHeader( "Content-Disposition", String.format("attachment; filename=\"%s\"", resultFile.getFilename()));
		try {
			response.getOutputStream().write(file);
		} catch (IOException e) {
			logger.error("Failed to send pdf file: "+e.getMessage());
		}

	}
	public static void sendExerciseSolutionFileMessage(AvailableExerciseSolutionFile solution, HttpServletResponse response) {
		byte[] file = solution.getFile();
		response.setContentType("application/pdf");
		response.setContentLength((int) file.length);
		response.setHeader( "Content-Disposition", String.format("attachment; filename=\"%s\"", solution.getFilename()));
		try {
			response.getOutputStream().write(file);
		} catch (IOException e) {
			logger.error("Failed to send pdf file: "+e.getMessage());
		}
	}


	public static void sendExerciseReferenceFileMessage(AvailableExercise exercise, HttpServletResponse response) {
		AvailableExerciseReferenceFile referenceFile = exercise.getReferenceFile();
		byte[] file = referenceFile.getFile();
		response.setContentType("application/pdf");
		response.setContentLength((int) file.length);
		response.setHeader( "Content-Disposition", String.format("attachment; filename=\"%s\"", referenceFile.getFilename()));
		try {
			response.getOutputStream().write(file);
		} catch (IOException e) {
			logger.error("Failed to send pdf file: "+e.getMessage());
		}

	}

	public static void sendUserTeamLeaderboard(TeamLeaderboard leaderboard, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(leadearboardUsers).create();
		String json = gson.toJson(leaderboard);
		send(json,response);
	}

	public static void sendUsersListAsMembers(List<User> users, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(memberUsers).create();
		String json = gson.toJson(users);
		send(json,response);
	}
	public static void sendUsersListMessage(List<User> users, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(usersList).create();
		String json = gson.toJson(users);
		send(json,response);
	}
	public static void sendPendingExerciseInstances(List<ExerciseInstance> pending, HttpServletResponse response) {

		List<PendingReview> reviews = new LinkedList<PendingReview>();
		for(ExerciseInstance e : pending){
			PendingReview r = new PendingReview();
			r.setEndTime(e.getEndTime());
			r.setExerciseName(e.getTitle());
			r.setExerciseTopic(e.getAvailableExercise().getSubtitle());
			r.setId(e.getIdExerciseInstance());
			r.setStartTime(e.getStartTime());
			r.setOrganization(e.getOrganization());
			r.setTechnology(e.getTechnology());
			r.setDuration(e.getDuration());
			r.setUser(e.getUser().getUsername());
			reviews.add(r);
		}
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(reviews);
		send(json,response);
	}

	public static void sendExerciseDetails(ExerciseInstance pending, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(pending);
		send(json,response);
	}

	public static void sendStatsMessage(Stats stats, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(stats);
		send(json,response);
	}

	public static void sendTeamListMessage(List<Team> teams, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(teams);
		send(json,response);

	}


	public static void sendReviewedExercises(List<ExerciseInstance> exercises, HttpServletResponse response) {
		List<CompletedReview> reviews = new LinkedList<CompletedReview>();
		for(ExerciseInstance e : exercises){
			CompletedReview r = new CompletedReview();
			r.setEndTime(e.getEndTime());
			r.setExerciseName(e.getTitle());
			r.setStatus(e.getStatus());
			r.setExerciseTopic(e.getAvailableExercise().getSubtitle());
			r.setId(e.getIdExerciseInstance());
			r.setDuration(e.getDuration());
			r.setOrganization(e.getOrganization());
			r.setStartTime(e.getStartTime());
			r.setTechnology(e.getTechnology());
			r.setUser(e.getUser().getUsername());
			r.setFeedback(e.getFeedback());
			if(e.getStatus().equals(ExerciseStatus.REVIEWED)){
				r.setScore(e.getScore().getResult());
				r.setTrophyAwarded(e.getTrophyAwarded());
				r.setNewIssueIntroduced(e.getNewIssuesIntroduced());
				r.setDate(e.getReviewedDate());
			}
			else{
				r.setScore(0);
				r.setTrophyAwarded(false);
				r.setNewIssueIntroduced(false);
				r.setDate(null);
			}
			reviews.add(r);
		}
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(reviews);
		send(json,response);	
	}
	public static void sendUserFeedbackMessage(Feedback feedback, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(feedback);
		send(json,response);
	}
	public static void sendOrganizationsMessage(List<Organization> organizations, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).create();
		String json = gson.toJson(organizations);
		send(json,response);
	}
	public static void sendGatewaysMessage(List<RTFGateway> gateways, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).create();
		String json = gson.toJson(gateways);
		send(json,response);
	}
	public static void sendNotificationsMessage(List<Notification> notifications, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(notifications);
		send(json,response);
	}
	public static void sendUsersListAddTeam(List<User> users, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(leadearboardUsers).create();
		String json = gson.toJson(users);
		send(json,response);
	}
	public static void sendNumberUpdatedMessage(Integer nrAdded, HttpServletResponse response) {
		JsonObject msg = new JsonObject();
		msg.addProperty(Constants.JSON_ATTRIBUTE_RESULT, nrAdded);
		send(msg.toString(),response);
	}
	public static void sendTeamDetailsMessage(Team team, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(teamManagers).create();
		String json = gson.toJson(team);
		send(json,response);
	}
	public static void sendAvailable(boolean b, HttpServletResponse response){
		JsonObject msg = new JsonObject();
		msg.addProperty(Constants.JSON_ATTRIBUTE_RESULT, b);
		send(msg.toString(), response);
	}
	public static void sendCodeValid(boolean b, HttpServletResponse response) {
		JsonObject msg = new JsonObject();
		msg.addProperty(Constants.JSON_ATTRIBUTE_RESULT, b);
		send(msg.toString(), response);
	}
	public static void sendIsExerciseInChallengeMessage(boolean isInChallenge, HttpServletResponse response) {
		JsonObject msg = new JsonObject();
		msg.addProperty(Constants.JSON_ATTRIBUTE_RESULT, isInChallenge);
		send(msg.toString(), response);

	}
	public static void sendChallengeDetailsMessage(Challenge challenge, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(challengeDetails).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(challenge);
		send(json,response);
	}
	public static void sendAllUserChallengesWithDetailsMessage(List<Challenge> challenges,
			HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(challengeDetails).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(challenges);
		send(json,response);
	}
	public static void sendAWSRegionsMessage(List<SupportedAWSRegion> regions, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(regions);
		send(json,response);
	}
	public static void sendExerciseTaskDefinitionsMessage(List<RTFECSTaskDefinitionForExerciseInRegion> tasks, HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(tasks);
		send(json,response);
	}

	public static void sendRunningExercisesMessage(List<ExerciseInstance> runningExercises, HttpServletResponse response) {
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(excludedLazyObjects).excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(runningExercises);
		send(json,response);
	}

	public static void sendInvitationCodesMessage(List<InvitationCodeForOrganization> codes,
			HttpServletResponse response) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(codes);
		send(json,response);
		
	}
}