package com.tecsolvent.wizspeak.model;

/**
 * Created by gopu on 31/3/16.
 */
public class MentorFollow {
	private long id,uId,mId;
	private String user_id, mentor_id;
	private String status, date_updated;


	public MentorFollow() {
	}

	public MentorFollow(long id, String user_id, String mentor_id, String status, String date_updated) {
		this.id = id;
		this.user_id = user_id;
		this.mentor_id = mentor_id;
		this.status = status;
		this.date_updated = date_updated;
	}

	//gettere
	public long getId() {
		return id;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getMentor_id() {
		return mentor_id;
	}

	public String getStatus() {
		return status;
	}

	public String getDate_updated() {
		return date_updated;
	}

	public long getuId() {
		return uId;
	}

	public long getmId() {
		return mId;
	}
//setteres


	public void setId(long id) {
		this.id = id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setMentor_id(String mentor_id) {
		this.mentor_id = mentor_id;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDate_updated(String date_updated) {
		this.date_updated = date_updated;
	}

	public void setuId(long uId) {
		this.uId = uId;
	}

	public void setmId(long mId) {
		this.mId = mId;
	}
}
