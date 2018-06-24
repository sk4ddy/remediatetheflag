package com.remediatetheflag.gateway.extras;
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


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AppContextListener implements ServletContextListener {

	private static Logger logger = LoggerFactory.getLogger(AppContextListener.class);
	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.debug("AppContextListener initialized");
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new KeepAlive(), 0, 5, TimeUnit.MINUTES);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.debug("AppContextListener destroyed");
		scheduler.shutdownNow();
	}
} 
