package com.tecsolvent.wizspeak.model;

/**
 * Created by gopu on 31/3/16.
 */
public class MentorRate {
	private long id;
	private int user_id, mentor_id;
	private String rating, date_rated;

	public MentorRate(long id, int user_id, int mentor_id, String rating, String date_rated) {
		this.id = id;
		this.user_id = user_id;
		this.mentor_id = mentor_id;
		this.rating = rating;
		this.date_rated = date_rated;
	}

	public MentorRate() {

	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUser_id(String user_id) {
		return this.user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getMentor_id(String mentor_id) {
		return this.mentor_id;
	}

	public void setMentor_id(int mentor_id) {
		this.mentor_id = mentor_id;
	}

	public String getRating(String rating) {
		return this.rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getDate_rated(String date_rated) {
		return this.date_rated;
	}

	public void setDate_rated(String date_rated) {
		this.date_rated = date_rated;
	}
}
