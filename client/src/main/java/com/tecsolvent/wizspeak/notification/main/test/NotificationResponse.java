package com.tecsolvent.wizspeak.notification.main.test;

import java.util.ArrayList;
import java.util.List;

import com.tecsolvent.wizspeak.notification.dao.Notification;

public class NotificationResponse {
	private long count = 0;
	private List<Notification> notifications = new ArrayList<Notification>();
	
	public NotificationResponse (long count, List<Notification> notifications) {
		this.count = count;
		this.notifications = notifications;
	}
	
	public long getCount() {
		return count;
	} 
	
	public  List<Notification> getNotifications() {
		return notifications;
	}
}
