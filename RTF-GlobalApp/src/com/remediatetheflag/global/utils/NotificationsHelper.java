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
package com.remediatetheflag.global.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.remediatetheflag.global.model.ExerciseInstance;
import com.remediatetheflag.global.model.Notification;
import com.remediatetheflag.global.model.User;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;

public class NotificationsHelper {
	
	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	public boolean addCompletedReviewNotification(User user, ExerciseInstance e){
		Notification n = new Notification();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		n.setDateStart(c.getTime());
		c.add(Calendar.DATE, 60); 
		n.setDateEnd(c.getTime());
		n.setIdUser(user.getIdUser());
		n.setLink(Constants.NOTIFICATION_LINK_COMPLETED_REVIEW);
		String text = Constants.NOTIFICATION_TEXT_COMPLETED_REVIEW;
		text = text.replace("{EXERCISE}", e.getTitle());
		n.setText(text);
		n.setUserRead(false);
		return hpc.addNotification(n);
	}
	public boolean addNewUserAdded(User user){
		List<User> admins = hpc.getManagementUsersForOrganization(user.getDefaultOrganization());
		List<Notification> notifications = new LinkedList<Notification>();
		for(User u : admins){
			if(u.getIdUser().equals(user.getIdUser()))
				continue;
			Notification n = new Notification();
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			n.setDateStart(c.getTime());
			c.add(Calendar.DATE, 60); 
			n.setDateEnd(c.getTime());
			n.setIdUser(u.getIdUser());
			String link = Constants.NOTIFICATION_LINK_NEW_USER_ADDED.replace("{USERNAME}",user.getUsername());
			n.setLink(link);
			String text = Constants.NOTIFICATION_TEXT_NEW_USER_ADDED.replace("{USERNAME}", user.getUsername());
			n.setText(text);
			n.setUserRead(false);
			notifications.add(n);
		}
		return hpc.addNotificationList(notifications);
	}
	public boolean addWelcomeToRTFNotification(User user){
		Notification n = new Notification();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		n.setDateStart(c.getTime());
		c.add(Calendar.DATE, 60); 
		n.setDateEnd(c.getTime());
		n.setIdUser(user.getIdUser());
		n.setLink(Constants.NOTIFICATION_LINK_WELCOME_TO_RTF);
		n.setText(Constants.NOTIFICATION_TEXT_WELCOME_TO_RTF);
		n.setUserRead(false);
		return hpc.addNotification(n);
	}
	public boolean addNewNotification(User user, String text) {
		Notification n = new Notification();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		n.setDateStart(c.getTime());
		c.add(Calendar.DATE, 60); 
		n.setDateEnd(c.getTime());
		n.setIdUser(user.getIdUser());
		n.setLink(null);
		n.setText(text);
		n.setUserRead(false);
		return hpc.addNotification(n);
	}
}