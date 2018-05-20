package com.tecsolvent.wizspeak.notification.dao;

import java.util.Date;
import java.util.Map;

import com.tecsolvent.wizspeak.notification.dao.Notification.Category;
import com.tecsolvent.wizspeak.notification.dao.Notification.Status;
import com.tecsolvent.wizspeak.notification.dao.Notification.Type;
import com.tecsolvent.wizspeak.notification.util.StringUtils;
import com.tecsolvent.wizspeak.notification.util.ValidationUtil;

/* Model class for notifications. */
public abstract class Notification {
	
	/* Dummy id to represent new object. */
	public static final String NEW_ID = "NEW_ID";
	
	/* notification identifier */
	protected String id;
	
	/* identifier of entity with which notification is associated with. */
	protected long associationId;
	
	/* picture associated with the notification. */
	protected String picUrl;
	
	/* notification message */
	protected String message;
	
	/* notification status */
	protected Status status;
	
	/* stores when the record was created. */
	protected Date dateAdded;
	
	/* stores when the record was last updated. */
	protected Date dateModified;
	
	/* type of the notification. */
	protected Type type;
	

	/* Category of the notification. */
	protected Category category;
	
	/* user identifier for whom notification belongs */
	protected long userId;
	
	protected Map<String, String> messageMap;

	/**
	 * Default constructor
	 */
	public Notification() {
		setId(NEW_ID);
	}	
	
	/**
	 * @param id
	 * @param picUrl
	 * @param userId
	 * @param message
	 * @param associationId
	 * @param status
	 * @param type
	 * @param category
	 */
	//public Notification(String id, String picUrl, long userId, String message, long associationId, Status status, Type type, Category category) {
	public Notification(String id, Map<String, String> messageMap, long userId, /*String message,*/ long associationId, Status status, Type type, Category category) {
		//this();
		System.out.println("Notification Contructor begins");
		this.messageMap = messageMap;
		setId(id);
		if(this.messageMap != null && !StringUtils.isEmpty(this.messageMap.get("picUrl"))){
                        setPicUrl(messageMap.get("picUrl"));
                }
		System.out.println("after pic url");
		setUserId(userId);
		setStatus(status);
		setType(type);
		setCategory(category);		
		setAssociationId(associationId);
		setMessage(constructMessage());
		System.out.println("getMessage --> " + getMessage());
	}
	

	/* Enum to hold different state/status of the notification. */
	public enum Status {
		
		READ(0), 
		UNREAD(1);
		
		/**
		 * @param value integer to represent the enum in database.
		 */
		private Status(int value) {
			this.value = value;
		}
		
		private int value;
		
		/**
		 * @return value.
		 */
		public int getValue() {
			return value;
		}

	}
	
	public enum Type {
		LIKE(0), //	 likes
		COMMENT(1), // comments
		FRND_REQ(2); // friend requests
		
		private int value;
		
		private Type(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	/* Different categories */
	public enum Category {
		AMBITION(0),
		HOBBIES(1),
		TEAMS(2);
		
		private int value;
		
		private Category(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}

	public Map<String, String> getMessageMap(){
		return messageMap;
	}

	public String getId() {
		return id;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public String getMessage() {
		return message;
	}

	public Status getStatus() {
		return status;
	}

	public Date getDateAdded() {
		return dateAdded;
	}
	
	public Date getDateModified() {
		return dateModified;
	}

	public Type getType() {
		return type;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setId(String id) {
		if (id == null || id.length() == 0) {
			throw new IllegalArgumentException("Invalid identifier.");
		}
		this.id = id;
	}

	private void setPicUrl(String picUrl) {
		ValidationUtil.validateNonNull(picUrl, "Picture url can't be null.");
		this.picUrl = picUrl;
	}

	public void setMessage(String message) {
		ValidationUtil.validateNonNull(message, "Message can't be null.");
		this.message = message;
	}

	public void setStatus(Status status) {
		ValidationUtil.validateNonNull(status, "Status can't be null.");
		this.status = status;
	}

	public void setDateAdded(Date dateAdded) {
		ValidationUtil.validateNonNull(dateAdded, "Date added can't be null.");
		this.dateAdded = dateAdded;
	}

	public void setDateModified(Date dateModified) {
		ValidationUtil.validateNonNull(dateModified, "Date modified can't be null.");
		this.dateModified = dateModified;
	}

	private void setType(Type type) {
		ValidationUtil.validateNonNull(type, "Notification type can't be null.");
		this.type = type;
	}

	private void setCategory(Category category) {
		ValidationUtil.validateNonNull(category, "Notification category can't be null.");
		this.category = category;
	}
	
	private void setUserId(long userId) {		
		this.userId = userId;
	}

	public long getAssociationId() {
		return associationId;
	}

	private void setAssociationId(long associationId) {
		this.associationId = associationId;
	}
	
	String getSearchKey() {
		return getSearchKey(String.valueOf(getUserId()), String.valueOf(getAssociationId()), getCategory(), getType());
	}
	
	static String getSearchKey(String userId, String assocId, Category category, Type type) {
		return userId + assocId + category.getValue() + type.getValue();
	}

	public abstract String constructMessage();

	@Override
	public String toString() {
		return "Notification [id=" + id + ", associationId=" + associationId + ", picUrl=" + picUrl + ", message="
				+ message + ", status=" + status + ", dateAdded=" + dateAdded + ", dateModified=" + dateModified
				+ ", type=" + type + ", category=" + category + ", userId=" + userId + ", messageMap=" + messageMap
				+ "]";
	}
}
