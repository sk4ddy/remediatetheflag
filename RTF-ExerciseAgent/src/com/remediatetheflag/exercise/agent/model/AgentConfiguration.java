package com.remediatetheflag.exercise.agent.model;

import java.util.List;

public class AgentConfiguration {
	
	private List<String> logFiles;
	private String originalSourceDirectory;
	private String exerciseSourceDirectory;
	private String testCommandLite;
	private String testCommandFull;
	private String exerciseStatusCommand;
	private String startApplicationServerCommand;
	private String isApplicationServerRunningCommand;
	
	public List<String> getLogFiles() {
		return logFiles;
	}
	public void setLogFiles(List<String> logFiles) {
		this.logFiles = logFiles;
	}
	public String getOriginalSourceDirectory() {
		return originalSourceDirectory;
	}
	public void setOriginalSourceDirectory(String originalSourceDirectory) {
		this.originalSourceDirectory = originalSourceDirectory;
	}
	public String getExerciseSourceDirectory() {
		return exerciseSourceDirectory;
	}
	public void setExerciseSourceDirectory(String exerciseSourceDirectory) {
		this.exerciseSourceDirectory = exerciseSourceDirectory;
	}
	public String getStartApplicationServerCommand() {
		return startApplicationServerCommand;
	}
	public void setStartApplicationServerCommand(String startApplicationServerCommand) {
		this.startApplicationServerCommand = startApplicationServerCommand;
	}
	public String getIsApplicationServerRunningCommand() {
		return isApplicationServerRunningCommand;
	}
	public void setIsApplicationServerRunningCommand(String isApplicationServerRunningCommand) {
		this.isApplicationServerRunningCommand = isApplicationServerRunningCommand;
	}
	public String getExerciseStatusCommand() {
		return exerciseStatusCommand;
	}
	public void setExerciseStatusCommand(String exerciseStatusCommand) {
		this.exerciseStatusCommand = exerciseStatusCommand;
	}
	public String getTestCommandLite() {
		return testCommandLite;
	}
	public void setTestCommandLite(String testCommandLite) {
		this.testCommandLite = testCommandLite;
	}
	public String getTestCommandFull() {
		return testCommandFull;
	}
	public void setTestCommandFull(String testCommandFull) {
		this.testCommandFull = testCommandFull;
	}

}